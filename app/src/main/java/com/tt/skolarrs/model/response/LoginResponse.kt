package com.tt.skolarrs.model.response


sealed class LoginResponseResult {
    data class Success(val leadResponse: LoginResponse) : LoginResponseResult()
    data class Error(val message: String) : LoginResponseResult()
}

    data class LoginResponse(
        val `data`: Data,
        val message: String,
        val success: Boolean
    )

    data class Data(
        val token: String,
        val user: User
    )

    data class User(
        val __v: Int,
        val _id: String,
        val accountHolderName: String,
        val accountNumber: String,
        val accountType: String,
        val age: Int,
        val annualCTC: String,
        val bankName: String,
        val createdAt: String,
        val currentStage: Int,
        val dateOfBirth: String,
        val dateOfJoining: String,
        val department: String,
        val designation: String,
        val empFirstName: String,
        val empID: String,
        val empLastName: String,
        val empMiddleName: String,
        val fatherName: String,
        val gender: String,
        val ifsc: String,
        val monthlyAmountBasic: Int,
        val monthlyAmountHouseRentAllowance: Int,
        val monthlyChildrenEducationAllowance: Int,
        val monthlyConveyanceAllowance: Int,
        val monthlyFixedAllowence: Int,
        val monthlyTotal: Int,
        val monthlyTransportAllowance: Int,
        val monthlyTravellingAllowance: Int,
        val officialMobileNumber: String,
        val password: String,
        val payment: String,
        val percentageOfBasic: Int,
        val percentageOfCtc: Int,
        val reAccountNumber: String,
        val stages: Stages,
        val updatedAt: String,
        val userID: String,
        val workLocation: String,
        val yearlyAmountBasic: Int,
        val yearlyAmountHouseRentAllowance: Int,
        val yearlyChildrenEducationAllowance: Int,
        val yearlyConveyanceAllowance: Int,
        val yearlyFixedAllowence: Int,
        val yearlyTotal: Int,
        val yearlyTransportAllowance: Int,
        val yearlyTravellingAllowance: Int
    )

    data class Stages(
        val `final`: Boolean,
        val five: Boolean,
        val four: Boolean,
        val one: Boolean,
        val three: Boolean,
        val two: Boolean
    )


sealed class LogInResponseError {
    data class BadRequest(val message: String, val error: String?) : LogInResponseError()
}