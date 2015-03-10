package de.unistuttgart.vis.wearable.os.bluetoothservice;


import de.unistuttgart.vis.wearable.os.internalapi.IGarmentDriver;
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

        sensor.setEnabled(true);
        if(sensorDriver != null) {


            //
            //
            //
            // //TODO Process and cut the byte array
            //
            //
            //


            try {
                //
                // Let the sensor driver do its work and insert the SensorData object
                // if the sensor driver was able to create the data array
                //
                long timeNow = Utils.getCurrentLongUnixTimeStamp();
                float[] data = this.sensorDriver.executeDriver(null);
                if(data != null)
                    sensor.addRawData(new SensorData(data, timeNow));
            } catch (android.os.RemoteException re) {
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
        this.onStart();
        while(!Thread.currentThread().isInterrupted()) {
            if(this.workFlag == RUNNING) {
                this.onRun();
            }

            else if(this.workFlag == RESUMING) {
                this.onResume();;
                this.workFlag = RUNNING;
            }

            else if(this.workFlag == PAUSED) {
                this.onPause();
            }

            //
            // Handle stop, pause and resume actions
            //
            try {
                Thread.sleep(this.currentSleepTimePerIteration);
            } catch (InterruptedException ioe) {
                if(this.workFlag == STOPPED)
                    Thread.currentThread().interrupt();
            }
        }
        this.onStop();
    }


    /**
     * Pause the current Thread, the thread can be resumed later on
     */
    public void pauseThread() {
        this.workFlag = PAUSED;
        this.currentSleepTimePerIteration = Long.MAX_VALUE;
        this.interrupt();
    }

    /**
     * Resume the Thread, note that this has no effect on an already running thread
     */
    public void resumeThread() {
        this.workFlag = RESUMING;
        this.currentSleepTimePerIteration = this.sleepTimePerIteration;
        this.interrupt();
    }

    /**
     * Stop the Thread, note stopping the Thread this way is intended not as using {@link Thread#stop()}
     */
    public void stopThread() {
        this.workFlag = STOPPED;
        this.interrupt();
    }
}

