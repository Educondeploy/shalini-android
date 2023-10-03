package com.tt.skolarrs.model.response

sealed class ApplyPermissionResponseResult {
    data class Success(val leadResponse: ApplyLeaveResponse) : ApplyPermissionResponseResult()
    data class Error(val message: String) : ApplyPermissionResponseResult()
}


data class ApplyPermissionResponse(
    val message: String,
    val success: Boolean
)

sealed class ApplyPermissionResponseError {
    data class BadRequest(val message: String, val error: String?) : ApplyPermissionResponseError()
}