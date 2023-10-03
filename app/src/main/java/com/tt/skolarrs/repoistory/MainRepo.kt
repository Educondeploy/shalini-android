package com.tt.skolarrs.repoistory

import androidx.lifecycle.ViewModel
import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.FcmIdSaveRequest
import com.tt.skolarrs.model.response.FcmIdSaveResponse
import com.tt.skolarrs.model.response.GetIndividualReportResponse
import retrofit2.Response

class MainRepo : ViewModel() {
    suspend fun saveFcm(token: String,deviceid: String, fcm_id: String): Response<FcmIdSaveResponse> {
        val request = FcmIdSaveRequest(fcm_id)
        return APIClient.client.saveFcm(token,deviceid,request)
    }
}

class GetIndividualReportRepo : ViewModel() {
    suspend fun getIndividualReportAndRecordings(token: String,deviceid: String, leadId: String): Response<GetIndividualReportResponse> {

        return APIClient.client.getIndividualReportAndRecordings(token,deviceid,leadId)
    }
}