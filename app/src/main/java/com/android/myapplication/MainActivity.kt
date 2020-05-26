package com.android.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_ENABLE_BT = 1

class MainActivity : AppCompatActivity() {
  private val mBluetoothAdapter: BluetoothAdapter by lazy {
    BluetoothAdapter.getDefaultAdapter();
  }

  var availableDevices = arrayListOf<String>()
  var availableMAC = arrayListOf<String>()

  val mScanAdapter: ArrayAdapter<String> by lazy {
    ArrayAdapter(
      this, android.R.layout.simple_list_item_1, availableDevices
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    availableListView.setAdapter(mScanAdapter)

    btSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        if (!mBluetoothAdapter.isEnabled) {
          val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
          startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        header.text = "Turn on Bluetooth to see the nearby devices."
        availableText.isInvisible = true
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(broadcastReceiver, filter)
        mBluetoothAdapter.startDiscovery()
      } else {
        unregisterReceiver(broadcastReceiver)
        mBluetoothAdapter.cancelDiscovery()
        header.text = "Your device is now visible to the nearby devices."
        availableText.isInvisible = false
        mScanAdapter.clear()
      }
    }
  }

  private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val action = intent.action
      if (BluetoothDevice.ACTION_FOUND == action) {
        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        // Create a new device item
        if (device != null) {
          val newDevice = device.name
          val newMAC = device.address
          if (!availableMAC.contains(newMAC)) {
            availableMAC.add(newMAC)
            if (newDevice != null) mScanAdapter.add(newDevice) else {
              mScanAdapter.add(newMAC)
            }
          }
        }
      }
    }

  }


}
