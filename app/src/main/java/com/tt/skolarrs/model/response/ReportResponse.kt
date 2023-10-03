package com.tt.skolarrs.model.response


sealed class ReportResponseResult {
    data class Success(val leadResponse: ReportResponse) : ReportResponseResult()
    data class Error(val message: String) : ReportResponseResult()
}

data class ReportResponse(
    val `data`: List<ReportData>,
    val message: String,
    val success: Boolean
)

data class ReportData(
    val _id: String,
    val callCount: Int,
    val lead: Lead,
    val report: List<Report1>,
    val totalReportTime: String,
    val userId: String
)

data class Lead(
    val _id: String,
    val currentCollege: String,
    val currentCourse: String,
    val date: String,
    val event: String,
    val eventLink: String,
    val ifAnyEntranceExam: String,
    val mobileNo: String,
    val name: String,
    val neetAspirants: Boolean,
    val place: String,
    val preferredPlaceToStudy: String
)

data class Report1(
    val callHistory: String,
    val currentStatus: String,
    val notes: String
)

sealed class ReportResponseError {
    data class BadRequest(val message: String, val error: String?) : ReportResponseError()
}