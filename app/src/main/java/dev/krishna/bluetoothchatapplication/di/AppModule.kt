package dev.krishna.bluetoothchatapplication.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.krishna.bluetoothchatapplication.data.chat.AndroidBluetoothController
import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothController
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context:Context): BluetoothController{
        return AndroidBluetoothController(context)
    }
}