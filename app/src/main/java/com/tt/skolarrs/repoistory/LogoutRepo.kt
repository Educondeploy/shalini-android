package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.LoginRequest
import com.tt.skolarrs.model.request.LogoutRequest
import com.tt.skolarrs.model.response.LeadResponse
import com.tt.skolarrs.model.response.LogoutResponse
import com.tt.skolarrs.model.response.LogoutResponseSuccess
import retrofit2.Response

class LogoutRepo {

    suspend fun logout(id: String,deviceid: String): Response<LogoutResponseSuccess> {
        val request = LogoutRequest(id)
        return APIClient.client.logout(id,deviceid)
    }

}