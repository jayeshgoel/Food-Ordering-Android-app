 package com.example.foodorderingapp

import android.app.Activity
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Adapter.RecentBuyAdapter
import com.example.foodorderingapp.Model.OrderDetails
import com.example.foodorderingapp.databinding.ActivityRecentOrderItemsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

 class RecentOrderItems : AppCompatActivity(),PaymentResultListener {

    private val binding:ActivityRecentOrderItemsBinding by lazy{
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }

    private lateinit var allFoodName:ArrayList<String>
    private lateinit var allImages:ArrayList<String>
    private lateinit var allFoodPrices:ArrayList<String>
    private lateinit var allFoodQuantities:ArrayList<Int>
    private lateinit var recenetOrderItems:ArrayList<OrderDetails>
    private var postion=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.backBtn.setOnClickListener{
            finish()
        }

        recenetOrderItems=intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>


        postion=intent.getIntExtra("position",0)
        recenetOrderItems?.let {orderDetails->
            if(orderDetails.isNotEmpty()){
                val recentOrderItem=orderDetails[postion]

                allFoodName=recentOrderItem.foodName as ArrayList<String>
                allImages=recentOrderItem.foodImages as ArrayList<String>
                allFoodPrices=recentOrderItem.foodPrices as ArrayList<String>
                allFoodQuantities=recentOrderItem.foodQuantities as ArrayList<Int>
                setOrderDetails(postion)
                setPaymentDetails(postion)
                setAdapter()
            }

        }
    }

     private fun setOrderDetails(position:Int) {
         binding.totalAmountOfOrder.text=recenetOrderItems[position].totalPrice
         if(recenetOrderItems[position].orderAccepted && !recenetOrderItems[position].orderDispatched){
             binding.orderAcceptanceStatus.text="Order Accepted by Restaurant"
         }
         else if(recenetOrderItems[position].orderAccepted && recenetOrderItems[position].orderDispatched){
             binding.orderAcceptanceStatus.text="Out for Delivery"
         }
         binding.payNowBtn.setOnClickListener{
             // razor pay portal
             openPaymentPortal(position)


         }

     }

      fun openPaymentPortal(position: Int) {
         val activity: Activity = this
         val co = Checkout ()
         co.setKeyID("rzp_test_7cvQIYDzzx1hsk")

         try {
             val options = JSONObject()
             options.put("name","Razorpay Corp")
             options.put("description","Food Order")
             //You can omit the image option to fetch the image from the dashboard
             options.put("image","http://example.com/image/rzp.jpg")
             options.put("theme.color", "#3399cc");
             options.put("currency","INR");

             val amount:String= recenetOrderItems[position].totalPrice.toString().removeSuffix("$").plus("00")
             options.put("amount",amount)//pass amount in currency subunits


             val contact: String =recenetOrderItems[position].phoneNumber?:""
             val name=recenetOrderItems[position].userName

             val prefill = JSONObject()
//             prefill.put("email","email")
             prefill.put("contact",contact)

             options.put("prefill",prefill)
             co.open(activity,options)
         }catch (e: Exception){
             Toast.makeText(this,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
             e.printStackTrace()
         }
     }

     private fun setPaymentDetails(position: Int){
         if(recenetOrderItems[position].paymentReceived){
             binding.orderPaymentStatus.text="Payment Received"
             binding.payNowBtn.visibility=View.GONE
         }
         else{
             binding.payNowBtn.visibility= View.VISIBLE
         }
     }
     private fun updatePaymentDetailsInDatabase(position: Int){
         val auth= Firebase.auth
         val userId=auth.currentUser?.uid?:""
         val key=recenetOrderItems[position].itemPushKey?:""
         val databaseRef=FirebaseDatabase.getInstance().reference
         val historyOrderRef=databaseRef.child("users").child(userId).child("BuyHistory").child(key).child("paymentReceived")
         historyOrderRef.setValue(true).addOnSuccessListener {
             Log.d("PAYMENTSTATUS","BUYHISTORY UPDATED")
         }



         val completedOrderRef=databaseRef.child("CompletedOrder")
         completedOrderRef.addListenerForSingleValueEvent(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.hasChild(key)){
                     completedOrderRef.child(key).child("paymentReceived").setValue(true).addOnSuccessListener {
                         Log.d("PAYMENTSTATUS","COMPPLETEORDER UPDATED")
                     }
                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }
         })

         val orderDetailRef=databaseRef.child("OrderDetails")
         orderDetailRef.addListenerForSingleValueEvent(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.hasChild(key)){
                     orderDetailRef.child(key).child("paymentReceived").setValue(true).addOnSuccessListener {
                         Log.d("PAYMENTSTATUS","ORDERDETAILS UPDATED")
                     }
                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }

         })


     }


     private fun setAdapter() {
         binding.recentBuyRecyclerView.layoutManager=LinearLayoutManager(this)
         binding.recentBuyRecyclerView.adapter=RecentBuyAdapter(this,allFoodName,allImages,allFoodPrices,allFoodQuantities)

     }

     override fun onPaymentSuccess(p0: String?) {
         recenetOrderItems[postion].paymentReceived=true
         setPaymentDetails(postion)
         updatePaymentDetailsInDatabase(postion)
         Toast.makeText(this,"Payment Successful", Toast.LENGTH_LONG).show()

     }

     override fun onPaymentError(p0: Int, p1: String?) {
         Log.d("ERROR_PAYMENT","Error : $p1")
         Toast.makeText(this,"Error : $p1",Toast.LENGTH_LONG).show()
     }
 }