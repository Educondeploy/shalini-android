package com.tt.skolarrs.com.github.axet.callrecorder.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.axet.androidlibrary.app.AlarmManager;
import com.github.axet.androidlibrary.app.ProximityShader;
import com.github.axet.androidlibrary.services.PersistentService;
import com.github.axet.androidlibrary.sound.AudioTrack;
import com.github.axet.androidlibrary.widgets.ErrorDialog;
import com.github.axet.androidlibrary.widgets.Toast;
import com.github.axet.audiolibrary.app.RawSamples;
import com.github.axet.audiolibrary.app.Sound;
import com.github.axet.audiolibrary.encoders.Factory;
import com.github.axet.audiolibrary.encoders.FileEncoder;
import com.github.axet.audiolibrary.encoders.OnFlyEncoding;
import com.github.axet.audiolibrary.filters.AmplifierFilter;
import com.github.axet.audiolibrary.filters.SkipSilenceFilter;
import com.github.axet.audiolibrary.filters.VoiceFilter;
import com.tt.skolarrs.com.github.axet.callrecorder.app.Storage;
import com.tt.skolarrs.BuildConfig;
import com.tt.skolarrs.R;
import com.tt.skolarrs.com.github.axet.callrecorder.app.CallApplication;
import com.tt.skolarrs.view.activity.MainActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RecordingActivity more likly to be removed from memory when paused then service. Notification button
 * does not handle getActvity without unlocking screen. The only option is to have Service.
 * <p/>
 * So, lets have it.
 * <p/>
 * Maybe later this class will be converted for fully feature recording service with recording thread.
 */
