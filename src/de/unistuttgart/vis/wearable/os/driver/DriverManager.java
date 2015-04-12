/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.driver;

import android.util.SparseArray;
import de.unistuttgart.vis.wearable.os.api.IGarmentDriver;
import de.unistuttgart.vis.wearable.os.internalapi.PGarmentDriver;
import de.unistuttgart.vis.wearable.os.utils.Constants;

import java.util.concurrent.Semaphore;

/**
 * @author roehrdor
 */
public class DriverManager {
    private static final SparseArray<IGarmentDriver> drivers = new SparseArray<IGarmentDriver>();
    private static final SparseArray<PGarmentDriver> pdrivers = new SparseArray<PGarmentDriver>();
    private static int maxID = 0;
    private static final Semaphore lock = new Semaphore(1);

    /**
     * Add a new driver
     *
     * @param driver the new driver to be added
     * @return the id of the driver
     */
    public static int addDriver(IGarmentDriver driver) {
        int num = Constants.ILLEGAL_VALUE;
        lock.acquireUninterruptibly();
        if(driver != null) {
            num = ++maxID;
            String name = null;
            try {
                driver.setID(num);
                name = driver.getDriverName();
            } catch (android.os.RemoteException e) {
            }
            if (name != null) {
                drivers.put(num, driver);
                pdrivers.put(num, new PGarmentDriver(num, name));
            }
        }
        lock.release();
        return num;
    }

    /**
     * Get the driver with the given id or null if the a driver for the id does not exist
     *
     * @param driverID the driver id to get the driver for
     * @return the driver or null
     */
    public static IGarmentDriver getDriverByID(int driverID) {
        return drivers.get(driverID);
    }

    /**
     * Get the parcelable driver objects that contain the name and id of the driver
     *
     * @return the parcelable driver object
     */
    public static PGarmentDriver[] getPDrivers() {
        final int LEN = drivers.size();
        PGarmentDriver[] garmentDrivers = new PGarmentDriver[LEN];
        for(int i = 0; i != LEN; ++i)
            garmentDrivers[i] = pdrivers.get(i);
        return garmentDrivers;
    }

    /**
     * Get all the drivers
     *
     * @return all the drivers in an array
     */
    public static IGarmentDriver[]  getDrivers() {
        final int LEN = drivers.size();
        IGarmentDriver[] garmentDrivers = new IGarmentDriver[LEN];
        for(int i = 0; i != LEN; ++i)
            garmentDrivers[i] = drivers.get(i);
        return garmentDrivers;
    }
}
