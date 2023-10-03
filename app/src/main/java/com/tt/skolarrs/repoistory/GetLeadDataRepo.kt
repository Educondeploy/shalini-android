package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.response.GetIndividualReportResponse
import retrofit2.Response

class GetLeadDataRepo {
    suspend fun getLeadByPhone(token: String, deviceid:String, phone: String): Response<GetIndividualReportResponse> {

    return APIClient.client.getLeadData(token,deviceid,phone)
}

}