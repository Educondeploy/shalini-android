package com.tt.skolarrs.model.response


sealed class CalenderViewResponseResult {
    data class Success(val leadResponse: GetPermissionResponse) : CalenderViewResponseResult()
    data class Error(val message: String) : CalenderViewResponseResult()
}

data class CalenderViewResponse(
    val `data`: List<CalenderData>,
    val message: String,
    val success: Boolean
)

data class CalenderData(
    val __v: Int,
    val _id: String,
    val date: String,
    val holiday: String
)

sealed class CalenderViewResponseError {
    data class BadRequest(val message: String, val error: String?) : CalenderViewResponseError()
}