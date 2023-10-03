package com.tt.skolarrs.model.response

sealed class GetIndividualReportResponseResult {
    data class Success(val leadResponse: GetIndividualReportResponse) : GetIndividualReportResponseResult()
    data class Error(val message: String) : GetIndividualReportResponseResult()
}


data class GetIndividualReportResponse(
    val `data`: List<IndividualReportData>,
    val message: String,
    val success: Boolean
)

data class IndividualReportData(
    val __v: Int,
    val _id: String,
    val assignedDate: String,
    val assignedOn: String,
    val createdAt: String,
    val currentCollege: String,
    val currentCourse: String,
    val event: String,
    val eventLink: String,
    val followups: List<Followup>,
    val leadStatus: String,
    val lead_id: String,
    val mobileNo: Long,
    val name: String,
    val place: String,
    val recordings: List<Recording>,
    val reports: List<IndidualReport>,
    val updatedAt: String
)

data class IndidualReport(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val leadId: String,
    val report: List<ReportY>,
    val updatedAt: String,
    val userId: String
)

data class ReportY(
    val callHistory: String,
    val currentStatus: String,
    val followUpDate: String,
    val nextFollowUpDate: String,
    val notes: String
)

data class Followup(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val date: String,
    val followUp: String,
    val followUpStatus: String,
    val leadId: String,
    val previousStatus: String,
    val time: String,
    val updatedAt: String,
    val userId: String
)

data class Recording(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val leadID: String,
    val recordingURL: String,
    val reportId: String,
    val userId: String
)

sealed class GetIndividualReportResponseError {
    data class BadRequest(val message: String, val error: String?) :
        GetIndividualReportResponseError()
}