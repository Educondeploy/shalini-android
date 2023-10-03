package com.tt.skolarrs.callrecorder

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CallRRecorder {
    companion object {
    private val audioBufferSize: Int = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
     var recorder: AudioRecord? = null
    private var audioData: ShortArray = ShortArray(audioBufferSize)





    fun onRecord(context: Context, isRecording: Boolean) {
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        val ss = intArrayOf( //                MediaRecorder.AudioSource.VOICE_CALL,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,  // mic source VOIP
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            MediaRecorder.AudioSource.CAMCORDER,
            MediaRecorder.AudioSource.MIC,  // mic
            MediaRecorder.AudioSource.DEFAULT,  // mic
            MediaRecorder.AudioSource.UNPROCESSED
        )
        var i = -1
        i = if (i == -1) 0 else indexOf(ss, i)

        requestAllPermission(context, i, isRecording)


    }

    fun requestAllPermission(context: Context, i: Int, isRecording: Boolean) {
        val requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
        )

        val anyPermissionNotGranted = requiredPermissions.all { permission ->
            context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
        }



        Log.d("TAG", "requestPermission: " + anyPermissionNotGranted)

        if (context.checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(
                context,
                "Please allow all permission",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (isRecording) {
                startRecording(context, i, isRecording)
            } else {
                stopRecording()
            }
        }
    }


    fun startRecording(context: Context, i: Int, isRecording: Boolean) {

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context?.cacheDir
        val outputFile = File(storageDir, "Call_$timestamp.3gp")




        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100 ,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            2000
        )

        recorder!!.startRecording()

        Thread {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context?.cacheDir
            // val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val audioFile = File(storageDir, "Call_$timestamp.3gp")
            // val audioFile = File(outputFile)
            audioFile.createNewFile()

            try {

                val fileOutputStream = FileOutputStream(audioFile)
                while (isRecording) {
                    val readSize = recorder!!.read(ShortArray(1000), 0, 2000)
                    for (i in 0 until readSize) {
                        fileOutputStream.write(audioData[i].toByte().toInt())
                    }
                }
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()


    }

    private fun stopRecording() {

        recorder!!.stop()
        recorder!!.release()

    }

    fun indexOf(ss: IntArray, s: Int): Int {
        for (i in ss.indices) {
            if (ss[i] == s) return i
        }
        return -1
    }


} }