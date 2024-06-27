package com.example.foodorderingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class Splash_Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val auth= Firebase.auth
            if(auth.currentUser!=null){
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent=Intent(this,StartActivity::class.java)
                startActivity(intent)
                finish()
            }

        },1000)
    }
}