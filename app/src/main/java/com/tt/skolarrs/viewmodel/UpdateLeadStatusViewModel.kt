package com.tt.skolarrs.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tt.skolarrs.model.response.LogOutResponseError
import com.tt.skolarrs.model.response.LogoutResponseSuccess
import com.tt.skolarrs.model.response.UpdateLeadStatusResponse
import com.tt.skolarrs.repoistory.LogoutRepo
import com.tt.skolarrs.repoistory.UpdateLeadStatusRepo
import com.tt.skolarrs.view.activity.LoginActivity
import java.net.ConnectException
import java.net.SocketTimeoutException

class UpdateLeadStatusViewModel : ViewModel() {

    private val repository = UpdateLeadStatusRepo()

    private val _response = MutableLiveData<UpdateLeadStatusResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<UpdateLeadStatusResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun UpdateLeadStatus(
        token: String,
        current_lead: String,
        leadId: String,
        previous_lead: String, context: Context?
    ) {
        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.UpdateLeadStatus(token,deviceId, current_lead, leadId, previous_lead)
            if( response.code() == 401) {
                var dialog1: AlertDialog? = AlertDialog.Builder(context).create()

                dialog1?.setMessage("Oops your session was expired, Please login again")
                dialog1?.setCancelable(false)
                dialog1?.setCanceledOnTouchOutside(false)

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                dialog1?.setButton("Ok") {
                    // When the user click yes button then app will close
                        dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent)

                }

                dialog1?.show()
            }

            if (response.isSuccessful) {
                var leadResponse = response.body()
                _response.value = response.body()

            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    Gson().fromJson(errorBody, LogOutResponseError.BadRequest::class.java)?.message
                val error = Gson().fromJson(errorBody, LogOutResponseError.BadRequest::class.java)
                _responseError.value = errorMessage

            }
        } catch (e: java.lang.Exception) {
            e.message
        } catch (e: SocketTimeoutException) {
            e.message
        }
        catch (e: ConnectException) {
            e.message
        }

    }

}