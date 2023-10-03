package com.tt.skolarrs.model.response


sealed class FollowUpResponseResult {
    data class Success(val leadResponse: FollowUpResponse) : FollowUpResponseResult()
    data class Error(val message: String) : FollowUpResponseResult()
}

data class FollowUpResponse(
    val `data`: List<FollowUpData>,
    val message: String,
    val success: Boolean
)

data class FollowUpData(
    val _id: String,
    val date: String,
    val followUp: String,
    val leadId: LeadId,
    val time: String,
    val userId: String
)

data class LeadId(
    val _id: String,
    val __v: String,
    val updatedAt: String,
    val createdAt: String,
    val currentCollege: String,
    val currentCourse: String,
    val date: String,
    val event: String,
    val eventLink: String,
    val ifAnyEntranceExam: String,
    val mobileNo: String,
    val name: String,
    val neetAspirants: Boolean,
    val place: Any,
    val preferredPlaceToStudy: String




)

sealed class FollowUpResponseError {
    data class BadRequest(val message: String, val error: String?) : FollowUpResponseError()
}