package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.unistuttgart.vis.wearable.os.utils.Utils;
import android.os.Looper;
import android.util.Log;

public class ActivityRecognitionModule {

	private static ActivityRecognitionModule instance;

	private List<Activity> listOfActivities = new ArrayList<Activity>();
	private NeuralNetworkManager neuralNetworkManager;
	
	private ScheduledExecutorService scheduler;
	private Future<?> future;

	private int skippedTrainings = 0;
	private int totalskippedTrainings = 0;
	private int nullSensorTimeWindows = 0;
	private int maximumSkippedTrainings = 0;
	private Activity currentActivity = null;

	public static ActivityRecognitionModule getInstance() {
		if (ActivityRecognitionModule.instance == null) {
			ActivityRecognitionModule.instance = new ActivityRecognitionModule();
		}
		return ActivityRecognitionModule.instance;
	}

	private ActivityRecognitionModule() {
		Log.i("har", "[ActivityRecognitionModule] load ActivityRecognitionModule");
		//loadActivities();
		neuralNetworkManager = new NeuralNetworkManager();
	}
	
	/**
	 * TODO
	 * Loads all existing activities from the database.
	 * If there are activities in the ActivityEnum which are not in the database,
	 * a new activity is created.
	 */
	/*private void loadActivities() {
		for(ActivityEnum activityEnum : ActivityEnum.values()) {
			Activity activity = APIFunctions.getActivity(activityEnum);
			if(activity == null) {
				Log.i("har", "[ActivityRecognitionModule]:[loadActivities] create new activiy: "
						+ activityEnum.toString());
				activity = new Activity();
				activity.setActivityEnum(activityEnum);
			} else {
				Log.i("har", "[ActivityRecognitionModule]:[loadActivities] load activity: "
						+ activityEnum.toString());
			}
			listOfActivities.add(activity);
			if(activityEnum.equals(ActivityEnum.NOACTIVITY)) {
				Log.i("har", "[ActivityRecognitionModule]:[loadActivities] set current activity: "
						+ activityEnum.toString());
				setCurrentActivity(activity.getActivityEnum().toString());
			}
		}
	}*/

	/**
	 * TODO
	 * saves a given activity to the database
	 * @param activity
	 * 				activity object to save
	 */
	/*public void saveActivity(Activity activity) {
		StorageModule.getInstance().saveActivity(activity);
	}*/
	
