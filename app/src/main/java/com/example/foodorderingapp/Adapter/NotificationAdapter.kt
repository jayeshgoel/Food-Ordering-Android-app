package com.example.foodorderingapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.databinding.NotificationItemBinding

class NotificationAdapter(private var notificationTexts:ArrayList<String>,private var notificationImages:ArrayList<Int>):RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {



    inner class NotificationViewHolder(private var binding: NotificationItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(s: String, i: Int) {
            binding.apply{
                NotificationTextView.text=s
                NotificationImageView.setImageResource(i)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        var binding=NotificationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationTexts.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationTexts[position],notificationImages[position])
    }
}