/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.properties;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to store the user properties such as the password for the encryption or the
 * path of the files.
 *
 * @author roehrdor
 */
public class Properties {
    // These are constant right now but could basically be changed dynamically
    public static java.io.File storageDirectory = new java.io.File("/data/data/de.unistuttgart.vis.wearable.os/files");

    //
    // This lock is used to synchronize concurrent access to the below data fields
    //
    public static final ReentrantLock FILE_STATUS_FIELDS_LOCK = new ReentrantLock(true);
    public static final AtomicInteger FILES_IN_USE = new AtomicInteger(0);
    public static final AtomicBoolean FILE_ARCHIVING = new AtomicBoolean(false);

    //
    // These constants are used to start the services
    //
    public static final String GARMENT_SERVICE = "de.unistuttgart.vis.wearable.os.service.GarmentOSSerivce";
    public static final String GARMENT_INTERNAL_SENSOR = "de.unistuttgart.vis.wearable.os.internalservice.GarmentOSServiceInternal";

}
