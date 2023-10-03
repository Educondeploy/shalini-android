package com.tt.skolarrs.model.request

data class UpdateLeadStatusRequest(
    val current_lead: String,
    val lead_id: String,
    val previous_lead: String
)