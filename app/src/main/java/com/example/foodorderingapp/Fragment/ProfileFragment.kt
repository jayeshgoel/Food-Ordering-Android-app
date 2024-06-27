package com.example.foodorderingapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodorderingapp.LoginActivity
import com.example.foodorderingapp.Model.UserModel
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {

    private val binding:FragmentProfileBinding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    private val auth=FirebaseAuth.getInstance()
    private val database=FirebaseDatabase.getInstance()
    private lateinit var userReference:DatabaseReference
    private lateinit var valueEventListener:ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        setUserData()

        // saving profile
        binding.profileSaveButton.setOnClickListener {
            val name=binding.profileName.text.toString()
            val email=binding.profileEmail.text.toString()
            val address=binding.profileAddress.text.toString()
            val phoneNumber=binding.profilePhoneNumber.text.toString()
            
            updateUserData(name,email,address,phoneNumber)
        }

        // log out
        binding.logOutButton.setOnClickListener {
            auth.signOut()
            val intent=Intent(context,LoginActivity::class.java)
            activity?.finish()
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        userReference.removeEventListener(valueEventListener)
    }
    private fun updateUserData(name: String, email: String, address: String, phoneNumber: String) {
        val userId=auth.currentUser?.uid
        if(userId!=null){
            val userReference=database.getReference("users").child(userId).child("profile")
            val userData= hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phoneNumber
            )
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(),"Profile Update Succesfully",Toast.LENGTH_LONG).show()
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Profile Update Failure",Toast.LENGTH_LONG).show()
                }

        }

    }

    private fun setUserData() {
        val userId=auth.currentUser?.uid
        if(userId!=null){
            userReference=database.getReference("users").child(userId).child("profile")
            valueEventListener=object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userProfile=snapshot.getValue(UserModel::class.java)
                        if(userProfile!=null) {
                            binding.profileName.setText(userProfile.name)
                            binding.profileAddress.setText(userProfile.address)
                            binding.profileEmail.setText(userProfile.email)
                            binding.profilePhoneNumber.setText(userProfile.phone)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            }
            userReference.addValueEventListener(valueEventListener)
        }
    }

    companion object {

    }
}