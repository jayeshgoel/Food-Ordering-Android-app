package com.example.foodorderingapp.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater

import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.DetailsActivity
import com.example.foodorderingapp.Model.CartItem
import com.example.foodorderingapp.Model.menuItem

import com.example.foodorderingapp.databinding.MenuItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class MenuAdapter(private val menuItems: List<menuItem>, private val requireContext:Context) :RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {


    inner class MenuViewHolder( val binding: MenuItemBinding):RecyclerView.ViewHolder(binding.root) {
        init{
            binding.root.setOnClickListener{
                val position= adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    openDetailActivity(position)
                }
            }
            binding.addToCartButton.setOnClickListener{
                val position=adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    addItemToCart(position)
                }
            }

        }
        // set data to recycler view item name
        fun bind(position: Int) {
            binding.apply {
                menuItemName.text=menuItems[position].foodName
                menuItemPrice.text=menuItems[position].foodPrice + "$"
                val Uri= Uri.parse(menuItems[position].foodImage)
                Glide.with(requireContext).load(Uri).into(menuItemImage)
            }
        }

    }

    private fun addItemToCart(position: Int){
        val auth=FirebaseAuth.getInstance()
        val databaseRef=FirebaseDatabase.getInstance().reference
        val userId= auth.currentUser?.uid?:""
        val menuItem1=menuItems[position]
        val cartItem= CartItem(menuItem1.key,menuItem1.foodName,menuItem1.foodPrice,menuItem1.foodDescription,menuItem1.foodImage,1)
        databaseRef.child("users").child(userId).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(requireContext,"Item added to cart successfully", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext,"Can't add item", Toast.LENGTH_LONG).show()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding=MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun openDetailActivity(position: Int) {
        val menuItem=menuItems[position]
        // intent to open detail activity and pass data
        val intent=Intent(requireContext,DetailsActivity::class.java).apply {
            putExtra("MenuItemKey",menuItem.key)
            putExtra("MenuItemName",menuItem.foodName)
            putExtra("MenuItemImage",menuItem.foodImage)
            putExtra("MenuItemDescription",menuItem.foodDescription)
            putExtra("MenuItemIngredient",menuItem.foodIngredient)
            putExtra("MenuItemPrice",menuItem.foodPrice)
        }
        requireContext.startActivity(intent)
    }


}


