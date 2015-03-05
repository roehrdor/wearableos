package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.Date;
import java.util.LinkedHashMap;

import de.unistuttgart.vis.wearable.os.internalapi.PSensorData;

/**
 * Contains all sensor data from all sensors in a specific time window with the
 * associated activity label and/or feature set depending on whether it is
 * training data or data to classify.
 *
 */
public class TimeWindow extends LinkedHashMap<String, float[][]> {
	private static final long serialVersionUID = -8506989075775065844L;
	// Activity of this time window
	private String activityLabel;
	// Feature set of this time window
	private FeatureSet featureSet;
	private Date begin, end;

	public TimeWindow(String activityLabel, Date begin, Date end) {
		if(activityLabel == null) {
			this.activityLabel = "dead (activity label was null)";
		} else {
			this.activityLabel = activityLabel;
		}
		this.begin = begin;
		this.end = end;
	}

	public void addSensorDataByID(String sid, float[][] values) {
		super.put(sid, values);
	}
	
	public float[][] getSensorData(String sid) {
		return this.get(sid);
	}

	public FeatureSet getFeatureSet() {
		return featureSet;
	}

	public void setFeatureSet(FeatureSet featureSet) {
		this.featureSet = featureSet;
	}

	public String getActivityLabel() {
		return activityLabel;
	}

	public void setActivityLabel(String activityLabel) {
		this.activityLabel = activityLabel;
	}

	public Date getBegin() {
		return begin;
	}

	public Date getEnd() {
		return end;
	}

	/*
	 * @deprecated Use addSensorDataByID(int sid) instead!
	 */
	@Deprecated
	public PSensorData put(Integer key, PSensorData value) {
		throw new UnsupportedOperationException(
				"Use addSensorDataByID(int sid) instead!");
	}
}
