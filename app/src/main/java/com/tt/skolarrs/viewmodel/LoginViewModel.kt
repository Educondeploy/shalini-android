package com.tt.skolarrs.viewmodel

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tt.skolarrs.model.response.LeadResponseError
import com.tt.skolarrs.model.response.LogInResponseError
import com.tt.skolarrs.model.response.LoginResponse
import com.tt.skolarrs.repoistory.LoginRepo

class LoginViewModel : ViewModel() {

    private val repository = LoginRepo()

    private val _response = MutableLiveData<LoginResponse>()
    private val _failed = MutableLiveData<String>()
    //private val _response = MutableLiveData<String>()

    val response: LiveData<LoginResponse>
        get() = _response

    val failedResponse: LiveData<String>
        get() = _failed

    suspend fun login(email: String, password: String, deviceId:String, coords:List<String>, address: String) {
       try {
           val response = repository.login(email, password, deviceId, coords, address)
           if (response.isSuccessful) {
               var leadResponse = response.body()
               _response.value = response.body()
               // Handle successful response
           } else {
               val errorBody = response.errorBody()?.string()
               val errorMessage =
                   Gson().fromJson(errorBody, LogInResponseError.BadRequest::class.java)?.message
               val error =
                   Gson().fromJson(errorBody, LogInResponseError.BadRequest::class.java)?.error
               _failed.value = errorMessage
               // Handle error response
           }
       }
       catch (e: Exception) {
           e.message
       }
   }
}


