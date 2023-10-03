package com.tt.skolarrs.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.CallLog
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.UpdateNotificationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    var lead_id: String = ""
    var mobileNumber: String = ""
    var type: String = ""
    var lead_type: String = ""
    var lead_name: String = ""
    var intentType: String = ""
    lateinit var MOBILE_NO: String

    private lateinit var updateNotificationViewModel: UpdateNotificationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        updateNotificationViewModel =
            ViewModelProvider(this)[UpdateNotificationViewModel::class.java]

        onNewIntent(intent)

        //  this.stopService(Intent(this, FirebaseMessageReceiver::class.java))

    }

    /* override fun onResume() {
         super.onResume()
         if (intent != null && intent.getStringExtra(Constant.NOTIFICATION_TYPE) != null && intent.getIntExtra(Constant.NOTIFICATION_ID, 0) != 0) {
             intentType = intent.getStringExtra(Constant.NOTIFICATION_TYPE)!!

 //            var id: String = intent.getStringExtra(Constant.PUSH_NOTIFICATION_ID)!!
             if (MyFunctions.isConnected(this)) {
                 val scope = CoroutineScope(Dispatchers.IO)
                 scope.launch(Dispatchers.Main) {

                    // updateNotification(id)

                 }
             } else {
                 Toast.makeText(
                     this,
                     this.getString(R.string.please_turn_on_your_internet_connection),
                     Toast.LENGTH_SHORT
                 )
                     .show()
             }

             when (intentType) {
                 "followUp" -> {
                    requestPermission()
                 }
                 "permission" -> {
                     startActivity(
                         Intent(
                             this@SplashScreenActivity,
                             MainActivity::class.java
                         ).putExtra(Constant.NOTIFICATION_TYPE, "permission")
                     )
                     intent = null
                     finish()
                 }
                 "leave" -> {
                     startActivity(
                         Intent(
                             this@SplashScreenActivity,
                             MainActivity::class.java
                         ).putExtra(Constant.NOTIFICATION_TYPE, "leave")
                     )
                     intent = null
                     finish()
                 }
                 "lead" -> {
                     startActivity(
                         Intent(
                             this@SplashScreenActivity,
                             MainActivity::class.java
                         ).putExtra(Constant.NOTIFICATION_TYPE, "lead")
                     )
                     intent = null
                     finish()
                 }

             }


         } else {
             if (type == Constant.CALL) {
                 getLastCalledNumber(this@SplashScreenActivity)
                 getLastCallDuration()
             } else {

                 Handler().postDelayed({
                     if (MyFunctions.getSharedPreference(this, Constant.isloggedIn, false)) {
                         // startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                         startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                     } else {
                         startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                     }
                     finish()
                 }, 2000)


             }
         }

     }*/

    override fun onResume() {
        super.onResume()
        if (intent != null && intent.getStringExtra(Constant.NOTIFICATION_TYPE) != null ) {
            intentType = intent.getStringExtra(Constant.NOTIFICATION_TYPE)!!
            Log.d("TAG", "onResume: " + intentType)
            startActivity(
                Intent(this@SplashScreenActivity, MainActivity::class.java).putExtra(
                    Constant.NOTIFICATION_TYPE,
                    "followUp"
                )
            )
        }

        else {
            if (intent != null && intent.getStringExtra(Constant.TYPE) != null) {
                requestPermission()
            }
            else {
                if (type == Constant.CALL) {

                //    MyFunctions.onRecord(false, this@SplashScreenActivity);
                    getLastCalledNumber(this@SplashScreenActivity)
                    getLastCallDuration()
                } else {
                    Handler().postDelayed({
                        if (MyFunctions.getSharedPreference(this, Constant.isloggedIn, false)) {
                            // startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                        } else {
                            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                        }
                        finish()
                    }, 2000)
                }
            }

        }


      /*  if (intent != null && intent.getStringExtra(Constant.NOTIFICATION_TYPE) != null && MyFunctions.getSharedPreference(
                this,
                Constant.isloggedIn,
                false
            )
        ) {
            intentType = intent.getStringExtra(Constant.NOTIFICATION_TYPE)!!
            startActivity(
                Intent(this@SplashScreenActivity, MainActivity::class.java).putExtra(
                    Constant.NOTIFICATION_TYPE,
                    "followUp"
                )
            )

            if (intentType == "followUp") {
                requestPermission()
            } else {
                startActivity(
                    Intent(this@SplashScreenActivity, MainActivity::class.java).putExtra(
                        Constant.NOTIFICATION_TYPE,
                        "followUp"
                    )
                )
            }
        } else {
            if (type == Constant.CALL) {
                getLastCalledNumber(this@SplashScreenActivity)
                getLastCallDuration()
            } else {
                Handler().postDelayed({
                    if (MyFunctions.getSharedPreference(this, Constant.isloggedIn, false)) {
                        // startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                    }
                    finish()
                }, 2000)
            }
        }*/

    }

    private fun getFollowUp() {
        if (MyFunctions.getSharedPreference(
                this,
                Constant.isloggedIn,
                false
            ) && intent != null && intent.getStringExtra(Constant.LEAD_ID) != null && intent.getStringExtra(
                Constant.LEAD_NUMBER
            ) != null && intent.getStringExtra(
                Constant.LEAD_TYPE
            ) != null && intent.getStringExtra(Constant.LEAD_NAME) != null
        ) {
            Log.d("TAG", "getFollowUp: " + intent.getStringExtra(Constant.LEAD_TYPE))
            lead_id = intent.getStringExtra(Constant.LEAD_ID)!!
            mobileNumber = intent.getStringExtra(Constant.LEAD_NUMBER)!!
            lead_type = intent.getStringExtra(Constant.LEAD_TYPE)!!
            lead_name = intent.getStringExtra(Constant.LEAD_NAME)!!
            type = Constant.CALL
            intent = null
         //   MyFunctions.onRecord(true, this@SplashScreenActivity)
            val u = Uri.parse("tel:$mobileNumber")
            val i = Intent(Intent.ACTION_DIAL, u)
            startActivity(i)
        } else {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }

    }

    private fun getLastCallDuration(): String? {
        type = ""
        var duration = "0"
        try {
            val cursor = this@SplashScreenActivity.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
            )
            if (cursor != null && cursor.moveToFirst()) {
                val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
                duration = cursor.getString(durationIndex)
                cursor.close()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        MyFunctions.setSharedPreference(
            this,
            Constant.DURATION,
            MyFunctions.formatSeconds(duration.toInt())
        )
        MyFunctions.setSharedPreference(this, Constant.LEAD_ID, lead_id)
        MyFunctions.setSharedPreference(this, Constant.LEAD_TYPE, lead_type)
        MyFunctions.setSharedPreference(this, Constant.LEAD_NAME, lead_name)
        MyFunctions.setSharedPreference(this, Constant.LEAD_NUMBER, mobileNumber)


        if (MOBILE_NO == mobileNumber) {
            MyFunctions.setSharedPreference(
                this@SplashScreenActivity,
                Constant.DURATION,
                MyFunctions.formatSeconds(duration.toInt())
            )
            startActivity(
                Intent(
                    this@SplashScreenActivity,
                    FollowUpActivity2::class.java
                )
            )
        } else {
            Toast.makeText(this@SplashScreenActivity, "Please make a call", Toast.LENGTH_SHORT)
                .show()
            startActivity(
                Intent(
                    this@SplashScreenActivity,
                    MainActivity::class.java
                )
            )
        }

        Log.d("TAG", "getLastCallDuration: " + duration)
        return duration
    }

    @SuppressLint("Range")
    fun getLastCalledNumber(context: Context): String? {
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )
        if (cursor?.moveToFirst() == true) {
            val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            cursor.close()
            Log.d("TAG", "getLastCalledNumber: $number")
            MOBILE_NO = number
            return number
        }
        cursor?.close()
        return null
    }

    private fun requestPermission() {
        if (this@SplashScreenActivity.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED && this@SplashScreenActivity.checkSelfPermission(
                Manifest.permission.WRITE_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED && this@SplashScreenActivity.checkSelfPermission(
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                ), 1
            )
        } else {
            getFollowUp()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            // Checking whether user granted the permission or not.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //   Toast.makeText(this, getString(R.string.please_allow_contact_permission), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                    this@SplashScreenActivity,
                    getString(R.string.please_allow_contact_permission),
                    Toast.LENGTH_SHORT
                ).show();
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            }
        }
    }

}