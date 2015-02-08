/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.storage;

import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.service.GarmentOSSerivce;

/**
 * Serialize sensor Data and save them to a file. Note that no duplicates will
 * be saved. Duplicated data sets are recognized by their time stamp the data
 * has been recorded on. This values will be skipped.
 * 
 * @author roehrdor
 */
public class SensorDataSerializer implements Runnable {
	private int sensorID = 0;
	private java.util.List<SensorData> sensorData;
	private java.io.File file = null;
	private java.io.RandomAccessFile raf = null;
	private android.content.Context context;
	
	/**
	 * Create a new serializer
	 * 
	 * @param sensorID
	 *            the id of the sensor where from this data sets are coming
	 * @param sensorData
	 *            the sensor data set to be serialized
	 */
	public SensorDataSerializer(int sensorID, java.util.List<SensorData> sensorData) {		
		this.sensorData = sensorData;
		this.sensorID = sensorID;							
		this.context = GarmentOSSerivce.getContext();
	}
	
	/**
	 * Create a new serializer
	 * 
	 * @param sensorID
	 *            the id of the sensor where from this data sets are coming
	 * @param sensorData
	 *            the sensor data set to be serialized
	 */
	public SensorDataSerializer(int sensorID, java.util.List<SensorData> sensorData, android.content.Context context) {		
		this.sensorData = sensorData;
		this.sensorID = sensorID;							
		this.context = context;
	}

	@Override
	public void run() {		
		try {			
			long currentFileLength = 0;
			int latestData = 0;
			int currentDate = 0;
			
			//
			// Create a new file object and test whether the file already
			// exists. If the file does not already exist we need to create this file
			//			
			file = new java.io.File(this.context.getFilesDir(), String.valueOf(sensorID));
			if(!file.exists()) {
				//
				// So we need to create a new file but also to insert the latest data 
				// date as well as the dimension of the data fields to this file
				//
				file.createNewFile();
				raf = new java.io.RandomAccessFile(file, "rw");
				raf.writeInt(0);
				raf.writeInt(sensorData.get(0).getData().length);
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
			for(SensorData sd : sensorData) {
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
		} catch (java.io.IOException ioe) {}
	}
}
