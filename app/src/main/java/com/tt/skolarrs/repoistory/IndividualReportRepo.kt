package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.LoginRequest
import com.tt.skolarrs.model.response.*
import retrofit2.Response

class IndividualReportRepo {

    suspend fun getIndividualReport(id: String, deviceid: String,token: String): Response<IndividualReportResponse> {
        // val user = LoginRequest(token)
        return APIClient.client.getIndividualReport(id,token,deviceid)
    }
}