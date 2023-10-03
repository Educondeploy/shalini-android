package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.response.FollowUpResponse
import retrofit2.Response

class FollowUpRepo {
    suspend fun getFollowUpRepo(token: String, deviceid: String): Response<FollowUpResponse> {
        return APIClient.client.getFollowUpList(token,deviceid)
    }
}