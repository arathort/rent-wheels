package com.example.drivetracker.ui.commenting

import androidx.lifecycle.ViewModel
import com.example.drivetracker.data.VehicleRepository
import com.example.drivetracker.data.comments.Comment
import com.example.drivetracker.data.items.CarItem
import com.example.drivetracker.data.items.TruckItem
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.properties.Delegates

class CommentScreenViewModel @Inject constructor(
    private val auth:FirebaseAuth,
    private val vehicleRepository: VehicleRepository
): ViewModel() {
    private lateinit var car:CarItem
    private lateinit var truck: TruckItem
    private var _isCar by Delegates.notNull<Boolean>()
    fun setCar(carItem: CarItem){
        car = carItem
        _isCar = true
    }

    fun setTruck(truckItem: TruckItem){
        truck = truckItem
        _isCar = false
    }

    fun getCar():CarItem{
        return car
    }
    fun getEmail():String{
        return auth.currentUser?.email.toString()
    }

    fun updateCarWithComment(comment: Comment){
        vehicleRepository.updateItemWithComment(car,comment)
    }

    fun updateTruckWithComment(comment: Comment){
        vehicleRepository.updateItemWithComment(truck, comment)
    }

    fun isCar():Boolean{
        return _isCar
    }
}