package com.tt.skolarrs.viewmodel

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tt.skolarrs.model.response.*
import com.tt.skolarrs.repoistory.LeadListRepo
import com.tt.skolarrs.repoistory.LogoutRepo
import java.net.ConnectException

class LogoutViewModel : ViewModel() {

    private val repository = LogoutRepo()

    private val _response = MutableLiveData<LogoutResponseSuccess>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<LogoutResponseSuccess>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun logout(id: String, context:Context) {
        try {

            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.logout(id, deviceId)
            if (response.isSuccessful) {
                var leadResponse = response.body()
                _response.value = response.body()
                // Handle successful response
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage =
                    Gson().fromJson(errorBody, LogOutResponseError.BadRequest::class.java)?.message
                val error = Gson().fromJson(errorBody, LogOutResponseError.BadRequest::class.java)
                _responseError.value = errorMessage
                // Handle error response
            }
        } catch (e: java.lang.Exception) {
            e.message
        } catch (e: ConnectException) {
            e.message
        }
    }
}