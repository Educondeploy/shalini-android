package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.ApplyLeaveRequest
import com.tt.skolarrs.model.request.FollowUpUpdateRequest
import com.tt.skolarrs.model.request.LeadStatusRequest
import com.tt.skolarrs.model.response.ApplyLeaveResponse
import com.tt.skolarrs.model.response.FollowUpUpdateResponse
import com.tt.skolarrs.model.response.GetLeaveResponse
import retrofit2.Response

class LeaveRepo {
    suspend fun applyLeave(token: String, deviceid: String,_dateFrom: String, dateTo: String, reason : String,message: String): Response<ApplyLeaveResponse> {
        val request =  ApplyLeaveRequest(_dateFrom,dateTo,reason,message)
        return APIClient.client.applyLeave(token,deviceid,request)
    }
    suspend fun getLeaveDetails(token: String, deviceid: String): Response<GetLeaveResponse> {
        return APIClient.client.getLeaveDetails(token,deviceid)
    }
}