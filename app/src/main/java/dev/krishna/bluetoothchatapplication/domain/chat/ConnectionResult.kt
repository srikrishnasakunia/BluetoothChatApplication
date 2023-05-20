package dev.krishna.bluetoothchatapplication.domain.chat

sealed interface ConnectionResult{

    object ConnectionEstablished: ConnectionResult
    data class Error(val message: String): ConnectionResult
}