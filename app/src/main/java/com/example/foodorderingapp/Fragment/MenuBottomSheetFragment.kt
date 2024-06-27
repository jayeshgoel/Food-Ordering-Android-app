package com.example.foodorderingapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Adapter.MenuAdapter
import com.example.foodorderingapp.Model.menuItem
import com.example.foodorderingapp.databinding.FragmentMenuBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var database:FirebaseDatabase
    private var menuItems:MutableList<menuItem> = mutableListOf()
    lateinit var adapter:MenuAdapter

    lateinit var foodRef: DatabaseReference
    lateinit var valueEventListener: ValueEventListener

    private lateinit var binding:FragmentMenuBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMenuBottomSheetBinding.inflate(inflater,container,false)

        setAdapter()
        // retrieving menu items from database
        retrieveMenuItems()
        return binding.root
    }

    private fun retrieveMenuItems() {
        database=FirebaseDatabase.getInstance()
        foodRef=database.reference.child("menu")




        valueEventListener=object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                for(foodSnapshot in snapshot.children){
                    val  menuItem=foodSnapshot.getValue(menuItem::class.java)
                   menuItem?.let {
                       menuItems.add(it)
                   }
                }

                adapter.notifyDataSetChanged()
                // once data is received set it through adapter

            }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        foodRef.addValueEventListener(valueEventListener)

    }
    private fun setAdapter() {
        adapter=MenuAdapter(menuItems,requireContext())
        binding.menuRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter=adapter

    }

    override fun onDestroy() {
        super.onDestroy()
        foodRef.removeEventListener(valueEventListener)
    }



    companion object {

    }
}