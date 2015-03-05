package de.unistuttgart.vis.wearable.os.activity;

import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public enum ActivityEnum implements Parcelable {
	NOACTIVITY("No activity"),
	WALKING("Walking"),
	RUNNING("Running"),
	SITTING("Sitting"),
	STANDING("Standing"),
	LYING("Lying"), 
	CLIMBINGSTAIRS("Climbing stairs"), 
	DESCENDINGSTAIRS("Descending stairs"),
	SPRINTING("Sprinting"), 
	CYCLING("Cycling"), 
	DRIVING("Driving"),
	RIDINGBUS("Riding bus"),
	RIDINGELEVATOR("Riding elevator"),
	RIDINGESCALATOR("Riding escalator");

	private final String name;

	private ActivityEnum(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}
	
	public static boolean contains(String s) {
		for(ActivityEnum a : ActivityEnum.values()) {
			if(a.name().equals(s.toUpperCase(Locale.US))) {
				return true;
			}
		}
		return false;
	}

	public static final Parcelable.Creator<ActivityEnum> CREATOR = new Parcelable.Creator<ActivityEnum>() {
		@Override
		public ActivityEnum createFromParcel(Parcel source) {
			return ActivityEnum.values()[source.readInt()];
		}

		@Override
		public ActivityEnum[] newArray(int size) {
			return new ActivityEnum[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(ordinal());
	}

}
