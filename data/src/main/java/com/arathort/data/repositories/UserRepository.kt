package com.arathort.data.repositories

import com.arathort.data.FirebaseDataSource
import com.arathort.data.user.User

class UserRepository(
    private val firebase: FirebaseDataSource
) {
    suspend fun getUserByEmail(email: String): User? {
        val users = firebase.fetchList(firebase.ref("Users"), User::class.java)
        return users.find { it.email == email }
    }

    fun addUser(user: User): String {
        val ref = firebase.ref("Users")
        val id = ref.push().key ?: throw RuntimeException("No ID generated")
        user.id = id
        ref.child(id).setValue(user)
        return id
    }
}