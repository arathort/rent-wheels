package com.arathort.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class FirebaseDataSource(
    private val databaseUrl: String = "https://drivetracker-ecf96-default-rtdb.europe-west1.firebasedatabase.app/"
) {
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)

    fun ref(path: String): DatabaseReference = db.getReference(path)

    suspend fun <T> fetchList(ref: DatabaseReference, clazz: Class<T>): List<T> =
        suspendCoroutine { cont ->
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = mutableListOf<T>()
                    for (child in snapshot.children) {
                        child.getValue(clazz)?.let(result::add)
                    }
                    cont.resume(result)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(RuntimeException(error.message))
                }
            })
        }
}