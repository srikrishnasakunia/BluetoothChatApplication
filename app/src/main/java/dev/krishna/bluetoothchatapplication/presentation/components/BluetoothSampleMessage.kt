package dev.krishna.bluetoothchatapplication.presentation.components

import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothMessage

object BluetoothSampleMessage {
    val messages = listOf<BluetoothMessage>(getMessage(), getSecondMessage(), getMessage(), getSecondMessage())

    fun getMessage(): BluetoothMessage{
        return BluetoothMessage(
            "Hello There",
            "Krishna",
            true,
            "1685902490408"
        )
    }

    fun getSecondMessage(): BluetoothMessage{
        return BluetoothMessage(
            "Hi",
            "lavanya",
            false,
            "1685902490408"
        )
    }
}