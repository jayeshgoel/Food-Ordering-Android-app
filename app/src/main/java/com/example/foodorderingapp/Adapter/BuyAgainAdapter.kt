package com.example.foodorderingapp.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.databinding.BuyAgainItemBinding

class BuyAgainAdapter(private val buyAgainFoodName:MutableList<String>,
                      private val buyAgainFoodPrice:MutableList<String>,
                      private val buyAgainFoodImage:MutableList<String>,
                    private val requireContext:Context,
    private val itemClickListener: onItemClickListener
    ) :RecyclerView.Adapter<BuyAgainAdapter.BuyAgainHolder>() {

    interface onItemClickListener   {
        fun onClicked( position:Int)
    }
   inner class BuyAgainHolder(private val binding: BuyAgainItemBinding) :RecyclerView.ViewHolder(binding.root) {

       fun bind(foodName: String, foodPrice: String, foodImage: String,position: Int) {
           binding.BuyAgainFoodName.text=foodName
           binding.BuyAgainFoodPrice.text=foodPrice+"$"
           val uriString=foodImage
           val uri= Uri.parse(uriString)
           Glide.with(requireContext).load(uri).into(binding.BuyAgainFoodImage)
            binding.previousOrderDetails.setOnClickListener{
                itemClickListener.onClicked(position)
            }
       }

   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainHolder {
        val binding=BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyAgainHolder(binding)
    }

    override fun getItemCount(): Int {
        return buyAgainFoodName.size
    }

    override fun onBindViewHolder(holder: BuyAgainHolder, position: Int) {
        holder.bind(buyAgainFoodName[position],buyAgainFoodPrice[position],buyAgainFoodImage[position],position)
    }
}