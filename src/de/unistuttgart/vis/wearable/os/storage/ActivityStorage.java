package de.unistuttgart.vis.wearable.os.storage;

import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityEnum;
import de.unistuttgart.vis.wearable.os.properties.Properties;
import de.unistuttgart.vis.wearable.os.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * @author roehrdor
 */
public class ActivityStorage {

    //
    // The file name the activities are stored into
    //
    public static final String FILE_NAME = "actvities";

    /**
     * Entry class for queueing activities with their time stamp are are to be
     * written to file
     */
    static class ActivityTime {
        ActivityEnum activity;
        long time;

        /**
         * Create a new entry object with the given time stamp and the given
         * activity
         *
         * @param time     the time the activity was performed at
         * @param activity the activity that has been performed
         */
        public ActivityTime(long time, ActivityEnum activity) {
            this.time = time;
            this.activity = activity;
        }
    }

    //
    // Queue the inputs before they are written to file
    //
    protected static final Vector<ActivityTime> activities = new Vector<ActivityTime>();

    //
    // Runner Thread doing all the work with writing the actual data to file
    // This Thread sleeps all the time as long as possible, if needed his sleep
    // is interrupted and the new data is finally written to the file
    //
    protected static final Thread runner = new Thread() {
        @Override
        public void run() {
            while (true) {
                //
                // Wait until the archiver process has terminated and then
                // tell the files are being used
                //
                Properties.FILE_STATUS_FIELDS_LOCK.lock();
                while (Properties.FILE_ARCHIVING.get()) {
                    Utils.sleepUninterrupted(200);
                }
                Properties.FILES_IN_USE.incrementAndGet();
                Properties.FILE_STATUS_FIELDS_LOCK.unlock();

                //
                // RandomAccessFile and file handle, as well as variable to store
                // the length of the file to append rather than overwrite
                //
                RandomAccessFile raf;
                File file;
                long currentFileLength;

                try {
                    file = new File(Properties.storageDirectory, FILE_NAME);
                    raf = new RandomAccessFile(file, "rw");
                    currentFileLength = file.length();
                    raf.seek(currentFileLength);

                    //
                    // Iterate over all the activities that are currently queue
                    //
                    while(!activities.isEmpty()) {
                        ActivityTime activity = activities.remove(0);

                        // Write the activity to the file
                        raf.writeLong(activity.time);
                        raf.writeInt(activity.activity.ordinal());
                    }

                    //
                    // Close the file
                    //
                    raf.close();

                } catch(IOException ioe) {
                }

                //
                // Tell we finished processing this file, therefore
                // decrement the count
                //
                Properties.FILES_IN_USE.decrementAndGet();

                //
                // Sleep until we have a new job that needs to be written
                //
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ie) {
                }
            }
        }
    };

    static {
        runner.start();
    }


    /**
     * Write a new Activity with time stamp to file
     *
     * @param time     the time the activity was started at
     * @param activity the activity that has been performed
     */
    public ActivityStorage(java.util.Date time, ActivityEnum activity) {
        this(time.getTime(), activity);
    }

    /**
     * Write a new Activity with time stamp to file
     *
     * @param time     the time the activity was started at
     * @param activity the activity that has been performed
     */
    public ActivityStorage(long time, ActivityEnum activity) {
        synchronized (activities) {
            ActivityTime activityTime = new ActivityTime(time, activity);
            activities.add(activityTime);
        }
        synchronized (runner) {
            runner.interrupt();
        }
    }

}
