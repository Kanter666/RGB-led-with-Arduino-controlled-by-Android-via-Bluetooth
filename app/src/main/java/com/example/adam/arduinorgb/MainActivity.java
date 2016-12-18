package com.example.adam.arduinorgb;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    private TextView text;
    private ColorPicker picker;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        picker = (ColorPicker) findViewById(R.id.picker);
    }
    public void save(View v) {
        picker.setOldCenterColor(picker.getColor());
        int myColor = picker.getColor();
        int red = (myColor >> 16) & 0xFF;
        int green = (myColor >> 8) & 0xFF;
        int blue = myColor & 0xFF;
        text.setText("Your colors:  red:"+red +"  green:" + green + "  blue: " + blue);
    }
    public void set(View view) {
        try {
            findBT();
            openBT();
        }
        catch (IOException ex) { }
        int myColor = picker.getColor();
        int red = (myColor >> 16) & 0xFF;
        int green = (myColor >> 8) & 0xFF;
        int blue = myColor & 0xFF;
        text.setText("Your colors:  red:"+red +"  green:" + green + "  blue: " + blue);
        message = red+","+green+","+blue;
        try {
            sendData();
        }
        catch (IOException ex) {
            Log.d("Testing", "Send failed");
        }
    }

    public void off(View activity_main) {
        text.setText("off");
        try {
            findBT();
            openBT();
        }
        catch (IOException ex) { }
        int red = 0;
        int green = 0;
        int blue = 0;
        text.setText("Your colors:  red:"+red +"  green:" + green + "  blue: " + blue);
        message = red+","+green+","+blue;
        try {
            sendData();
        }
        catch (IOException ex) {
            Log.d("Testing", "Send failed");
        }
    }

    void openBT() throws IOException {
        if (mmDevice != null) {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
        }
    }

    void sendData() throws IOException {
        if (mmOutputStream != null) {
            message += "*";
            mmOutputStream.write(message.getBytes());
            Log.d("Testing", "Data Sent");
            Log.d("Testing", message);
        }
    }

    void closeBT() throws IOException {
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        Toast.makeText(getBaseContext(), "Bluetooth Closed", Toast.LENGTH_SHORT).show();
    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                System.out.println("device: " + device.getName());
                if(device.getName().equals("HC-06")) {
                    mmDevice = device;
                    break;
                }
            }
        }
    }

}
