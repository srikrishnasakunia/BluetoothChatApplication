package dev.krishna.bluetoothchatapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothController
import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothDeviceDomain
import dev.krishna.bluetoothchatapplication.domain.chat.ConnectionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
):ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevice,
        bluetoothController.pairedDevice,
        _state
    ){ scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevice = scannedDevices,
            pairedDevice = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),_state.value)

    var deviceConnectionJob: Job? = null

    init {
        bluetoothController.isConnected.onEach { _isConnected ->
            _state.update { it.copy(isConnected = _isConnected) }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { _errors ->
            _state.update { it.copy(errorMessage = _errors) }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(device: BluetoothDeviceDomain){
       _state.update { it.copy(isConnecting = true) }
       deviceConnectionJob = bluetoothController
           .connectToDevice(device)
           .listen()
    }

    fun disconnectFromConnectedDevice(){
        deviceConnectionJob?.cancel()
        bluetoothController.closeTheConnectedConnection()
        _state.update { it.copy(
            isConnecting = false,
            isConnected = false
        ) }
    }

    fun waitForIncomingConnectionRequests(){
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }
    fun startScan(){
        bluetoothController.startDiscovery()
    }

    fun stopScan(){
        bluetoothController.stopDiscovery()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { _result ->
            when (_result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = _result.message
                        )
                    }
                }
            }
        }.catch {
            bluetoothController.closeTheConnectedConnection()
            _state.update {
                it.copy(
                    isConnected = false,
                    isConnecting = false
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.releaseDevices()
    }
}