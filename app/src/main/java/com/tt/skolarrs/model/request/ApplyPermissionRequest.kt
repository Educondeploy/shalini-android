package com.tt.skolarrs.model.request

data class ApplyPermissionRequest(
    val date: String,
    val timeFrom: String,
    val timeTo: String,
    val reason: String,
    val message: String,
)