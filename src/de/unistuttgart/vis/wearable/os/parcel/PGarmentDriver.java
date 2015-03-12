/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.parcel;

/**
 * This class exists to identify a driver in the settings app
 * @author roehrdor
 */
public class PGarmentDriver {
    public int driverID;
    public String driverName;

    /**
     * Create a new PGarmentDriver object with the given driver id and the given
     * driver name
     * @param driverID the driver id for the driver
     * @param driverName the driver name for the driver
     */
    public PGarmentDriver(int driverID, String driverName) {
        this.driverID = driverID;
        this.driverName = driverName;
    }

    /**
     * Get the driver id
     * @return the driver id
     */
    public int getDriverID() {
        return driverID;
    }

    /**
     * Get the driver name
     * @return the driver name
     */
    public String getDriverName() {
        return driverName;
    }
}
