package com.arathort.data

import com.arathort.common.comments.Comment
import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem
import com.arathort.data.items.VehicleItem
import com.arathort.data.records.CarRentalRecord
import com.arathort.data.records.TruckRentalRecord
import com.arathort.data.rent.RentalRecord
import com.arathort.data.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class VehicleRepository(
    private val firebase: FirebaseDatabase = FirebaseDatabase.getInstance("https://drivetracker-ecf96-default-rtdb.europe-west1.firebasedatabase.app/")
) {
    private var selectedVehicle: VehicleItem? = null

    fun setSelectedVehicle(vehicle: VehicleItem) {
        selectedVehicle = vehicle
    }

    fun getSelectedVehicle(): VehicleItem? = selectedVehicle

    private fun getItemRef(type: String): DatabaseReference =
        firebase.getReference("Vehicles/$type")

    suspend fun getVehicleById(id: String): VehicleItem? {
        val cars = listenForValue(getItemRef("Cars"), CarItem::class.java)
        val trucks = listenForValue(getItemRef("Trucks"), TruckItem::class.java)
        return (cars + trucks).firstOrNull { it.id == id }
    }


    private fun getRecordRef(type: String): DatabaseReference =
        firebase.getReference("RentalRecords/$type")

    private val usersRef: DatabaseReference = firebase.getReference("Users")
    private val paymentsRef: DatabaseReference = firebase.getReference("Payments")

    private suspend fun <T> listenForValue(ref: DatabaseReference, clazz: Class<T>): List<T> =
        suspendCoroutine { cont ->
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<T>()
                    for (child in snapshot.children) {
                        child.getValue(clazz)?.let { list.add(it) }
                    }
                    cont.resume(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(RuntimeException("DB error: ${error.message}"))
                }
            })
        }

    suspend fun getVehicleItems(type: String): List<VehicleItem> {
        return when (type) {
            "Cars" -> listenForValue(getItemRef(type), CarItem::class.java)
            "Trucks" -> listenForValue(getItemRef(type), TruckItem::class.java)
            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    fun addVehicleItem(item: VehicleItem): String {
        val ref = when (item) {
            is CarItem -> getItemRef("Cars")
            is TruckItem -> getItemRef("Trucks")
            else -> throw IllegalArgumentException("Unsupported item type")
        }
        val id = ref.push().key ?: throw RuntimeException("Failed to generate ID")
        item.id = id
        ref.child(id).setValue(item)
        return id
    }

    fun updateVehicleItem(item: VehicleItem) {
        if (item.id.isEmpty()) throw IllegalStateException("Item has no ID")
        val ref = when (item) {
            is CarItem -> getItemRef("Cars").child(item.id)
            is TruckItem -> getItemRef("Trucks").child(item.id)
            else -> throw IllegalArgumentException("Unsupported item type")
        }
        ref.setValue(item)
    }

    fun deleteVehicleItem(item: VehicleItem) {
        if (item.id.isEmpty()) throw IllegalStateException("Item has no ID")
        val ref = when (item) {
            is CarItem -> getItemRef("Cars").child(item.id)
            is TruckItem -> getItemRef("Trucks").child(item.id)
            else -> throw IllegalArgumentException("Unsupported item type")
        }
        ref.removeValue()
    }

    fun addRentalRecord(record: RentalRecord): String {
        val ref = when (record) {
            is CarRentalRecord -> getRecordRef("Cars")
            is TruckRentalRecord -> getRecordRef("Trucks")
            else -> throw IllegalArgumentException("Unsupported record type")
        }
        val id = ref.push().key ?: throw RuntimeException("Failed to generate ID")
        record.id = id
        ref.child(id).setValue(record)
        return id
    }

    fun updateRentalRecord(record: RentalRecord) {
        if (record.id.isEmpty()) throw IllegalStateException("Record has no ID")
        val ref = when (record) {
            is CarRentalRecord -> getRecordRef("Cars").child(record.id)
            is TruckRentalRecord -> getRecordRef("Trucks").child(record.id)
            else -> throw IllegalArgumentException("Unsupported record type")
        }
        ref.setValue(record)
    }

    fun deleteRentalRecord(record: RentalRecord) {
        if (record.id.isEmpty()) throw IllegalStateException("Record has no ID")
        val ref = when (record) {
            is CarRentalRecord -> getRecordRef("Cars").child(record.id)
            is TruckRentalRecord -> getRecordRef("Trucks").child(record.id)
            else -> throw IllegalArgumentException("Unsupported record type")
        }
        ref.removeValue()
    }

    suspend fun getRentalRecords(type: String): List<RentalRecord> {
        return when (type) {
            "Cars" -> listenForValue(getRecordRef(type), CarRentalRecord::class.java)
            "Trucks" -> listenForValue(getRecordRef(type), TruckRentalRecord::class.java)
            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    suspend fun getRentalRecordsByUser(user: User, type: String): List<RentalRecord> {
        val all = getRentalRecords(type)
        return all.filter { it.renter.email == user.email && it.isActive }
    }

    fun addUser(user: User) {
        val id = usersRef.push().key ?: throw RuntimeException("Failed to generate ID")
        user.id = id
        usersRef.child(id).setValue(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        val users = listenForValue(usersRef, User::class.java)
        return users.find { it.email == email }
    }

    fun addPayment(payment: Payment): String {
        val id = paymentsRef.push().key ?: throw RuntimeException("Failed to generate ID")
        payment.id = id
        paymentsRef.child(id).setValue(payment)
        return id
    }

    fun updateItemWithComment(item: VehicleItem, comment: Comment) {
        item.addComment(comment)
        updateVehicleItem(item)
    }

    fun updateRecordToPassive(record: RentalRecord) {
        record.setPassive()
        updateRentalRecord(record)
    }

}