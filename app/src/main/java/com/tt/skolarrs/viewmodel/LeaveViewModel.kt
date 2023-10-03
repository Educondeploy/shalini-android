package com.tt.skolarrs.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tt.skolarrs.model.response.*
import com.tt.skolarrs.repoistory.LeaveRepo
import com.tt.skolarrs.view.activity.LoginActivity

class LeaveViewModel: ViewModel()  {

    private val repository = LeaveRepo()

    private val _response = MutableLiveData<ApplyLeaveResponse>()
    private val _failed = MutableLiveData<String>()

    private val leaveResponse = MutableLiveData<GetLeaveResponse>()
    private val leaveFailed = MutableLiveData<String>()
    //private val _response = MutableLiveData<String>()

    val response: LiveData<ApplyLeaveResponse>
        get() = _response

    val failedResponse: LiveData<String>
        get() = _failed

    val LeaveResponse: LiveData<GetLeaveResponse>
        get() = leaveResponse

    val LeavefailedResponse: LiveData<String>
        get() = leaveFailed


    suspend fun applyLeave(token: String,dateFrom: String,dateTo: String, reason : String,message: String, context: Context?) {
        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.applyLeave(token,deviceId,dateFrom,dateTo,reason,message)
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
                    Gson().fromJson(errorBody, ApplyLeaveResponseError.BadRequest::class.java)?.message
                val error =
                    Gson().fromJson(errorBody, ApplyLeaveResponseError.BadRequest::class.java)?.error
                _failed.value = errorMessage
                // Handle error response
            }
        }
        catch (e: Exception) {
            e.message
        }
    }

    suspend fun getLeaveDetails(token: String, context: Context,) {
        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.getLeaveDetails(token,deviceId)
            if (response.isSuccessful) {
                var leadResponse = response.body()
                leaveResponse.value = response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    Gson().fromJson(errorBody, GetLeaveResponseError.BadRequest::class.java)?.message
                val error =
                    Gson().fromJson(errorBody, GetLeaveResponseError.BadRequest::class.java)?.error
                _failed.value = errorMessage
            }
        }
        catch (e: Exception) {
            e.message
        }
    }


}