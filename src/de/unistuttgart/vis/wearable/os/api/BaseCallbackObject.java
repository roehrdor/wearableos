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
 * BaseCallbackObject class represents the type of objects that are send back by
 * callback from the garment OS service. The callback object can also be any kind
 * of object that is inherited from this class.
 * 
 * @author roehrdor
 */
public class BaseCallbackObject implements android.os.Parcelable {
	
	public static final android.os.Parcelable.Creator<BaseCallbackObject> CREATOR = new android.os.Parcelable.Creator<BaseCallbackObject>() {

		@Override
		public BaseCallbackObject createFromParcel(Parcel source) {
			// object id tells us which object we should use
			int objectID = source.readInt();
			BaseCallbackObject ret = null;
			switch (objectID) {
			case 0:
				ret = new CallBackObject(source);
				break;
            case 1:
                ret = new ValueChangedCallback(source);
                break;
            case 2:
                ret = new ActivityChangedCallback(source);
                break;
			default:
				break;
			}
			return ret;
		}

		@Override
		public BaseCallbackObject[] newArray(int size) {
			return new BaseCallbackObject[size];
		}
		
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {		
	}
}
