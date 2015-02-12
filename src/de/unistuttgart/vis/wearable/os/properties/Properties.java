/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.properties;

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
    // These constants are used to start the services
    //
    public static final String GARMENT_SERVICE = "de.unistuttgart.vis.wearable.os.service.GarmentOSSerivce";
    public static final String GARMENT_INTERNAL_SENSOR = "de.unistuttgart.vis.wearable.os.internalservice.GarmentOSServiceInternal";

}
