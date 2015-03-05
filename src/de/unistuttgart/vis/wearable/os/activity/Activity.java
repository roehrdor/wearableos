package de.unistuttgart.vis.wearable.os.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Activity {
//	private static final long serialVersionUID = -4467998334536435851L;
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
}
