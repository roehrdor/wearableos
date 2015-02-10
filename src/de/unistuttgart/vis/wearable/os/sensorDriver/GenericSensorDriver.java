/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.sensorDriver;

import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * A simple generic sensor driver
 *
 * This is still work in progress since a lot of improvements are to be made
 *
 * @author roehrdor
 */
public class GenericSensorDriver {
    protected final int BUFFER_SIZE = 0x4;
    protected final byte[] BUFFER = new byte[BUFFER_SIZE];

    //
    // Attributes to store the data
    //
    protected byte[] dataChunk;
    protected byte[] chunks;
    protected int sensorID;
    protected int dimension;
    protected int dataChunkSize;
    protected String driverName;
    protected Sensor sensor;
    protected SensorDriverCallback sensorDriverCallback;

    /**
     * Create a new Generic Driver with the given parameters
     *
     * @param driverName           the driver name
     * @param dimension            the dimension
     * @param dataChunkSize        the size of a data chunk (single data with all dimensions)
     * @param chunks               this contains the offset and the size of each dimension of the data chunks
     * @param sensorID             the sensor the driver shall be used for
     * @param sensorDriverCallback if this callback handle is set the according function is called every time we get new sensor data
     */
    public GenericSensorDriver(String driverName, int dimension, int dataChunkSize, byte[] chunks,
                               int sensorID, SensorDriverCallback sensorDriverCallback) {
        this.driverName = driverName;
        this.dimension = dimension;
        this.chunks = chunks;
        this.dataChunkSize = dataChunkSize;
        this.dataChunk = new byte[this.dataChunkSize];
        this.sensorID = sensorID;
        this.sensor = SensorManager.getSensorByID(this.sensorID);
        this.sensorDriverCallback = sensorDriverCallback;
    }

    /**
     * Reset the buffer, looking at the byte representation set all bytes to zero
     */
    protected void cleanBuffer() {
        for(int i = 0; i != this.BUFFER_SIZE; ++i)
            this.BUFFER[i] = 0;
    }

    /**
     * Get the input bytes from the byte array starting at the given offset in the given array
     *
     * @param b      the array to store all the values
     * @param offset the offset where to start reading in the array
     * @return true if input parameters were valid and data has been set
     */
    public boolean input(byte[] b, int offset) {
        if(b.length - offset < this.dataChunkSize)
            return false;
        System.arraycopy(b, offset, this.dataChunk, 0, this.dataChunkSize);
        return true;
    }

    /**
     * Execute the driver and process the read data
     */
    public void execute() {
        int currentOffset = 0, currentSize;
        int timeStamp = 0;
        float[] data = new float[this.dimension];

        //
        // For all the dimensions iterate
        //
        for(int i = 0; i != this.dimension; ++i) {
            //
            // Get the current offset and the chunk size
            //
            currentOffset += this.chunks[i << 0x1];
            currentSize = this.chunks[(i << 0x1) + 1];

            //
            // Clean the buffer and copy the data chunk to the buffer
            //
            this.cleanBuffer();
            System.arraycopy(this.dataChunk, currentOffset, this.BUFFER, 0, currentSize);

            // insert into the data
            //TODO Cast or get the bytewise representation?
            //data[i] = getFloatFromByte(this.BUFFER);
            data[i] = Utils.getIntFromByte(this.BUFFER);

            currentOffset += currentSize;
        }

        SensorData sensorData = new SensorData(data, Utils.getCurrentUnixTimeStamp());


        //
        // In case we got all callback handle call the function
        //
        if(this.sensorDriverCallback != null)
            this.sensorDriverCallback.callback(sensorData);

        //
        // If we have a sensor object add the read data to it
        //
        if(this.sensor != null)
            this.sensor.addRawData(sensorData);
    }

    /**
     * Check if the given input values are valid
     *
     * @param driverName    the driver name
     * @param dimension     the dimension
     * @param dataChunkSize the size of a data chunk (single data with all dimensions)
     * @param chunks        this contains the offset and the size of each dimension of the data chunks
     * @return 0 if no error is found
     */
    public static int checkFailure(String driverName, int dimension, int dataChunkSize, byte[] chunks) {
        if(driverName == null || dimension <= 0 || chunks == null)
            return 0x1;
        if(chunks.length != (dimension << 0x1))
            return 0x2;
        int sum = 0;
        for(byte b : chunks)
            sum += b;
        if(dataChunkSize != sum)
            return 0x3;
        return 0x0;
    }
}
