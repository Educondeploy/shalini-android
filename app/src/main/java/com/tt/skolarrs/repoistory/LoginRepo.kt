package com.tt.skolarrs.repoistory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.LoginRequest
import com.tt.skolarrs.model.response.LoginResponse
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.login.LoginException

class LoginRepo {

    suspend fun login(email: String, password: String, deviceId : String, coords:List<String>, address:String): Response<LoginResponse> {
        val user = LoginRequest(email, password,deviceId,coords,address )
        Log.d("TAG", "login: " + user)
        return APIClient.client.login(user)
    }

  /*  suspend fun login(email: String, password: String): LoginResponse {
        val user = LoginRequest(email, password)
        val response = APIClient.client.login(user)
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.equals(true)) {
                    return it
                } else {
                    throw LoginException("it.error")
                }
            } ?: throw LoginException("Unknown error occurred.")
        } else {
            throw LoginException("Failed to login. Please try again.")
        }
    }*/


    private val login: MutableLiveData<List<LoginResponse>> =
        MutableLiveData<List<LoginResponse>>()
    private lateinit var loginResponse: LoginResponse


}