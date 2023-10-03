package com.tt.skolarrs.repoistory

import android.util.Log
import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.response.NotificationListResponse
import retrofit2.Response

class NotificationRepo {

    suspend fun getNotificationList(token: String,deviceid: String): Response<NotificationListResponse> {

        return APIClient.client.getNotificationList(token,deviceid)
    }

}