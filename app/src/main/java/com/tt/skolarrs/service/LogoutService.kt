package com.tt.skolarrs.service

import android.app.AlertDialog
import android.app.Application
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.tt.skolarrs.R
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.LoginActivity
import com.tt.skolarrs.viewmodel.LogoutViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogoutService : Service() {

    private var timer: Timer? = null
    private lateinit var logoutViewModel: LogoutViewModel
    private var receiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        logoutViewModel = LogoutViewModel()
        timer = Timer()

        // 8 hours in milliseconds
       timer?.schedule(LogoutTask(), 8 * 60 * 60 * 1000)

        // 45 minutes in milliseconds
     //    timer?.schedule(LogoutTask(), 60000)


        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == Intent.ACTION_SHUTDOWN) {
                    Log.d("TAG", "onCreate: ")
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch(Dispatchers.Main) {
                        logout()
                    }

                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_SHUTDOWN)
        registerReceiver(receiver, filter)

        // 2 minutes in milliseconds


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start the service in the foreground to keep it from being killed

        // Set up a BroadcastReceiver to listen for the ACTION_SHUTDOWN intent


        // Return START_STICKY to indicate that the service should be restarted if it's killed
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver when the service is destroyed
        unregisterReceiver(receiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

   /* private fun resetTimer() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(LogoutTask(), 8 * 60 * 60 * 1000) // 8 hours in milliseconds
    }*/


    inner class LogoutTask : TimerTask() {
        override fun run() {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {
                logout()
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        Log.d("TAG", "background notificationManager: $notificationManager")
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch(Dispatchers.Main) {
            logout()
        }
        stopSelf()
    }

    private suspend fun logout() {
        MyFunctions.setSharedPreference(applicationContext, Constant.isloggedIn, false)
        val id = MyFunctions.getSharedPreference(
            applicationContext,
            Constant.USER_ID,
            Constant.USER_ID
        )!!
        logoutViewModel.logout(id, applicationContext)

        try {
            if (logoutViewModel.response.value != null) {
                val response = logoutViewModel.response!!


                MyFunctions.setSharedPreference(applicationContext, Constant.TOKEN, "")
                MyFunctions.setSharedPreference(
                    applicationContext,
                    Constant.ID,
                    ""
                )
                MyFunctions.setSharedPreference(
                    applicationContext,
                    Constant.isloggedIn,
                    false
                )
                MyFunctions.setSharedPreference(
                    applicationContext,
                    Constant.USER_ID,
                    ""
                )
                MyFunctions.deleteSharedPreference(applicationContext)

                val builder = AlertDialog.Builder(this)

                // Set the message show for the Alert time
                builder.setMessage("Oops your session was expired, Please login again")

                // Set Alert Title
                //builder.setTitle("Alert !")

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false)

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Ok") {
                    // When the user click yes button then app will close
                        dialog, which ->   val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(intent)

                }
                val alertDialog = builder.create()
                // Show the Alert Dialog box
                alertDialog.show()


            } else {

                if (logoutViewModel.responseError.value != null) {
                    Log.d("TAG", "background logout: " + logoutViewModel.responseError.value!!)
                    Toast.makeText(
                        applicationContext,
                        logoutViewModel.responseError.value!!,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: Exception) {
            // Toast.makeText(context, logoutViewModel.responseError.value!!, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "getLeads: " + e.message)
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            e.message
            Log.d("TAG", "getLeads: " + e.message)
        }


        //  stopSelf()
    }

}
