package com.tt.skolarrs.model.response

sealed class LogoutResponse {
    data class Success(val leadResponse: LogoutResponse) :  LogoutResponse()
    data class Error(val message: String) :  LogoutResponse() }

    data class LogoutResponseSuccess(
        val error: String,
        val message: String,
        val success: Boolean
    )


sealed class LogOutResponseError {
    data class BadRequest(val message: String, val error: String?) :  LogOutResponseError()
}