/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.activityRecognition;

import android.os.Looper;
import android.util.Log;

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

import de.unistuttgart.vis.wearable.os.activityRecognition.NeuralNetworkManager.Status;
import de.unistuttgart.vis.wearable.os.api.ActivityChangedCallback;
import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.properties.Properties;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.service.GarmentOSService;
import de.unistuttgart.vis.wearable.os.storage.ActivityLoad;

/**
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
		instance.neuralNetworkManager = new NeuralNetworkManager(Properties.storageDirectory);
		instance.loadActivities();
	}

    private List<Activity> activities = new ArrayList<Activity>();
	private NeuralNetworkManager neuralNetworkManager;	
	private Activity currentActivity;
	
	private int trainingNumber = 0;
	private Date beginActivity;
	private Date endActivity;
	private boolean training = false;
	private boolean recognizing = false;
    private boolean finished = false;
	
	private ScheduledExecutorService scheduler;
	private Future<?> recognitionFuture;
	private Future<?> trainingFuture;
    private int errorCount;

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
			if(activity.getActivityEnum() == ActivityEnum.NOACTIVITY) {
				currentActivity = activity;
			}
		}
		ActivityLoad activityLoad = new ActivityLoad(activities);
		activityLoad.work();
		
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

            if(!SensorManager.getSensorByID(Integer.valueOf(sid)).isEnabled()) {
                timeWindow.setActivityLabel("dead (sensor disabled)");
                return timeWindow;
            }

			int sensorDimension = SensorManager.getSensorByID(
					Integer.valueOf(sid)).getRawData().get(0).getDimension();
			if(sensorDimension < 1) {
				timeWindow.setActivityLabel("dead (sensor dimension is smaller than 1)");
				return timeWindow;
			}
			Vector<SensorData> vector = SensorManager.getSensorByID(
					Integer.valueOf(sid)).getRawData(begin, end);
			if(vector.size() < 1) {
				timeWindow.setActivityLabel("dead (sensor data is smaller than 1)");
				return timeWindow;
			}
			float[][] values = new float[sensorDimension][vector.size()];
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
	 * This method tries to guess the currently performed activity and saves it to the
	 * current activity.
	 * @param windowLength
	 * 				the time window length
	 */
	public void recognize(final int windowLength) throws IllegalStateException {
		if(recognizing) {
			throw new IllegalStateException("Recognition is already executing!");
		}
		try {
			loadNeuralNetwork();
		} catch (FileNotFoundException e) {
			Log.e("har",
					"FileNotFoundException in recognize: "
							+ e.getLocalizedMessage());
			return;
		}
		recognizing = true;
		scheduler = Executors.newScheduledThreadPool(4);
		try {
			recognitionFuture = scheduler.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if(Looper.myLooper() == null) {
						Looper.prepare();
					}
					
					Date end = new Date();
					Date begin = new Date();
					begin.setTime(end.getTime() - windowLength);
					TimeWindow timeWindow;
					try {
						timeWindow = createTimeWindow("unlabeled", begin, end);
					} catch (ArrayIndexOutOfBoundsException e) {
						Log.e("har", "ArrayIndexOutOfBoundsException in train: " +
                                e.getLocalizedMessage());
						return;
					}
					double recognizedActivity = 0;
                    if(timeWindow.getActivityLabel().equals(
                            "dead (sensor disabled)")) {
                        throw new CancellationException("sensor is disabled");
                    }
					if(!timeWindow.getActivityLabel().substring(0, 4).equals("dead")) {
						try {
							recognizedActivity = neuralNetworkManager.recognise(timeWindow);
						} catch (NullPointerException e) {
							Log.e("har", "NullPointerException in recognize: " +
                                    e.getLocalizedMessage());
							return;
						} catch (IllegalArgumentException e) {
							Log.e("har", "IllegalArgumentException in recognize: " +
                                    e.getLocalizedMessage());
							return;
						}
					}
					String closestActivity = closestActivity(recognizedActivity);
					if(!closestActivity.equals(getCurrentActivity().getActivityEnum().toString())) {
						boolean firstActivity = false;
						if(getCurrentActivity().getActivityEnum().name()
                                .equals(ActivityEnum.NOACTIVITY.name())) {
							setBeginActivity(new Date());
							firstActivity = true;
						}
						setCurrentActivity(closestActivity);
						setEndActivity(new Date());
						if (!firstActivity) {
							getCurrentActivity().addPeriod(beginActivity,
									endActivity);
							getCurrentActivity().saveActivity();
							firstActivity = false;
						}
						setBeginActivity(new Date());
						GarmentOSService.callback(
								CallbackFlags.ACTIVITY_CHANGED,
								new ActivityChangedCallback(currentActivity
										.getActivityEnum()));
					}
				}
				
			}, 1000, windowLength, TimeUnit.MILLISECONDS);
			
			recognitionFuture.get();
		} catch(InterruptedException e) {
			Log.e("har", "InterruptedException in recognize: " + e.getLocalizedMessage());
			recognizing = false;
			closeNeuralNetwork();
		} catch(CancellationException e) {
			Log.e("har", "CancellationException in recognize: " + e.getLocalizedMessage());
			recognizing = false;
			closeNeuralNetwork();
		} catch(ExecutionException e) {
			Log.e("har", "ExecutionException in recognize: " + e.getLocalizedMessage());
			recognizing = false;
			closeNeuralNetwork();
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
        double LONG_MAX = 4294967296.0;
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
	 * This method trains the neural network live.
	 * @param activity
	 * 			the activity that should be trained
	 * @param windowLength
	 * 			the time window length
	 */
	public void train(final String activity, final int windowLength)
			throws IllegalArgumentException, IllegalStateException {
		if (training) {
			throw new IllegalStateException("Training is already executing!");
		}
		if(!ActivityEnum.contains(activity)) {
			throw new IllegalArgumentException("Activity not defined in activity enum!");
		}
		try {
			loadNeuralNetwork();
		} catch (FileNotFoundException e) {
			Log.e("har",
					"FileNotFoundException in recognize: "
							+ e.getLocalizedMessage());
			return;
		}
		training = true;
		scheduler = Executors.newScheduledThreadPool(4);
		try {
			trainingFuture = scheduler.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    if (errorCount >= 8) {
                        errorCount = 0;
                        restartTraining(activity, windowLength);
                        return;
                    }
                    Date end = new Date();
                    Date begin = new Date();
                    begin.setTime(end.getTime() - windowLength);
                    TimeWindow timeWindow;
                    try {
                        timeWindow = createTimeWindow(activity, begin, end);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e("har", "ArrayIndexOutOfBoundsException in train: " +
                                e.getLocalizedMessage());
                        return;
                    }
                    if (timeWindow.getActivityLabel().equals(
                            "dead (sensor disabled)")) {
                        throw new CancellationException("sensor is disabled");
                    }
                    if (!timeWindow.getActivityLabel().substring(0, 4).equals("dead")) {
                        try {
                            neuralNetworkManager.train(timeWindow);
                            errorCount = 0;
                        } catch (NullPointerException e) {
                            Log.e("har", "NullPointerException in train: " +
                                    e.getLocalizedMessage());
                            errorCount++;
                        } catch (IllegalArgumentException e) {
                            Log.e("har", "IllegalArgumentException in train: " +
                                    e.getLocalizedMessage());
                        }
                    }
                }

            }, 1000, windowLength / 2, TimeUnit.MILLISECONDS);
			trainingFuture.get();
		} catch(InterruptedException e) {
			Log.e("har", "InterruptedException in train: " + e.getLocalizedMessage());
			training = false;
			closeNeuralNetwork();
		} catch(CancellationException e) {
			Log.e("har", "CancellationException in train: " + e.getLocalizedMessage());
			training = false;
			closeNeuralNetwork();
		} catch(ExecutionException e) {
			Log.e("har", "ExecutionException in train: " + e.getLocalizedMessage());
			training = false;
			closeNeuralNetwork();
		}
	}

    private void restartTraining(String activity, int windowLength) {
        trainingFuture.cancel(false);
        training = false;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        train(activity, windowLength);
    }
	
	/**
	 * This method trains the neural network from already saved data.
	 * @param activity
	 * 			the activity that should be trained
	 * @param windowLength
	 * 			the time window length
	 * @param begin
	 * 			the start time of the saved data
	 * @param end
	 * 			the end time of the saved data
	 */
	public void train(final String activity, final int windowLength, final Date begin, 
			final Date end) throws IllegalArgumentException, IllegalStateException {
		if (training) {
			throw new IllegalStateException("Training is already executing!");
		}
		if(!ActivityEnum.contains(activity)) {
			throw new IllegalArgumentException("Activity not defined in activity enum!");
		}
		try {
			loadNeuralNetwork();
		} catch (FileNotFoundException e) {
			Log.e("har",
					"FileNotFoundException in recognize: "
							+ e.getLocalizedMessage());
			return;
		}
        finished = false;
		trainingNumber = 0;
		training = true;
		scheduler = Executors.newScheduledThreadPool(4);
		try {
			trainingFuture = scheduler.submit(new Runnable() {

				@Override
				public void run() {
					if(Looper.myLooper() == null) {
						Looper.prepare();
					}
                    if(errorCount >= 8) {
                        errorCount = 0;
                        return;
                    }
					while (!finished) {
						Date timeWindowBegin = new Date();
						timeWindowBegin.setTime(begin.getTime() + (windowLength / 2)
								* trainingNumber);
						Date timeWindowEnd = new Date();
						timeWindowEnd.setTime(timeWindowBegin.getTime()
								+ windowLength);
						if (timeWindowEnd.getTime() >= end.getTime()) {
							finished = true;
							training = false;
							closeNeuralNetwork();
							return;
						}
						TimeWindow timeWindow = createTimeWindow(activity,
								timeWindowBegin, timeWindowEnd);
                        if(timeWindow.getActivityLabel().equals(
                                "dead (sensor disabled)")) {
                            throw new CancellationException("sensor is disabled");
                        }
						if (!timeWindow.getActivityLabel().substring(0, 4)
								.equals("dead")) {
							try {
								neuralNetworkManager.train(timeWindow);
                                errorCount = 0;
                            } catch (NullPointerException e) {
                                Log.e("har", "NullPointerException in train: " +
                                        e.getLocalizedMessage());
                                errorCount++;
                                return;
                            } catch (IllegalArgumentException e) {
                                Log.e("har", "IllegalArgumentException in train: " +
                                        e.getLocalizedMessage());
                                return;
                            }
						}
						incTrainingNumber();
					}
                    finished = false;
				}
				
			});
			trainingFuture.get();
		} catch(InterruptedException e) {
			Log.e("har", "InterruptedException in train: " + e.getLocalizedMessage());
			training = false;
			closeNeuralNetwork();
		} catch(CancellationException e) {
			Log.e("har", "CancellationException in train: " + e.getLocalizedMessage());
			training = false;
			closeNeuralNetwork();
		} catch(ExecutionException e) {
			Log.e("har", "ExecutionException in train: " + e.getLocalizedMessage());
			training = false;
			closeNeuralNetwork();
		}
	}

	private synchronized void incTrainingNumber() {
		trainingNumber++;
	}

	/**
	 * Stops the currently running training.
	 */
	public void stopTraining() {
        finished = true;
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
	
	public void deleteNeuralNetwork() throws FileNotFoundException {
		neuralNetworkManager.delete();
	}
	
	/**
	 * Creates a new neural network with the right number of input neurons.
	 * @return
	 * 		true if the neural network was created successfully
	 * 		false otherwise
	 * @throws IllegalArgumentException
	 */
	public boolean createNeuralNetwork() throws IllegalArgumentException {
		if (getSensors().size() < 1) {
			throw new IllegalArgumentException(
					"No sensors found at neural network manager!");
		}
		int inputNeurons = 0;
		for (String sid : getSensors()) {
			if (SensorManager.getSensorByID(Integer.valueOf(sid))
					.getSensorType().equals(SensorType.ACCELEROMETER)) {
				if (SensorManager.getSensorByID(Integer.valueOf(sid))
						.getRawData().get(0).getDimension() > 1) {
					inputNeurons += SensorManager
							.getSensorByID(Integer.valueOf(sid)).getRawData()
							.get(0).getDimension() * 10;
				} else {
					inputNeurons += SensorManager
							.getSensorByID(Integer.valueOf(sid)).getRawData()
							.get(0).getDimension() * 10;
				}
			} else {
				inputNeurons += SensorManager
						.getSensorByID(Integer.valueOf(sid)).getRawData()
						.get(0).getDimension() * 8;
			}
		}
		try {
			return neuralNetworkManager.create(inputNeurons);
		} catch (IllegalArgumentException e) {
			Log.e("har", "IllegalArgumentException in createNeuralNetwork: " +
                    e.getLocalizedMessage());
			return false;
		}

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
	
	public void removeSensor(String sensor) {
		neuralNetworkManager.removeSensor(sensor);
	}
	
	public List<String> getSupportedActivities() {
		return neuralNetworkManager.getActivities();
	}
	
	public void addActivity(String activity) {
		neuralNetworkManager.addActivity(activity);
	}
	
	public void removeActivity(String activity) {
		neuralNetworkManager.removeActivity(activity);
	}

	/**
	 * @return
	 * 		true if the system is currently training
	 * 		false otherwise
	 */
	public boolean isTraining() {
		return training;
	}

	/**
	 * @return
	 * 		true if the system is currently recognizing
	 * 		false otherwise
	 */
	public boolean isRecognizing() {
		return recognizing;
	}

}
