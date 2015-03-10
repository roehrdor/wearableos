package de.unistuttgart.vis.wearable.os.driver;

import android.util.SparseArray;
import de.unistuttgart.vis.wearable.os.api.IGarmentDriver;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author roehrdor
 */
public class DriverManager {
    private static final SparseArray<IGarmentDriver> drivers = new SparseArray<IGarmentDriver>();
    private static final AtomicInteger maxID = new AtomicInteger(0);

    public static int addDriver(IGarmentDriver driver) {
        int num = maxID.getAndIncrement();
        try {
            driver.setID(num);
        } catch(android.os.RemoteException e) {
        }
        drivers.put(num, driver);
        return num;
    }

    public static IGarmentDriver[] getDrivers() {
        // TODO
        return null;
    }
}
