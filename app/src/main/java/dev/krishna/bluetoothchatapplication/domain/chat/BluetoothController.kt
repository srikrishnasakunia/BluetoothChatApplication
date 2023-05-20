package dev.krishna.bluetoothchatapplication.domain.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController  {

    val scannedDevice: StateFlow<List<BluetoothDevice>>
    val pairedDevice: StateFlow<List<BluetoothDevice>>
    val isConnected:StateFlow<Boolean>
    val errors:SharedFlow<String>

    fun startDiscovery()
    fun stopDiscovery()

    fun releaseDevices()


    /**This function will start the Server and initiate the connection dialogue**/
    fun startBluetoothServer(): Flow<ConnectionResult>

    /**This function deals with the request in the other device to accept and connect to the common
     *  server for exchanging data.**/
    fun connectToDevice(device: BluetoothDevice):Flow<ConnectionResult>

    /**This function deals with the termination request to terminate the connection. **/
    fun closeTheConnectedConnection()
}