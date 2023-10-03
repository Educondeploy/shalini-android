package com.tt.skolarrs.model.response

sealed class NotificationListResponseResult {
    data class Success(val leadResponse: NotificationListResponse) :
        NotificationListResponseResult()

    data class Error(val message: String) : NotificationListResponseResult()
}

 data class NotificationListResponse(
     val `data`: List<NotificationData>,
     val message: String,
     val success: Boolean
 )

data class NotificationData(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val notification: Notification,
    val read: Boolean,
    val updatedAt: String,
    val userId: String
)

data class Notification(
    val `data`: DataXX,
    val notification: NotificationX,
    val priority: String,
    val to: String
)

data class DataXX(
    val AssignLeadData: List<AssignLeadData>,
    val followUpData: FollowUpData1,
    val leaveData: LeaveData1,
    val message: String,
    val permissionData: PermissionData1,
    val title: String,
    val type: String
)

data class NotificationX(
    val body: String,
    val title: String
)

data class AssignLeadData(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val currentCollege: String,
    val currentCourse: String,
    val date: String,
    val event: String,
    val eventLink: String,
    val ifAnyEntranceExam: Any,
    val mobileNo: String,
    val name: String,
    val neetAspirants: String,
    val place: String,
    val preferredPlaceToStudy: String,
    val updatedAt: String
)

data class DataXXX(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val currentCollege: String,
    val currentCourse: String,
    val date: String,
    val event: String,
    val eventLink: String,
    val ifAnyEntranceExam: Any,
    val mobileNo: String,
    val name: String,
    val neetAspirants: String,
    val place: String,
    val preferredPlaceToStudy: String,
    val updatedAt: String
)

data class FollowUpData1(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val date: String,
    val followUp: String,
    val leadId: LeadId1,
    val previousStatus: String,
    val time: String,
    val updatedAt: String,
    val userId: String
)

data class LeaveData1(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val dateFrom: String,
    val dateTo: String,
    val message: String,
    val reason: String,
    val status: String,
    val updatedAt: String,
    val userId: String
)

data class PermissionData1(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val date: String,
    val message: String,
    val reason: String,
    val status: String,
    val timeFrom: String,
    val timeTo: String,
    val updatedAt: String,
    val userId: String
)

data class LeadId1(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val currentCollege: String,
    val currentCourse: String,
    val date: String,
    val event: String,
    val eventLink: String,
    val ifAnyEntranceExam: Any,
    val mobileNo: String,
    val name: String,
    val neetAspirants: String,
    val place: String,
    val preferredPlaceToStudy: String,
    val updatedAt: String
)

sealed class NotificationListResponseError {
    data class BadRequest(val message: String, val error: String?) : NotificationListResponseError()
}


