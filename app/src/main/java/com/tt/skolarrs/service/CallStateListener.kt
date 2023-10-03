package com.tt.skolarrs.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.PhoneStateListener
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.DashBoardListViewModel
import com.tt.skolarrs.viewmodel.GetLeadDataViewModel
import kotlinx.coroutines.withTimeoutOrNull
import java.lang.NullPointerException
import java.net.ConnectException

class CallStateListener(private val context: Context) : PhoneStateListener() {

    private lateinit var dashViewModel: GetLeadDataViewModel

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)

        val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
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

        subscriptionManager.activeSubscriptionInfoList

        when (state) {

            TelephonyManager.CALL_STATE_IDLE -> {
                // Call state: Idle (no call)
                Log.d("CallListener", "Call state: Idle")
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                val sim1 = MyFunctions.getSharedPreference(context,Constant.SIM_SLOT,"0");

                val sim = subscriptionManager.activeSubscriptionInfoList[0].simSlotIndex.toString()

                if (sim == sim1) {
                }

            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                // Call state: Off-hook (call in progress)
                Log.d("CallListener", "Call state: Off-hook")
            }
        }
    }

    private suspend fun getDashBoardCount(num:String) {

      val token = MyFunctions.getSharedPreference(context, Constant.TOKEN, Constant.TOKEN)!!

        withTimeoutOrNull(10000L) {
            dashViewModel.getLeadDataByNumber(token, num, context)
            //ViewModel.getFollowUpList((token))

            try {
                // MyFunctions.progressDialogDismiss()
                //  val data = leadViewModel.response.value!!.data
                if (dashViewModel.response.value != null) {
                    //  MyFunctions.progressDialogDismiss()

                } else {
                    //  MyFunctions.progressDialogDismiss()
                }

                //  Log.d("TAG", "getCount: $disconnect_the_call,$notInterest ,$invalid_number")


            } catch (e: Exception) {
                // MyFunctions.progressDialogDismiss()
                val responseError = dashViewModel.responseError.value ?: "Unknown error"

                //   Toast.makeText(requireActivity(), responseError, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: NullPointerException) {
                //  MyFunctions.progressDialogDismiss()
                e.message
            } catch (e: ConnectException) {
                // MyFunctions.progressDialogDismiss()
                e.message
            }

        } ?: run {
            // Handle the timeout case when the API response is delayed more than 10 seconds
            //     MyFunctions.progressDialogDismiss()
            // Handle the timeout error here
            // getLeads();
        }


    }


}
