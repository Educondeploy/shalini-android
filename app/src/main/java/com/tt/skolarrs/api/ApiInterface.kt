package com.tt.skolarrs.api

import android.provider.Settings
import com.tt.skolarrs.model.request.*
import com.tt.skolarrs.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.HashMap

interface ApiInterface {

    @POST("api/user/emplogin")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("api/event-expo/lead")
    suspend fun getLeads(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<LeadResponse>

    @GET("api/event-expo/report")
    suspend fun getReports(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<ReportResponse>

    @GET("api/employee/notification")
    suspend fun getNotificationList(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<NotificationListResponse>

    @GET("api/employee/leave")
    suspend fun getLeaveDetails(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<GetLeaveResponse>

    @GET("api/employee/calendar")
    suspend fun getCalenderList(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<CalenderViewResponse>

    @POST("api/user/logout")
    suspend fun logout(
        @Query("_id") _id: String,
        @Header("deviceid") deviceid: String
    ): Response<LogoutResponseSuccess>

    @PUT("api/employee/notification")
    suspend fun updateNotificationStatus(
        @Query("id") _id: String,
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<UpdateNotificationResponse>

    @GET("api/event-expo/report")
    suspend fun getIndividualReport(
        @Query("id") _id: String,
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<IndividualReportResponse>

    @POST("api/employee/utils")
    suspend fun saveFcm(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Body fcmIdSaveRequest: FcmIdSaveRequest
    ): Response<FcmIdSaveResponse>

    @POST("api/employee/leave")
    suspend fun applyLeave(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Body applyLeaveRequest: ApplyLeaveRequest
    ): Response<ApplyLeaveResponse>

    @POST("api/employee/permission")
    suspend fun applyPermission(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Body applyLeaveRequest: ApplyPermissionRequest
    ): Response<ApplyPermissionResponse>

    @GET("api/employee/permission")
    suspend fun getPermissionDetails(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<GetPermissionResponse>

    @GET("api/employee/pagination/permission")
    suspend fun getPermissionPaginationList(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Query("_page") _page: Int,
        @Query("_limit") _limit: Int,
    ): Response<GetPermissionResponse>

    @POST("api/event-expo/report")
    suspend fun leadStatusResponse(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Body leadResponseStatusRequest: LeadStatusRequest
    ): Response<LeadStatusResponse>

    @POST("api/event-expo/followup")
    suspend fun updateFollowUpStatus(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Body followUpUpdateRequest: FollowUpUpdateRequest
    ): Response<FollowUpUpdateResponse>

    @GET("api/event-expo/followup")
    suspend fun getFollowUpList(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String
    ): Response<FollowUpResponse>

    @POST("api/event-expo/lead")
    suspend fun updateLeadStatus(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Body followUpUpdateRequest: UpdateLeadStatusRequest
    ): Response<UpdateLeadStatusResponse>

    @GET("api/event-expo/pagination/lead")
    suspend fun getLeadsUsingPagination(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Query("_page") _page: Int,
        @Query("_limit") _limit: Int,
        @Query("assignedOn") assignedOn: String,
        @Query("leadStatus") leadStatus: String,
        @Query("leadStatus") leadStatus1: String,
        @Query("leadStatus") leadStatus2: String,
        @Query("mobileNo") mobileNo: String? = null,
    ): Response<GetLeadPaginationResponse>


    @GET("api/event-expo/mobile-dashboard")
    suspend fun getDashBoardList(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Query("userId") userId: String,
    ): Response<DashBoardListResponse>

    @GET("api/event-expo/get-lead-history")
    suspend fun getIndividualReportAndRecordings(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Query("_id") leadId: String,
    ): Response<GetIndividualReportResponse>


    @GET("api/event-expo/get-lead-data")
    suspend fun getLeadData(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @Query("phone") phone: String,
    ): Response<GetIndividualReportResponse>

    @Multipart
    @POST("api/employee/call-record")
    suspend fun storeRecordedFile(
        @Header("token") token: String,
        @Header("deviceid") deviceid: String,
        @PartMap partMap: HashMap<String, RequestBody>,
        @Part file: MultipartBody.Part
    ): Response<StoreCallRecordingResponse>

    //http://52.66.35.154:3001/api/event-expo/get-lead-data?phone=7708350578




}