package de.unistuttgart.vis.wearable.os.storage;

import de.unistuttgart.vis.wearable.os.sensors.SensorData;

/**
 * This deserializer reads data from a file and creates an object for the data.
 * The file to read should have been created using the
 * {@link SensorDataSerializer} class
 * 
 * @author roehrdor
 */
public class SensorDataDeSerializer implements Runnable {
	private int sensorID = 0;
	private java.util.List<SensorData> sensorData;
	private int noDatasetsToRead = 0;
	
	private java.io.File file = null;
	private java.io.RandomAccessFile raf = null;
	
	/**
	 * Create a new deserialized object that reads the given number of data sets
	 * from the file and inserts them into the given sensor Data list. If the
	 * file contains less than noDatasetsToRead all the values from the file
	 * will be deserialized.
	 * 
	 * @param sensorID
	 *            the sensor if to read the data for
	 * @param sensorData
	 *            the list to insert the data into
	 * @param noDatasetsToRead
	 *            the number of element to be deserialized. This number needs to
	 *            be at least 1, ohterwise an {@link IllegalArgumentException}
	 *            will be thrown
	 * @throws IllegalArgumentException if less than one element shall be read
	 */
	public SensorDataDeSerializer(int sensorID, java.util.List<SensorData> sensorData, int noDatasetsToRead) {
		if(noDatasetsToRead < 1)
			throw new IllegalArgumentException("Reading less than 1 element is not allowed");
			
		this.sensorData = sensorData;
		this.sensorID = sensorID;
		this.noDatasetsToRead = noDatasetsToRead;
	}

	@Override
	public void run() {
		try {			
			int currentFileLength = 0;
			int numberOfDataSetsInFile = 0;
			int dataDimension = 0;
			
			//
			// If the file does not exist we can not read anything 
			//
			file = new java.io.File(String.valueOf(sensorID));
			if(!file.exists()) {
				return;
			}
			
			//
			// Otherwise open the file as RandomAccessFile
			//
			raf = new java.io.RandomAccessFile(file, "rw");	
			
			//
			// Based on the file size compute the number of entries in the file
			//
			currentFileLength = (int)file.length();
			raf.seek(4);
			dataDimension = raf.readInt();
			numberOfDataSetsInFile = (currentFileLength - 8) / ((dataDimension + 1) * 4); 
			
			//
			// If there are less data sets available than we want to read read
			// them all
			//
			if(numberOfDataSetsInFile < noDatasetsToRead) {
				raf.seek(8);
				while(raf.getFilePointer() < currentFileLength) {
					float f[] = new float[dataDimension];
					int date = raf.readInt();
					for(int i = 0 ; i != dataDimension; ++i) {						
						f[i] = raf.readFloat();
					}
					sensorData.add(new SensorData(f, date));
				}
			} 
			
			//
			// Otherwise compute the starting position where we need to start reading
			//
			else {
				raf.seek(currentFileLength - noDatasetsToRead * (dataDimension + 1));
				while(raf.getFilePointer() < currentFileLength) {
					float f[] = new float[dataDimension];
					int date = raf.readInt();
					for(int i = 0 ; i != dataDimension; ++i) {						
						f[i] = raf.readFloat();
					}
					sensorData.add(new SensorData(f, date));
				}
			}
			
			//
			// Close the file
			//
			raf.close();
		} catch (java.io.IOException ioe) {}
	}

}
