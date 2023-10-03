package com.tt.skolarrs.view.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.databinding.CardNotificationListBinding
import com.tt.skolarrs.model.response.NotificationData
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.DateDifference
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.view.activity.SplashScreenActivity
import com.tt.skolarrs.viewmodel.UpdateNotificationViewModel
import java.text.ParseException

class NotificationListAdapter(
    var context: Context,
    var list: List<NotificationData>,
    var itemSelectListener: NotificationSelectListener,
) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    lateinit var subMenuListAdapter: SubMenuAdapter

    private lateinit var updateNotificationViewModel: UpdateNotificationViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            CardNotificationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = list[position]

        updateNotificationViewModel = UpdateNotificationViewModel()



        holder.binding.time.text = list.createdAt
        holder.binding.notificationHeading.text = list.notification.notification?.title
        holder.binding.notificationContent.text = list.notification.notification?.body

        var date1: String? = ""

        if (list.notification.data.type.isNotEmpty()) {

            if (list.notification.data.type == "leave") {
                date1 = try {
                    DateDifference.getDateDifference(list.createdAt)
                } catch (e: ParseException) {
                    throw RuntimeException(e)
                }


            }
            if (list.notification.data.type == "permission") {
                date1 = try {
                    DateDifference.getDateDifference(list.createdAt)
                } catch (e: ParseException) {
                    throw RuntimeException(e)
                }


            }
            if (list.notification.data.type == "followUp") {
                Log.d("TAG", "onBindViewHolder: " + DateDifference.getDateDifference(list.createdAt)+ DateDifference.getDateDifference(list.notification.data.followUpData.leadId.createdAt))

                date1 = try {
                    DateDifference.getDateDifference(list.createdAt)
                } catch (e: ParseException) {
                    throw RuntimeException(e)
                }


            }
            if (list.notification.data.type == "lead") {

                date1 = try {
                    DateDifference.getDateDifference(list.createdAt)
                } catch (e: ParseException) {
                    throw RuntimeException(e)
                }


            }

            holder.binding.time.text = date1
        }

        holder.binding.read.visibility = View.VISIBLE

       /* if (list.read) {
            holder.binding.read.visibility = View.GONE
          //  holder.itemView.visibility = View.GONE
            holder.binding.parent.visibility = View.GONE

        } else {
            holder.binding.read.visibility = View.VISIBLE
            // holder.itemView.visibility = View.VISIBLE
            holder.binding.parent.visibility = View.VISIBLE
        }


        if (!list.read) {
            holder.binding.read.visibility = View.VISIBLE
           // holder.itemView.visibility = View.VISIBLE
            holder.binding.parent.visibility = View.VISIBLE
        } else {
            holder.binding.read.visibility = View.GONE
         //   holder.itemView.visibility = View.GONE
            holder.binding.parent.visibility = View.GONE
        }*/


        holder.itemView.setOnClickListener(View.OnClickListener {
            itemSelectListener.updateNotification(list)

        })


    }

    private suspend fun updateNotification(notificationList: NotificationData) {
        val token = MyFunctions.getSharedPreference(context, Constant.TOKEN, Constant.TOKEN)!!
        updateNotificationViewModel.updateNotification(notificationList._id, token, context)

        try {
            if (updateNotificationViewModel.response.value != null) {
                val response = updateNotificationViewModel.response

                if (notificationList.notification.data.type == "followUp") {
                    context.startActivity(
                        Intent(context, SplashScreenActivity::class.java).putExtra(
                            Constant.LEAD_ID,
                            notificationList.notification.data.followUpData.leadId._id
                        )
                            .putExtra(
                                Constant.LEAD_NUMBER,
                                notificationList.notification.data.followUpData.leadId.mobileNo
                            )
                            .putExtra(
                                Constant.LEAD_TYPE,
                                notificationList.notification.data.followUpData.previousStatus
                            )
                            .putExtra(
                                Constant.LEAD_NAME,
                                notificationList.notification.data.followUpData.leadId.name
                            ).putExtra(
                                Constant.NOTIFICATION_TYPE,
                                "followUp"
                            )
                    )

                }
                if (notificationList.notification.data.type == "leave") {
                    context.startActivity(
                        Intent(context, MainActivity::class.java).putExtra(
                            Constant.NOTIFICATION_TYPE,
                            "leave"
                        )
                    )
                }
                if (notificationList.notification.data.type == "permission") {
                    context.startActivity(
                        Intent(context, MainActivity::class.java).putExtra(
                            Constant.NOTIFICATION_TYPE,
                            "permission"
                        )
                    )
                }
                if (notificationList.notification.data.type == "lead") {
                    context.startActivity(
                        Intent(context, MainActivity::class.java).putExtra(
                            Constant.NOTIFICATION_TYPE,
                            "lead"
                        )
                    )
                }

                //    context.startActivity(Intent(context, LoginActivity::class.java))
            } else {
                if (updateNotificationViewModel.responseError.value != null) {
                    Toast.makeText(
                        context,
                        updateNotificationViewModel.responseError.value!!,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: Exception) {
            //  Toast.makeText(context, updateNotificationViewModel.responseError.value!!, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            e.message
        }
    }

    class ViewHolder(itemView: CardNotificationListBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: CardNotificationListBinding

        init {
            binding = itemView
        }
    }

    interface NotificationSelectListener {
        fun onSelect(position: Int)
        fun unSelect(position: Int)
        fun updateNotification(notificationList: NotificationData)

    }


}




