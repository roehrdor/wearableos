package de.unistuttgart.vis.wearable.os.bluetoothservice;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import de.unistuttgart.vis.wearable.os.api.IGarmentDriver;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * @author roehrdor
 */
public class GarmentOSBluetooth extends Thread {

    //
    // Constants expressing whether the Thread shall be running,
    // sleeping or paused
    //
    public static final int RUNNING = 0x0;
    public static final int PAUSED = 0x1;
    public static final int RESUMING = 0x2;
    public static final int STOPPED = 0x4;

    //
    // Local Thread attributes
    //
    protected long sleepTimePerIteration = 100;
    protected long currentSleepTimePerIteration = sleepTimePerIteration;
    protected int workFlag = RUNNING;

    // BT connection attributes
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device = null;
    private String macAddress = "";
    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothSocket mmSocket = null;
    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;

    //
    // Local Sensor attributes
    //
    Sensor sensor;
    IGarmentDriver sensorDriver;

    /**
     * Create a new GarmentOSBluetooth Object
     * This constructor should not be used it is only for demonstrating purposes
     */
    @Deprecated
    public GarmentOSBluetooth() {
        this.sensorDriver = sensor.getSensorDriver();
    }


    /**
     * This function will be called once the thread starts its work
     */
    protected void onStart() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if ( mBluetoothAdapter != null ) {
            macAddress = sensor.getBluetoothID();
            if (macAddress == null && macAddress.length() == 0) {
                onStop();
            } else {
                //start thread
            }
        }

    }

    /**
     * This function will be called once per call to {@link GarmentOSBluetooth#resumeThread()}
     */
    protected void onResume() {

    }

    /**
     * This function will be called once per call to {@link GarmentOSBluetooth#pauseThread()}
     */
    protected void onPause() {
        //
        // Work done before the Thread is finally paused
        //
        sensor.setEnabled(false);
    }

    /**
     * This function will be executed when the Thread is running
     */
    protected void onRun() {

        //
        // Running Thread work shall be done here
        //
        byte[] recievedData = null;
        sensor.setEnabled(true);
        if(sensorDriver != null) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e("Printer Service", "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;


            while (true) {
                recievedData = null;
                try {
                    byte[] buffer = new byte[128];
                    String readMessage;
                    int bytes;
                    if (mmInStream.available() > 2) {
                        try {
                            // Read from the InputStream
                            bytes = mmInStream.read(buffer);
                            String test = new String(buffer, 0, bytes);
                            recievedData = Arrays.copyOfRange(buffer, 0, bytes);

                        } catch (IOException e) {
                            break;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    //
                    // Let the sensor driver do its work and insert the SensorData object
                    // if the sensor driver was able to create the data array
                    //
                    long timeNow = Utils.getCurrentLongUnixTimeStamp();
                    //sensorDriver is executed with the recieved Data form the socket as byte array
                    float[] data = this.sensorDriver.executeDriver(recievedData);
                    if (data != null)
                        sensor.addRawData(new SensorData(data, timeNow));
                } catch (android.os.RemoteException re) {
                }
            }
        }
    }

    /**
     * This function will be executed last after {@link GarmentOSBluetooth#stopThread()} is called
     */
    protected void onStop() {

    }


    @Override
    public void run() {
        //
        // onStart is called once the Thread is started
        //
        this.onStart();

        //
        //Connect to the bt device
        //

        device = mBluetoothAdapter.getRemoteDevice( macAddress );
        if ( !sensor.isEnabled() ) {
            this.onStop();
        } else {
            BluetoothSocket tmp = null;
            try {
                //IMPORTANT do not change UUID until you know what your btdevice does!
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        //
        // Run until the interrupt flag is visible in this state
        // the interrupt flag will be only be available if the
        // stopThread() function has been called
        //
        while(!Thread.currentThread().isInterrupted()) {
            //
            // If the work flag is set to running call the onRun()
            // method
            //
            if(this.workFlag == RUNNING) {
                this.onRun();
            }

            //
            // If the work flag is set to resuming call the
            // onResume() function once and set the flag to running
            // afterwards
            //
            else if(this.workFlag == RESUMING) {
                this.onResume();
                this.workFlag = RUNNING;
            }

            //
            // If the work flag is set to paused call the onPause()
            // function
            //
            else if(this.workFlag == PAUSED) {
                this.onPause();
            }

            //
            // Handle stop, pause and resume actions
            //
            try {
                Thread.sleep(this.currentSleepTimePerIteration);
            } catch (InterruptedException ioe) {
                //
                // Since catching the InterruptedException will clear the interruption
                // flag it will not be visible to the condition in the while loop.
                // For the most interrupts we don't want to let the while condition
                // see the flag since we do not want to stop the Thread yet, but if we
                // want to stop the thread set the flag again by interrupting the current
                // Thread by calling the interrupt() method
                //
                if(this.workFlag == STOPPED)
                    Thread.currentThread().interrupt();
            }
        }

        //
        // In the end call the onStop() function once before the Thread
        // finally terminates
        //
        this.onStop();
    }


    /**
     * Pause the current Thread, the thread can be resumed later on
     */
    public void pauseThread() {
        if(this.workFlag != RUNNING) {
            // Print a WARN message to the error output stream to notify the user
            // that only a running Thread can be paused
            System.err.println("WARN: Only a running Thread can be paused");
        } else {
            this.workFlag = PAUSED;
            this.currentSleepTimePerIteration = Long.MAX_VALUE;
            this.interrupt();
        }
    }

    /**
     * Resume the Thread, note that this has no effect on an already running thread
     */
    public void resumeThread() {
        if(this.workFlag != PAUSED) {
            // Print a WARN message to the error output stream to notify the user
            // that only a paused server can be resumed
            System.err.println("WARN: Only a paused Thread can be resumed");
        } else {
            this.workFlag = RESUMING;
            this.currentSleepTimePerIteration = this.sleepTimePerIteration;
            this.interrupt();
        }
    }

    /**
     * Stop the Thread, note stopping the Thread this way is intended not as using {@link Thread#stop()}
     */
    /**
     * Stop the Thread, note stopping the Thread this way is intended not as using {@link Thread#stop()}
     */
    public void stopThread() {
        this.workFlag = STOPPED;
        this.interrupt();
    }
}

