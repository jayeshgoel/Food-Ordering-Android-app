package com.example.foodorderingapp.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context: Context,
    private val names: MutableList<String>,
    private val prices: MutableList<String>,
    private val images: MutableList<String>,
    private val descriptions: MutableList<String>,
    private val quantity: MutableList<Int>,

    ) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = names.size
        itemQuantities = IntArray(cartItemNumber) { 1 }
        cartItemsReference = database.reference.child("users").child(userId).child("CartItems")

    }

    companion object {
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                cartItemName.text = names[position]
                cartItemPrice.text = prices[position] + "$"

                // Load image using glide
                val uriString = Uri.parse(images[position])
                Glide.with(context).load(uriString).into(cartItemImage)

                cartItemQuantity.text = quantity[position].toString()


                minusButton.setOnClickListener {
                    decreaseQuantity(position)
                    notifyDataSetChanged()
                }
                plusButton.setOnClickListener {
                    increaseQuantity(position)
                    notifyDataSetChanged()
                }


            }

        }

        private fun decreaseQuantity(position: Int) {
            if (quantity[position] == 1) {
                deleteItem(position)
            } else {
//                itemQuantities[position]--
                quantity[position]--
                binding.cartItemQuantity.text = quantity[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if (quantity[position] < 10) {
//                itemQuantities[position]++
                quantity[position]++
                binding.cartItemQuantity.text = quantity[position].toString()

            }
        }


        private fun deleteItem(position: Int) {
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }

            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null) {
                cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_LONG).show()

                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to Delete", Toast.LENGTH_LONG).show()
                }

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    fun getUpdatedItemQuantities(): MutableList<Int> {
        val itemQuantiy = mutableListOf<Int>()
        itemQuantiy.addAll(quantity)
        return itemQuantiy
    }

    fun updateQuantityInDatabase() {

        for (i in 0..<names.size) {
            getUniqueKeyAtPosition(i) { uniqueKey ->
                if (uniqueKey != null) {
                    cartItemsReference.child(uniqueKey).child("foodQuantity").setValue(quantity[i])
                }
            }
        }
    }

    fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
        cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey: String? = null
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == positionRetrieve) {
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}