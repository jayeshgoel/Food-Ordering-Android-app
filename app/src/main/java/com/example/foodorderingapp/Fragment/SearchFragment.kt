package com.example.foodorderingapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Adapter.MenuAdapter
import com.example.foodorderingapp.Model.menuItem
import com.example.foodorderingapp.databinding.FragmentSearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {
    private lateinit var binding:FragmentSearchBinding
    private lateinit var adapter:MenuAdapter
    private lateinit var database:FirebaseDatabase
    private val originalMenuItems= mutableListOf<menuItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSearchBinding.inflate(inflater,container,false)
        // retrieve Menu Items
        retrieveMenuItem()

        // set up search view
        setUpSearchView()
        

        return binding.root
    }

    private fun retrieveMenuItem() {
        database=FirebaseDatabase.getInstance()
        val foodRef=database.reference.child("menu")
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val menuItem=foodSnapshot.getValue(menuItem::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showAllMenu() {
        val filteredMenuItem=ArrayList(originalMenuItems)
        setAdapter(filteredMenuItem)
    }

    private fun setAdapter(filteredMenuItem: List<menuItem>) {
        adapter=MenuAdapter(filteredMenuItem,requireContext())
        binding.searchRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.searchRecyclerView.adapter=adapter
    }


    private fun setUpSearchView() {

        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItem(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItem(newText)
                return true
            }
        })
    }

    private fun filterMenuItem(query: String) {
        val filteredMenuItems=originalMenuItems.filter {
            it.foodName?.contains(query,ignoreCase = true)==true
                    || it.foodPrice?.contains(query,ignoreCase = true)==true
        }
        setAdapter(filteredMenuItems)
    }

    companion object {

    }
}