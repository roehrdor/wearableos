/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.internalapi;

/**
 * Parcelable version of the {@link de.unistuttgart.vis.wearable.os.parcel.PGarmentDriver} class that can be used in the
 * internal API
 * @author roehrdor
 */
public class PGarmentDriver extends de.unistuttgart.vis.wearable.os.parcel.PGarmentDriver implements android.os.Parcelable {

    /**
     * Create a new PGarmentDriver object with the given driver id and the given driver name
     * @param driverID the driver id
     * @param driverName the driver name
     */
    public PGarmentDriver(int driverID, String driverName) {
        super(driverID, driverName);
    }


    // =====================================================================
    //
    // Parcel procedures
    //
    // =====================================================================
    //
    // Creator Object that is used to transmit objects
    //
    public static final android.os.Parcelable.Creator<PGarmentDriver> CREATOR = new android.os.Parcelable.Creator<PGarmentDriver>() {
        @Override
        public PGarmentDriver createFromParcel(android.os.Parcel source) {
            return new PGarmentDriver(source.readInt(), source.readString());
        }

        @Override
        public PGarmentDriver[] newArray(int size) {
            return new PGarmentDriver[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(this.driverID);
        dest.writeString(this.driverName);
    }
}
