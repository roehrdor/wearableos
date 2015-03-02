/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.api;

import android.os.Parcel;

/**
 * Callback object that is actually used to be sent back. To represent a value
 * this class has an object as attribute which can be used to identify any
 * change or modification or signal any event.
 * 
 * @author roehrdor
 */
public class CallBackObject extends BaseCallbackObject {
	public Object value = new Object();

	/**
	 * Create a new call back object with the given value
	 * 
	 * @param value
	 *            the value to be used
	 */
	public CallBackObject(Object value) {
		this.value = value;
	}

	/**
	 * Create a new call back object from the given parcel
	 * 
	 * @param in the value to be used is read from the parcel
	 */
	public CallBackObject(Parcel in) {
		value = in.readInt();
	}

	public static final Creator<CallBackObject> CREATOR = new Creator<CallBackObject>() {
		@Override
		public CallBackObject createFromParcel(Parcel source) {
			return new CallBackObject(source);
		}

		@Override
		public CallBackObject[] newArray(int size) {
			return new CallBackObject[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// Signal the object being send is type of call back object
		dest.writeInt(0);
		dest.writeInt((Integer) value);
	}
}
