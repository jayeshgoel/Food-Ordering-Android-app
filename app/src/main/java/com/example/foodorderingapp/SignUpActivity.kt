package com.example.foodorderingapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodorderingapp.Model.UserModel

import com.example.foodorderingapp.databinding.ActivitySignupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {


    private lateinit var email:String
    private lateinit var password:String
    private lateinit var userName:String

    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var googleSignInClient:GoogleSignInClient


    private val binding:ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.alreadyhaveaccountButton.setOnClickListener{
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Initialize Firebase Auth
        auth=Firebase.auth

        // Initialize Firebase Database
        database=Firebase.database.reference



        binding.createAccountButton.setOnClickListener {
            userName=binding.createAccountUserName.text.toString().trim()
            email=binding.createAccountEmail.text.toString().trim()
            password=binding.createAccountPassword.text.toString().trim()

            if(userName.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all details",Toast.LENGTH_LONG).show()
            }
            else{
                createAccount(email,password)
            }

        }


        // Google Sign in Set up
        val googleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build()
        googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions)

        binding.createeAccountWithGoogle.setOnClickListener {
            val signIntent=googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }



    }

    // launcher for google sign in
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode== Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account:GoogleSignInAccount ?= task.result
                val credential=GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Sign in Successful",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Sign in Failed",Toast.LENGTH_LONG).show()
                    }

                }
            }

        }
        else{
            Toast.makeText(this,"Sign in Failed",Toast.LENGTH_LONG).show()
        }

    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_LONG).show()
                saveUserData()
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_LONG).show()
                Log.d("ACCOUNT","Create Account : FAILURE " , task.exception)
            }
        }
    }

    private fun saveUserData() {
        userName=binding.createAccountUserName.text.toString().trim()
        email=binding.createAccountEmail.text.toString().trim()
        password=binding.createAccountPassword.text.toString().trim()
        val user=UserModel(userName,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid
        val userData= hashMapOf(
            "name" to userName,
            "address" to "",
            "email" to email,
            "phone" to ""
        )

        database.child("users").child(userId).child("registration").setValue(user)
        database.child("users").child(userId).child("profile").setValue(userData)
    }
}