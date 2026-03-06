package com.kaboom.bloodbank

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.kaboom.bloodbank.databinding.ActivityMyDonationStatusBinding
import com.kaboom.bloodbank.db.DonationRequest
import com.kaboom.bloodbank.db.DonorDB

class MyDonationStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyDonationStatusBinding
    private var donorPhone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDonationStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val requestId = intent.getStringExtra("requestId") ?: return

        supportActionBar?.title = "Request Status"

        Firebase.database.reference.child("donation_requests").child(requestId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val request = snapshot.getValue(DonationRequest::class.java) ?: return
                    
                    binding.tvDonorName.text = "Donor: ${request.donorName}"
                    binding.tvHospitalName.text = "Hospital: ${request.hospital}"

                    when (request.status) {
                        "pending" -> {
                            binding.tvStatus.text = "Waiting for donor response..."
                            binding.tvStatus.setTextColor(Color.GRAY)
                            binding.layoutContactDetails.visibility = View.GONE
                        }
                        "accepted" -> {
                            binding.tvStatus.text = "✓ Donor accepted your request!"
                            binding.tvStatus.setTextColor(Color.GREEN)
                            showDonorContactDetails(request.donorId)
                        }
                        "declined" -> {
                            binding.tvStatus.text = "✗ Donor could not help this time."
                            binding.tvStatus.setTextColor(Color.RED)
                            binding.layoutContactDetails.visibility = View.GONE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        binding.btnCallDonor.setOnClickListener {
            donorPhone?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        }
    }

    private fun showDonorContactDetails(donorId: String) {
        Firebase.database.reference.child("donors").child(donorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val donor = snapshot.getValue(DonorDB::class.java)
                    donorPhone = donor?.phoneNumber
                    if (donorPhone != null) {
                        binding.tvDonorPhone.text = donorPhone
                        binding.layoutContactDetails.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
