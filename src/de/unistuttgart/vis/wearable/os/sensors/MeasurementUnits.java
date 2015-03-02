package de.unistuttgart.vis.wearable.os.sensors;

import java.util.Arrays;

/**
 * @author pfaehlfd
 */
public enum MeasurementUnits {
    NONE(new MeasurementSystems[]{MeasurementSystems.RADIAN, MeasurementSystems.PERCENT, MeasurementSystems.TEMPERATURE, MeasurementSystems.LUX, MeasurementSystems.GPS}, 1, false, ""),
    HECTO(new MeasurementSystems[]{MeasurementSystems.PASCAL}, 0.01, false, "h"),
    DEKA(new MeasurementSystems[]{MeasurementSystems.PASCAL}, 0.1, false, "da"),
    KILO(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.001, false, "K"),
    MEGA(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.000001, false, "M"),
    GIGA(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.000000001, false, "G"),
    TERA(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.000000000001, false, "T"),
    PETA(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.000000000000001, false, "P"),
    MILLI(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.001, true, "m"),
    CENTI(new MeasurementSystems[]{MeasurementSystems.METRICAL}, 0.01, true, "c"),
    MICRO(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.000001, true, "µ"),
    NANO(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.000000001, true, "n"),
    PICO(new MeasurementSystems[]{MeasurementSystems.METRICAL, MeasurementSystems.PASCAL, MeasurementSystems.TESLA}, 0.000000000001, true, "p"),
    INCH(new MeasurementSystems[]{MeasurementSystems.ANGLOSAXON}, 12, false, "in"),
    FOOT(new MeasurementSystems[]{MeasurementSystems.ANGLOSAXON}, 1, true, "ft"),
    YARD(new MeasurementSystems[]{MeasurementSystems.ANGLOSAXON}, 3, true, "yd"),
    MILE(new MeasurementSystems[]{MeasurementSystems.ANGLOSAXON}, 5280, true, "mi"),

    METER_PER_SECONDS_SQUARE(new MeasurementSystems[]{MeasurementSystems.METRICAL}, 0.000000001, true, "m/s^2");


    MeasurementSystems[] measurementSystems;
    double factor;
    boolean multiply;
    String acronym;


    MeasurementUnits(MeasurementSystems[] measurementSystems, double factor, boolean multiply, String acronym) {
        this.measurementSystems = measurementSystems;
        this.factor = factor;
        this.multiply = multiply;
        this.acronym = acronym;
    }

    /**
     * returns the factor to multiply or divide with to get the source value
     * getMultiplyOrDevide tells you if you have to multiply or divide
     * @return
     */
    public double getFactor() {
        return factor;
    }

    /**
     * returns true if you have to multiply with the factor the get the source value in the source unit
     * returns false if you have to divide by the factor the get the source value in the source unit
     *
     * e.g. kilo returns 0.001 and false so you have to divide by 0.001!
     *
     * @return	true if you have to multiply
     * 			or false if you have to divide
     */
    public boolean getMultiplyOrDevide() {
        return multiply;
    }

    /**
     * returns the Acronym of the MeasurementUnit
     * e.g. "m" for MILLI-meter
     * @return
     */
    public String getAcronym() {
        return acronym;
    }

    /**
     * returns the measurementSystem which the measurementUnit belongs to
     * e.g. METRICAL for MILLI
     * @return	a MeasurementSystems element
     */
    public MeasurementSystems[] getMeasurementSystems() {
        return measurementSystems;
    }

    /**
     * returns true if the MeasurementUnit contains the given MeasurementSystem
     * @param measurementSystem	a MeasurementSystem element
     */
    public boolean containsMeasurementSystem(MeasurementSystems measurementSystem) {
        return Arrays.asList(this.measurementSystems).contains(measurementSystem);
    }
}
