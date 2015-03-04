package de.unistuttgart.vis.wearable.os.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Vector;

import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * @author pfaehlfd
 */
public class InternalSensors implements SensorEventListener {
    private static InternalSensors instance = null;

    private final float DEFAULT_SMOOTHNESS = 0.2f;
    private final int DEFAULT_SAMPLERATE = 30;
    private final int DEFAULT_SAVEPERIOD = 1000;
    private final int GPS_SENSOR_ID = 64;

    private android.hardware.SensorManager sensorManager;

    private android.hardware.Sensor androidAccelerometerSensor;
    private android.hardware.Sensor androidMagneticFieldSensor;
    private android.hardware.Sensor androidGyroscopeSensor;
    private android.hardware.Sensor androidLightSensor;
    private android.hardware.Sensor androidPressureSensor;
    private android.hardware.Sensor androidProximitySensor;
    private android.hardware.Sensor androidGravitySensor;
    private android.hardware.Sensor androidRotationVectorSensor;
    private android.hardware.Sensor androidRelativeHumiditySensor;
    private android.hardware.Sensor androidAmbientTemperatureSensor;

    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;
    private Sensor gyroscopeSensor;
    private Sensor lightSensor;
    private Sensor pressureSensor;
    private Sensor proximitySensor;
    private Sensor gravitySensor;
    private Sensor rotationVectorSensor;
    private Sensor relativeHumiditySensor;
    private Sensor ambientTemperatureSensor;
    private Sensor gpsSensor;

    LocationManager locationManager;

