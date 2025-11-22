package com.arathort.data.repositories

import com.arathort.data.FirebaseDataSource
import com.arathort.data.records.CarRentalRecord
import com.arathort.data.records.TruckRentalRecord
import com.arathort.data.rent.RentalRecord
import com.arathort.data.user.User

class RentalRepository(
    private val firebase: FirebaseDataSource
) {
    fun addRental(record: RentalRecord): String {
        val ref = when (record) {
            is CarRentalRecord -> firebase.ref("RentalRecords/Cars")
            is TruckRentalRecord -> firebase.ref("RentalRecords/Trucks")
            else -> throw IllegalArgumentException("Unsupported record type")
        }
        val id = ref.push().key ?: throw RuntimeException("No ID generated")
        record.id = id
        ref.child(id).setValue(record)
        return id
    }

    suspend fun getRentals(type: String): List<RentalRecord> {
        return when (type) {
            "Cars" -> firebase.fetchList(firebase.ref("RentalRecords/Cars"), CarRentalRecord::class.java)
            "Trucks" -> firebase.fetchList(firebase.ref("RentalRecords/Trucks"), TruckRentalRecord::class.java)
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    suspend fun getRentalsByUser(user: User, type: String): List<RentalRecord> {
        val all = getRentals(type)
        return all.filter { it.renter.email == user.email && it.isActive }
    }

    fun markAsPassive(record: RentalRecord) {
        record.setPassive()
        val path = when (record) {
            is CarRentalRecord -> "RentalRecords/Cars/${record.id}"
            is TruckRentalRecord -> "RentalRecords/Trucks/${record.id}"
            else -> throw IllegalArgumentException("Unsupported type")
        }
        firebase.ref(path).setValue(record)
    }

    fun updateRentalRecord(record: RentalRecord) {
        if (record.id.isEmpty()) throw IllegalStateException("Record has no ID")
        val path = when (record) {
            is CarRentalRecord -> "RentalRecords/Cars/${record.id}"
            is TruckRentalRecord -> "RentalRecords/Trucks/${record.id}"
            else -> throw IllegalArgumentException("Unsupported type")
        }
        firebase.ref(path).setValue(record)
    }
}