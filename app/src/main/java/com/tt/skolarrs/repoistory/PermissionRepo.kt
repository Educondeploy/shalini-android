package com.tt.skolarrs.repoistory

import com.tt.skolarrs.api.APIClient
import com.tt.skolarrs.model.request.ApplyLeaveRequest
import com.tt.skolarrs.model.request.ApplyPermissionRequest
import com.tt.skolarrs.model.response.*
import retrofit2.Response

class PermissionRepo {

    suspend fun applyPermission(token: String, deviceid: String,date: String, timeFrom: String, timeTo : String,reason: String,message: String): Response<ApplyPermissionResponse> {
        val request =  ApplyPermissionRequest(date,timeFrom,timeTo,reason,message)
        return APIClient.client.applyPermission(token,deviceid,request)
    }

    suspend fun getPermissionDetails(token: String, deviceid: String): Response<GetPermissionResponse> {
        return APIClient.client.getPermissionDetails(token,deviceid)
    }

}