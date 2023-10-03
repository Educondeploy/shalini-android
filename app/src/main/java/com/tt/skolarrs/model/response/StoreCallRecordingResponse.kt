package com.tt.skolarrs.model.response


sealed class StoreCallRecordingResponseResult {
    data class Success(val leadResponse: StoreCallRecordingResponse) : StoreCallRecordingResponseResult()
    data class Error(val message: String) : LeadStatusResponseResult() }

data class StoreCallRecordingResponse(
    val `data`: Data0,
    val message: String,
    val success: Boolean
)

data class Data0(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val leadID: String,
    val recordingURL: String,
    val reportId: String,
    val userId: String
)



sealed class StoreCallRecordingResponseError {
    data class BadRequest(val message: String, val error: String?) : StoreCallRecordingResponseError()
}