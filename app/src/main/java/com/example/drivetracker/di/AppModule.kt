package com.example.drivetracker.di

import com.arathort.data.FirebaseDataSource
import com.arathort.data.repositories.PaymentRepository
import com.arathort.data.repositories.RentalRepository
import com.arathort.data.repositories.UserRepository
import com.arathort.data.repositories.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDataSource(): FirebaseDataSource = FirebaseDataSource()

    @Provides
    @Singleton
    fun provideVehicleRepository(firebase: FirebaseDataSource): VehicleRepository =
        VehicleRepository(firebase)

    @Provides
    @Singleton
    fun provideRentalRepository(firebase: FirebaseDataSource): RentalRepository =
        RentalRepository(firebase)

    @Provides
    @Singleton
    fun provideUserRepository(firebase: FirebaseDataSource): UserRepository =
        UserRepository(firebase)

    @Provides
    @Singleton
    fun providePaymentRepository(firebase: FirebaseDataSource): PaymentRepository =
        PaymentRepository(firebase)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

}
