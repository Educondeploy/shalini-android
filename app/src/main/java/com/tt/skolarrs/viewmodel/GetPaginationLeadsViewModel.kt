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
import com.tt.skolarrs.model.response.*
import com.tt.skolarrs.repoistory.DashBoardListRepo
import com.tt.skolarrs.repoistory.LeadListRepo
import com.tt.skolarrs.repoistory.PaginationLeadListRepo
import com.tt.skolarrs.view.activity.LoginActivity



class GetPaginationLeadsViewModel : ViewModel() {

    private val repository = PaginationLeadListRepo()

    private val _response = MutableLiveData<GetLeadPaginationResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<GetLeadPaginationResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun getPaginationLeads(token: String, page:Int, limit:Int,assignedOn:String,leadStatus:String,leadStatus1:String,leadStatus2:String,mobileNo:String,context: Context?) {
        try {
          val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.getPaginationLeadList(token,deviceId, page, limit,assignedOn,leadStatus, leadStatus1, leadStatus2, mobileNo)
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
                    Gson().fromJson(errorBody, GetLeadPaginationResponseError.BadRequest::class.java)?.message
                val error = Gson().fromJson(errorBody, GetLeadPaginationResponseError.BadRequest::class.java)
                _responseError.postValue(errorMessage)
                // Handle error response
            }

        } catch (e: java.lang.Exception) {
            Log.d("TAG", "getLeads5: " +  e.message);
            e.message
        }
    }

}


class DashBoardListViewModel : ViewModel() {

    private val repository = DashBoardListRepo()

    private val _response = MutableLiveData<DashBoardListResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<DashBoardListResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun getDashBoardCount(token: String,userId:String,context: Context?) {
        val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        try {
            val response = repository.getDashBoardListCount(token, deviceId,userId)
            Log.d("TAG", "getLeads1: " + response.code());
            if( response.code() == 401) {
                val dialog1: AlertDialog? = AlertDialog.Builder(context).create()

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
                    Gson().fromJson(errorBody, DashBoardListResponseError.BadRequest::class.java)?.message
                val error = Gson().fromJson(errorBody, DashBoardListResponseError.BadRequest::class.java)
                _responseError.postValue(errorMessage)
                // Handle error response
            }

        } catch (e: java.lang.Exception) {
            Log.d("TAG", "getLeads5: " +  e.message);
            e.message
        }
    }

}