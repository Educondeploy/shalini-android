package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.FollowUpUpdateRequest
import com.tt.skolarrs.model.request.UpdateLeadStatusRequest
import com.tt.skolarrs.model.response.FollowUpUpdateResponse
import com.tt.skolarrs.model.response.UpdateLeadStatusResponse
import retrofit2.Response

class UpdateLeadStatusRepo {
    suspend fun UpdateLeadStatus(token: String, deviceid: String,current_lead : String,leadId: String,previous_lead:String): Response<UpdateLeadStatusResponse> {
        val request = UpdateLeadStatusRequest(current_lead,leadId,previous_lead)
        return APIClient.client.updateLeadStatus(token,deviceid,request)
    }
}