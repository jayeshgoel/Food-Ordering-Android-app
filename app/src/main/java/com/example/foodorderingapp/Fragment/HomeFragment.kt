package com.example.foodorderingapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodorderingapp.Adapter.MenuAdapter
import com.example.foodorderingapp.Model.menuItem
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.coroutineContext

class HomeFragment : Fragment() {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var database:FirebaseDatabase
    private var menuItems:MutableList<com.example.foodorderingapp.Model.menuItem> = mutableListOf()
    private var subSetMenuItems: MutableList<menuItem> = mutableListOf()
    private lateinit var adapter:MenuAdapter
    private var inProgress=true
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var foodRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= FragmentHomeBinding.inflate(inflater,container,false)


        // opening bottom sheet fragment
        binding.ViewMenuButton.setOnClickListener{
            val bottomSheetDialog=MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager,"TEST")
        }
        // retrieve Popular Menu Items


        setPopularItemsAdapter()
        retrieveAndDisplayPopularItems()

        return binding.root


    }

    private fun updateProgressBarView(){
        if(inProgress){
//            binding.popularItemsRecyclerView.visibility=View.INVISIBLE
            binding.progressBar.visibility=View.VISIBLE
        }
        else{
            binding.progressBar.visibility=View.INVISIBLE
            binding.popularItemsRecyclerView.visibility=View.VISIBLE
        }
    }
    private fun retrieveAndDisplayPopularItems() {


        database= FirebaseDatabase.getInstance()
        foodRef=database.reference.child("menu")



        valueEventListener=object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                inProgress=true
                updateProgressBarView()
                menuItems.clear()

                for(foodSnapshot in snapshot.children){
                    val menuItem=foodSnapshot.getValue(menuItem::class.java)
                    menuItem?.let{
                        menuItems.add(it)
                    }
                }
                randomPopularItems()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        foodRef.addValueEventListener(valueEventListener)
    }

    private fun randomPopularItems() {
        // create a shuffle list of menu item
        subSetMenuItems.clear()
        subSetMenuItems.addAll(menuItems.shuffled().take(6))
        adapter.notifyDataSetChanged()

        inProgress=false
        updateProgressBarView()

    }

    private fun setPopularItemsAdapter() {
        adapter=MenuAdapter(subSetMenuItems,requireContext())
        binding.popularItemsRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.popularItemsRecyclerView.adapter=adapter


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageList=ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3,ScaleTypes.FIT))

        val imageslider=binding.imageSlider
        imageslider.setImageList(imageList,ScaleTypes.FIT)

        imageslider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition=imageList[position]
                val itemMessage="Selected item $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_LONG).show()

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        foodRef.removeEventListener(valueEventListener)
    }

    companion object {

    }
}