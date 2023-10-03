package com.tt.skolarrs.viewmodel

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tt.skolarrs.model.response.FcmIdSaveResponse
import com.tt.skolarrs.model.response.GetIndividualReportResponse
import com.tt.skolarrs.model.response.LogOutResponseError
import com.tt.skolarrs.model.response.LogoutResponseSuccess
import com.tt.skolarrs.repoistory.GetIndividualReportRepo
import com.tt.skolarrs.repoistory.LogoutRepo
import com.tt.skolarrs.repoistory.MainRepo
import java.net.ConnectException

class MainViewModel : ViewModel() {

    private val repository = MainRepo()

    private val _response = MutableLiveData<FcmIdSaveResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<FcmIdSaveResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun saveFcm(token: String,fcm_id: String, context: Context) {

        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.saveFcm(token, deviceId ,fcm_id)
            if (response.isSuccessful) {
                var leadResponse = response.body()
                _response.value = response.body()
                // Handle successful response
            } else {
                val errorBody = response.errorBody()?.string()
                _responseError.value = errorBody
                // Handle error response
            }
        } catch (e: java.lang.Exception) {
            e.message
        } catch (e: ConnectException) {
            e.message
        }
    }

}

class GetIndividualReportViewModel : ViewModel() {

    private val repository = GetIndividualReportRepo()

    private val _response = MutableLiveData<GetIndividualReportResponse>()
    private val _responseError = MutableLiveData<String>()

    val response: LiveData<GetIndividualReportResponse>
        get() = _response
    val responseError: LiveData<String>
        get() = _responseError

    suspend fun getIndividualReport(token: String,leadId: String, context: Context) {

        try {
            val  deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val response = repository.getIndividualReportAndRecordings(token, deviceId ,leadId)
            if (response.isSuccessful) {
                var leadResponse = response.body()
                _response.value = response.body()
                // Handle successful response
            } else {
                val errorBody = response.errorBody()?.string()
                _responseError.value = errorBody
                // Handle error response
            }
        } catch (e: java.lang.Exception) {
            e.message
        } catch (e: ConnectException) {
            e.message
        }
    }

}