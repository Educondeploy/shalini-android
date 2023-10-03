package com.tt.skolarrs.model.response


sealed class GetLeadPaginationResponseResult {
    data class Success(val leadResponse: GetLeadPaginationResponse) : GetLeadPaginationResponseResult()
    data class Error(val message: String) : LeadResponseResult()
}

data class GetLeadPaginationResponse(
    val data: PaginationData,
    val message: String,
    val success: Boolean
)

data class PaginationData(
    val data: List<DataX>,
    val total: Int
)

data class DataX(
    val __v: Int,
    val _id: String,
    val assignedDate: String,
    val assignedOn: String,
    val createdAt: String,
    val currentCollege: String,
    val currentCourse: String,
    val date: String,
    val event: String,
    val eventLink: String,
    val ifAnyEntranceExam: Any,
    val leadStatus: String,
    val mobileNo: String,
    val name: String,
    val neetAspirants: String,
    val place: String,
    val preferredPlaceToStudy: String,
    val updatedAt: String
)

data class UpdatedPaginationList(
    val modifiedList: DataX,
    val extraParam: String
)

sealed class GetLeadPaginationResponseError {
    data class BadRequest(val message: String, val error: String?) : GetLeadPaginationResponseError()
}