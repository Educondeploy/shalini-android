package com.tt.skolarrs.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tt.skolarrs.R
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.SplashScreenActivity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class FirebaseMessageReceiver : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TAG", "onNewToken: $token")
        MyFunctions.setSharedPreference(applicationContext, Constant.TOKEN, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        //  val data =  remoteMessage.data["data"] as PushNotificationResponse


        Log.d("TAG", "onMessageReceived:data ${remoteMessage.data}")

        if (remoteMessage.data.isNotEmpty()) {

            val dataPayload = remoteMessage.data["data"]
            val jsonObject = JSONObject(dataPayload!!)
            Log.d("TAG", "onMessageReceived: " + jsonObject)
            sendNotification(
                remoteMessage.data, jsonObject, remoteMessage

            )
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun getCustomDesign(title: String, message: String): RemoteViews? {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.notificationTittle, title)
        remoteViews.setTextViewText(R.id.notificationMessage, message)
        return remoteViews
    }

    private fun sendNotification(
        data: Map<*, *>,
        dataObject: JSONObject,
        remoteMessage: RemoteMessage
    ) {

        val title = (data["title"].toString())
        val message = (data["message"].toString())
        val type = (data["type"].toString())

        var i: Intent? = null
        val notificationId = createID()

        if (type == "leave") {
            val notificationData = remoteMessage.data["leaveData"]
            val jsonObject2= JSONObject(notificationData!!)
            val id = jsonObject2.getString("_id")
            Log.d("TAG", "sendNotification: leave $type")
            i = Intent(
                applicationContext,
                SplashScreenActivity::class.java
            ).putExtra(Constant.NOTIFICATION_TYPE, "leave").putExtra(Constant.NOTIFICATION_ID, notificationId).putExtra(Constant.PUSH_NOTIFICATION_ID,id)
        }

        if (type == "permission") {
           val notificationData = remoteMessage.data["permissionData"]
            val jsonObject2= JSONObject(notificationData!!)
            val id = jsonObject2.getString("_id")
            i = Intent(
                applicationContext,
                SplashScreenActivity::class.java
            ).putExtra(Constant.NOTIFICATION_TYPE, "permission").putExtra(Constant.NOTIFICATION_ID, notificationId).putExtra(Constant.PUSH_NOTIFICATION_ID,id )

        }

        if (type == "lead") {
            val leadData = remoteMessage.data["AssignLeadData"]
            val jsonObject1 = JSONObject(leadData!!)
            val id = jsonObject1.getString("_id")
            Log.d("TAG", "sendNotification: AssignLeadData " + type)
            i = Intent(
                applicationContext,
                SplashScreenActivity::class.java
            ).putExtra(Constant.NOTIFICATION_TYPE, "lead").putExtra(Constant.NOTIFICATION_ID, notificationId).putExtra(Constant.PUSH_NOTIFICATION_ID,id )
        }
        if (type == "followUp") {
            val followUp = remoteMessage.data["followUpData"]
            val jsonObject1 = JSONObject(followUp!!)
            val id = jsonObject1.getString("_id")
            val leadData =/* remoteMessage.data["leadId"]*/ jsonObject1.getJSONObject("leadId")
            val jsonObject3 = jsonObject1.getJSONObject("leadId")
            val mobileNumber = jsonObject3.getString("mobileNo")
            val lead_id = jsonObject3.getString("_id")
            val leadType = jsonObject1.getString("previousStatus")
            val leadName = dataObject.getString("name")
            i = Intent(
                applicationContext,
                SplashScreenActivity::class.java
            ).putExtra(Constant.LEAD_ID, lead_id).putExtra(Constant.LEAD_NUMBER, mobileNumber)
                .putExtra(Constant.LEAD_TYPE, leadType).putExtra(Constant.LEAD_NAME, leadName)
                .putExtra(Constant.NOTIFICATION_TYPE, type).putExtra(Constant.NOTIFICATION_ID, notificationId).putExtra(Constant.PUSH_NOTIFICATION_ID,id )
        }

        val pendingIntent =
            PendingIntent.getActivity(applicationContext, createID(), i, PendingIntent.FLAG_IMMUTABLE)

        val channelId = resources.getString(R.string.default_notification_channel_id)

        var builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.app_logo)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setGroupSummary(true)


        builder = if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.JELLY_BEAN
        ) {
            builder.setContent(
                getCustomDesign(title, message)
            )
        } // If Android Version is lower than Jelly Beans,
        else {
            builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.app_logo)
        }

        val notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        // Check if the Android Version is greater than Oreo
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channelId, "web_app",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }

        notificationManager.notify(createID(), builder.build())

        fun removeNotification(view: View?) {
            if (createID() !== 0) notificationManager.cancel(createID())
        }

    }

    fun createID(): Int {
        val now = Date()
        return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
    }

}


