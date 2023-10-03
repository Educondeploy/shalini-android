package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.FollowUpUpdateRequest
import com.tt.skolarrs.model.response.FollowUpUpdateResponse
import retrofit2.Response

class FollowUpUpdateRepo {
    suspend fun updateFollowUpStatus(token: String, deviceid: String,_id : String,leadId: String,userId:String,date: String,time: String,followUp: String, previousStatus: String): Response<FollowUpUpdateResponse> {
        val request = FollowUpUpdateRequest(/*_id,*/date,followUp,leadId,time, userId, previousStatus)
        return APIClient.client.updateFollowUpStatus(token,deviceid,request)
    }
}