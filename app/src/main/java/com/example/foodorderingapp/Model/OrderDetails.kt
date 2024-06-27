package com.example.foodorderingapp.Model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import kotlin.collections.ArrayList

class OrderDetails() : Serializable {
    var userId: String? = null
    var userName: String? = null
    var foodName: MutableList<String>? = null
    var foodImages: MutableList<String>? = null
    var foodPrices: MutableList<String>? = null
    var foodQuantities: MutableList<Int>? = null
    var address: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var orderAccepted: Boolean = false
    var paymentReceived: Boolean = false
    var orderDispatched: Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

    constructor(
        userId: String,
        userName: String,
        foodName: ArrayList<String>,
        foodImages: ArrayList<String>,
        foodPrices: ArrayList<String>,
        foodQuantities: ArrayList<Int>,
        address: String,
        totalPrice: String,
        phoneNumber: String,
        orderAccepted: Boolean,
        paymentReceived: Boolean,
        orderDispatched:Boolean,
        itemPushKey: String?,
        currentTime: Long
    ) : this() {
        this.userId = userId
        this.userName = userName
        this.foodName = foodName
        this.foodImages = foodImages
        this.foodPrices = foodPrices
        this.foodQuantities = foodQuantities
        this.address = address
        this.totalPrice = totalPrice
        this.phoneNumber = phoneNumber
        this.orderAccepted = orderAccepted
        this.paymentReceived = paymentReceived
        this.orderDispatched=orderDispatched
        this.itemPushKey = itemPushKey
        this.currentTime = currentTime
    }
}
