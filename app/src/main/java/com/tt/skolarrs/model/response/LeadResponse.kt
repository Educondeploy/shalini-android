package com.tt.skolarrs.model.response

sealed class LeadResponseResult {
    data class Success(val leadResponse: LeadResponse) : LeadResponseResult()
    data class Error(val message: String) : LeadResponseResult()
}

data class LeadResponse(
    val data: List<Data1>,
    val message: String,
    val success: Boolean
)

data class Data1(
    val _id: String,
    val complete: List<Complete>,
    val eventExpo: String,
    val incomplete: List<Incomplete>,
    val busyOnOtherCall: List<busyOnOtherCall>,
    val hot: List<hot>,
    val cold: List<cold>,
    val warm: List<warm>,
    val switchOff: List<switchOff>,
    val outofCoverage: List<outOfCoverage>,
    val userDisconnectTheCall: List<userDisconnectTheCall>,
    val invalidNumber: List<inValidNumber>,
    val didNotPick: List<didNotPick>,
    val notInterested: List<customerSayNotInterest>,
    val userId: String
)

data class customerSayNotInterest(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override val date: String,
    override val event: String,
    override val eventLink: String,
    override val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override val name: String,
    override val neetAspirants: Boolean,
    override val place: String,
    override val preferredPlaceToStudy: String
) : TemperatureData

data class Complete(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override val date: String,
    override val event: String,
    override val eventLink: String,
    override val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override val name: String,
    override val neetAspirants: Boolean,
    override val place: String,
    override val preferredPlaceToStudy: String
) : TemperatureData

data class Incomplete(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override val date: String,
    override val event: String,
    override val eventLink: String,
    override val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override val name: String,
    override val neetAspirants: Boolean,
    override val place: String,
    override val preferredPlaceToStudy: String
) : TemperatureData

data class outOfCoverage(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override val date: String,
    override val event: String,
    override val eventLink: String,
    override val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override  val name: String,
    override  val neetAspirants: Boolean,
    override  val place: String,
    override val preferredPlaceToStudy: String
) : TemperatureData


data class inValidNumber(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override  val currentCollege: String,
    override  val currentCourse: String,
    override  val date: String,
    override  val event: String,
    override  val eventLink: String,
    override val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override  val name: String,
    override  val neetAspirants: Boolean,
    override  val place: String,
    override  val preferredPlaceToStudy: String
) : TemperatureData

data class switchOff(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override  val date: String,
    override  val event: String,
    override  val eventLink: String,
    override  val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override  val name: String,
    override val neetAspirants: Boolean,
    override  val place: String,
    override  val preferredPlaceToStudy: String
) : TemperatureData

data class busyOnOtherCall(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override val date: String,
    override val event: String,
    override val eventLink: String,
    override  val ifAnyEntranceExam: String,
    override  val mobileNo: String,
    override  val name: String,
    override  val neetAspirants: Boolean,
    override val place: String,
    override val preferredPlaceToStudy: String
) : TemperatureData

data class cold(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override val date: String,
    override val event: String,
    override val eventLink: String,
    override val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override val name: String,
    override val neetAspirants: Boolean,
    override val place: String,
    override val preferredPlaceToStudy: String
): TemperatureData

data class warm(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override val date: String,
    override val event: String,
    override val eventLink: String,
    override val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override val name: String,
    override val neetAspirants: Boolean,
    override val place: String,
    override val preferredPlaceToStudy: String
) : TemperatureData

data class didNotPick(
    override  val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override  val currentCourse: String,
    override  val date: String,
    override  val event: String,
    override  val eventLink: String,
    override   val ifAnyEntranceExam: String,
    override  val mobileNo: String,
    override val name: String,
    override val neetAspirants: Boolean,
    override  val place: String,
    override  val preferredPlaceToStudy: String
) : TemperatureData


data class userDisconnectTheCall(
    override val _id: String,
    override val __v: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val currentCollege: String,
    override val currentCourse: String,
    override val date: String,
    override val event: String,
    override val eventLink: String,
    override val ifAnyEntranceExam: String,
    override val mobileNo: String,
    override val name: String,
    override val neetAspirants: Boolean,
    override val place: String,
    override val preferredPlaceToStudy: String
) : TemperatureData

/*data class followUp(
    val _id: String,
    val __v: String,
    val createdAt: String,
    val updatedAt: String,
    val currentCollege: String,
    val currentCourse: String,
    val date: String,
    val event: String,
    val eventLink: String,
    val ifAnyEntranceExam: String,
    val mobileNo: String,
    val name: String,
    val neetAspirants: Boolean,
    val place: String,
    val preferredPlaceToStudy: String
)*/

data class hot(
  override val _id: String,
  override val __v: String,
  override val createdAt: String,
  override val updatedAt: String,
  override val currentCollege: String,
  override val currentCourse: String,
  override val date: String,
  override val event: String,
  override val eventLink: String,
  override val ifAnyEntranceExam: String,
  override val mobileNo: String,
  override val name: String,
  override val neetAspirants: Boolean,
  override val place: String,
  override val preferredPlaceToStudy: String
) : TemperatureData


data class UpdatedList(
    val modifiedList: TemperatureData,
    val extraParam: String
)

interface TemperatureData {
    val _id: String
    val __v: String
    val createdAt: String
    val updatedAt: String
    val currentCollege: String
    val currentCourse: String
    val date: String
    val event: String
    val eventLink: String
    val ifAnyEntranceExam: String
    val mobileNo: String
    val name: String
    val neetAspirants: Boolean
    val place: String
    val preferredPlaceToStudy: String
}


sealed class LeadResponseError {
    data class BadRequest(val message: String, val error: String?) : LeadResponseError()
}

