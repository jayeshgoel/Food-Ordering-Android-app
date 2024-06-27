package com.example.foodorderingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Adapter.NotificationAdapter
import com.example.foodorderingapp.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Notification_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding:FragmentNotificationBottomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentNotificationBottomBinding.inflate(inflater,container,false)

        val notificationList= listOf("Your Order has been cancelled Successfully","Order has been taken by the driver","Congrats Your Order Placed")
        val notificationImageList= listOf(R.drawable.notification1,R.drawable.notification2,R.drawable.notification3)

        binding.NotificationsRecyclerView.adapter=NotificationAdapter(ArrayList(notificationList),
            ArrayList(notificationImageList)
        )
        binding.NotificationsRecyclerView.layoutManager=LinearLayoutManager(requireContext())

        return binding.root
    }

    companion object {

    }
}