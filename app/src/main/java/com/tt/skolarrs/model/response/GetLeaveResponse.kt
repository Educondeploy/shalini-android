package com.tt.skolarrs.model.response

sealed class GetLeaveResponseResult {
    data class Success(val leadResponse: GetLeaveResponse) : GetLeaveResponseResult()
    data class Error(val message: String) : GetLeaveResponseResult()
}

data class GetLeaveResponse(
    val `data`: List<LeaveDetails>,
    val message: String,
    val success: Boolean
)

data class LeaveDetails(
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

sealed class GetLeaveResponseError {
    data class BadRequest(val message: String, val error: String?) : GetLeaveResponseError()
}