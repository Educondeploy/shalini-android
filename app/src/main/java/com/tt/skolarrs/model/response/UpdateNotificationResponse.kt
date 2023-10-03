package com.tt.skolarrs.model.response


sealed class UpdateNotificationResponseResult {
    data class Success(val leadResponse: UpdateNotificationResponse) :  UpdateNotificationResponseResult()
    data class Error(val message: String) :  UpdateNotificationResponseResult() }

data class UpdateNotificationResponse(
    val error: String,
    val message: String,
    val success: Boolean
)


sealed class UpdateNotificationResponseError {
    data class BadRequest(val message: String, val error: String?) :  UpdateNotificationResponseError()
}