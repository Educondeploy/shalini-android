package com.tt.skolarrs.model.response


sealed class IndividualReportResponseResult {
    data class Success(val leadResponse: ReportResponse) : ReportResponseResult()
    data class Error(val message: String) : ReportResponseResult()
}

data class IndividualReportResponse(
    val `data`: List<IndividualReport>,
    val message: String,
    val success: Boolean
)

data class IndividualReport(
    val _id: String,
    val callCount: Int,
    val lead: Lead1,
    val report: List<IndividualReport1>,
    val totalReportTime: String,
    val userId: String
)

data class Lead1(
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

data class IndividualReport1(
    val callHistory: String,
    val currentStatus: String,
    val notes: String,
    val followUpDate: String?
)

sealed class IndividualReportResponseError {
    data class BadRequest(val message: String, val error: String?) :
        IndividualReportResponseError()
}