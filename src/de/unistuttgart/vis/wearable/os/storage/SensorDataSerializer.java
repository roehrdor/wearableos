/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.storage;

import android.util.Log;
import de.unistuttgart.vis.wearable.os.properties.Properties;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.utils.Utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Serialize sensor Data and save them to a file. Note that no duplicates will
 * be saved. Duplicated data sets are recognized by their time stamp the data
 * has been recorded on. This values will be skipped.
 * 
 * @author roehrdor
 */
public class SensorDataSerializer {

    //
    // Map that assigns a File Data Tuple object to a sensor ID
    //
    protected static final Map<Integer, Vector<SensorData>> mSensorData = new HashMap<Integer, Vector<SensorData>>();
    protected static final Vector<Integer> jobIDS = new Vector<Integer>();

    protected static final Thread runner = new Thread() {
        @Override
        public void run() {
            //
            // Wait until the archiver process has terminated and then
            // tell the files are being used
            //
            Properties.FILE_STATUS_FIELDS_LOCK.lock();
            while(Properties.FILE_ARCHIVING.get()) {
                Utils.sleepUninterrupted(200);
            }
            Properties.FILES_IN_USE.incrementAndGet();
            Properties.FILE_STATUS_FIELDS_LOCK.unlock();

            RandomAccessFile raf;
            File file;

            //
            // As long as we have jobs to be done
            //
            while(!jobIDS.isEmpty()) {
                int id = jobIDS.remove(0);
                Vector<SensorData> data = mSensorData.get(id);

                //
                // Synchronize the access to the object
                //
                synchronized (mSensorData.get(id)) {
                    try {
                        long currentFileLength;
                        int latestData = 0;
                        int currentDate = 0;

                        //
                        // Create a new file object and test whether the file already
                        // exists. If the file does not already exist we need to create this file
                        //
                        file = new java.io.File(Properties.storageDirectory, String.valueOf(id));
                        if(!file.exists()) {
                            //
                            // So we need to create a new file but also to insert the latest data
                            // date as well as the dimension of the data fields to this file
                            //
                            if(!file.createNewFile()) {
                                Log.e("GarmentOS", "File does not exist, but creating it tells us it does already exist?");
                            }
                            raf = new java.io.RandomAccessFile(file, "rw");
                            raf.writeInt(0);
                            raf.writeInt(data.get(0).getData().length);
                        } else {
                            raf = new java.io.RandomAccessFile(file, "rw");
                        }


                        //
                        // Now we need to check whether any of our sensor data has already
                        // been written to the file. Since we write the time stamp of the
                        // latest inserted data to the beginning of the file this is quite
                        // easy
                        //
                        currentFileLength = file.length();
                        if(currentFileLength != 0L) {
                            latestData = raf.readInt();
                        }

                        //
                        // Seek to the end of the file to insert new data
                        //
                        raf.seek(currentFileLength);

                        //
                        // Iterate over the sensor Data set and write the data fields
                        //
                        for(SensorData sd : data) {
                            // The data has already been inserted so we can skip it
                            if((currentDate = sd.getUnixDate()) < latestData)
                                continue;

                            // Otherwise the data has not yet been inserted, so
                            raf.writeInt(currentDate);
                            for(float fsd : sd.getData())
                                raf.writeFloat(fsd);
                        }

                        //
                        // Write the latest date to the beginning of the file
                        //
                        raf.seek(0);
                        raf.writeInt(currentDate);

                        //
                        // Close the file
                        //
                        raf.close();
                    } catch (java.io.IOException ioe) {
                        Log.e("GarmentOS", "SensorDataSerializer - writing to file failed");
                    }
                }
            }

            //
            // Tell we finished processing this file, therefore
            // decrement the count
            //
            Properties.FILES_IN_USE.decrementAndGet();
        }
    };

	/**
	 * Create a new serializer
	 * 
	 * @param sensorID
	 *            the id of the sensor where from this data sets are coming
	 * @param sensorData
	 *            the sensor data set to be serialized
	 */
	@SuppressWarnings("unchecked")
	public SensorDataSerializer(int sensorID, java.util.Vector<SensorData> sensorData) {
        synchronized (mSensorData) {
            if(!mSensorData.containsKey(sensorID))
                mSensorData.put(sensorID, new Vector<SensorData>());
            else
                mSensorData.get(sensorID).addAll(sensorData);
        }
        synchronized (runner) {
            jobIDS.add(sensorID);
            if (!runner.isAlive())
                runner.start();
        }
	}
}
