package com.tt.skolarrs.model.request

import com.tt.skolarrs.model.response.Data

data class ApplyLeaveRequest(
    val dateFrom: String,
    val dateTo: String,
    val reason: String,
    val message: String,
)

