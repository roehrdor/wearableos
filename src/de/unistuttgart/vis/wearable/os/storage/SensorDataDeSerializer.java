/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.storage;

import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.service.GarmentOSService;

/**
 * <p>
 * This deserializer reads data from a file and creates an object for the data.
 * The file to read should have been created using the
 * {@link SensorDataSerializer} class
 * </p>
 * <p>
 * Note: This class executes asynchronously. 
 * </p>
 * 
 * @author roehrdor
 */
public class SensorDataDeSerializer implements Runnable {
	private int sensorID = 0;
	private java.util.List<SensorData> sensorData;	
	private android.content.Context context;
	
	private long threadID = 0;
	
	private java.io.File file = null;
	private java.io.RandomAccessFile raf = null;
	
	private int startTime = 0;
	private int endTime = 0;
	private int noDatasetsToRead = 0;
	
	private int jobFlag = 0;
	private static final int LATEST_DATA = 0x0;
	private static final int START_NUMBER = 0x1;
	private static final int START_END_UNLIMITED = 0x2;
	private static final int START_END_LIMIT = 0x3;
	
	private static final java.util.Set<Long> activeWorkers = new java.util.HashSet<Long>();
	
	
	
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
	public SensorDataDeSerializer(int sensorID, java.util.List<SensorData> sensorData, int noDatasetsToRead, 
									android.content.Context context) {
		if(noDatasetsToRead < 1)
			throw new IllegalArgumentException("Reading less than 1 element is not allowed");
			
		this.sensorData = sensorData;
		this.sensorID = sensorID;
		this.noDatasetsToRead = noDatasetsToRead;
		this.context = context;
		this.jobFlag = LATEST_DATA;
	}
	
	/**
	 * Create a new deserialized object that reads the beginning from the start
	 * time the given number of datasets into the given Data list. If the start
	 * time is older than the oldest available data field in the file the
	 * reading process starts by the oldest one.
	 * 
	 * @param sensorID
	 *            the sensor if to read the data for
	 * @param sensorData
	 *            the list to insert the data into
	 * @param noDatasetsToRead
	 *            the number of element to be deserialized. This number needs to
	 *            be at least 1, ohterwise an {@link IllegalArgumentException}
	 *            will be thrown
	 * @param startTime
	 *            the oldest data set to be read
	 * @throws IllegalArgumentException
	 *             if noDatasetsToRead is less than 1
	 */
	public SensorDataDeSerializer(int sensorID, java.util.List<SensorData> sensorData, int noDatasetsToRead, int startTime) {
		if(noDatasetsToRead < 1)
			throw new IllegalArgumentException("Reading less than 1 element is not allowed");
		this.sensorID = sensorID;
		this.noDatasetsToRead = noDatasetsToRead;
		this.startTime = startTime;
		this.context = de.unistuttgart.vis.wearable.os.service.GarmentOSService.getContext();
		this.sensorData = sensorData;
		this.jobFlag = START_NUMBER;
	}
	
	
	/**
	 * Create a new deserialized object that reads the beginning from the start
	 * time stamp to the end time stamp. This procedure can also be limited to
	 * not to read too many data sections.
	 * The range returned can be expressed with [starTime, endTime)
	 * 
	 * @param sensorID
	 *            the sensor if to read the data for
	 * @param sensorData
	 *            the list to insert the data into
	 * @param startTime
	 *            the oldest data to be read
	 * @param endTime
	 *            anything before this data will be read
	 * @param maxNumberOfValues
	 *            is the maximum amount of data sets to be read. If this is set
	 *            to 0 there will be no limit and all found data will be read
	 * @throws IllegalArgumentException
	 *             if maxNumberOfValues is less than 0
	 */
	public SensorDataDeSerializer(int sensorID, java.util.List<SensorData> sensorData, int startTime, int endTime, int maxNumberOfValues) {
		this.sensorID = sensorID;
		this.context = GarmentOSService.getContext();
		this.sensorData = sensorData;
		
		if(maxNumberOfValues < 0)
			throw new IllegalArgumentException("Reading less than 0 element is not possible");		
		else if(maxNumberOfValues == 0)
			this.jobFlag = START_END_UNLIMITED;
		else
			this.jobFlag = START_END_LIMIT;
		
		this.startTime = startTime;
		this.endTime = endTime;
		this.noDatasetsToRead = maxNumberOfValues;
	}
	
