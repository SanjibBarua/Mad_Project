package com.kaboom.bloodbank.db

data class DonorDB(
    val address: String = "",
    val bloodGroup: String  = "",
    val division: String = "",
    val dateOfBirth: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
