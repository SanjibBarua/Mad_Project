package com.kaboom.bloodbank


data class DonorDB(
    var uid: String = "",
    val fullName: String = "",       // used in MainActivity → "Welcome $fullName"
    val name: String = "",           // used in FindDonorActivity
    val bloodGroup: String = "",
    val phone: String = "",
    val division: String = "",
    val district: String = "",
    val area: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val lastDonationDate: String = "",
    val isAvailable: Boolean = true,
    val totalDonations: Int = 0,
    val profileImageUrl: String = ""
)