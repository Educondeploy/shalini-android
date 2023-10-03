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
import com.tt.skolarrs.model.request.Report
import com.tt.skolarrs.model.response.LeadResponseError
import com.tt.skolarrs.model.response.LeadStatusResponse
import com.tt.skolarrs.model.response.ReportResponse
import com.tt.skolarrs.model.response.ReportResponseError
import com.tt.skolarrs.repoistory.LeadStatusRepo
import com.tt.skolarrs.repoistory.ReportRepo
import com.tt.skolarrs.view.activity.LoginActivity

class ReportResponseViewModel : ViewModel() {
    private val repository = ReportRepo()

    private val _response = MutableLiveData<ReportResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<ReportResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun getReportList(
        token: String, context: Context?
    ) {

        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        val response = repository.getReportList(token, deviceId  )
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
                   Gson().fromJson(errorBody, ReportResponseError.BadRequest::class.java)?.message
               val error = Gson().fromJson(errorBody, ReportResponseError.BadRequest::class.java)
               _responseError.postValue(errorMessage)
            // Handle error response
        }
    }
    catch (e:java.lang.Exception) {
        e.message
    }


    }

}