public class RecordingService extends PersistentService implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = RecordingService.class.getSimpleName();

    public static final int NOTIFICATION_PERSISTENT_ICON = 1;
    public static final int RETRY_DELAY = 60 * AlarmManager.SEC1; // 1 min

    public static String SHOW_ACTIVITY = RecordingService.class.getCanonicalName() + ".SHOW_ACTIVITY";
    public static String PAUSE_BUTTON = RecordingService.class.getCanonicalName() + ".PAUSE_BUTTON";
    public static String STOP_BUTTON = RecordingService.class.getCanonicalName() + ".STOP_BUTTON";
    public static String CALLSCREEN = RecordingService.class.getCanonicalName() + ".CALLSCREEN";

    Handler handle = new Handler();

    AtomicBoolean interrupt = new AtomicBoolean();
    Thread thread;
    CallInfo incoming = new CallInfo(); // incoming call
    CallInfo call; // handling call recording info
    CallInfo encoding; // handling call encoding info

    Storage storage;
    RecordingReceiver receiver;
    PhoneStateReceiver state;
    PhoneStateChangeListener pscl;

    HashMap<File, CallInfo> mapTarget = new HashMap<>(); // pending encoding info

    FileEncoder encoder; // current encoder

    Runnable encodingNext = new Runnable() {
        @Override
        public void run() {
            encodingNext();
        }
    };

    public static void Post(Context context, Throwable e) {
        Log.e(TAG, "Post", e);
        Toast.Post(context, "CallRecorder: " + ErrorDialog.toMessage(e));
    }

    public static void Error(Context context, Throwable e) {
        Log.d(TAG, "Error", e);
        Toast.Text(context, "CallRecorder: " + ErrorDialog.toMessage(e));
    }

    public static void setEnabled(Context context, boolean b) {
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = shared.edit();
        edit.putBoolean(CallApplication.PREFERENCE_CALL, b);
        edit.commit();
        if (b) {
            RecordingService.start(context);
            Toast.makeText(context," R.string.recording_enabled", Toast.LENGTH_SHORT).show();
        } else {
            RecordingService.stop(context);
            Toast.makeText(context, "R.string.recording_disabled", Toast.LENGTH_SHORT).show();
        }
    }

    public static void start(Context context) {
        start(context, new Intent(context, RecordingService.class));
    }

    public static boolean isEnabled(Context context) {
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        boolean b = shared.getBoolean(CallApplication.PREFERENCE_CALL, false);
        //if (!Storage.permitted(context, MainActivity.MUST))
            b = false;
        return b;
    }

    public static void startIfEnabled(Context context) {
        if (isEnabled(context))
            start(context);
    }

    public static void stop(Context context) {
        stop(context, new Intent(context, RecordingService.class));
    }

    public static void pauseButton(Context context) {
        Intent intent = new Intent(PAUSE_BUTTON);
        context.sendBroadcast(intent);
    }

    public static void stopButton(Context context) {
        Intent intent = new Intent(STOP_BUTTON);
        context.sendBroadcast(intent);
    }

    public static int[] concat(int[] first, int[] second) {
        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static class MediaRecorderThread extends Thread {
        public MediaRecorderThread() {
            super("RecordingThread");
        }

        @Override
        public void run() {
            super.run();
        }
    }

    public interface Success {
        void run(Uri u);
    }

    public static class CallInfo {
        public Uri targetUri;
        public String phone;
        public String contact;
        public String contactId;
        public String call;
        public long now;
        public long samplesTime; // how many samples passed for current recording
        public int source = -1; // audiorecorder / mediarecorder source
        public RawSamples.Info info;

        public CallInfo() {
        }

        public CallInfo(CallInfo c) {
            targetUri = c.targetUri;
            phone = c.phone;
            contact = c.contact;
            contactId = c.contactId;
            call = c.call;
            now = c.now;
            samplesTime = c.samplesTime;
            source = c.source;
            info = c.info;
        }

        public CallInfo(Uri t, String p, String c, String cid, String call, long now) {
            this.targetUri = t;
            this.phone = p;
            this.contact = c;
            this.contactId = cid;
            this.call = call;
            this.now = now;
        }
    }

    class RecordingReceiver extends BroadcastReceiver {
        IntentFilter filter;

        public RecordingReceiver() {
            filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(PAUSE_BUTTON);
            filter.addAction(STOP_BUTTON);
        }

        public void register(Context context) {
            context.registerReceiver(this, filter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String a = intent.getAction();
                if (a.equals(PAUSE_BUTTON))
                    pauseButton();
                if (a.equals(STOP_BUTTON))
                    finish();
            } catch (RuntimeException e) {
                Error(RecordingService.this, e);
            }
        }
    }

    class PhoneStateReceiver extends BroadcastReceiver {
        IntentFilter filters;

        public PhoneStateReceiver() {
            filters = new IntentFilter();
            filters.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            filters.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            filters.addAction(CALLSCREEN);
        }

        public void register(Context context) {
            context.registerReceiver(this, filters);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            String a = intent.getAction();
            if (a.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
                setPhone(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER), incoming.call);
            if (a.equals(Intent.ACTION_NEW_OUTGOING_CALL))
                setPhone(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER), CallApplication.CALL_OUT);
            if (a.equals(CALLSCREEN))
                setPhone(intent.getStringExtra("phone"), intent.getStringExtra("call"));
        }
    }

    class PhoneStateChangeListener extends PhoneStateListener {
        public boolean wasRinging;
        public boolean startedByCall;
        public TelephonyManager tm;
        @TargetApi(31)
        public TelephonyCallback e;

        @TargetApi(31)
        public class TelephonyCallback extends android.telephony.TelephonyCallback implements android.telephony.TelephonyCallback.CallStateListener {
            @Override
            public void onCallStateChanged(int i) {
                PhoneStateChangeListener.this.onCallStateChanged(i, "");
            }
        }

        public PhoneStateChangeListener() {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        }

        public void create() {
            Context context = RecordingService.this;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= 31 && getApplicationInfo().targetSdkVersion >= 31) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    e = new TelephonyCallback();
                    tm.registerTelephonyCallback(getMainExecutor(), e);
                }
            } else {
                tm.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }

        public void close() {
            tm.listen(pscl, PhoneStateListener.LISTEN_NONE);
        }

        @Override
        public void onCallStateChanged(final int s, final String incomingNumber) {
            Log.d(TAG, "onCallStateChanged");
            try {
                switch (s) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        setPhone(incomingNumber, CallApplication.CALL_IN);
                        wasRinging = true;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        setPhone(incomingNumber, incoming.call);
                        if (thread == null) { // handling restart while current call
                            begin(wasRinging);
                            startedByCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (startedByCall) {
                            if (tm.getCallState() != TelephonyManager.CALL_STATE_OFFHOOK) // current state maybe differed from queued (s) one
                                finish();
                            else
                                return; // fast clicking. new call already stared. keep recording. do not reset startedByCall
                        } else {
                            if (storage.recordingPending()) // handling restart after call finished
                                finish();
                            else if (storage.recordingNextPending()) // only call encodeNext if we have next encoding
                                encodingNext();
                        }
                        wasRinging = false;
                        startedByCall = false;
                        incoming = new CallInfo();
                        break;
                }
            } catch (RuntimeException e) {
                Error(RecordingService.this, e);
            }
        }
    }

    public RecordingService() {
    }

    @SuppressLint("Range")
    public void setPhone(String s, String c) {
        if (s == null || s.isEmpty())
            return;

        incoming = new CallInfo();
        incoming.phone = PhoneNumberUtils.formatNumber(s);

        incoming.contact = "";
        incoming.contactId = "";
       // if (Storage.permitted(this, SettingsActivity.CONTACTS)) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(s));
            try {
                ContentResolver contentResolver = getContentResolver();
                Cursor contactLookup = contentResolver.query(uri, null, null, null, null);
                if (contactLookup != null) {
                    try {
                        if (contactLookup.moveToNext()) {
                            incoming.contact = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                            incoming.contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                        }
                    } finally {
                        contactLookup.close();
                    }
                }
            } catch (RuntimeException e) {
                Error(RecordingService.this, e);
            }
//        }

        incoming.call = c;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        receiver = new RecordingReceiver();
        receiver.register(this);

        storage = new Storage(this);

        deleteOld();

        pscl = new PhoneStateChangeListener();
        pscl.create();

        state = new PhoneStateReceiver();
        state.register(this);

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        shared.registerOnSharedPreferenceChangeListener(this);

        try {
            encodingNext();
        } catch (RuntimeException e) {
            Error(RecordingService.this, e);
        }
    }

