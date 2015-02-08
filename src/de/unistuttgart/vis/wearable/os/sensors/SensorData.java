package de.unistuttgart.vis.wearable.os.sensors;

import java.io.Serializable;
import java.util.Date;

/**
 * SensorData holds the value of one sensor measurement and the timestamp when
 * the measurement was taken.
 *
 * @author pfaehlfd
 */
public class SensorData implements Serializable{
    private static final long serialVersionUID = 1411769527157941008L;

    double[] data;
    Date date;

    public SensorData(double[] data, Date date) {
        this.data = data;
        this.date = date;
    }

    public double[] getData() {
        return data;
    }

    public Date getDate() {
        return date;
    }
}
