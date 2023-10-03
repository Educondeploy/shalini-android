package com.tt.skolarrs.model.response


sealed class DashBoardListResponseResult {
    data class Success(val leadResponse: DashBoardListResponse) : DashBoardListResponseResult()
    data class Error(val message: String) : CalenderViewResponseResult()
}

data class DashBoardListResponse(
    val `data`: List<DashBoardData>,
    val message: String,
    val success: Boolean
)

data class DashBoardData(
    val _id: String,
    val busyOnOtherCall: Int,
    val cold: Int,
    val completed: Int,
    val didNotPick: Int,
    val hot: Int,
    val incomplete: Int,
    val interested: Int,
    val invalidNumber: Int,
    val notInterested: Int,
    val outofCoverage: Int,
    val switchOff: Int,
    val userDisconnectTheCall: Int,
    val warm: Int
)

sealed class DashBoardListResponseError {
    data class BadRequest(val message: String, val error: String?) : DashBoardListResponseError()
}