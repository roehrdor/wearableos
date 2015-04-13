/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.storage;

import de.unistuttgart.vis.wearable.os.activityRecognition.Activity;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityEnum;
import de.unistuttgart.vis.wearable.os.properties.Properties;

public class ActivityLoad implements Runnable {
	
	public static final String FILE_NAME = "actvities";
	
	private java.util.List<Activity> activities;
	
	private long threadID = 0;
	
	java.io.File file = null;

    private static final java.util.Set<Long> activeWorkers = new java.util.HashSet<Long>();

	public ActivityLoad(java.util.List<Activity> activities) {
		this.activities = activities;
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
	public static boolean jobFinished(long id) {
		synchronized (activeWorkers) {		
			return !activeWorkers.contains(id);
		}
	}
	
	
	
	@Override
    public void run() {
        try {
            int currentFileLength;
            int numberOfReadingIterations;
            boolean skip = false;

            //
            // If the file does not exist we can not read anything
            //
            file = new java.io.File(Properties.storageDirectory, String.valueOf(FILE_NAME));
            if(!file.exists()) {
                workerEnd(threadID);
                return;
            }

            //
            // Otherwise open the file as RandomAccessFile
            //
            java.io.RandomAccessFile raf = new java.io.RandomAccessFile(file, "rw");

            //
            // Based on the file size compute the number of entries in the file
            //
            currentFileLength = (int)file.length();
            raf.seek(0);
            numberOfReadingIterations = currentFileLength / 12;

            //
            // If not data fields are available, skip
            //
            if(currentFileLength <= 12)
                skip = true;

            if(!skip) {
            	boolean first = true;
            	java.util.Date dateBegin = new java.util.Date();
                //
                // Now that we have set the random access file pointer and the
                // number of reading iterations we can read the data from the file
                //
                while (--numberOfReadingIterations >= 0) {
                	if (first) {
						dateBegin = new java.util.Date(raf.readLong());
                        first = false;
					}
					ActivityEnum activityEnum = ActivityEnum.values()[raf.readInt()];
                	int i = 0;
                	for(Activity activity : activities) {
                		if(activityEnum == activity.getActivityEnum()) {
                			break;
                		}
                		i++;
                	}
                    if(numberOfReadingIterations != 0) {
                        java.util.Date dateEnd = new java.util.Date(raf.readLong());
                        activities.get(i).addPeriod(dateBegin, dateEnd);
                        dateBegin = dateEnd;
                    }
                }
            }
            //
            // Close the file
            //
            raf.close();
        } catch (java.io.IOException ioe) {
        }
        workerEnd(threadID);

    }
	
	/**
     * The worker finished, remove from active queue
     * @param threadID the thread that has finished
     */
    private void workerEnd(long threadID) {
        //
        // Remove the id to signal the job is finished
        //
        synchronized (activeWorkers) {
            activeWorkers.remove(threadID);
        }
    }
}
