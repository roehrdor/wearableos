package de.unistuttgart.vis.wearable.os.api;

import android.os.Parcel;

public class BaseCallbackObject implements android.os.Parcelable {

	public static final android.os.Parcelable.Creator<BaseCallbackObject> CREATOR = new android.os.Parcelable.Creator<BaseCallbackObject>() {

		@Override
		public BaseCallbackObject createFromParcel(Parcel source) {
			int objectID = source.readInt();
			BaseCallbackObject ret = null;
			switch (objectID) {
			case 0:
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {		
	}
}
