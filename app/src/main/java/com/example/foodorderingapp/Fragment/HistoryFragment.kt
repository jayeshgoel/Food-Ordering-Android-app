package com.example.foodorderingapp.Fragment

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodorderingapp.Adapter.BuyAgainAdapter
import com.example.foodorderingapp.Model.OrderDetails
import com.example.foodorderingapp.R
import com.example.foodorderingapp.RecentOrderItems
import com.example.foodorderingapp.databinding.BuyAgainItemBinding
import com.example.foodorderingapp.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment(),BuyAgainAdapter.onItemClickListener {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private lateinit var userId:String
    private var listOfOrderItem:ArrayList<OrderDetails> = arrayListOf()

    private lateinit var binding:FragmentHistoryBinding
    private lateinit var buyAgainAdapter:BuyAgainAdapter
    private var inProgress=false

    private lateinit var valueEventListener:ValueEventListener
    private lateinit var shortingQuery:Query


    private var buyAgainFoodName= mutableListOf<String>()
    private var buyAgainFoodPrice= mutableListOf<String>()
    private var buyAgainFoodImage= mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHistoryBinding.inflate(layoutInflater,container,false)

        //Retrieve and di splay the User Order History
        auth= FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

        setPreviousBuyItemsInRecyclerView()
        retrieveBuyHistory()


        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        shortingQuery.removeEventListener(valueEventListener)

    }
    private fun updateProgressView(){
        if(inProgress){
            binding.buyAgainRecyclerView.visibility=View.INVISIBLE
            binding.progressBar2.visibility=View.VISIBLE
        }else{
            binding.progressBar2.visibility=View.INVISIBLE
            binding.buyAgainRecyclerView.visibility=View.VISIBLE
        }
    }

    private fun retrieveBuyHistory() {


        userId=auth.currentUser?.uid?:""
        val buyItemReference:DatabaseReference=database.reference.child("users").child(userId).child("BuyHistory")
        shortingQuery=buyItemReference.orderByChild("currentTime")
        valueEventListener=(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                inProgress=true
                updateProgressView()

                listOfOrderItem.clear()
                buyAgainFoodName.clear()
                buyAgainFoodPrice.clear()
                buyAgainFoodImage.clear()

                for(buySnapShot in snapshot.children){
                    val buyHistoryItem=buySnapShot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let{
                        listOfOrderItem.add(it)
                        Log.d("STATUS",it.paymentReceived.toString().plus(it.orderAccepted).plus(it.orderDispatched))
                    }
                }
                listOfOrderItem.reverse()
                for(i in 0 until  listOfOrderItem.size){
                    listOfOrderItem[i].foodName?.firstOrNull()?.let { buyAgainFoodName.add(it) }
                    listOfOrderItem[i].foodPrices?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
                    listOfOrderItem[i].foodImages?.firstOrNull()?.let { buyAgainFoodImage.add(it) }
                }
                buyAgainAdapter.notifyDataSetChanged()

                inProgress=false
                updateProgressView()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Failure to fetch data",Toast.LENGTH_LONG).show()
            }
        })
        shortingQuery.addValueEventListener(valueEventListener)

    }



    private fun setPreviousBuyItemsInRecyclerView() {
        buyAgainAdapter=BuyAgainAdapter(buyAgainFoodName,buyAgainFoodPrice,buyAgainFoodImage,requireContext(),this)
        binding.buyAgainRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.buyAgainRecyclerView.adapter=buyAgainAdapter

    }

    companion object {

    }

    override fun onClicked(position: Int) {
        val intent = Intent(requireContext(), RecentOrderItems::class.java)

        intent.putExtra("RecentBuyOrderItem", listOfOrderItem)
        intent.putExtra("position", position)
        startActivity(intent)
    }
}


