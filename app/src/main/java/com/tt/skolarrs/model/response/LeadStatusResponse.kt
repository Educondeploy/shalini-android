package com.tt.skolarrs.model.response


sealed class LeadStatusResponseResult {
    data class Success(val leadResponse: LeadStatusResponse) : LeadStatusResponseResult()
    data class Error(val message: String) : LeadStatusResponseResult() }

data class LeadStatusResponse(
    val `data`: Data3,
    val message: String,
    val success: Boolean
)

data class Data3(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val leadId: String,
    val report: List<Report>,
    val updatedAt: String,
    val userId: String
)

data class Report(
    val callHistory: String,
    val currentStatus: String
)

sealed class LeadStatusResponseError {
    data class BadRequest(val message: String, val error: String?) : LeadStatusResponseError()
}