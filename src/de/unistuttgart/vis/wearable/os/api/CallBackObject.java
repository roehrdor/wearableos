package de.unistuttgart.vis.wearable.os.api;

import android.os.Parcel;

public class CallBackObject extends BaseCallbackObject {
	Object value = new Object();

	public CallBackObject(Object value) {
		this.value = value;
	}

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
		dest.writeInt(0);
		dest.writeInt((Integer) value);
	}
}