	/**
	 * Start the deserialization process. This procedure will execute
	 * asynchronously and return a worker ID. By calling
	 * {@link SensorDataDeSerializer#jobFinsihed(long)} it can be checked
	 * whether the job has terminated or is still running.
	 * 
	 * @return the worker id for the current job
	 */
	public long work() {
		Thread th = new Thread(this);
		this.threadID = th.getId();		
		th.start();
		synchronized (activeWorkers) {
			activeWorkers.add(this.threadID);
		}
		return this.threadID;
	}
	
	/**
	 * <p>
	 * Check whether the current job is still running or has already finished
	 * executing.
	 * </p>
	 * <p>
	 * Note for any random value that is no worker id this function will return
	 * true. So if this function returns false the job is still running,
	 * otherwise there is no job with the given id or the job has already been
	 * finished.
	 * </p>
	 * 
	 * @param id
	 *            the id of the worker
	 * @return false if the job is still running, true otherwise
	 */
	public static boolean jobFinsihed(long id) {
		synchronized (activeWorkers) {		
			return !activeWorkers.contains(id);
		}
	}

	
	@Override
	public void run() {
		try {			
			int currentFileLength = 0;
			int numberOfDataSetsInFile = 0;
			int dataDimension = 0;
			int numberOfReadingIterations = 0;
			
			//
			// If the file does not exist we can not read anything 
			//
			file = new java.io.File(this.context.getFilesDir(), String.valueOf(sensorID));
			if(!file.exists()) {
				android.util.Log.d("orDEBUG", "File does not exist");
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
			android.util.Log.d("orDEBUG", "Open file, length " + currentFileLength + " " +dataDimension);
			numberOfDataSetsInFile = (currentFileLength - 8) / ((dataDimension + 1) * 4); 
			
			
			//
			// Now we have to check what working flag we have set and what we actually shall do
			//
			switch(jobFlag) {		
				
				//
				// We want to read the latest data fields
				//
				case LATEST_DATA: {
					
					//
					// If there are less data sets available than we want to read,
					// we want to read them all otherwise just set up the file pointer
					//
					if(numberOfDataSetsInFile < noDatasetsToRead) {
						raf.seek(8);
						numberOfReadingIterations = numberOfDataSetsInFile;
					} else {
						raf.seek(currentFileLength - (noDatasetsToRead) * (dataDimension + 1) * 4);
						numberOfReadingIterations = noDatasetsToRead;						
					}
					break;
				}						
					
				
				
				//
				// In this case we have got our time stamp to begin and a number of
				// elements to read
				//
				case START_NUMBER: {
					// The size of a dataChunk, 4 bytes per value, 1 for time,
					// dataDimension for data
					int chunkSize = ( dataDimension + 1 ) * 4;
					
					// Get the position of the start time 
					int timePos = searchNotOlder(this.raf, this.startTime, 8, currentFileLength, chunkSize);
					
					// timePos as 0 indicates an error, since the first 8 byte shall never
					// be searched by this procedure 
					if(timePos == 0)
						break;					
				
					// Set the file pointer to the correct position
					// and set the number of reading iterations. If there are less
					// elements left to be read than we shall read read all
					// remaining, otherwise read the given amount of values
					raf.seek(timePos);								
					numberOfReadingIterations = (currentFileLength - timePos) / chunkSize;
					numberOfReadingIterations = numberOfReadingIterations < noDatasetsToRead ? numberOfReadingIterations : noDatasetsToRead;					
					break;
				}
				
				//
				// We want to search 
				//
				case START_END_LIMIT:
				case START_END_UNLIMITED:
				{
					// The size of a dataChunk, 4 bytes per value, 1 for time,
					// dataDimension for data
					int chunkSize = ( dataDimension + 1 ) * 4;
					
					// Get the position of the start time and check for illegal return value
					int timePos = searchNotOlder(this.raf, this.startTime, 8, currentFileLength, chunkSize);
					if(timePos == 0)
						break;
					
					// Now search for the latest data that is older than the end
					// time and check for illegal return
					int upperTimePos = searchNotYounger(this.raf, this.endTime, 8, currentFileLength, chunkSize);					
					if(upperTimePos == 0)
						break;
					
					raf.seek(timePos);
					
					int noElements = (upperTimePos - timePos) / chunkSize + 1;
					if(noElements < 0)
						break;
					
					if(jobFlag == START_END_LIMIT)
						numberOfReadingIterations = noElements > this.noDatasetsToRead ? this.noDatasetsToRead : noElements;				
					else
						numberOfReadingIterations = noElements;
					
					
					break;
				}

			}
			
			//
			// Now that we have set the random access file pointer and the
			// number of reading iterations we can read the data from the file
			//
			while(--numberOfReadingIterations >= 0) {
				float f[] = new float[dataDimension];
				int date = raf.readInt();
				for(int i = 0; i != dataDimension; ++i) {
					f[i] = raf.readFloat();
				}						
				sensorData.add(new SensorData(f, date));
			}
			
			
			//
			// Close the file
			//
			raf.close();
		} catch (java.io.IOException ioe) {
			android.util.Log.d("orDEBUG", "Exception " + ioe.getLocalizedMessage());
		}
		
		//
		// Remove the id to signal the job is finsihed
		//
		synchronized (activeWorkers) {
			activeWorkers.remove((Long)threadID);
		}
	}
	
	
	/**
	 * Search in the given file for the latest time stamp that is not older than
	 * the given one 
	 * 
	 * @param raf
	 *            the file to search the time stamp in
	 * @param value
	 *            the time to search for
	 * @param offset
	 *            the offset from the beginning of the file
	 * @param fileSize
	 *            the total file size
	 * @param chunkSize
	 *            the chunk size
	 * @return the file pointer
	 * @throws java.io.IOException
	 */
	private static int searchNotOlder(java.io.RandomAccessFile raf, int value, int offset, int fileSize, int chunkSize) throws java.io.IOException {		
		int currentPos = offset - chunkSize;
		int currentValue = 0;
		int max = 0;
		
		raf.seek(fileSize-chunkSize);
		max = raf.readInt();
		
		// There is no data not older than the given value
		if(max < value)
			return 0;
		
		while(currentValue < value) {
			raf.seek(currentPos += chunkSize);
			currentValue = raf.readInt();
		}
		
		return currentPos;
	}
	
	/**
	 * Search in the given file for the newest time stamp that is older than the
	 * given one
	 * 
	 * @param raf
	 *            the file to search the time stamp in
	 * @param value
	 *            the time to search for
	 * @param offset
	 *            the offset from the beginning of the file
	 * @param fileSize
	 *            the total file size
	 * @param chunkSize
	 *            the chunk size
	 * @return the file pointer
	 * @throws java.io.IOException
	 */
	private static int searchNotYounger(java.io.RandomAccessFile raf, int value, int offset, int fileSize, int chunkSize) throws java.io.IOException {
		int currentPos = fileSize - chunkSize;
		int currentValue = Integer.MAX_VALUE;
		int min = 0;
		
		raf.seek(offset);
		min = raf.readInt();
		
		// There is no data not younger than the given one
		if(min > value)
			return 0;
		
		while(currentValue > value) {
			raf.seek(currentPos -= chunkSize);
			currentValue = raf.readInt();
		}
		
		return currentPos;
	}	
}
