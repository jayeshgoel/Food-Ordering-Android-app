package com.example.foodorderingapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodorderingapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {


    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var googleSignInClient:GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialization of Firebase Auth
        auth = Firebase.auth
        // Initialization of Firebase database
        database=Firebase.database.reference



        binding.loginButton.setOnClickListener{
            email=binding.loginEmail.text.toString().trim()
            password=binding.loginPassword.text.toString().trim()
            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Fill all the Details",Toast.LENGTH_LONG).show()
            }
            else{
                loginUser(email,password)
            }

        }

        // SIGN IN WITH GOOGLE
        val googleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build()
        googleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions)
        binding.loginWithGoogle.setOnClickListener {
            val singInIntent=googleSignInClient.signInIntent
            launcher.launch(singInIntent)
        }



        binding.dontHaveAccountButton.setOnClickListener{
            val intent=Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode== Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account:GoogleSignInAccount ?= task.result
                val credential= GoogleAuthProvider.getCredential(account?.idToken,null)
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

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
            if(task.isSuccessful){
                val user=auth.currentUser
                Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show()
                updateUI(user)
            }
            else{
                Toast.makeText(this,"Authentication Failed",Toast.LENGTH_LONG).show()
                Log.d("ACCOUNT","Login Failed",task.exception)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if(currentUser!=null){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}