package dev.krishna.bluetoothchatapplication.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krishna.bluetoothchatapplication.data.chat.convertToTime
import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothMessage
import dev.krishna.bluetoothchatapplication.ui.theme.BluetoothChatApplicationTheme
import dev.krishna.bluetoothchatapplication.ui.theme.Plum
import dev.krishna.bluetoothchatapplication.ui.theme.Teal500
import dev.krishna.bluetoothchatapplication.ui.theme.Vanilla

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (message.isFromLocalSender) 15.dp else 0.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = if (message.isFromLocalSender) 0.dp else 15.dp
                )
            )
            .background(
                if (message.isFromLocalSender) Teal500 else Plum
            )
            .padding(16.dp)
    ) {
        Text(
            text = message.senderName,
            fontSize = 10.sp,
            color = Color.Black,
        )
        Text(
            text = message.message,
            color = Color.Black,
            modifier = Modifier.widthIn(max = 250.dp)
        )
        Text(
            text = convertToTime(message.timestamp),
            color = Color.White,
            fontSize = 8.sp,
            modifier = if (message.isFromLocalSender) modifier.align(Alignment.End) else modifier.align(Alignment.Start)
        )
    }
}

@Preview
@Composable
fun ChatMessagePreview() {
    BluetoothChatApplicationTheme {
        ChatMessage(message = BluetoothMessage(
            message = "Hello World",
            senderName = "Android 12",
            isFromLocalSender = false,
            timestamp = "1685902490408"
        ))
    }
}