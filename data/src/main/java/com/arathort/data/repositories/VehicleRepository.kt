package com.arathort.data.repositories

import com.arathort.common.comments.Comment
import com.arathort.data.FirebaseDataSource
import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem
import com.arathort.data.items.VehicleItem

class VehicleRepository(
    private val firebase: FirebaseDataSource
) {
    private var selectedVehicle: VehicleItem? = null

    fun setSelectedVehicle(vehicle: VehicleItem) {
        selectedVehicle = vehicle
    }

    fun getSelectedVehicle(): VehicleItem? = selectedVehicle

    suspend fun getAllVehicles(): List<VehicleItem> {
        val cars = firebase.fetchList(firebase.ref("Vehicles/Cars"), CarItem::class.java)
        val trucks = firebase.fetchList(firebase.ref("Vehicles/Trucks"), TruckItem::class.java)
        return cars + trucks
    }
    suspend fun getVehicleItems(type: String): List<VehicleItem> {
        return when (type) {
            "Cars" -> firebase.fetchList(firebase.ref("Vehicles/Cars"), CarItem::class.java)
            "Trucks" -> firebase.fetchList(firebase.ref("Vehicles/Trucks"), TruckItem::class.java)
            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    suspend fun getVehicleById(id: String): VehicleItem? {
        return getAllVehicles().firstOrNull { it.id == id }
    }

    fun addVehicle(item: VehicleItem): String {
        val ref = when (item) {
            is CarItem -> firebase.ref("Vehicles/Cars")
            is TruckItem -> firebase.ref("Vehicles/Trucks")
            else -> throw IllegalArgumentException("Unsupported type")
        }
        val id = ref.push().key ?: throw RuntimeException("No ID generated")
        item.id = id
        ref.child(id).setValue(item)
        return id
    }

    fun updateVehicle(item: VehicleItem) {
        val ref = when (item) {
            is CarItem -> firebase.ref("Vehicles/Cars/${item.id}")
            is TruckItem -> firebase.ref("Vehicles/Trucks/${item.id}")
            else -> throw IllegalArgumentException("Unsupported type")
        }
        ref.setValue(item)
    }

    fun deleteVehicle(item: VehicleItem) {
        val ref = when (item) {
            is CarItem -> firebase.ref("Vehicles/Cars/${item.id}")
            is TruckItem -> firebase.ref("Vehicles/Trucks/${item.id}")
            else -> throw IllegalArgumentException("Unsupported type")
        }
        ref.removeValue()
    }

    fun updateItemWithComment(item: VehicleItem, comment: Comment) {
        item.addComment(comment)
        updateVehicle(item)
    }
}
