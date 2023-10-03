package com.tt.skolarrs.model.request

data class FollowUpUpdateRequest(
    /*val _id: String,*/
    val date: String,
    val followUp: String,
    val leadId: String,
    val time: String,
    val userId: String,
    val previousStatus: String
)