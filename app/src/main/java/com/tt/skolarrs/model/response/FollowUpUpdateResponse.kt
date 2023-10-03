package com.tt.skolarrs.model.response

sealed class FollowUpUpdateResponseResult {
    data class Success(val leadResponse: FollowUpUpdateResponse) : FollowUpUpdateResponseResult()
    data class Error(val message: String) : FollowUpUpdateResponseResult() }

data class FollowUpUpdateResponse(
    val `data`: Data4,
    val message: String,
    val success: Boolean
)

data class Data4(
    val _id: String,
    val date: String,
    val followUp: String,
    val leadId: String,
    val time: String,
    val userId: String,
    val __v: String,
    val createdAt: String,
    val updatedAt: String
)

sealed class FollowUpUpdateResponseError {
    data class BadRequest(val message: String, val error: String?) : FollowUpUpdateResponseError()
}