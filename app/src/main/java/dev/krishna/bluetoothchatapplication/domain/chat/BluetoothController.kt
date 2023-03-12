package dev.krishna.bluetoothchatapplication.domain.chat

import kotlinx.coroutines.flow.StateFlow

interface BluetoothController  {

    val scannedDevice: StateFlow<List<BluetoothDevice>>
    val pairedDevice: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun releaseDevices()
}