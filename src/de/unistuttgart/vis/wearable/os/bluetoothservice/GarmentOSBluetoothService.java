package de.unistuttgart.vis.wearable.os.bluetoothservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.sensorDriver.SensorDriver;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.utils.Utils;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import javax.net.ssl.SSLParameters;

/**
 * Created by Manuel on 05.03.2015.
 * Provides the infrastructure for every bluetooth communication between that settingsapp and a sensor.
 * only start this service when creating an sensor!
 *
 * @author Manuel
 *
 */
public class GarmentOSBluetoothService extends Service{
	
    private BluetoothAdapter mBluetoothAdapter;
    public static final String BT_DEVICE = "btDevice";
    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String GOS_UUID = "00009101-0000-1000-8000-00805F9B34FB";
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
	private static final String BT_DRIVER = "btDriver";
	private static final String BT_ID = "btId";
    // device
    private ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;
    // public mInHangler mHandler = new mInHangler(this);
    private static Handler mHandler = null;
    public static int mState = STATE_NONE;
    public static String deviceName;
    public Vector<Byte> packdata = new Vector<Byte>(2048);
    public static BluetoothDevice device = null;
    private String driver = "";
    private int sensorId = 0;
    private de.unistuttgart.vis.wearable.os.internalapi.PSensor saveSensor;
    
    
    //HARDCODE
    String sensorName = "test";

    @Override
    public void onCreate() {
        Log.d("BluetoothComService", "Service started");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        GarmentOSBluetoothService getService() {
            return GarmentOSBluetoothService.this;
        }
    }



    private final IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("BluetoothComService", "Onstart Command");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {

            driver = "stepDriver";
            sensorId = intent.getIntExtra(BT_ID, 0);
            saveSensor = de.unistuttgart.vis.wearable.os.internalapi.APIFunctions.API_getSensorById(sensorId);
            String macAddress = saveSensor.getBluetoothID();
            device = mBluetoothAdapter.getRemoteDevice(saveSensor.getBluetoothID());
            deviceName = device.getName();
            if (macAddress != null && macAddress.length() > 0) {
                connectToDevice(macAddress);
            } else {
                stopSelf();
                return 0;
            }
        }
        String stopservice = intent.getStringExtra("stopservice");
        if (stopservice != null && stopservice.length() > 0) {
            stop();
        }
        return START_STICKY;
    }

    /**
     *
     * @param macAddress
     */
    private synchronized void connectToDevice(String macAddress) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    private void setState(int state) {
        GarmentOSBluetoothService.mState = state;
    }

    public synchronized void stop() {
        setState(STATE_NONE);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {
        setState(STATE_NONE);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mBluetoothAdapter.cancelDiscovery();
        return super.stopService(name);
    }

    private void connectionFailed() {
        GarmentOSBluetoothService.this.stop();
    }

    private void connectionLost() {
        GarmentOSBluetoothService.this.stop();
    }

    private static Object obj = new Object();

    public static void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (obj) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private synchronized void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

        // Message msg =
        // mHandler.obtainMessage(AbstractActivity.MESSAGE_DEVICE_NAME);
        // Bundle bundle = new Bundle();
        // bundle.putString(AbstractActivity.DEVICE_NAME, "p25");
        // msg.setData(bundle);
        // mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            Log.i("BT4", device.getAddress() + "/" + device.getName());
            BluetoothSocket tmp = null;
            try {
                //IMPORTANT do not change UUID until you know what your btdevice does!
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            setName("ConnectThread");
            mBluetoothAdapter.cancelDiscovery();
            try {
            	//connect(device);
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                connectionFailed();
                return;
            }
            synchronized (GarmentOSBluetoothService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("BluetoothComService", "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("Printer Service", "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (!parseData(mmInStream)) {
                    	mState = STATE_NONE;
                        connectionLost();
                        break;
                    } else {
                   
                    }
         
                    // mHandler.obtainMessage(AbstractActivity.MESSAGE_READ,
                    // bytes, -1, buffer).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                    connectionLost();
                    GarmentOSBluetoothService.this.stop();
                    break;
                }

            }
        }

        private byte[] btBuff;


        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e("BluetoothComService", "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();

            } catch (IOException e) {
                Log.e("BluetoothComService", "close() of connect socket failed", e);
            }
        }

    }

    @Override
    public void onDestroy() {
        stop();
        Log.i("Printer Service", "Destroyed");
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {//
        	byte[] writeBuf = (byte[]) msg.obj;
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                case 1:
                	String writeMessage = new String(writeBuf);
                	Log.i("BTInput", writeMessage);
                	break;

                
                    case 3:

                        break;

                    case 4:

                        break;
                    case 5:
                        break;

                    case -1:
                        break;
                }
            }
            super.handleMessage(msg);
        }

    };

    /**
     * listen to the inputstream for ever or until an error ocour.
     * The recieved data ist processed by the driver call with reflection.
     * Stores the processed raw data
     *
     * @param input
     * @return
     */
    private boolean parseData (InputStream input) {
    	
    	// Keep listening to the InputStream while connected
        while (true) {

             try {
                 byte[] buffer = new byte[128];
                 String readMessage;
                 int bytes;
                if (input.available()>2) {
                    try {
                       // Read from the InputStream
                        bytes = input.read(buffer);
                        String test = new String(buffer, 0, bytes);
                        for (int i = 0;  i < bytes; i++) {
                                String test2 = new String(buffer, i, 1);
                        }

                        byte[] sendBuffer = Arrays.copyOfRange(buffer, 0, bytes);
                        float[] dataFloat = loadDriver(driver, buffer);

                        SensorData data = new SensorData(dataFloat, Utils.dateToUnix(new Date()));
                        saveSensor.addRawData(data);
                        SystemClock.sleep(1000);
                       }catch (IOException e) {
                        Log.e("BTConnection", "disconnected", e);
                        break;
                    }
                    
        } else {
               }
            } catch (IOException e) {

                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                 e.printStackTrace();
             } catch (NoSuchMethodException e) {
                 e.printStackTrace();
             } catch (InvocationTargetException e) {
                 e.printStackTrace();
             } catch (InstantiationException e) {
                 e.printStackTrace();
             } catch (IllegalAccessException e) {
                 e.printStackTrace();
             }

        }
    	return false;

    }

    /**
     * Loads driverclass, uses reflection to call encodeData() implemented by the driver class.
     * Returns processed inputstream as float[]
     *
     *
     * @param driver
     * @param message
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private float[] loadDriver(String driver, byte[] message) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class newDriver = Class.forName("de.unistuttgart.vis.wearable.os.sensorDriver." + driver);
        Object reciever = newDriver.newInstance();
        Method encodeData = newDriver.getMethod("encodeData", byte[].class);
        return (float[]) encodeData.invoke(reciever, message);
    }

}
