package com.tt.skolarrs.model.response

sealed class GetPermissionDetails {
    data class Success(val leadResponse: GetPermissionResponse) : GetPermissionDetails()
    data class Error(val message: String) : GetPermissionDetails()
}

data class GetPermissionResponse(
    val `data`: List<PermissionDetails>,
    val message: String,
    val success: Boolean
)

data class PermissionDetails(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val date: String,
    val timeFrom: String,
    val timeTo: String,
    val message: String,
    val reason: String,
    val status: String,
    val updatedAt: String,
    val userId: String
)

sealed class GetPermissionDetailsError {
    data class BadRequest(val message: String, val error: String?) : GetPermissionDetailsError()
}