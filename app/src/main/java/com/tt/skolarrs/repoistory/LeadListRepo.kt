package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.LoginRequest
import com.tt.skolarrs.model.response.LeadResponse
import com.tt.skolarrs.model.response.LeadResponseResult
import com.tt.skolarrs.model.response.LoginResponse
import retrofit2.Response

class LeadListRepo {

    suspend fun getLeadList(token: String, deviceid: String): Response<LeadResponse> {
       // val user = LoginRequest(token)
        return APIClient.client.getLeads(token, deviceid)
    }
}