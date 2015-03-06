package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.Looper;
import android.util.Log;
import de.unistuttgart.vis.wearable.os.activityRecognition.NeuralNetworkManager.Status;
import de.unistuttgart.vis.wearable.os.activity.Activity;
import de.unistuttgart.vis.wearable.os.activity.ActivityEnum;
import de.unistuttgart.vis.wearable.os.activityRecognition.FeatureSet;
import de.unistuttgart.vis.wearable.os.activityRecognition.TimeWindow;
import de.unistuttgart.vis.wearable.os.api.ActivityChangedCallback;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
import de.unistuttgart.vis.wearable.os.service.GarmentOSService;

/**
 * TODO AutoSave when training and recognition stops
 * @author Tobias
 *
 */
public class ActivityRecognitionModule {
	
	private static final ActivityRecognitionModule instance;
	
	/**
	 * Create a singleton of the module, initialize the neural network manager and set the 
	 * current activity to no activity.
	 */
	static {
		try {
			instance = new ActivityRecognitionModule();
		} catch(Exception e) {
			throw new RuntimeException("Exception occured in creating singleton instance!");
		}
		instance.neuralNetworkManager = new NeuralNetworkManager(""/*
				GarmentOSService.getContext().getFilesDir().getAbsolutePath()*/);
		(instance.currentActivity = new Activity()).setActivityEnum(ActivityEnum.NOACTIVITY);
		instance.loadActivities();
		Log.i("har", "ActivityRecognitionModule loaded");
	}
	
	private final double LONG_MAX = 4294967296.0;
	
	private List<Activity> activities = new ArrayList<Activity>();
	private NeuralNetworkManager neuralNetworkManager;	
	private Activity currentActivity;
	
	private int trainingNumber = 0;
	private Date beginActivity;
	private Date endActivity;
	private boolean training = false;
	private boolean recognizing = false;
	
	private ScheduledExecutorService scheduler;
	private Future<?> recognitionFuture;
	private Future<?> trainingFuture;
	
	public static synchronized ActivityRecognitionModule getInstance() {
		return instance;
	}
	
	private ActivityRecognitionModule() {
	}
	
	private void loadActivities() {
		for(ActivityEnum activityEnum : ActivityEnum.values()) {
			Activity activity = new Activity();
			activity.setActivityEnum(activityEnum);
			activities.add(activity);
		}
	}
	
	/**
	 * Creates a new time window, which contains all sensor data from the neural network
	 * supported sensors.
	 * @param activity
	 * 			String with the activity label
	 * @param begin
	 * 			Date of the begin
	 * @param end
	 * 			Date of the end
	 * @return
	 * 			The created time window
	 */
	public TimeWindow createTimeWindow(String activity, Date begin, Date end) {
		TimeWindow timeWindow = new TimeWindow(activity, begin, end);
		
		for(String sid : neuralNetworkManager.getSensors()) {
			
			int sensorDimension = SensorManager.getSensorByID(
					Integer.valueOf(sid)).getRawData().get(0).getDimension();
			Vector<SensorData> vector = SensorManager.getSensorByID(
					Integer.valueOf(sid)).getRawData(begin, end);
			float[][] values = new float[sensorDimension][vector.size() / sensorDimension];
			
			int counter = 0;
			for (SensorData sensorData : vector) {

				for (int dimension = 0; dimension < sensorDimension; dimension++) {

					values[dimension][counter] = sensorData.getData()[dimension];
				}
				counter++;
			}
			timeWindow.addSensorDataByID(sid
					+ "_"
					+ SensorManager.getSensorByID(Integer.valueOf(sid))
							.getSensorType(), values);
		}
		FeatureSet featureSet = new FeatureSet(timeWindow);
		timeWindow.setFeatureSet(featureSet);
		return timeWindow;
	}
	
