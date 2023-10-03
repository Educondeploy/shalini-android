package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.response.CalenderViewResponse
import com.tt.skolarrs.model.response.LeadResponse
import retrofit2.Response

class CalenderViewRepo {

    suspend fun getCalenderList(token: String, deviceid: String): Response<CalenderViewResponse> {
        // val user = LoginRequest(token)
        return APIClient.client.getCalenderList(token, deviceid)
    }

}