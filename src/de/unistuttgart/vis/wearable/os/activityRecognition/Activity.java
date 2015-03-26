/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.unistuttgart.vis.wearable.os.storage.ActivityStorage;

public class Activity {
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

	public void saveActivity() {
		new ActivityStorage(activityTimes.get(activityTimes.size() - 1)[0],
				this.activityEnum);
	}
}