//    @Override
//    public void onCreateOptimization() {
//        optimization = new OptimizationPreferenceCompat.ServiceReceiver(this, NOTIFICATION_PERSISTENT_ICON, CallApplication.PREFERENCE_OPTIMIZATION, CallApplication.PREFERENCE_NEXT) {
//            @Override
//            public Notification build(Intent intent) {
//                if (thread == null && encoding == null)
//                    return buildPersistent(icon.notification);
//                else
//                    return buildNotification(icon.notification);
//            }
//        };
//        optimization.create();
//    }

    void deleteOld() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        String d = shared.getString(CallApplication.PREFERENCE_DELETE, CallApplication.PREFERENCE_DELETE_OFF);
        if (d.equals(CallApplication.PREFERENCE_DELETE_OFF))
            return;

        try {
            final String[] ee = Storage.ENCODERS;
            Uri path = storage.getStoragePath();

            List<Storage.Node> nn = Storage.list(this, path, new Storage.NodeFilter() {
                @Override
                public boolean accept(Storage.Node n) {
                    for (String e : ee) {
                        e = e.toLowerCase();
                        if (n.name.endsWith(e))
                            return true;
                    }
                    return false;
                }
            });

            for (Storage.Node n : nn) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(n.last);
                Calendar cur = c;

                if (d.equals(CallApplication.PREFERENCE_DELETE_1DAY)) {
                    cur = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_YEAR, 1);
                }
                if (d.equals(CallApplication.PREFERENCE_DELETE_1WEEK)) {
                    cur = Calendar.getInstance();
                    c.add(Calendar.WEEK_OF_YEAR, 1);
                }
                if (d.equals(CallApplication.PREFERENCE_DELETE_1MONTH)) {
                    cur = Calendar.getInstance();
                    c.add(Calendar.MONTH, 1);
                }
                if (d.equals(CallApplication.PREFERENCE_DELETE_3MONTH)) {
                    cur = Calendar.getInstance();
                    c.add(Calendar.MONTH, 3);
                }
                if (d.equals(CallApplication.PREFERENCE_DELETE_6MONTH)) {
                    cur = Calendar.getInstance();
                    c.add(Calendar.MONTH, 6);
                }

                if (c.before(cur)) {
                    if (!CallApplication.getStar(this, n.uri)) // do not delete favorite recorings
                        Storage.delete(this, n.uri);
                }
            }
        } catch (RuntimeException e) {
            Log.d(TAG, "unable to delete old", e); // hide all deleteOld IO errors
        }
    }

    @Override
    public void onStartCommand(Intent intent) {
        String a = intent.getAction();
        if (a == null) {
            ; // nothing
        } else if (a.equals(PAUSE_BUTTON)) {
            pauseButton(this);
        } else if (a.equals(STOP_BUTTON)) {
            stopButton(this);
        } else if (a.equals(SHOW_ACTIVITY)) {
            ProximityShader.closeSystemDialogs(this);
           // MainActivity.startActivity(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handle.removeCallbacks(encodingNext);

        stopRecording();

        handle.removeCallbacksAndMessages(null); // drop 'done' events

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        shared.unregisterOnSharedPreferenceChangeListener(this);

        if (receiver != null) {
            receiver.unregister(this);
            receiver = null;
        }

        if (state != null) {
            state.unregister(this);
            state = null;
        }

        if (pscl != null) {
            pscl.close();
            pscl = null;
        }
    }

    public String getSourceText() {
        if (call == null)
            return "";
        switch (call.source) {
            case MediaRecorder.AudioSource.VOICE_UPLINK:
                return "(VOICE_UPLINK)";
            case MediaRecorder.AudioSource.VOICE_DOWNLINK:
                return "(VOICE_DOWNLINK)";
            case MediaRecorder.AudioSource.VOICE_CALL:
                return getString(R.string.source_line);
            case MediaRecorder.AudioSource.VOICE_COMMUNICATION:
                return "(VoIP)";
            case MediaRecorder.AudioSource.MIC:
                return getString(R.string.source_mic);
            case MediaRecorder.AudioSource.DEFAULT:
                return getString(R.string.source_default);
            case MediaRecorder.AudioSource.UNPROCESSED:
                return "(RAW)";
            case MediaRecorder.AudioSource.VOICE_RECOGNITION:
                return "(VOICE_RECOGNITION)";
            case MediaRecorder.AudioSource.CAMCORDER:
                return "(Camcorder)";
            default:
                return "" + call.source;
        }
    }

//    @SuppressLint("RestrictedApi")
//    public Notification buildNotification(Notification when) {
//        boolean recording = thread != null;
//
//        PendingIntent main = PendingIntent.getService(this, 0,
//                new Intent(this, RecordingService.class).setAction(SHOW_ACTIVITY),
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        PendingIntent pe = PendingIntent.getService(this, 0,
//                new Intent(this, RecordingService.class).setAction(STOP_BUTTON),
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        RemoteNotificationCompat.Builder builder = new RemoteNotificationCompat.Builder(this, R.layout.notifictaion);
//
//        String title;
//        String text;
//
//        title = encoding != null ? getString(R.string.encoding_title) : (getString(R.string.recording_title) + " " + getSourceText());
//        text = ".../" + Storage.getName(this, encoding != null ? encoding.targetUri : call.targetUri);
//        builder.setViewVisibility(R.id.notification_pause, View.VISIBLE);
//        builder.setImageViewResource(R.id.notification_pause, recording ? R.drawable.ic_stop_black_24dp : R.drawable.ic_play_arrow_black_24dp);
//
//        title = title.trim();
//
//        builder.setOnClickPendingIntent(R.id.notification_pause, pe);
//        builder.setViewVisibility(R.id.notification_record, View.GONE);
//
//        if (encoding != null)
//            builder.setViewVisibility(R.id.notification_pause, View.GONE);
//
//        builder.setTheme(CallApplication.getTheme(this, R.style.RecThemeLight, R.style.RecThemeDark))
//                .setImageViewTint(R.id.icon_circle, builder.getThemeColor(R.attr.colorButtonNormal))
//                .setMainIntent(main)
//                .setIcon(R.drawable.ic_mic_24dp)
//                .setTitle(title)
//                .setText(text)
//                .setChannel(CallApplication.from(this).channelStatus)
//                .setWhen(when)
//                .setOngoing(true)
//                .setSmallIcon(R.drawable.ic_launcher_notification_call);
//
//        return builder.build();
//    }
//
//    @SuppressLint("RestrictedApi")
//    public Notification buildPersistent(Notification when) {
//        PendingIntent main = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        RemoteNotificationCompat.Builder builder = new RemoteNotificationCompat.Low(this, R.layout.notifictaion);
//
//        builder.setViewVisibility(R.id.notification_pause, View.GONE);
//        builder.setViewVisibility(R.id.notification_record, View.GONE);
//
//        builder.setTheme(CallApplication.getTheme(this, R.style.RecThemeLight, R.style.RecThemeDark))
//                .setChannel(CallApplication.from(this).channelPersistent)
//                .setMainIntent(main)
//                .setTitle(getString(R.string.app_name))
//                .setText(getString(R.string.recording_enabled))
//                .setIcon(R.drawable.ic_call_black_24dp)
//                .setImageViewTint(R.id.icon_circle, builder.getThemeColor(R.attr.colorButtonNormal))
//                .setWhen(when)
//                .setOngoing(true)
//                .setSmallIcon(R.drawable.ic_launcher_notification_service);
//
//        return builder.build();
//    }

//    public void updateIcon(boolean show) {
//        boolean recording = thread != null;
//        CallInfo info = encoding;
//        if (call != null)
//            info = call;
//        if (info != null)
//            MainActivity.showProgress(RecordingService.this, show, info.phone, info.samplesTime / info.info.hz, recording);
//        optimization.icon.updateIcon(show ? new Intent() : null);
//    }

//    public void showDone(Uri targetUri) {
//        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
//        if (!shared.getBoolean(CallApplication.PREFERENCE_DONE_NOTIFICATION, false))
//            return;
//        RecentCallActivity.startActivity(this, targetUri, true);
//    }

    void startRecording() {
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);

        int[] ss = new int[]{
                MediaRecorder.AudioSource.VOICE_CALL,
                MediaRecorder.AudioSource.VOICE_COMMUNICATION, // mic source VOIP
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                MediaRecorder.AudioSource.CAMCORDER,
                MediaRecorder.AudioSource.MIC, // mic
                MediaRecorder.AudioSource.DEFAULT, // mic
                MediaRecorder.AudioSource.UNPROCESSED,
        };
        int i;
        int s = Integer.valueOf(shared.getString(CallApplication.PREFERENCE_SOURCE, "-1"));
        if (s == -1) {
            i = 0;
        } else {
            i = Sound.indexOf(ss, s);
            if (i == -1) { // missing source, add as first
                ss = concat(new int[]{s}, ss);
                i = 0;
            }
        }

        String ext = shared.getString(CallApplication.PREFERENCE_ENCODING, "");
        if (Storage.isMediaRecorder(ext))
            startMediaRecorder(ext, ss, i);
        else
            startAudioRecorder(ss, i);

        //updateIcon(true);
    }

    void startAudioRecorder(int[] ss, int i) {
        final CallInfo call = this.call;

        final OnFlyEncoding fly = new OnFlyEncoding(storage, call.targetUri, call.info);

        final AudioRecord recorder = Sound.createAudioRecorder(this, fly.info.format, fly.info.hz, ss, i);
        call.source = recorder.getAudioSource();

        final Thread old = thread;
        final AtomicBoolean oldb = interrupt;

        interrupt = new AtomicBoolean(false);

        thread = new Thread("RecordingThread") {
            @Override
            public void run() {
                if (old != null) {
                    oldb.set(true);
                    old.interrupt();
                    try {
                        old.join();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wlcpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.APPLICATION_ID + ":cpulock");
                wlcpu.acquire();

                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                Runnable done = new Runnable() {
                    @Override
                    public void run() {
                        deleteOld();
                        stopRecording();
                        //updateIcon(false);
                    }
                };

                Runnable save = new Runnable() {
                    @Override
                    public void run() {
                        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(RecordingService.this);
                        SharedPreferences.Editor edit = shared.edit();
                        edit.putString(CallApplication.PREFERENCE_LAST, Storage.getName(RecordingService.this, fly.targetUri));
                        edit.commit();

                        CallApplication.setContact(RecordingService.this, call.targetUri, call.contactId);
                        CallApplication.setCall(RecordingService.this, call.targetUri, call.call);
                      //  MainActivity.last(RecordingService.this);
                        //showDone(call.targetUri);
                    }
                };

                try {
                    long start = System.currentTimeMillis();
                    recorder.startRecording();

                    int samplesTimeCount = 0;
                    // how many samples we need to update 'samples'. time clock. every 1000ms.
                    int samplesTimeUpdate = 1000 * call.info.hz / 1000;

                    AudioTrack.SamplesBuffer buffer = new AudioTrack.SamplesBuffer(Sound.DEFAULT_AUDIOFORMAT, 100 * call.info.hz / 1000 * fly.info.channels);

                    boolean stableRefresh = false;

                    while (!interrupt.get()) {
                        final int readSize;
                        switch (buffer.format) {
                            case AudioFormat.ENCODING_PCM_16BIT:
                                readSize = recorder.read(buffer.shorts, 0, buffer.capacity);
                                break;
                            default:
                                throw new RuntimeException("Unsupported format");
                        }
                        if (readSize < 0)
                            break;
                        long end = System.currentTimeMillis();

                        long diff = (end - start) * call.info.hz / 1000;

                        start = end;

                        int samples = readSize / fly.info.channels;

                        if (stableRefresh || diff >= samples) {
                            stableRefresh = true;

                            fly.encode(buffer, 0, readSize);

                            call.samplesTime += samples;
                            samplesTimeCount += samples;
                            if (samplesTimeCount > samplesTimeUpdate) {
                                samplesTimeCount -= samplesTimeUpdate;
                               // MainActivity.showProgress(RecordingService.this, true, call.phone, call.samplesTime / call.info.hz, true);
                            }
                        }
                    }
                } catch (final RuntimeException e) {
                    Storage.delete(RecordingService.this, fly.targetUri);
                    Post(RecordingService.this, e);
                    return; // no save
                } finally {
                    wlcpu.release();

                    handle.post(done);

                    if (recorder != null)
                        recorder.release();

                    if (fly != null) {
                        try {
                            fly.close();
                        } catch (RuntimeException e) {
                            Storage.delete(RecordingService.this, fly.targetUri);
                            Post(RecordingService.this, e);
                            return; // no save
                        }
                    }
                }
                handle.post(save);
            }
        };
        thread.start();
    }

    void startMediaRecorder(String ext, int[] ss, int i) {
        final CallInfo call = this.call;

        try {
            FileDescriptor fd;
            String s = call.targetUri.getScheme();
            if (Build.VERSION.SDK_INT >= 21 && s.equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver resolver = getContentResolver();
                Uri root = Storage.getDocumentTreeUri(call.targetUri);
                resolver.takePersistableUriPermission(root, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                String path = Storage.getDocumentChildPath(call.targetUri);
                Uri out = Storage.createFile(this, root, path);
                if (out == null)
                    throw new RuntimeException("Unable to create file, permissions?");
                ParcelFileDescriptor pfd = resolver.openFileDescriptor(out, "rw");
                fd = pfd.getFileDescriptor();
            } else {
                FileOutputStream os = new FileOutputStream(Storage.getFile(call.targetUri));
                fd = os.getFD();
            }

            final MediaRecorder recorder = new MediaRecorder();
            recorder.setAudioChannels(Sound.getChannels(this));
            recorder.setAudioSource(ss[i]);
            recorder.setAudioEncodingBitRate(Factory.getBitrate(call.info.hz));

            call.source = ss[i];

            switch (ext) {
                case Storage.EXT_3GP:
                    recorder.setAudioSamplingRate(8192);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    break;
                case Storage.EXT_3GP16:
                    recorder.setAudioSamplingRate(16384);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                    break;
                case Storage.EXT_AAC:
                    recorder.setAudioSamplingRate(call.info.hz);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    break;
                case Storage.EXT_AACHE:
                    recorder.setAudioSamplingRate(call.info.hz);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
                    break;
                case Storage.EXT_AACELD:
                    recorder.setAudioSamplingRate(call.info.hz);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
                    break;
                case Storage.EXT_WEBM:
                    recorder.setAudioSamplingRate(call.info.hz);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.WEBM);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.VORBIS);
                    break;
                default:
                    recorder.setAudioSamplingRate(call.info.hz);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            }
            recorder.setOutputFile(fd);
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Log.d(TAG, "MediaRecorder error" + what + " " + extra);
                    stopRecording();
                }
            });
            recorder.prepare();
            final Thread old = thread;
            final AtomicBoolean oldb = interrupt;

            interrupt = new AtomicBoolean(false);
            thread = new MediaRecorderThread() {
                @Override
                public void run() {
                    if (old != null) {
                        oldb.set(true);
                        old.interrupt();
                        try {
                            old.join();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    Runnable done = new Runnable() {
                        @Override
                        public void run() {
                            deleteOld();
                            stopRecording();
                            //updateIcon(false);
                        }
                    };

                    Runnable save = new Runnable() {
                        @Override
                        public void run() {
                            final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(RecordingService.this);
                            SharedPreferences.Editor edit = shared.edit();
                            edit.putString(CallApplication.PREFERENCE_LAST, Storage.getName(RecordingService.this, call.targetUri));
                            edit.commit();

                            CallApplication.setContact(RecordingService.this, call.targetUri, call.contactId);
                            CallApplication.setCall(RecordingService.this, call.targetUri, call.call);
                          //  MainActivity.last(RecordingService.this);
                            //showDone(call.targetUri);
                        }
                    };

                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock wlcpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.APPLICATION_ID + ":cpulock");
                    wlcpu.acquire();

                    boolean start = false;
                    try {
                        Thread.sleep(2000); // sleep after prepare, some devices requires to record opponent side
                        recorder.start();
                        start = true;
                        while (!interrupt.get()) {
                            Thread.sleep(1000);
                            call.samplesTime += 1000 * call.info.hz / 1000; // per 1 second
                        //    MainActivity.showProgress(RecordingService.this, true, call.phone, call.samplesTime / call.info.hz, true);
                        }
                    } catch (RuntimeException e) {
                        Storage.delete(RecordingService.this, call.targetUri);
                        Post(RecordingService.this, e);
                        return; // no save
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        wlcpu.release();
                        handle.post(done);
                        if (start) {
                            try {
                                recorder.stop();
                            } catch (
                                    RuntimeException e) { // https://stackoverflow.com/questions/16221866
                                Storage.delete(RecordingService.this, call.targetUri);
                                Post(RecordingService.this, e);
                            }
                        }
                        recorder.release();
                    }
                    handle.post(save);
                }
            };
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void encoding(final File in, final CallInfo call, final Runnable done, final Success success) {
        final OnFlyEncoding fly = new OnFlyEncoding(storage, call.targetUri, call.info);

        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(RecordingService.this);

        encoder = new FileEncoder(this, in, call.info, fly);

        if (shared.getBoolean(CallApplication.PREFERENCE_VOICE, false))
            encoder.filters.add(new VoiceFilter(call.info));
        float amp = shared.getFloat(CallApplication.PREFERENCE_VOLUME, 1);
        if (amp != 1)
            encoder.filters.add(new AmplifierFilter(amp));
        if (shared.getBoolean(CallApplication.PREFERENCE_SKIP, false))
            encoder.filters.add(new SkipSilenceFilter(call.info));

        final Runnable save = new Runnable() {
            @Override
            public void run() {
                Storage.delete(in); // delete raw recording

              //  MainActivity.showProgress(RecordingService.this, false, call.phone, call.samplesTime / call.info.hz, false);

                SharedPreferences.Editor edit = shared.edit();
                edit.putString(CallApplication.PREFERENCE_LAST, Storage.getName(RecordingService.this, call.targetUri));
                edit.commit();

                success.run(call.targetUri);
                done.run();
                encodingNext();
            }
        };

        encoder.run(new Runnable() {
            @Override
            public void run() {  // progress
                //MainActivity.setProgress(RecordingService.this, encoder.getProgress());
            }
        }, new Runnable() {
            @Override
            public void run() {  // success only call, done
                save.run();
            }
        }, new Runnable() {
            @Override
            public void run() { // error
                Storage.delete(RecordingService.this, call.targetUri);
              //  MainActivity.showProgress(RecordingService.this, false, call.phone, call.samplesTime / call.info.hz, false);
                Error(RecordingService.this, encoder.getException());
                done.run();
                handle.removeCallbacks(encodingNext);
                handle.postDelayed(encodingNext, RETRY_DELAY);
            }
        });
    }

    void pauseButton() {
        if (thread != null)
            stopRecording();
        else
            startRecording();
        CallInfo info = encoding;
        if (call != null)
            info = call;
        if (info != null) {}
          //  MainActivity.showProgress(this, true, info.phone, info.samplesTime / info.info.hz, thread != null);
    }

    void stopRecording() {
        if (thread != null) {
            interrupt.set(true);
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            thread = null;
        }
    }

    void begin(boolean wasRinging) {
        call = new CallInfo(incoming);
        call.now = System.currentTimeMillis();
        call.targetUri = storage.getNewFile(call.now, call.phone, call.contact, call.call);
        call.info = new RawSamples.Info(Sound.DEFAULT_AUDIOFORMAT, Sound.getSampleRate(this), Sound.getChannels(this));
        if (encoder != null)
            encoder.pause();
        if (storage.recordingPending()) {
            RawSamples rs = new RawSamples(storage.getTempRecording(), call.info);
            call.samplesTime = rs.getSamples();
        } else {
            call.samplesTime = 0;
        }
        startRecording();
    }

    void finish() {
        stopRecording();
        CallInfo info = new CallInfo(call);
        File tmp = storage.getTempRecording();
        if (tmp.exists() && tmp.length() > 0) {
            File parent = tmp.getParentFile();
            File in = Storage.getNextFile(parent, Storage.TMP_REC, null);
            Storage.move(tmp, in);
            mapTarget.put(in, info);
            if (encoder == null) // double finish()? skip
                encodingNext();
            else
                encoder.resume();
        } else { // if encoding failed, we will get no output file, hide notifications
            deleteOld();
            //updateIcon(false);
        }
        call = null;
    }

    void encodingNext() {
        handle.removeCallbacks(encodingNext); // clean next
        if (encoder != null) // can be called twice, exit if alreay encoding
            return;
        if (thread != null) // currently recorindg
            return;
        final File inFile = storage.getTempNextRecording();
        if (inFile == null)
            return;
        if (!inFile.exists())
            return;
        if (inFile.length() == 0) {
            mapTarget.remove(inFile);
            Storage.delete(inFile);
            return;
        }
        CallInfo c = mapTarget.get(inFile);
        if (c == null) { // service restarted, additional info lost
            c = new CallInfo();
            c.now = inFile.lastModified();
            c.targetUri = storage.getNewFile(c.now, c.phone, c.contact, c.call);
            c.info = new RawSamples.Info(Sound.DEFAULT_AUDIOFORMAT, Sound.getSampleRate(this), Sound.getChannels(this));
        }
        encoding = c; // update current encoding info
        final Runnable done = new Runnable() { //  allways called when done
            @Override
            public void run() {
                deleteOld();
                //updateIcon(false);
                encoder = null;
                encoding = null;
            }
        };
       // updateIcon(true); // update status (encoding)
        Log.d(TAG, "Encoded " + inFile.getName() + " to " + Storage.getDisplayName(this, c.targetUri));
        final CallInfo info = c;
        encoding(inFile, info, done, new Success() {
            @Override
            public void run(Uri t) { // called on success
                mapTarget.remove(inFile);
                CallApplication.setContact(RecordingService.this, t, info.contactId);
                CallApplication.setCall(RecordingService.this, t, info.call);
              //  MainActivity.last(RecordingService.this);
                //showDone(t);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(CallApplication.PREFERENCE_DELETE))
            deleteOld();
        if (key.equals(CallApplication.PREFERENCE_STORAGE))
            encodingNext();
    }
}
