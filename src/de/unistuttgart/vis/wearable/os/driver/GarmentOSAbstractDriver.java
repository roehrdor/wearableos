/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.driver;

import android.os.RemoteException;
import de.unistuttgart.vis.wearable.os.api.IGarmentDriver;
import de.unistuttgart.vis.wearable.os.utils.Constants;

/**
 * This class represents the base driver class and shall be derived by any written driver.
 * To have the ability to uniquely identify the drivers the needed functions have been
 * already implemented.
 *
 * @author roehrdor
 */
public abstract class GarmentOSAbstractDriver extends IGarmentDriver.Stub {
    private int driverID = Constants.ILLEGAL_VALUE;

    @Override
    public final void setID(int id) throws RemoteException {
        this.driverID = id;
    }

    @Override
    public final int getID() throws RemoteException {
        return this.driverID;
    }
}