    // Define a listener that responds to location updates
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location
            // provider.
            gpsChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    /**
     * Creates and saves the instance of InternalSensors.
     * Loads the internal sensors from the storage,
     * creates the new supported internal sensors
     * and saves them to the SensorManagers sensor list.
     */
    public InternalSensors(Context context) {
        InternalSensors.instance = this;
        sensorManager = (android.hardware.SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        getAndroidSensors();

        Vector<Sensor> internalSensorsFromStorage = new Vector<Sensor>();
        for(Sensor sensor : SensorManager.getAllSensors()) {
            if (sensor.getSensorID() <= SensorManager.MAXIMUM_INTERNAL_SENSOR_ID) {
                internalSensorsFromStorage.add(sensor);
            }
        }

        getInternalSensorsFromStorage(internalSensorsFromStorage);
        createNewInternalSensors();
    }

    /**
     * returns the instance of InternalSensors.
     * The constructor must be called one time before getInstance.
     * Otherwise this method will throw a UnsupportedOperationException.
     */
    public static InternalSensors getInstance() {
        if (InternalSensors.instance == null) {
            throw new UnsupportedOperationException();
        }
        return InternalSensors.instance;
    }

    /**
     * Saves the android Sensors to this class.
     */
    private void getAndroidSensors() {
        androidAccelerometerSensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        androidMagneticFieldSensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_MAGNETIC_FIELD);
        androidGyroscopeSensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_GYROSCOPE);
        androidLightSensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);
        androidPressureSensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_PRESSURE);
        androidProximitySensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_PROXIMITY);
        androidGravitySensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_GRAVITY);
        androidRotationVectorSensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR);
        androidRelativeHumiditySensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY);
        androidAmbientTemperatureSensor = sensorManager
                .getDefaultSensor(android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    /**
     * Saves the given senors (from the storage) to the Sensor objects in this class,
     * depending on the SensorType of  each sensor
     */
    private void getInternalSensorsFromStorage(Vector<Sensor> internalSensorsFromDB) {
        for (Sensor sensor : internalSensorsFromDB) {
            if (sensor != null) {
                if (sensor.getSensorType() == SensorType.ACCELEROMETER) {
                    accelerometerSensor = sensor;
                } else if (sensor.getSensorType() == SensorType.MAGNETIC_FIELD) {
                    magneticFieldSensor = sensor;
                } else if (sensor.getSensorType() == SensorType.GYROSCOPE) {
                    gyroscopeSensor = sensor;
                } else if (sensor.getSensorType() == SensorType.LIGHT) {
                    lightSensor = sensor;
                } else if (sensor.getSensorType() == SensorType.PRESSURE) {
                    pressureSensor = sensor;
                } else if (sensor.getSensorType() == SensorType.PROXIMITY) {
                    proximitySensor = sensor;
                } else if (sensor.getSensorType() == SensorType.GRAVITY) {
                    gravitySensor = sensor;
                } else if (sensor.getSensorType() == SensorType.ROTATION_VECTOR) {
                    rotationVectorSensor = sensor;
                } else if (sensor.getSensorType() == SensorType.RELATIVE_HUMIDITY) {
                    relativeHumiditySensor = sensor;
                } else if (sensor.getSensorType() == SensorType.TEMPERATURE) {
                    ambientTemperatureSensor = sensor;
                } else if (sensor.getSensorType() == SensorType.GPS_SENSOR) {
                    gpsSensor = sensor;
                }
                SensorManager.addNewSensor(sensor);
                Log.d("fpDEBUG", "Loaded Sensor from storage " + sensor.getDisplayedSensorName());
                if (sensor.isEnabled()) {
                    enableInternalSensor(sensor);
                }
            }
        }
    }

    /**
     * creates the sensors supported by the device,
     * which are not yet in the storage.
     * The sensorID of each sensor will be the same
     * as in Android.
     * (see android.hardware.Sensor
     * http://developer.android.com/reference/android/hardware/Sensor.html)
     */
    private void createNewInternalSensors() {
        int numberOfCreatedSensors = 0;
        if (accelerometerSensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER).size() > 0) {
            accelerometerSensor = new Sensor(androidAccelerometerSensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_accelerometer",
                    SensorType.ACCELEROMETER, MeasurementSystems.METRICAL,
                    MeasurementUnits.METER_PER_SECONDS_SQUARE);
            numberOfCreatedSensors++;
        }
        if (magneticFieldSensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_MAGNETIC_FIELD).size() > 0) {
            magneticFieldSensor = new Sensor(androidMagneticFieldSensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_magneticfiled_sensor",
                    SensorType.MAGNETIC_FIELD, MeasurementSystems.TESLA,
                    MeasurementUnits.MICRO);
            numberOfCreatedSensors++;
        }
        if (gyroscopeSensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_GYROSCOPE).size() > 0) {
            gyroscopeSensor = new Sensor(androidGyroscopeSensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_gyroscope",
                    SensorType.GYROSCOPE, MeasurementSystems.RADIAN,
                    MeasurementUnits.NONE);
            numberOfCreatedSensors++;
        }
        if (lightSensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_LIGHT).size() > 0) {
            lightSensor = new Sensor(androidLightSensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_light_sensor",
                    SensorType.LIGHT, MeasurementSystems.LUX,
                    MeasurementUnits.NONE);
            numberOfCreatedSensors++;
        }
        if (pressureSensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_PRESSURE).size() > 0) {
            pressureSensor = new Sensor(androidPressureSensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_pressure_sensor",
                    SensorType.PRESSURE, MeasurementSystems.PASCAL,
                    MeasurementUnits.HECTO);
            numberOfCreatedSensors++;
        }
        if (proximitySensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_PROXIMITY).size() > 0) {
            proximitySensor = new Sensor(androidProximitySensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_proximity_sensor",
                    SensorType.PROXIMITY, MeasurementSystems.METRICAL,
                    MeasurementUnits.CENTI);
            numberOfCreatedSensors++;
        }
        if (gravitySensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_GRAVITY).size() > 0) {
            gravitySensor = new Sensor(androidGravitySensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_gravity_sensor",
                    SensorType.GRAVITY, MeasurementSystems.METRICAL,
                    MeasurementUnits.METER_PER_SECONDS_SQUARE);
            numberOfCreatedSensors++;
        }
        if (rotationVectorSensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_ROTATION_VECTOR).size() > 0) {
                // TODO stimmt hier die Einheit usw?
            rotationVectorSensor = new Sensor(androidRotationVectorSensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_rotation_vector",
                    SensorType.ROTATION_VECTOR, MeasurementSystems.RADIAN,
                    MeasurementUnits.NONE);
            numberOfCreatedSensors++;
        }
        if (relativeHumiditySensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY).size() > 0) {
            relativeHumiditySensor = new Sensor(androidRelativeHumiditySensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_relative_humidity_sensor",
                    SensorType.RELATIVE_HUMIDITY, MeasurementSystems.PERCENT,
                    MeasurementUnits.NONE);
            numberOfCreatedSensors++;
        }
        if (ambientTemperatureSensor == null &&
                sensorManager.getSensorList(android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE).size() > 0) {
            ambientTemperatureSensor = new Sensor(androidAmbientTemperatureSensor, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_ambient_temperature_sensor",
                    SensorType.TEMPERATURE, MeasurementSystems.TEMPERATURE,
                    MeasurementUnits.NONE);
            numberOfCreatedSensors++;
        }
        if (gpsSensor == null) {
            gpsSensor = new Sensor(GPS_SENSOR_ID, DEFAULT_SAMPLERATE,
                    DEFAULT_SAVEPERIOD, DEFAULT_SMOOTHNESS, "internal_gps_sensor",
                    SensorType.GPS_SENSOR, MeasurementSystems.GPS,
                    MeasurementUnits.NONE);
            numberOfCreatedSensors++;
        }
        Log.d("fpDEBUG", "Created " + numberOfCreatedSensors + " Sensors");
    }

    /**
     * Enables the given internal sensor.
     * => sets enabled true and registers the according listener.
     */
    protected void enableInternalSensor(Sensor sensor) {
        if (!sensor.isInternalSensor()) {
            throw new UnsupportedOperationException();
        }

        if (sensor.getSensorType() == SensorType.GPS_SENSOR) {

            // check whether the network provider does exist
            // and is enabled, otherwise do not try to requestLocationUpdates
            if (locationManager.getAllProviders().contains(
                    LocationManager.NETWORK_PROVIDER)
                    && locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                // Register the listener with the Location Manager to receive
                // location updates
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                    }
                });
            }
        } else {
            sensorManager.registerListener(
                    this,
                    getAndroidSensorBySensor(sensor),
                    1000000 / sensor.getSampleRate());
        }
        Log.d("fpDEBUG", "Enabled " + sensor.getDisplayedSensorName());
    }

    /**
     * Disables the given internal sensor.
     * => sets enabled false and unregisters the according listener.
     */
    public void disableInternalSensor(Sensor sensor) {
        if (!sensor.isInternalSensor()) {
            throw new UnsupportedOperationException();
        }

        if (sensor.getSensorType() == SensorType.GPS_SENSOR) {
            locationManager.removeUpdates(mLocationListener);
        } else {
            getAndroidSensors();
            sensorManager.unregisterListener(this, getAndroidSensorBySensor(sensor));
        }
        Log.d("fpDEBUG", "Disabled " + sensor.getDisplayedSensorName());
    }

    /**
     * returns the android sensor according to the given sensor object.
     */
    private android.hardware.Sensor getAndroidSensorBySensor(Sensor sensor) {
        switch (sensor.getSensorType()) {
            case ACCELEROMETER:
                return androidAccelerometerSensor;
            case GRAVITY:
                return androidGravitySensor;
            case GYROSCOPE:
                return androidGyroscopeSensor;
            case LIGHT:
                return androidLightSensor;
            case MAGNETIC_FIELD:
                return androidMagneticFieldSensor;
            case PRESSURE:
                return androidPressureSensor;
            case PROXIMITY:
                return androidProximitySensor;
            case RELATIVE_HUMIDITY:
                return androidRelativeHumiditySensor;
            case ROTATION_VECTOR:
                return androidRotationVectorSensor;
            case TEMPERATURE:
                return androidAmbientTemperatureSensor;
            default:
                break;
        }
        return null;
    }

    /**
     * returns the sensor object according to the given android sensor.
     */
    private Sensor getSensorByAndroidSensor(android.hardware.Sensor sensor) {
        switch (sensor.getType()) {
            case android.hardware.Sensor.TYPE_ACCELEROMETER:
                return accelerometerSensor;
            case android.hardware.Sensor.TYPE_GRAVITY:
                return gravitySensor;
            case android.hardware.Sensor.TYPE_GYROSCOPE:
                return gyroscopeSensor;
            case android.hardware.Sensor.TYPE_LIGHT:
                return lightSensor;
            case android.hardware.Sensor.TYPE_MAGNETIC_FIELD:
                return magneticFieldSensor;
            case android.hardware.Sensor.TYPE_PRESSURE:
                return pressureSensor;
            case android.hardware.Sensor.TYPE_PROXIMITY:
                return proximitySensor;
            case android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY:
                return relativeHumiditySensor;
            case android.hardware.Sensor.TYPE_ROTATION_VECTOR:
                return rotationVectorSensor;
            case android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE:
                return ambientTemperatureSensor;
            default:
                break;
        }
        return null;
    }

    /**
     * saves the incoming sensor values to the according sensor object.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        android.hardware.Sensor androidSensor = sensorEvent.sensor;
        Sensor sensor = getSensorByAndroidSensor(androidSensor);
        if (sensor == null) {
            return;
        }
        int dimensions = sensor.getSensorType().getDimension();

        if (sensor.isEnabled()) {
            float[] data = new float[dimensions];
            System.arraycopy(sensorEvent.values, 0, data, 0, dimensions);
            sensor.addRawData(new SensorData(Utils.getCurrentLongUnixTimeStamp(),
                    sensor.getSensorType().getDimension(), data));
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {}

    /**
     * saves the incoming gps sensor value to the gps sensor object.
     */
    private void gpsChanged(Location location) {
        try {
            float[] data = {(float) location.getLatitude(), (float) location.getLongitude(),
                    location.getAccuracy(), location.getSpeed()};
            gpsSensor.addRawData(new SensorData (data, Utils.getCurrentLongUnixTimeStamp()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
