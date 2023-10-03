package com.tt.skolarrs.model.request

data class GetLeadPaginationRequest(
    val _limit: Int,
    val _page: Int,
    val assignedOn: String,
    val leadStatus: String
)