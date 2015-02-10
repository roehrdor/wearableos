/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.sensorDriver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Load a sensor driver from a given file
 *
 * @author roehrdor
 */
public class GenericSensorDriverLoader {
    private GenericSensorDriverLoader() {}

    /**
     * Create a new Generic Driver object from the given file
     *
     * @param driverFile           the file to read the driver from
     * @param sensorID             the sensor ID of the sensor the driver shall be used for
     * @param sensorDriverCallback if this callback handle is set the according function is called every time we get new sensor data
     * @return the created driver object or null if the file was not valid
     */
    public static GenericSensorDriver cretaeFromFile(String driverFile, int sensorID, SensorDriverCallback sensorDriverCallback) {
        GenericSensorDriver genericDriver = null;
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String line;
        int noDimensionsRead = 0;

        String driverName = null;
        int noDimensions = 0;
        int dataChunkSize = 0;
        byte[] dimensions = null;

        try {
            //
            // Read the whole file line by line
            //
            fileReader = new FileReader(driverFile);
            bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                //
                // Check for driver name
                //
                if(line.contains("DRIVER_NAME")) {
                    driverName = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                }

                //
                // Check for data chunk size
                //
                if(line.contains("DATA_CHUNK_SIZE")) {
                    dataChunkSize = Integer.valueOf(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));
                }

                //
                // check for dimensions
                //
                if(line.contains("DIMENSIONS")) {
                    noDimensions = Integer.valueOf(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));
                    dimensions = new byte[noDimensions << 0x1];
                }

                //
                // check for the chunks (dimensions) and their offset
                //
                if(line.contains("OFFSET")) {
                    dimensions[noDimensionsRead++] = Byte.valueOf(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));
                }

                //
                // check for the chunks (dimensions) and their size
                //
                if(line.contains("SIZE") && !line.contains("DATA_CHUNK_SIZE")) {
                    dimensions[noDimensionsRead++] = Byte.valueOf(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));
                }
            }

            //
            // Check whether the given file was valid, if so create the new object
            //
            if(GenericSensorDriver.checkFailure(driverName, noDimensions, dataChunkSize, dimensions) == 0)
                genericDriver = new GenericSensorDriver(driverName, noDimensions, dataChunkSize, dimensions, sensorID, sensorDriverCallback);

        } catch (IOException ioe) {} finally {
            if(fileReader != null)
                try {
                    fileReader.close();
                }catch (IOException ioe) {}
            if(bufferedReader != null)
                try {
                    bufferedReader.close();
                }catch (IOException ioe) {}
        }

        return genericDriver;
    }
}




// Example driver file
/*
GARMENTOS_SENSOR_DRIVER {
 DRIVER_NAME:  "Demo Driver",
 DIMENSIONS:   "2",
 DATA_CHUNK_SIZE: "8",

 DIMENSION1 {
  OFFSET:   "1",
  SIZE:   "3",
 }

 DIMENSION2 {
  OFFSET:   "2",
  SIZE:    "2",
 }
}
 */