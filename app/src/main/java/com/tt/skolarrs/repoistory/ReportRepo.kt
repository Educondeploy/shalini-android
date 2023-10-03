package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.LoginRequest
import com.tt.skolarrs.model.response.LeadResponse
import com.tt.skolarrs.model.response.LeadResponseResult
import com.tt.skolarrs.model.response.LoginResponse
import com.tt.skolarrs.model.response.ReportResponse
import retrofit2.Response

class ReportRepo {

    suspend fun getReportList(token: String, deviceid: String): Response<ReportResponse> {
        // val user = LoginRequest(token)
        return APIClient.client.getReports(token, deviceid)
    }
}