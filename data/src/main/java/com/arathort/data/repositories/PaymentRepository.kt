package com.arathort.data.repositories

import com.arathort.data.FirebaseDataSource
import com.arathort.data.Payment

class PaymentRepository(
    private val firebase: FirebaseDataSource
) {
    fun addPayment(payment: Payment): String {
        val ref = firebase.ref("Payments")
        val id = ref.push().key ?: throw RuntimeException("No ID generated")
        payment.id = id
        ref.child(id).setValue(payment)
        return id
    }
}