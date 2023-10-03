package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.LoginRequest
import com.tt.skolarrs.model.request.LogoutRequest
import com.tt.skolarrs.model.response.LeadResponse
import com.tt.skolarrs.model.response.LogoutResponse
import com.tt.skolarrs.model.response.LogoutResponseSuccess
import com.tt.skolarrs.model.response.UpdateNotificationResponse
import retrofit2.Response

class UpdateNotificationRepo {

    suspend fun updateNotification(id: String, token: String, deviceid: String): Response<UpdateNotificationResponse> {
        return APIClient.client.updateNotificationStatus(id,token,deviceid)
    }

}