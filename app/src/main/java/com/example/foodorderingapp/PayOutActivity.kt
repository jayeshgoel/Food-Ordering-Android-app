package com.example.foodorderingapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorderingapp.Fragment.CongratsBottomSheet
import com.example.foodorderingapp.Model.OrderDetails
import com.example.foodorderingapp.databinding.ActivityPayOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class PayOutActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPayOutBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var userId:String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var name:String
    private lateinit var address:String
    private lateinit var phone:String
    private lateinit var totalAmount:String

    private lateinit var foodItemName : ArrayList<String>
    private lateinit var foodItemPrice : ArrayList<String>
    private lateinit var foodItemImage : ArrayList<String>
    private lateinit var foodItemQuantity : ArrayList<Int>
    private lateinit var foodItemDescription : ArrayList<String>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("DEBUGGING","on CREATE")
        binding= ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize firebase and user details
        auth =FirebaseAuth.getInstance()
        databaseRef=FirebaseDatabase.getInstance().getReference()
        setUserData()

        val intent = intent
        foodItemName  = intent.getStringArrayListExtra("FoodItemNames")  as ArrayList<String>
        foodItemPrice = intent.getStringArrayListExtra("FoodItemPrices") as ArrayList<String>
        foodItemImage = intent.getStringArrayListExtra("FoodItemImages") as ArrayList<String>
        foodItemDescription = intent.getStringArrayListExtra("FoodItemDescription") as ArrayList<String>
        foodItemQuantity = intent.getIntegerArrayListExtra("FoodItemQuantity") as ArrayList<Int>


        totalAmount=calculateTotalAmount().toString() + "$"
        binding.payOutAmount.isEnabled=false
        binding.payOutAmount.setText(totalAmount)

        binding.placeMyOrder.setOnClickListener{
            // get data from text View
            name=binding.payOutName.text.toString().trim()
            address=binding.payOutAddress.text.toString().trim()
            phone=binding.payOutPhone.text.toString().trim()

            if(name.isBlank() || address.isBlank() || phone.isBlank() ){
                Toast.makeText(this,"Please fill all Details",Toast.LENGTH_LONG).show()
            }
            else{
                placeOrder()
            }


        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DEBUGGING","on RESUME")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DEBUGGING","on Destroy")
    }

    private fun placeOrder() {

        userId=auth.currentUser?.uid?:""
        val time=System.currentTimeMillis()
        val itemPushKey=databaseRef.child("OrderDetails").push().key
        val orderDetails=OrderDetails(userId,name,foodItemName,foodItemImage,foodItemPrice,foodItemQuantity,address,totalAmount,phone,false,false,false,itemPushKey,time)


        val orderReference=databaseRef.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails)
            .addOnSuccessListener {

                removeItemFromCart()
                addOderToHistory(orderDetails)

                val bottomSheetDialog=CongratsBottomSheet()
                bottomSheetDialog.show(supportFragmentManager,"TEST")

            }
            .addOnFailureListener {
                Toast.makeText(this,"Failed to Place Order",Toast.LENGTH_LONG).show()
            }

    }

    private fun addOderToHistory(orderDetails: OrderDetails) {
        databaseRef.child("users").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails)
    }

    private fun removeItemFromCart() {
        val cartItemsReference=databaseRef.child("users").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }

    private fun calculateTotalAmount(): Int  {
        var totalAmount :Int =0;
        for(i in 0 until foodItemPrice.size){
            var price=foodItemPrice[i]
            val lastChar=price.last()
            val priceIntVal=if(lastChar=='$' ){
                price.dropLast(1).toInt()
            }
            else{
                price.toInt()
            }
            var quantity=foodItemQuantity[i]
            totalAmount += priceIntVal * quantity
        }
        
        return totalAmount
    }

    private fun setUserData() {
        val user=auth.currentUser
        if(user!=null){
            userId=user.uid
            val userReference = databaseRef.child("users").child(userId).child("profile")
            userReference.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        name = snapshot.child("name").getValue(String::class.java) ?: ""
                        address = snapshot.child("address").getValue(String::class.java) ?: ""
                        phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                        binding.apply {
                            payOutName.setText(name)
                            payOutAddress.setText(address)
                            payOutPhone.setText(phone)

                        }
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }
    }
}