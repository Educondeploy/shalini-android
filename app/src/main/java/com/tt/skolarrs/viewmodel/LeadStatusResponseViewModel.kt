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
import com.tt.skolarrs.model.response.LeadResponseError
import com.tt.skolarrs.model.response.LeadStatusResponse
import com.tt.skolarrs.model.response.StoreCallRecordingResponse
import com.tt.skolarrs.model.response.StoreCallRecordingResponseError
import com.tt.skolarrs.repoistory.LeadStatusRepo
import com.tt.skolarrs.repoistory.UploadRecordFilesRepo
import com.tt.skolarrs.view.activity.LoginActivity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.HashMap

class LeadStatusResponseViewModel : ViewModel() {
    private val repository = LeadStatusRepo()

    private val _response = MutableLiveData<LeadStatusResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<LeadStatusResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun getLeadStatus(
        token: String, _id: String, leadId: String,
        callHistory: String,
        currentStatus: String,
        userId: String,
        notes: String,
        followUpDate: String,
        nextFollowUpDate: String, context: Context?
    ) {

        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        val response = repository.getLeadStatus(token, _id, leadId, callHistory, currentStatus, userId,notes,followUpDate,nextFollowUpDate,deviceId)
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
                   Gson().fromJson(errorBody, LeadResponseError.BadRequest::class.java)?.message
               val error = Gson().fromJson(errorBody, LeadResponseError.BadRequest::class.java)
               _responseError.postValue(errorMessage)
            // Handle error response
        }
    }
    catch (e:java.lang.Exception) {
        e.message
    }


    }

}



class UploadLeadCallRecord : ViewModel() {
    private val repository = UploadRecordFilesRepo()

    private val _response = MutableLiveData<StoreCallRecordingResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<StoreCallRecordingResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun storeRecordedFile(
        token: String, partMap: HashMap<String, RequestBody>, file: MultipartBody.Part, context: Context?
    ) {

        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.getUploadRecordFiles( token, deviceId,partMap,file)

            Log.d("TAG", "storeRecordedFile:viewModel " + response.code()+responseError)
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
                    Gson().fromJson(errorBody, StoreCallRecordingResponseError.BadRequest::class.java)?.message
                val error = Gson().fromJson(errorBody, StoreCallRecordingResponseError.BadRequest::class.java)
                _responseError.postValue(errorMessage)
                // Handle error response
            }
        }
        catch (e:java.lang.Exception) {
            e.message
        }


    }

}