	/**
	 * @param activityEnum
	 * 				to return the activity
	 * @return the activity to a given activity enum
	 */
	public Activity getActivity(ActivityEnum activityEnum) {
		for (Activity activity : listOfActivities) {
			if (activity.getActivityEnum().equals(activityEnum)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return list of all activities in the Activity Enum
	 */
	public List<Activity> getListOfActivities() {
		return listOfActivities;
	}

	/**
	 * creates a time window with all relevant sensor data and computes all
	 * features
	 * 
	 * @param timeWindowBegin
	 * @param timeWindowEnd
	 */
	public TimeWindow createTimeWindow(String activity, int timeWindowBegin,
			int timeWindowEnd) {
		
		Log.i("har", "createTimeWindow");
		TimeWindow timeWindow = new TimeWindow(activity, timeWindowBegin, timeWindowEnd);
		
		for (int sensorID : getSupportedSensorList()) {
			
			timeWindow.addSensorDataByID(sensorID);
			
			if (timeWindow.get(sensorID) == null) {
				timeWindow.setActivityLabel("broken (requested sensor is null)");
				return timeWindow;
			}
			//TODO look error
//			if(timeWindow.getVirutalSensor(sensor).getRawData().length == 0) {
//				timeWindow.setActivityLabel("broken (no raw data available)");
//				return timeWindow;
//			}
		}
		FeatureSet featureSet = new FeatureSet(timeWindow);
		if (neuralNetworkManager.getInputNeurons() == featureSet.size()) {
			timeWindow.setFeatureSet(featureSet);
		} else {
			timeWindow.setActivityLabel("broken (wrong feature number)");
		}
		return timeWindow;
	}

	/**
	 * starts the activity recognition thread
	 */
	public void startRecognising(final int windowLength) {
		neuralNetworkManager.setCurrentlyRecognizing(true);
		Log.i("har",
				"[ActivityRecognitionModule]:[startRecognising] starting recognizing");

		scheduler = Executors
				.newScheduledThreadPool(4);
		try {
			future = scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {

					if(Looper.myLooper() == null) {
						Looper.prepare();
					}
					
					int timeWindowBegin = Utils.getCurrentUnixTimeStamp();
					int timeWindowEnd = Utils.getCurrentUnixTimeStamp();
					timeWindowBegin = timeWindowBegin - windowLength;
					TimeWindow timeWindow = createTimeWindow("unlabeled", timeWindowBegin,
							timeWindowEnd);
					double recognizedActivity = 0;
					
					if (!timeWindow.getActivityLabel().substring(0, 5).equals("broken")) {
						recognizedActivity = recognizeActivity(timeWindow);
						
					} else {
						// TODO delete
						Log.e("har", "[ActivityRecognitionModule]:[startRecognising] skipping "
								+ "recognition, because the time window is "
								+ timeWindow.getActivityLabel());
					}
					
					for(String s : getSupportedActivityList()) {
						if(s.hashCode() / 4294967296.0 == recognizedActivity) {
							setCurrentActivity(s);
							break;
						}
					}
					String activitiesString = "";
					
					for(String s : getSupportedActivityList()) {
						double value = s.hashCode() / 4294967296.0;
						activitiesString = activitiesString + String.valueOf(value) + ", ";
					}
					Log.i("har",
							"[ActivityRecognitionModule]:[startRecognising] " +
							String.valueOf(recognizedActivity) + ",- "
							+ String.valueOf(activitiesString));
					if(getCurrentActivity() == null) {
						Log.i("har",
								"[ActivityRecognitionModule]:[startRecognising] finished recognition: "
								+ "no activity");
					} else {
						Log.i("har",
							"[ActivityRecognitionModule]:[startRecognising] finished recognition: "
							+ getCurrentActivity().getActivityEnum().toString());
					}
					
				}

			}, 0, windowLength / 4, TimeUnit.MILLISECONDS);
			
			future.get();
			
		} catch (InterruptedException e) {
			neuralNetworkManager.setCurrentlyRecognizing(false);
			Log.e("har",
					"[ActivityRecognitionModule]:[startRecognising] Scheduled "
							+ "execution was interrupted: " + e.toString());
		} catch (CancellationException e) {
			neuralNetworkManager.setCurrentlyRecognizing(false);
			Log.i("har",
					"[ActivityRecognitionModule]:[startRecognising] Watcher thread "
							+ "has been cancelled: " + e.toString());
		} catch (ExecutionException e) {
			neuralNetworkManager.setCurrentlyRecognizing(false);
			Log.e("har",
					"[ActivityRecognitionModule]:[startRecognising] Uncaught "
							+ "exception in scheduled execution: "
							+ e.toString());
		}
	}

	/**
	 * stops the activity recognition
	 */
	public void stopRecognising() {
		if(future.cancel(false)) {
			Log.i("har", "[ActivityRecognitionModule]:[stopRecognising] recognition stopped");
			neuralNetworkManager.setCurrentlyRecognizing(false);
		} else {
			Log.i("har", "[ActivityRecognitionModule]:[stopRecognising] recognition not stopped");
		}
		
	}

	/**
	 * recognizes the current activity
	 * 
	 * @param windowLength
	 *            Length of the time window in which the activity should be
	 *            recognized
	 */
	private double recognizeActivity(TimeWindow timeWindow) {
		try {
			return neuralNetworkManager.recognizeActivity(timeWindow);
		} catch (NullPointerException e) {
			Log.e("har", "[ActivityRecognitionModule]:[recognizeActivity] skipping recognition: "
					+ e.getMessage());
		}
		return 0.0;
	}

	/**
	 * starts new threads to train the neural network from live data
	 * @param activity
	 * 				current activity that is trained
	 * @param windowLength
	 * 				length of the time window
	 */
	public void startTraining(final String activity, final int windowLength) {
		neuralNetworkManager.setCurrentlyTraining(true);
		skippedTrainings = 0;
		scheduler = Executors
				.newScheduledThreadPool(4);
		try {
			future = scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {

					if(Looper.myLooper() == null) {
						Looper.prepare();
					}
					
					int timeWindowBegin = Utils.getCurrentUnixTimeStamp();
					int timeWindowEnd = Utils.getCurrentUnixTimeStamp();
					timeWindowBegin = timeWindowBegin - windowLength;
					TimeWindow timeWindow = createTimeWindow(activity, timeWindowBegin,
							timeWindowEnd);
					
					if (!timeWindow.getActivityLabel().substring(0, 5).equals("broken")) {
						trainNeuralNetwork(timeWindow);
						skippedTrainings = 0;
						
					} else {
						totalskippedTrainings++;
						skippedTrainings++;
						Log.e("har", "[ActivityRecognitionModule]:[startTraining] skipping "
								+ "training, because the time window is "
								+ timeWindow.getActivityLabel());
					}
					if(skippedTrainings >= 64) {
						skippedTrainings = 0;
						future.cancel(false);
//						throw new CancellationException("scheduler stopped due to too"
//								+ " many errors while creating time windows");
					}
				}

			}, 1000, windowLength / 4, TimeUnit.MILLISECONDS);
			
			future.get();
			
		} catch (InterruptedException e) {
			neuralNetworkManager.setCurrentlyTraining(false);
			Log.e("har",
					"[ActivityRecognitionModule]:[startTraining] Scheduled "
							+ "execution was interrupted: " + e.getMessage());
		} catch (CancellationException e) {
			neuralNetworkManager.setCurrentlyTraining(false);
			Log.i("har",
					"[ActivityRecognitionModule]:[startTraining] Watcher thread "
							+ "has been cancelled: " + e.getMessage());
		} catch (ExecutionException e) {
			neuralNetworkManager.setCurrentlyTraining(false);
			Log.e("har",
					"[ActivityRecognitionModule]:[startTraining] Uncaught "
							+ "exception in scheduled execution: "
							+ e.getMessage());
		}
	}
	
	/**
	 * starts new threads to train the neural network from database data
	 * @param activity
	 * 				activity, that was performed between start and end date
	 * @param windowLength
	 * 				length of the time window
	 * @param startDate
	 * 				begin of the activity
	 * @param endDate
	 * 				end of the activity
	 */
	public void startTraining(final String activity, final int windowLength,
			final int startDate, final int endDate) {
		neuralNetworkManager.setCurrentlyTraining(true);
		skippedTrainings = 0;
		scheduler = Executors
				.newScheduledThreadPool(4);
		try {
			future = scheduler.schedule(new Runnable() {
				@Override
				public void run() {
					
					if(Looper.myLooper() == null) {
						Looper.prepare();
					}
					
					int timeWindowBegin = Utils.getCurrentUnixTimeStamp();
					timeWindowBegin = startDate + windowLength * neuralNetworkManager.getNumberOfTrainings();
					int timeWindowEnd = Utils.getCurrentUnixTimeStamp();
					timeWindowEnd = startDate + windowLength;
					if(timeWindowEnd > endDate) {
						future.cancel(false);
//						throw new CancellationException("End Date reached");
					}
					
					timeWindowBegin = timeWindowBegin - windowLength;
					TimeWindow timeWindow = createTimeWindow(activity, timeWindowBegin,
							timeWindowEnd);
					
					if (!timeWindow.getActivityLabel().equals("broken")) {
						trainNeuralNetwork(timeWindow);
						skippedTrainings = 0;
						
					} else {
						totalskippedTrainings++;
						skippedTrainings++;
						Log.e("har", "[ActivityRecognitionModule]:[startTraining] skipping "
								+ "training, because the time window is broken");
					}
					if(skippedTrainings >= 8) {
						skippedTrainings = 0;
						future.cancel(false);
//						throw new CancellationException("scheduler stopped due to too"
//								+ " many errors while creating time windows");
					}
				}

			}, windowLength / 4, TimeUnit.MILLISECONDS);
			
			future.get();
			
		} catch (InterruptedException e) {
			neuralNetworkManager.setCurrentlyTraining(false);
			Log.e("har",
					"[ActivityRecognitionModule]:[startTraining] Scheduled "
							+ "execution was interrupted: " + e.getMessage());
		} catch (CancellationException e) {
			neuralNetworkManager.setCurrentlyTraining(false);
			Log.i("har",
					"[ActivityRecognitionModule]:[startTraining] Watcher thread "
							+ "has been cancelled: " + e.getMessage());
		} catch (ExecutionException e) {
			neuralNetworkManager.setCurrentlyTraining(false);
			Log.e("har",
					"[ActivityRecognitionModule]:[startTraining] Uncaught "
							+ "exception in scheduled execution: "
							+ e.getMessage());
		}
	}

	public void stopTraining() {
		if(future.cancel(false)) {
			neuralNetworkManager.setCurrentlyTraining(false);
			Log.i("har", "[ActivityRecognitionModule]:[stopTraining] training stopped");
		} else {
			Log.i("har", "[ActivityRecognitionModule]:[stopTraining] training not stopped");
		}
	}

	/**
	 * trains the neural network with the selected data
	 * 
	 * @param timeWindow
	 *            with the training data
	 */
	public void trainNeuralNetwork(TimeWindow timeWindow) {
		try {
			Log.i("har", "[ActivityRecognitionModule]:[trainNeuralNetwork]");
			neuralNetworkManager.trainNeuralNetwork(timeWindow);
			nullSensorTimeWindows = 0;
		} catch (NullPointerException e) {
			totalskippedTrainings++;
			nullSensorTimeWindows++;
			if(maximumSkippedTrainings < nullSensorTimeWindows) {
				maximumSkippedTrainings = nullSensorTimeWindows;
			}
			Log.e("har", "[ActivityRecognitionModule]:[trainNeuralNetwork] sensors are Null");
		}
		if(nullSensorTimeWindows > 63) {
			future.cancel(false);
//			throw new CancellationException("The sensore in the time window were null "
//					+ "8 times in a row, stopping training!");
		}
	}

	/**
	 * loads a neural network from a file, if available, otherwise a new one is
	 * created
	 */
	public void loadNeuralNetwork() {
		neuralNetworkManager.loadNeuralNetwork();
	}

	/**
	 * saves the neural network for later use
	 */
	public void saveNeuralNetworkToFile() {
		neuralNetworkManager.saveNeuralNetworkToFile();
	}

	/**
	 * closes the neural network should only be used, when library gets closed
	 */
	public void deleteNeuralNetworkFromRAM() {
		neuralNetworkManager.deleteNeuralNetworkFromRAM();
	}

	/**
	 * deletes and closes the neural network
	 */
	public void deleteNeuralNetworkFromMemory() {
		neuralNetworkManager.deleteNeuralNetworkFromMemory();
	}

	/**
	 * returns weather the activity recognition is running, ready or the neural
	 * network is not trained
	 * 
	 * @return 0 if the neural network is not trained 
	 * 		   1 if the neural network is currently training 
	 *         2 if the neural network is currently recognizing 
	 *         3 if the neural network is idling
	 */
	public byte getNeuralNetworkStatus() {
		return neuralNetworkManager.getNeuralNetworkStatus();
	}

	/**
	 * @return the currentActivity
	 */
	public Activity getCurrentActivity() {
		return currentActivity;
	}

	/**
	 * @param currentActivity the currentActivity to set
	 */
	public void setCurrentActivity(String currentActivity) {
		for(Activity activity : listOfActivities) {
			if(activity.getActivityEnum().toString().equals(currentActivity)) {
				this.currentActivity = activity;
				return;
			}
		}
	}
	
	/**
	 * @return the neuralNetworkTrained
	 */
	public boolean isNeuralNetworkTrained() {
		return neuralNetworkManager.isNeuralNetworkTrained();
	}
	
	/**
	 * @return the currentlyRecognizing
	 */
	public boolean isCurrentlyRecognizing() {
		return neuralNetworkManager.isCurrentlyRecognizing();
	}
	
	/**
	 * @return the currentlyTraining
	 */
	public boolean isCurrentlyTraining() {
		return neuralNetworkManager.isCurrentlyTraining();
	}
	
	/**
	 * @return the numberOfTrainings
	 */
	public int getNumberOfTrainings() {
		return neuralNetworkManager.getNumberOfTrainings();
	}

	/**
	 * @return the supportedSensorList
	 */
	public List<Integer> getSupportedSensorList() {
		return neuralNetworkManager.getSupportedSensorList();
	}
	
	/**
	 * remove a supported sensor
	 * @param sensorName
	 * @return
	 * 			true if the sensor was successfully removed
	 * 			false else
	 */
	public void removeSupportedSensor(int sensorName) {
		neuralNetworkManager.removeSupportedSensor(sensorName);		
	}
	
	/**
	 * add a new sensor to the supported sensor list
	 * (neural network might be retrained)
	 * @param sensorID
	 * @return
	 * 			true if the sensor was added
	 * 			else false
	 */
	public boolean addSupportedSensor(int sensorID) {
		return neuralNetworkManager.addSupportedSensor(sensorID);
	}

	/**
	 * @return the supportedActivityList
	 */
	public List<String> getSupportedActivityList() {
		return neuralNetworkManager.getSupportedActivityList();
	}
	
	/**
	 * remove a supported activity
	 * @param activityName
	 * @return
	 * 			true if the sensor was successfully removed
	 * 			false else
	 */
	public boolean removeSupportedActivity(String activityName) {
		return neuralNetworkManager.removeSupportedActivity(activityName);
	}
	
	/**
	 * add a new sensor to the supported sensor list
	 * (neural network might be retrained)
	 * @param activityName
	 * @return
	 * 			true if the sensor was added
	 * 			else false
	 */
	public boolean addSupportedActivity(String activityName) {
		return neuralNetworkManager.addSupportedActivity(activityName);
	}
	
	/**
	 * @return the neuralNetworkExisting
	 */
	public boolean isNeuralNetworkExisting() {
		return neuralNetworkManager.isNeuralNetworkExisting();
	}
	
	/**
	 * creates a new neural network from the given supported sensor list
	 * (only if no neural network was created or loaded before)
	 * @return
	 * 			true if created
	 * 			else false
	 */
	public boolean createNeuralNetwork() {
		return neuralNetworkManager.createNeuralNetwork();
	}

	/**
	 * @return the totalskippedTrainings
	 */
	public int getTotalskippedTrainings() {
		return totalskippedTrainings;
	}

	/**
	 * @return the maximumSkippedTrainings
	 */
	public int getMaximumSkippedTrainings() {
		return maximumSkippedTrainings;
	}
}
