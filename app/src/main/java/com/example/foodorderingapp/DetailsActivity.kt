package com.example.foodorderingapp

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodorderingapp.Model.CartItem
import com.example.foodorderingapp.databinding.ActivityDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailsBinding

    private var itemKey:String? = null
    private var foodName:String? = null
    private var foodImage:String? = null
    private var foodDescription:String? = null
    private var foodIngredient:String? = null
    private var foodPrice:String? = null
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding=ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemKey=intent.getStringExtra("MenuItemKey")
        foodName=intent.getStringExtra("MenuItemName")
        foodDescription=intent.getStringExtra("MenuItemDescription")
        foodIngredient=intent.getStringExtra("MenuItemIngredient")
        foodPrice=intent.getStringExtra("MenuItemPrice")
        foodImage=intent.getStringExtra("MenuItemImage")

        with(binding){
            detailedFoodName.text=foodName
            detailedFoodDescription.text=foodDescription
            detailedFoodIngredient.text=foodIngredient
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(detailedFoodImage)
        }



        binding.detailedFoodImage.setOnClickListener{
            finish()
        }

        binding.detailAddToCartButton.setOnClickListener {
            addItemToCartFunction()
        }
    }

    private fun addItemToCartFunction() {
        auth=FirebaseAuth.getInstance()
        val database=FirebaseDatabase.getInstance().reference

        var userId=auth.currentUser?.uid?:""

        // create a cart Item Object
        val cartItem=CartItem(itemKey,foodName.toString(),foodPrice.toString(),foodDescription.toString(),foodImage.toString(),1)
        // save data to cart Item to FireBase
        database.child("users").child(userId).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this,"Item added to cart successfully",Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            Toast.makeText(this,"Can't add item",Toast.LENGTH_LONG).show()
        }
    }


}