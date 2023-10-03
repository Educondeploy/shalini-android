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
import com.tt.skolarrs.model.response.LeadResponse
import com.tt.skolarrs.model.response.LeadResponseError
import com.tt.skolarrs.repoistory.LeadListRepo
import com.tt.skolarrs.view.activity.LoginActivity

class LeadViewModel : ViewModel() {

    private val repository = LeadListRepo()

    private val _response = MutableLiveData<LeadResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<LeadResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun getLeads(token: String, context: Context?) {
        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.getLeadList(token, deviceId)
            Log.d("TAG", "getLeads1: " + response.code());
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
          else if (response.isSuccessful) {

                var leadResponse = response.body()
                _response.value = response.body()
                // Handle successful response
            } else {
                Log.d("TAG", "getLeads: " + response.errorBody()?.string());
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    Gson().fromJson(errorBody, LeadResponseError.BadRequest::class.java)?.message
                val error = Gson().fromJson(errorBody, LeadResponseError.BadRequest::class.java)
                _responseError.postValue(errorMessage)
                // Handle error response
            }

        } catch (e: java.lang.Exception) {
            Log.d("TAG", "getLeads5: " +  e.message);
            e.message
        }
    }

}