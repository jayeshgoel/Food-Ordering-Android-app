package com.example.foodorderingapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.DetailsActivity
import com.example.foodorderingapp.databinding.PopularItemBinding

class PopularAdapter(private val Items:List<String>,private val prices:List<String>,private val images:List<Int>,private val requireContext:Context): RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun getItemCount(): Int {
        return Items.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item=Items[position]
        val image=images[position]
        val price=prices[position]
        holder.bind(item,price,image)

        holder.itemView.setOnClickListener{
            val intent= Intent(requireContext,DetailsActivity::class.java)
            intent.putExtra("MenuItemName",item)
            intent.putExtra("MenuItemImage",image)
            requireContext.startActivity(intent)
        }


    }

    class PopularViewHolder(private val binding:PopularItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: String, price: String, image: Int) {
            binding.foodNamePopular.text=item
            binding.popularItemPrice.text=price
            binding.popularItemImage.setImageResource(image)

        }

    }
}