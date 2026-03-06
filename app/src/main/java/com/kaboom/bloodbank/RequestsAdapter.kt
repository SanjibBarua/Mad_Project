package com.kaboom.bloodbank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.kaboom.bloodbank.databinding.ItemDonationRequestBinding
import com.kaboom.bloodbank.db.DonationRequest
import java.util.Locale

class RequestsAdapter(private val requests: List<DonationRequest>) :
    RecyclerView.Adapter<RequestsAdapter.RequestViewHolder>() {

    class RequestViewHolder(val binding: ItemDonationRequestBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemDonationRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        holder.binding.apply {
            tvRequesterName.text = "Requester: ${request.requesterName}"
            tvHospital.text = "Hospital: ${request.hospital}"
            tvUrgency.text = "Urgency: ${request.urgency}"
            tvMessage.text = "Message: ${request.message}"

            if (request.status == "pending") {
                layoutActions.visibility = View.VISIBLE
                tvStatus.visibility = View.GONE

                btnAccept.setOnClickListener {
                    respondToRequest(it.context, request.requestId, "accepted")
                }
                btnDecline.setOnClickListener {
                    respondToRequest(it.context, request.requestId, "declined")
                }
            } else {
                layoutActions.visibility = View.GONE
                tvStatus.visibility = View.VISIBLE
                tvStatus.text = "Status: ${request.status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }}"
            }
        }
    }

    private fun respondToRequest(context: android.content.Context, requestId: String, response: String) {
        FirebaseDatabase.getInstance().reference
            .child("donation_requests")
            .child(requestId)
            .child("status")
            .setValue(response)
            .addOnSuccessListener {
                val msg = if (response == "accepted") "You accepted this request!" else "You declined this request."
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update status: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount() = requests.size
}
