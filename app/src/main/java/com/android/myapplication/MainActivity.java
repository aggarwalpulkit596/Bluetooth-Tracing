package com.android.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_ENABLE_BT = 1;
  private BluetoothAdapter mBluetoothAdapter;

  public ArrayList<String> availableDevices;
  public ArrayList<String> availableMAC;
  public ListView availableDevicesList;
  public ArrayAdapter<String> mScanAdapter;

  private TextView header;
  private TextView availableTextView;
  private Switch bTSwitch;
  private final static String bTOffMessage= "Turn on Bluetooth to see the nearby devices.";
  private final static String bTOnMessage = "Your device is now visible to the nearby devices.";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    bTSwitch = findViewById(R.id.btSwitch);
    header = findViewById(R.id.header);
    availableDevicesList = findViewById(R.id.availableListView);
    header = findViewById(R.id.header);
    availableTextView = findViewById(R.id.availableText);

    availableTextView.setVisibility(View.INVISIBLE);

    availableMAC = new ArrayList<>();
    availableDevices = new ArrayList<>();
    mScanAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, availableDevices);
    availableDevicesList.setAdapter(mScanAdapter);

    // create an instance of Bluetooth Adapter
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    bTSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(mBluetoothAdapter == null) {
          Toast.makeText(getApplicationContext(), "Bluetooth service unavailable", Toast.LENGTH_SHORT).show();
        }
        else {
          if (isChecked) {
            if (!mBluetoothAdapter.isEnabled()) {
              Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
              startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            header.setText(bTOnMessage);
            availableTextView.setVisibility(View.VISIBLE);

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, filter);
            mBluetoothAdapter.startDiscovery();
          }
          else {
            unregisterReceiver(broadcastReceiver);
            mBluetoothAdapter.cancelDiscovery();
            header.setText(bTOffMessage);
            availableTextView.setVisibility(View.INVISIBLE);
            mScanAdapter.clear();
          }
        }
      }
    });
  }

  final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        // Create a new device item
        if(device != null) {
          String newDevice = device.getName();
          String newMAC = device.getAddress();

          if (!availableMAC.contains(newMAC)) {
            availableMAC.add(newMAC);
            if(newDevice != null)
              mScanAdapter.add(newDevice);
            else {
              mScanAdapter.add(newMAC);
            }
          }
        }
      }
    }
  };

  /*
  private void findPairedDevices() {
      Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
      pairedDevices = new ArrayList<>();
      if(devices.size() > 0) {
          for(BluetoothDevice device: devices) {
              pairedDevices.add(device.getName());
          }
          mArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, pairedDevices);
          pairedDevicesList.setAdapter(mArrayAdapter);
      }
  }
  */
  @SuppressLint("MissingSuperCall")
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == 1) {
      if (resultCode == RESULT_OK) {
        Toast.makeText(this, "Bluetooth is now enabled", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
