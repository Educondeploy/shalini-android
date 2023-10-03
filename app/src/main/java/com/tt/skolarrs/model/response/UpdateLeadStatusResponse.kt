package com.tt.skolarrs.model.response


sealed class UpdateLeadStatusResponseResult {
    data class Success(val leadResponse: UpdateLeadStatusResponse) :
        UpdateLeadStatusResponseResult()

    data class Error(val message: String) : UpdateLeadStatusResponseResult()
}

data class UpdateLeadStatusResponse(

    val data: List<LeadData>,
    val message: String,
    val success: Boolean
)


data class LeadData(
    val current_lead: String,
    val lead_id: String,
    val previous_lead: String
)

sealed class UpdateLeadStatusResponseError {
    data class BadRequest(val message: String, val error: String?) : UpdateLeadStatusResponseError()
}