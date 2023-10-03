package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.LeadStatusRequest
import com.tt.skolarrs.model.request.Report
import com.tt.skolarrs.model.response.LeadStatusResponse
import com.tt.skolarrs.model.response.StoreCallRecordingResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.util.HashMap

class LeadStatusRepo {
    suspend fun getLeadStatus(
        token : String,_id: String, leadId: String,
        callHistory: String,
        currentStatus: String,
        userId: String,
        notes: String,
        followUpDate: String,
        nextFollowUpDate: String,
        deviceid: String
    ): Response<LeadStatusResponse> {
       val report = Report(callHistory, currentStatus,notes,followUpDate,nextFollowUpDate)

        val request = LeadStatusRequest(/*_id,*/ leadId, report, userId)
        return APIClient.client.leadStatusResponse(token,deviceid,request)
    }

}


class UploadRecordFilesRepo {
    suspend fun getUploadRecordFiles(
        token: String, deviceid: String,partMap: HashMap<String, RequestBody>, file: MultipartBody.Part
    ): Response<StoreCallRecordingResponse> {

        return APIClient.client.storeRecordedFile(token, deviceid , partMap, file)
    }

}