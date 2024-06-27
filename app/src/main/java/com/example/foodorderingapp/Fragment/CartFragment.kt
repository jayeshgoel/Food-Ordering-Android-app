package com.example.foodorderingapp.Fragment

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Adapter.CartAdapter
import com.example.foodorderingapp.Model.CartItem
import com.example.foodorderingapp.PayOutActivity
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding

    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var valueEventListener:ValueEventListener
    private lateinit var foodRef:DatabaseReference


    private var names:MutableList<String> = mutableListOf()
    private var images:MutableList<String> = mutableListOf()
    private var prices:MutableList<String> = mutableListOf()
    private var description : MutableList<String> = mutableListOf()
    private var quantity : MutableList<Int> = mutableListOf()

    private lateinit var adapter:CartAdapter
    private lateinit var userId:String

    private var inProgress=false
    val isCartEmpty:Boolean
        get()=names.size==0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding=FragmentCartBinding.inflate(inflater,container,false)

        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()




        setAdapter()
        retrieveCartItems()





        binding.proceed.setOnClickListener{
            // get Order Item Details before processing to check out
            getItemOrderDetails()

        }



        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onPause() {
        super.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        adapter.updateQuantityInDatabase()
        foodRef.removeEventListener(valueEventListener)
    }
    private fun updateProgressView(){
        if(inProgress){
            binding.apply {
                cartRecyclerView.visibility=View.INVISIBLE
                isCartEmpty.visibility=View.INVISIBLE
                proceed.visibility=View.INVISIBLE
                progressBar3.visibility=View.VISIBLE
            }
        }
        else{
            binding.apply {

                progressBar3.visibility=View.INVISIBLE
                if(names.isNotEmpty()){
                    cartRecyclerView.visibility=View.VISIBLE
                    proceed.visibility=View.VISIBLE
                }
                else{
                    isCartEmpty.visibility=View.VISIBLE
                }
            }
        }
    }

    private fun getItemOrderDetails() {
        val orderIdReference:DatabaseReference=database.reference.child("users").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice= mutableListOf<String>()
        val foodDescription= mutableListOf<String>()
        val foodImage= mutableListOf<String>()
        val foodQuantity = adapter.getUpdatedItemQuantities()


        orderIdReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapShot in snapshot.children){
                    // get cart Item
                    val orderItem = foodSnapShot.getValue(CartItem::class.java)
                    if(orderItem!=null) {

                        orderItem.foodName?.let { foodName.add(it) }
                        orderItem.foodPrice?.let { foodPrice.add(it) }
                        orderItem.foodDescription?.let { foodDescription.add(it) }
                        orderItem.foodImage?.let { foodImage.add(it) }
                    }
                }
                if(foodName.isNotEmpty()) {
                    orderNow(foodName, foodPrice, foodDescription, foodImage, foodQuantity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Order failed", Toast.LENGTH_LONG).show()

            }
        })


    }

    private fun orderNow(foodName: MutableList<String>, foodPrice: MutableList<String>, foodDescription: MutableList<String>, foodImage: MutableList<String>, foodQuantity: MutableList<Int>) {
        if(isAdded && context!=null){
            val intent=Intent(requireContext(),PayOutActivity::class.java)
            intent.putExtra("FoodItemNames", foodName as ArrayList<String>)
            intent.putExtra("FoodItemPrices", foodPrice as ArrayList<String>)
            intent.putExtra("FoodItemImages", foodImage as ArrayList<String>)
            intent.putExtra("FoodItemDescription", foodDescription as ArrayList<String>)
            intent.putExtra("FoodItemQuantity", foodQuantity as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        // database reference to the firebase

        userId=auth.currentUser?.uid?:""
        foodRef=database.reference.child("users").child(userId).child("CartItems")

        valueEventListener=object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                inProgress=true
                updateProgressView()

                names.clear()
                prices.clear()
                description.clear()
                images.clear()
                quantity.clear()

                for(foodSnapShot in snapshot.children){
                    val cartItem=foodSnapShot.getValue(CartItem::class.java)
                    if(cartItem!=null) {
                        Log.d("CART ITEM " ,cartItem.foodName.toString())
                        cartItem?.foodName?.let { names.add(it) }
                        cartItem?.foodPrice?.let { prices.add(it) }
                        cartItem?.foodDescription?.let { description.add(it) }
                        cartItem?.foodQuantity?.let { quantity.add(it) }
                        cartItem?.foodImage?.let { images.add(it) }
                    }
                }

                adapter.notifyDataSetChanged()

                inProgress=false
                updateProgressView()


                Toast.makeText(requireContext(),"Data fetched",Toast.LENGTH_LONG).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Data cannot be fetched",Toast.LENGTH_LONG).show()
            }
        }
        foodRef.addValueEventListener(valueEventListener)

    }

    private fun setAdapter() {
        adapter= CartAdapter(requireContext().applicationContext,names,prices,images,description,quantity)
        binding.cartRecyclerView.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.cartRecyclerView.adapter=adapter

    }

    companion object {

    }
}