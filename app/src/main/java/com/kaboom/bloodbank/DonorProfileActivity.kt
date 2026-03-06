package com.kaboom.bloodbank

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.kaboom.bloodbank.databinding.ActivityDonorProfileBinding
import com.kaboom.bloodbank.db.DonorDB
import com.kaboom.bloodbank.db.Users

class DonorProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val donorId = intent.getStringExtra("donorId") ?: return

        Firebase.database.reference
            .child("donors").child(donorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val donor = snapshot.getValue(DonorDB::class.java) ?: return
                    binding.apply {
                        tvDonorName.text = donor.fullName
                        tvBloodGroup.text = donor.bloodGroup
                        tvLocation.text = donor.address
                        tvPhone.text = donor.phoneNumber
                        tvLastDonation.text = "Last Donated: Unknown"
                        tvTotalDonations.text = "Total Donations: 0"

                        tvAvailability.text = "Available to Donate"
                        tvAvailability.setBackgroundColor(Color.GREEN)

                        Glide.with(this@DonorProfileActivity)
                            .load(R.drawable.ic_launcher_background)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(ivProfile)

                        btnRequest.setOnClickListener {
                            sendDonationRequest(donorId, donor)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DonorProfileActivity, "Failed to load donor profile", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendDonationRequest(donorId: String, donor: DonorDB) {
        val currentUser = Firebase.auth.currentUser ?: return
        val db = Firebase.database.reference

        // Fetch requester's name first
        db.child("users").child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    val requesterName = user?.fullName ?: "Unknown"
                    val requestId = db.child("donation_requests").push().key ?: return

                    val request = mapOf(
                        "requesterId" to currentUser.uid,
                        "donorId" to donorId,
                        "requesterName" to requesterName,
                        "donorName" to donor.fullName,
                        "bloodGroup" to donor.bloodGroup,
                        "hospital" to binding.etHospital.text.toString(),
                        "message" to binding.etMessage.text.toString(),
                        "urgency" to binding.spinnerUrgency.selectedItem.toString(),
                        "status" to "pending",
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.child("donation_requests").child(requestId)
                        .setValue(request)
                        .addOnSuccessListener {
                            Toast.makeText(this@DonorProfileActivity, "Request sent!", Toast.LENGTH_SHORT).show()
                            
                            val intent = Intent(this@DonorProfileActivity, MyDonationStatusActivity::class.java)
                            intent.putExtra("requestId", requestId)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@DonorProfileActivity, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DonorProfileActivity, "Failed to get user info", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
