package com.kaboom.bloodbank

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kaboom.bloodbank.databinding.ActivityFindDonorBinding
import com.kaboom.bloodbank.db.DonorDB

class FindDonorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindDonorBinding
    private lateinit var database: DatabaseReference
    private lateinit var donorAdapter: DonorsAdapter
    private val donorList = ArrayList<DonorDB>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindDonorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        // Setup RecyclerView
        donorAdapter = DonorsAdapter(this, donorList)
        binding.rvDonors.layoutManager = LinearLayoutManager(this)
        binding.rvDonors.adapter = donorAdapter

        binding.btnSearch.setOnClickListener {
            val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
            val division = binding.etDivision.text.toString().trim()

            binding.tvNoResult.visibility = View.GONE

            searchDonors(bloodGroup, division)
        }
    }

    private fun searchDonors(bloodGroup: String, division: String) {
        if (bloodGroup == "All") {
            database.child("donors")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        donorList.clear()
                        if (!snapshot.exists()) {
                            binding.tvNoResult.visibility = View.VISIBLE
                            donorAdapter.notifyDataSetChanged()
                            return
                        }
                        for (child in snapshot.children) {
                            val donor = child.getValue(DonorDB::class.java) ?: continue
                            if (division.isEmpty() || donor.division.equals(division, ignoreCase = true)) {
                                donorList.add(donor)
                            }
                        }
                        donorAdapter.notifyDataSetChanged()
                        if (donorList.isEmpty()) {
                            binding.tvNoResult.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@FindDonorActivity,
                            "Error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            return
        }

        database.child("donors")
            .orderByChild("bloodGroup")
            .equalTo(bloodGroup)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    donorList.clear()
                    if (!snapshot.exists()) {
                        binding.tvNoResult.visibility = View.VISIBLE
                        donorAdapter.notifyDataSetChanged()
                        return
                    }
                    for (child in snapshot.children) {
                        val donor = child.getValue(DonorDB::class.java) ?: continue
                        if (division.isEmpty() || donor.division.equals(division, ignoreCase = true)) {
                            donorList.add(donor)
                        }
                    }
                    donorAdapter.notifyDataSetChanged()
                    if (donorList.isEmpty()) {
                        binding.tvNoResult.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@FindDonorActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
