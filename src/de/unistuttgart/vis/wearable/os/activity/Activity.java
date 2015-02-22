package de.unistuttgart.vis.wearable.os.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Activity implements Parcelable,Serializable {
	private static final long serialVersionUID = -4467998334536435851L;
	private List<Date[]> activityTimes = new ArrayList<Date[]>();
	private ActivityEnum activityEnum;

	public Activity() {

	}

	public Activity(List<Date[]> activityTimes) {
		this.activityTimes = activityTimes;
	}

	public void addPeriod(Date begin, Date end) {
		Date[] durationTime = new Date[2];
		durationTime[0] = begin;
		durationTime[1] = end;
		activityTimes.add(durationTime);
	}

	public void setActivityEnum(ActivityEnum activityEnum) {
		this.activityEnum = activityEnum;
	}

	public ActivityEnum getActivityEnum() {
		return activityEnum;
	}

	public Date[] lastDatePeriod() {
		return activityTimes.get(activityTimes.size() - 1);
	}

	public List<Date[]> getActivityTimes() {
		return activityTimes;
	}

	public static final Parcelable.Creator<Activity> CREATOR = new Parcelable.Creator<Activity>() {
		@Override
		public Activity createFromParcel(Parcel source) {
			Activity ret = new Activity();
			int size = source.readInt();
			for (int i = 0; i != size; ++i)
				ret.activityTimes.add((Date[]) source.readSerializable());
			ret.activityEnum = (ActivityEnum) source.readSerializable();
			return ret;
		}

		@Override
		public Activity[] newArray(int size) {
			return new Activity[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.activityTimes.size());
		for (Date[] d : this.activityTimes)
			dest.writeSerializable(d);
		dest.writeSerializable(activityEnum);
	}
}
