package com.tt.skolarrs.model.response

sealed class ApplyLeaveResponseResult {
    data class Success(val leadResponse: ApplyLeaveResponse) : ApplyLeaveResponseResult()
    data class Error(val message: String) : ApplyLeaveResponseResult()
}
data class ApplyLeaveResponse(
    val message: String,
    val success: Boolean
)

sealed class ApplyLeaveResponseError {
    data class BadRequest(val message: String, val error: String?) : ApplyLeaveResponseError()
}

