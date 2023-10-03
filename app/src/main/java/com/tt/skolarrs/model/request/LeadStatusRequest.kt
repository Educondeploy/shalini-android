package com.tt.skolarrs.model.request

 data class LeadStatusRequest(
  /*  val _id: String,*/
    val leadId: String,
    val report: Report,
    val userId: String
)

data class Report(
    val callHistory: String,
    val currentStatus: String,
    val notes: String?,
    val followUpDate: String?,
    val nextFollowUpDate: String?
)