	/**
	 * TODO comments
	 * @param windowLength
	 */
	public void recognize(final int windowLength) {
		recognizing = true;
		scheduler = Executors.newScheduledThreadPool(4);
		try {
			recognitionFuture = scheduler.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if(Looper.myLooper() == null) {
						Looper.prepare();
					}
					
					Date end = new Date();;
					Date begin = new Date();
					begin.setTime(end.getTime() - windowLength);
					TimeWindow timeWindow = createTimeWindow("unlabeled", begin, end);
					
					double recognizedActivity = 0;
					if(!timeWindow.getActivityLabel().substring(0, 4).equals("dead")) {
						try {
							recognizedActivity = neuralNetworkManager.recognise(timeWindow);
						} catch (NullPointerException e) {
							return;
						} catch (IllegalArgumentException e) {
							return;
						}
					}
					
					String closestActivity = closestActivity(recognizedActivity);
					if(!closestActivity.equals(getCurrentActivity())) {
						boolean firstActivity = false;
						if(getCurrentActivity().equals(ActivityEnum.NOACTIVITY.name())) {
							setBeginActivity(new Date());
							firstActivity = true;
						}
						setCurrentActivity(closestActivity);
						setEndActivity(new Date());
						if (!firstActivity) {
							getCurrentActivity().addPeriod(beginActivity,
									endActivity);
							firstActivity = false;
						}
						setBeginActivity(new Date());
						GarmentOSService.callback(
								CallbackFlags.ACTIVITY_CHANGED,
								new ActivityChangedCallback(currentActivity
										.getActivityEnum()));
					}
				}
				
			}, 5000, windowLength / 2, TimeUnit.MILLISECONDS);
			
			recognitionFuture.get();
		} catch(InterruptedException e) {
			
		} catch(CancellationException e) {
			
		} catch(ExecutionException e) {
			
		}
	}

	private synchronized void setBeginActivity(Date beginActivity) {
		this.beginActivity = beginActivity;
	}

	private synchronized void setEndActivity(Date endActivity) {
		this.endActivity = endActivity;
	}

	/**
	 * Stops the currently running recognition.
	 */
	public void stopRecognition() {
		recognitionFuture.cancel(false);
		setCurrentActivity(ActivityEnum.NOACTIVITY.name());
		recognizing = false;
	}
	
	/**
	 * Guesses and returns the closest string to the calculated activity.
	 * @param find
	 * 			Hash of the string to find
	 * @return
	 * 			The closest sting to the hash
	 */
	private String closestActivity(double find) {
		String closest = neuralNetworkManager.getActivities().get(0);
		double distance = Math.abs(closest.hashCode() / LONG_MAX - find);
		for(String s : neuralNetworkManager.getActivities()) {
			double tempDistance = Math.abs(s.hashCode() / LONG_MAX - find);
			if(distance >= tempDistance) {
				closest = s;
				distance = tempDistance;
			}
		}
		return closest;
	}
	
	/**
	 * TODO comments
	 * @param activity
	 * @param windowLength
	 */
	public void train(final String activity, final int windowLength) throws IllegalArgumentException {
		if(!ActivityEnum.contains(activity)) {
			throw new IllegalArgumentException("Activity not defined in activity enum!");
		}
		training = true;
		scheduler = Executors.newScheduledThreadPool(4);
		try {
			trainingFuture = scheduler.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if(Looper.myLooper() == null) {
						Looper.prepare();
					}
					Date end = new Date();
					Date begin = new Date();
					begin.setTime(end.getTime() - windowLength);
					TimeWindow timeWindow = createTimeWindow(activity, begin, end);
					if(!timeWindow.getActivityLabel().substring(0, 4).equals("dead")) {
						try {
							neuralNetworkManager.train(timeWindow);
						} catch (NullPointerException e) {
							return;
						} catch (IllegalArgumentException e) {
							return;
						}
					}
				}
				
			}, 5000, windowLength / 2, TimeUnit.MILLISECONDS);
			trainingFuture.get();
		} catch(InterruptedException e) {
			
		} catch(CancellationException e) {
			
		} catch(ExecutionException e) {
			
		}
	}
	
	/**
	 * TODO comments
	 * @param activity
	 * @param windowLength
	 */
	public void train(final String activity, final int windowLength, final Date begin, 
			final Date end) {
		if(!ActivityEnum.contains(activity)) {
			throw new IllegalArgumentException("Activity not defined in activity enum!");
		}
		training = true;
		scheduler = Executors.newScheduledThreadPool(4);
		try {
			trainingFuture = scheduler.schedule(new Runnable() {

				@Override
				public void run() {
					if(Looper.myLooper() == null) {
						Looper.prepare();
					}
					Date timeWindowBegin = new Date();
					timeWindowBegin.setTime(begin.getTime() + windowLength * trainingNumber);
					Date timeWindowEnd = new Date();
					timeWindowEnd.setTime(timeWindowBegin.getTime() + windowLength);
					if(timeWindowEnd.getTime() >= end.getTime()) {
						trainingFuture.cancel(false);
						return;
					}
					TimeWindow timeWindow = createTimeWindow(activity, timeWindowBegin, timeWindowEnd);
					if(!timeWindow.getActivityLabel().substring(0, 4).equals("dead")) {
						try {
							neuralNetworkManager.train(timeWindow);
							incTrainingNumber();
						} catch (NullPointerException e) {
							return;
						} catch (IllegalArgumentException e) {
							return;
						}
					}
				}
				
			}, windowLength / 2, TimeUnit.MILLISECONDS);
			trainingFuture.get();
		} catch(InterruptedException e) {
			
		} catch(CancellationException e) {
			
		} catch(ExecutionException e) {
			
		}
	}
	
	private synchronized void incTrainingNumber() {
		trainingNumber++;
	}
	
	/**
	 * Stops the currently running training.
	 */
	public void stopTraining() {
		trainingFuture.cancel(false);
		training = false;
	}

	/**
	 * @return the activities
	 */
	public List<Activity> getActivities() {
		return activities;
	}
	
	/**
	 * @return the names of the activities
	 */
	public List<String> getActivityNames() {
		List<String> list = new ArrayList<String>();
		for(Activity activity : activities) {
			list.add(activity.getActivityEnum().toString());
		}
		return list;
	}
	
	/**
	 * Returns the activity which was performed at a specific time. 
	 * If no activity was found at that time, an activity object with
	 * NOACTIVITY will be returned.
	 * @param date
	 * 		The time when the activity was performed
	 * @return
	 * 		Activity object with the performed activity or NOACTIVITY
	 */
	public Activity getActivityAtTime(Date date) {
		for(Activity activity : activities) {
			for(Date[] dateArr : activity.getActivityTimes()) {
				if(date.getTime() >= dateArr[0].getTime() &&
						date.getTime() <= dateArr[1].getTime()) {
					return activity;
				}
			}
		}
		Activity activity = new Activity();
		activity.setActivityEnum(ActivityEnum.NOACTIVITY);
		return activity;
	}
	
	/**
	 * @param currentActivity the currentActivity to set
	 */
	public void setCurrentActivity(String currentActivity) {
		for(Activity activity : activities) {
			if(activity.getActivityEnum().toString().equals(currentActivity)) {
				this.currentActivity = activity;
				return;
			}
		}
	}

	/**
	 * @return the currentActivity
	 */
	public Activity getCurrentActivity() {
		return currentActivity;
	}
	
	/**
	 * TODO link comment
	 * @throws FileNotFoundException 
	 */
	public void loadNeuralNetwork() throws FileNotFoundException {
		neuralNetworkManager.load();
	}
	
	public void saveNeuralNetwork() throws FileNotFoundException {
		neuralNetworkManager.save();
	}
	
	public void closeNeuralNetwork() {
		neuralNetworkManager.close();
	}
	
	public void deleteNeuralNetwork(String file) throws FileNotFoundException {
		neuralNetworkManager.delete(file);
	}
	
	public Status getNeuralNetworkStatus() {
		return neuralNetworkManager.getStatus();
	}
	
	public List<String> getSensors() {
		return neuralNetworkManager.getSensors();
	}
	
	public void addSensor(String sensor) {
		neuralNetworkManager.addSensor(sensor);
	}
	
	public List<String> getSupportedActivities() {
		return neuralNetworkManager.getActivities();
	}
	
	public void addActivity(String activity) {
		neuralNetworkManager.addActivity(activity);
	}

	public boolean isTraining() {
		return training;
	}

	public boolean isRecognizing() {
		return recognizing;
	}
	
	// TODO
//	public int getNumberOfTrainings() {
//		neuralNetworkManager.getTrainings();
//	}

}
