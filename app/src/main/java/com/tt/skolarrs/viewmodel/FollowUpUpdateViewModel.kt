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
import com.tt.skolarrs.model.response.FollowUpUpdateResponse
import com.tt.skolarrs.model.response.FollowUpUpdateResponseError
import com.tt.skolarrs.model.response.LeadResponseError
import com.tt.skolarrs.repoistory.FollowUpUpdateRepo
import com.tt.skolarrs.view.activity.LoginActivity
import java.net.ConnectException
import java.net.SocketTimeoutException

class FollowUpUpdateViewModel : ViewModel() {
    private val repository = FollowUpUpdateRepo()

    private val _response = MutableLiveData<FollowUpUpdateResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<FollowUpUpdateResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun updateFollowUpStatus(
        token: String,
        _id: String,
        leadId: String,
        userId: String,
        date: String,
        time: String,
        followUp: String,
        previousStatus: String, context: Context?
    ) {
        val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        val response = repository.updateFollowUpStatus(
            token,
            deviceId,
            _id,
            leadId,
            userId,
            date,
            time,
            followUp,
            previousStatus
        )
        try {
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
                // Handle successful response
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    Gson().fromJson(
                        errorBody,
                        FollowUpUpdateResponseError.BadRequest::class.java
                    )?.message
                val error =
                    Gson().fromJson(errorBody, FollowUpUpdateResponseError.BadRequest::class.java)
                _responseError.postValue(errorMessage)
                // Handle error response
            }

        } catch (e:java.lang.Exception) {
            e.message
        }catch (e: SocketTimeoutException) {
            e.message
        }
        catch (e: ConnectException) {
            e.message
        }
    }

}