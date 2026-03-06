package com.kaboom.bloodbank.db

data class DonationRequest(
    var requestId: String = "",
    val requesterId: String = "",
    val donorId: String = "",
    val requesterName: String = "",
    val donorName: String = "",
    val bloodGroup: String = "",
    val hospital: String = "",
    val message: String = "",
    val urgency: String = "",
    val status: String = "pending",
    val timestamp: Long = 0L
)
