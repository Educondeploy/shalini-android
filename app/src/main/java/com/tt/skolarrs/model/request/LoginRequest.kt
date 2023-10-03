package com.tt.skolarrs.model.request

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




data class LoginRequest(
    val userId: String,
    val password: String,
    val deviceid: String,
    val coords: List<String>?,
    val location: String
)

