package com.munchbot.munchbot.ui.fragments.home.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.Home2Binding
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.S)

class Home2Fragment : MunchBotFragments() {
    private var _binding: Home2Binding? = null
    private val binding get() = _binding!!

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket? = null
    private lateinit var outputStream: OutputStream
    private val deviceName = "ESP32_MunchBot"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Home2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.black)
        setupBluetoothConnection()

        binding.pawSend.setOnClickListener {
            'A'.sendCharacter()
        }

        binding.icBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this@Home2Fragment)
                .commit()

        }
    }

    private fun setupBluetoothConnection() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            return
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = bluetoothAdapter.bondedDevices
        var esp32Device: BluetoothDevice? = null

        for (device in pairedDevices) {
            if (device.name == deviceName) {
                esp32Device = device
                break
            }
        }

        esp32Device?.let {
            connectToDevice(it)
        } ?: run {
            Toast.makeText(requireContext(), "MunchBot Device not found", Toast.LENGTH_SHORT)
                .show()
            binding.pawSend.visibility = View.GONE
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            return
        }

        Thread {
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket!!.outputStream

                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Connected to MunchBot", Toast.LENGTH_SHORT)
                        .show()
                    binding.state.setImageResource(R.drawable.ic_state_on)
                    binding.pawSend.visibility = View.VISIBLE
                }
            } catch (e: IOException) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Connection failed", Toast.LENGTH_SHORT)
                        .show()
                    binding.state.setImageResource(R.drawable.ic_state_off)
                    binding.pawSend.visibility = View.GONE
                }
            }
        }.start()
    }

    private fun Char.sendCharacter() {
        try {
            outputStream.write(toString().toByteArray())
            Toast.makeText(requireContext(), "Sent: $this", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to send data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bluetoothSocket?.close()
        _binding = null
    }
}
