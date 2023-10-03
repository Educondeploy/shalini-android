package com.tt.skolarrs.repoistory

import android.provider.Settings
import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.ApplyPermissionRequest
import com.tt.skolarrs.model.request.GetLeadPaginationRequest
import com.tt.skolarrs.model.response.DashBoardListResponse
import com.tt.skolarrs.model.response.GetLeadPaginationResponse
import com.tt.skolarrs.model.response.LeadResponse
import retrofit2.Response

class PaginationLeadListRepo {
    suspend fun getPaginationLeadList(token: String,deviceID:String,page:Int, limit:Int,assignedOn:String,leadStatus:String,leadStatus1:String,leadStatus2:String, mblNo:String): Response<GetLeadPaginationResponse> {
     //   val request =  GetLeadPaginationRequest(page, limit,assignedOn,leadStatus)
        if (mblNo.isNotEmpty()) {
            return APIClient.client.getLeadsUsingPagination(token, deviceID,page,limit,assignedOn,leadStatus, leadStatus1,leadStatus2, mblNo)
        } else {
            return APIClient.client.getLeadsUsingPagination(token, deviceID,page,limit,assignedOn,leadStatus, leadStatus1,leadStatus2)
        }

    }
}

class DashBoardListRepo {
    suspend fun getDashBoardListCount(token: String,deviceID:String, userId:String): Response<DashBoardListResponse> {
        //   val request =  GetLeadPaginationRequest(page, limit,assignedOn,leadStatus)
        return APIClient.client.getDashBoardList(token, deviceID, userId)
    }
}