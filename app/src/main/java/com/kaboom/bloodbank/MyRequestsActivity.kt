package com.kaboom.bloodbank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kaboom.bloodbank.databinding.ActivityMyRequestsBinding
import com.kaboom.bloodbank.db.DonationRequest

class MyRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRequestsBinding
    private lateinit var adapter: RequestsAdapter
    private val requestList = mutableListOf<DonationRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Incoming Requests"

        adapter = RequestsAdapter(requestList)
        binding.rvRequests.layoutManager = LinearLayoutManager(this)
        binding.rvRequests.adapter = adapter

        loadRequests()
    }

    private fun loadRequests() {
        val currentUser = Firebase.auth.currentUser ?: return
        
        Firebase.database.reference
            .child("donation_requests")
            .orderByChild("donorId")
            .equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    requestList.clear()
                    for (child in snapshot.children) {
                        val req = child.getValue(DonationRequest::class.java) ?: continue
                        req.requestId = child.key ?: continue
                        requestList.add(req)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}
