package com.tt.skolarrs.view.activity


import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.service.CallStateListener
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.LogoutViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


abstract class BaseActivity : AppCompatActivity(), Runnable {
    private var handler: Handler? = null
    private var preferences: SharedPreferences? = null
    private lateinit var logoutViewModel: LogoutViewModel
    private var isAppInBackground: Boolean = false
    private var isTimerStarted = false
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callStateListener: CallStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logoutViewModel = ViewModelProvider(this)[LogoutViewModel::class.java]

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        handler = Handler()

        isAppInBackground = false;

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        callStateListener = CallStateListener(applicationContext)

        // Register the listener to start receiving call state updates

        // Check and request permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
        } else {
            telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode ==1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            } else {
                // Permission denied, handle accordingly (show message, disable features, etc.)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume: ")
        isAppInBackground = false;
        stopTimer()
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStop: ")
        isAppInBackground = true
        if (!isTimerStarted) {
            startTimer();
            isTimerStarted = true;
        }

    }

    /*override fun onPause() {
        super.onPause()
        isAppInBackground = true
        if (!isTimerStarted) {
            startTimer();
            isTimerStarted = true;
        }

        Log.d("TAG", "onPause: ")
    }*/


    override fun run() {
        Log.d("TAG", "run:$isAppInBackground")
        if (isAppInBackground) {
            val editor = preferences!!.edit()
            editor.clear()
            editor.apply()
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {
               // logOut()
            }
        }

    }

    protected fun startTimer() {
        handler!!.postDelayed(this, 15 * 60 * 1000) // 15 minutes in milliseconds
    }

    protected fun stopTimer() {
        handler!!.removeCallbacks(this)
        isTimerStarted = false;
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the listener when the activity is destroyed
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
    }

    open suspend fun logOut() {
        val id = MyFunctions.getSharedPreference(this, Constant.USER_ID, Constant.USER_ID)!!
        logoutViewModel.logout(id, applicationContext)
        try {
            if (logoutViewModel.response.value != null) {
                val response = logoutViewModel.response

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()

                /*Toast.makeText(
                    this,
                    logoutViewModel.response.value!!.message,
                    Toast.LENGTH_SHORT
                ).show()*/
                MyFunctions.setSharedPreference(this, Constant.TOKEN, "")
                MyFunctions.setSharedPreference(this, Constant.ID, "")
                MyFunctions.setSharedPreference(this, Constant.isloggedIn, false)
                MyFunctions.setSharedPreference(this, Constant.USER_ID, "")
                MyFunctions.deleteSharedPreference(this)
                this.startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
               /* if (logoutViewModel.responseError.value != null) {
                    Toast.makeText(
                        this,
                        logoutViewModel.responseError.value!!,
                        Toast.LENGTH_SHORT
                    ).show()
                }*/
            }

        } catch (e: Exception) {
            Toast.makeText(this, logoutViewModel.responseError.value!!, Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            e.message
        }
    }

}

/*abstract class BaseActivity : AppCompatActivity(), Runnable {

    private var handler: Handler? = null
    private var preferences: SharedPreferences? = null
    private lateinit var logoutViewModel: LogoutViewModel
    private var appInForeground = false

    private val appLifecycleObserver = object : Application.ActivityLifecycleCallbacks {

        private var activityReferences = 0
        private var isActivityChangingConfigurations = false

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        }

        override fun onActivityStarted(activity: Activity) {
            if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                appInForeground = true
            }
        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {
            isActivityChangingConfigurations = activity?.isChangingConfigurations ?: false
            if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                appInForeground = false
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logoutViewModel = ViewModelProvider(this)[LogoutViewModel::class.java]

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        handler = Handler()

        // register the activity lifecycle observer
        application.registerActivityLifecycleCallbacks(appLifecycleObserver)
    }

    override fun onDestroy() {
        // unregister the activity lifecycle observer
        application.unregisterActivityLifecycleCallbacks(appLifecycleObserver)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume: ")
        stopTimer()
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStop: ")
        startTimer()
    }

    override fun run() {
        Log.d("TAG", "run:" )
        if (!appInForeground) { // check if app is in background
            val editor = preferences!!.edit()
            editor.clear()
            editor.apply()
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {
                logOut()
            }
        }
    }

    protected fun startTimer() {
        handler!!.postDelayed(this, 2 * 60 * 1000) // 15 minutes in milliseconds
    }

    protected fun stopTimer() {
        handler!!.removeCallbacks(this)
    }



}*/
