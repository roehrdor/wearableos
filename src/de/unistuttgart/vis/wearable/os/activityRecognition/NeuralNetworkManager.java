package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import android.os.Environment;
import android.util.Log;

/**
 * @author Tobias
 *
 */
public class NeuralNetworkManager {
	private NeuralNetwork neuralNetwork;
	// checks whether the NeuralNetwork is trained or not
	private boolean neuralNetworkTrained = false;
	// checks whether the NeuralNetwork is currently recognizing activities
	private boolean currentlyRecognizing = false;
	// checks whether the NeuralNetwork is currently training
	private boolean currentlyTraining = false;
	// checks whether the NeuralNetwork exists
	private boolean neuralNetworkExisting = false;
	// counts the training sessions
	private int numberOfTrainings = 0;
	// list with all currently supported sensors
	private List<Integer> supportedSensorList = new ArrayList<Integer>();
	// list with all currently supported activities
	private List<String> supportedActivityList = new ArrayList<String>();
	// name of the file where the neural network data is saved
	private final File neuralNetworkFile = new File(
			Environment.getExternalStorageDirectory() + "/nn.data");
	private final File neuralNetworkDataFile = new File(
			Environment.getExternalStorageDirectory() + "/nnm.data");
	private int inputNeurons = 0;

	public NeuralNetworkManager() {
		if (!loadNeuralNetwork()) {
			Log.i("har",
					"[NeuralNetworkManager] no existing neural network found");
		} else {
			Log.i("har",
					"[NeuralNetworkManager] loaded existing neural network");
		}
	}

	/**
	 * trains the NeuralNetwork with a given time window which contains a
	 * feature set
	 * 
	 * @param timeWindow
	 *            time window which contains a feature set
	 */
	public void trainNeuralNetwork(TimeWindow timeWindow) {
		if (supportedSensorList.size() == 0) {
			Log.e("har",
					"[ActivityRecognitionModule]:[startTraining] supportedSensorList = 0");
			throw new NullPointerException();
		}
		double[] features = new double[timeWindow.getFeatureSet().size()];
		double[] target = { timeWindow.getActivityLabel().hashCode() / 4294967296.0 };

		if (supportedSensorList.size() == 1) {
			int i = 0;
			for (Entry<String, Double> entry : timeWindow.getFeatureSet()
					.entrySet()) {
				features[i] = entry.getValue() / 4294967296.0;
				i++;
			}
			neuralNetwork.train(features, target);
			numberOfTrainings++;
			neuralNetworkTrained = true;
			Log.i("har",
					"[ActivityRecognitionModule]:[startTraining] trained training: "
							+ getNumberOfTrainings() + " target: "
							+ String.valueOf(target[0]));
		}

		for (int i = 1; i < Math.pow(supportedSensorList.size(), 2) - 1; i++) {
			String formatPattern = "%" + supportedSensorList.size() + "s";

			int j = 0, k = 0;

			for (Entry<String, Double> entry : timeWindow.getFeatureSet()
					.entrySet()) {

				// if the features(, from the feature set), from one sensor are
				// processed,
				// increase k to skip to the next sensor from sensor list.
				if (!entry
						.getKey()
						.substring(
								0,
								String.valueOf(supportedSensorList.get(k))
										.length())
						.equals(String.valueOf(supportedSensorList.get(k)))) {
					k++;
				}

				if (String.format(formatPattern, Integer.toBinaryString(i))
						.charAt(k) == '1') {
					features[j] = entry.getValue() / 4294967296.0;
					j++;

				} else {
					features[j] = 0;
					j++;
				}
			}

			try {
				neuralNetwork.train(features, target);
				if (i == Math.pow(supportedSensorList.size(), 2) - 2) {
					numberOfTrainings++;
					neuralNetworkTrained = true;
					Log.i("har",
							"[ActivityRecognitionModule]:[startTraining] trained training: "
									+ getNumberOfTrainings() + " target: "
									+ String.valueOf(target[0]));
				}

			} catch (IllegalArgumentException e) {
				Log.i("har",
						"[NeuralNetworkManager]:[trainNeuralNetwork] feature set size: "
								+ features.length);
				Log.e("har",
						"[NeuralNetworkManager]:[trainNeuralNetwork] IllegalArgumentException"
								+ e.getLocalizedMessage());
			}
		}
	};

	/**
	 * recognizes the activities in a specific time window which is created
	 * depending on the window length
	 * 
	 * @param timeWindow
	 *            length of the time window
	 * @return a array with the result
	 */
	public double recognizeActivity(TimeWindow timeWindow) {
		currentlyRecognizing = true;
		double[] features = new double[timeWindow.getFeatureSet().size()];
		int i = 0;

		for (Entry<String, Double> entry : timeWindow.getFeatureSet()
				.entrySet()) {
			features[i] = entry.getValue() / 4294967296.0;
			i++;
		}
		double[] targetArray = new double[1];
		try {
			neuralNetwork.classifiy(features, targetArray);
		} catch (IllegalArgumentException e) {
			Log.e("har",
					"[NeuralNetworkManager]:[recognizeActivity] IllegalArgumentException"
							+ e.toString());
		}
		return targetArray[0];
	}

	/**
	 * loads local saved data from the NeuralNetwork
	 * 
	 * @param fileName
	 *            string with the file name TODO
	 */
	@SuppressWarnings("unchecked")
	public boolean loadNeuralNetwork() {
		Log.i("har", "[NeuralNetworkManager]:[loadNeuralNetwork] loading");
		if (!neuralNetworkFile.exists() && !neuralNetworkDataFile.exists()) {
			return false;
		}
		boolean nnconfig = false;
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(
					neuralNetworkDataFile));
			this.supportedSensorList = (List<Integer>) ois.readObject();
			Log.i("har", "[NeuralNetworkManager]:[loadNeuralNetwork] "
					+ "supportedSensorList: " + supportedSensorList.size());
			this.supportedActivityList = (List<String>) ois.readObject();
			Log.i("har", "[NeuralNetworkManager]:[loadNeuralNetwork] "
					+ "supportedActivityList: " + supportedActivityList.size());
			this.inputNeurons = ois.readInt();
			Log.i("har", "[NeuralNetworkManager]:[loadNeuralNetwork] "
					+ "inputNeurons: " + inputNeurons);
			this.numberOfTrainings = ois.readInt();
			Log.i("har", "[NeuralNetworkManager]:[loadNeuralNetwork] "
					+ "numberOfTrainings: " + numberOfTrainings);
			ois.close();
			nnconfig = true;

		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			nnconfig = false;
			Log.e("har", "[NeuralNetworkManager]:[loadNeuralNetwork] "
					+ "StreamCorruptedException: " + e.getLocalizedMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			nnconfig = false;
			Log.e("har", "[NeuralNetworkManager]:[loadNeuralNetwork] "
					+ "FileNotFoundException: " + e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			nnconfig = false;
			Log.e("har", "[NeuralNetworkManager]:[loadNeuralNetwork] "
					+ "IOException: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			nnconfig = false;
			Log.e("har", "[NeuralNetworkManager]:[loadNeuralNetwork] "
					+ "ClassNotFoundException: " + e.getLocalizedMessage());
		}

		Log.i("har",
				"[NeuralNetworkManager]:[loadNeuralNetwork] loading from file");
		if (nnconfig) {
			neuralNetwork = new NeuralNetwork(
					neuralNetworkFile.getAbsolutePath());
			neuralNetworkTrained = true;
			neuralNetworkExisting = true;
			if (numberOfTrainings > 0) {
				neuralNetworkTrained = true;
			}
			return true;
		} else {
			Log.e("har",
					"[NeuralNetworkManager]:[loadNeuralNetwork] config not found");
		}
		return false;
	};

	/**
	 * creates a new neural network from the given supported sensor list (only
	 * if no neural network was created or loaded before)
	 * 
	 * @return true if created else false
	 */
	public boolean createNeuralNetwork() {
		if (neuralNetwork != null) {
			return false;
		}
		Log.i("har",
				"[NeuralNetworkManager]:[loadNeuralNetwork] creating new neural network");
		for (int sid : supportedSensorList) {
			if (SensorType.values()[APIFunctions
					.SENSORS_SENSOR_getSensorType(sid)]
					.equals(SensorType.ACCELEROMETER)) {
				if (APIFunctions.SENSORS_SENSOR_getRawData(sid).getDimension() == 1) {
					inputNeurons += 10;
				} else {
					inputNeurons += APIFunctions.SENSORS_SENSOR_getRawData(sid)
							.getDimension() * 12;
				}
			} else {
				inputNeurons += APIFunctions.SENSORS_SENSOR_getRawData(sid)
						.getDimension() * 8;
			}
		}
		if (inputNeurons > 0) {
			try {
				neuralNetwork = new NeuralNetwork(new int[] { inputNeurons,
						inputNeurons / 2, 1 });
				neuralNetworkExisting = true;
			} catch (IllegalArgumentException e) {
				Log.e("har",
						"[NeuralNetworkManager]:[loadNeuralNetwork] IllegalArgumentException "
								+ e.toString());
			}
		} else {
			// TODO
			return false;
		}
		return true;
	}

	/**
	 * saves data from the NeuralNetwork to a local file
	 * 
	 * @param fileName
	 *            string with the file name
	 */
	public void saveNeuralNetworkToFile() {
		Log.i("har", "[NeuralNetworkManager]:[saveNeuralNetworkToFile] saving");
		neuralNetwork.saveNetwork(neuralNetworkFile.getAbsolutePath());
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(
					neuralNetworkDataFile));
			oos.writeObject(supportedSensorList);
			Log.i("har", "[NeuralNetworkManager]:[saveNeuralNetworkToFile] "
					+ "supportedSensorList: " + supportedSensorList.size());
			oos.writeObject(supportedActivityList);
			Log.i("har", "[NeuralNetworkManager]:[saveNeuralNetworkToFile] "
					+ "supportedActivityList: " + supportedActivityList.size());
			oos.writeInt(inputNeurons);
			Log.i("har", "[NeuralNetworkManager]:[saveNeuralNetworkToFile] "
					+ "inputNeurons: " + inputNeurons);
			oos.writeInt(numberOfTrainings);
			Log.i("har", "[NeuralNetworkManager]:[saveNeuralNetworkToFile] "
					+ "numberOfTrainings: " + numberOfTrainings);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	};

	/*
	 * deletes the NeuralNetwork and its data from the ram
	 */
	public void deleteNeuralNetworkFromRAM() {
		Log.i("har",
				"[NeuralNetworkManager]:[deleteNeuralNetworkFromRAM] closing");
		neuralNetwork.delete();
		neuralNetworkTrained = false;
	};

	/**
	 * deletes the local file with the NeuralNetwork data
	 */
	public void deleteNeuralNetworkFromMemory() {
		Log.i("har",
				"[NeuralNetworkManager]:[deleteNeuralNetworkFromMemory] deleting");

		if (neuralNetworkFile.exists() && neuralNetworkDataFile.exists()) {
			neuralNetworkFile.delete();
			neuralNetworkDataFile.delete();
		}
		neuralNetwork.delete();
		neuralNetworkTrained = false;
		neuralNetworkExisting = false;
		this.supportedSensorList.clear();
		this.supportedActivityList.clear();
		this.inputNeurons = 0;
		this.numberOfTrainings = 0;
	};

	/**
	 * returns weather the activity recognition is currently running, training,
	 * ready or the NeuralNetwork is not trained
	 * 
	 * @return 0 if the neural network is not trained 1 if the neural network is
	 *         currently training 2 if the neural network is currently
	 *         recognizing 3 if the neural network is idling
	 */
	public byte getNeuralNetworkStatus() {
		if (!neuralNetworkTrained) {
			return 0;

		} else if (currentlyTraining) {
			return 1;

		} else if (currentlyRecognizing) {
			return 2;

		} else {
			return 3;
		}
	}

	/**
	 * @return the neuralNetworkTrained
	 */
	public boolean isNeuralNetworkTrained() {
		return neuralNetworkTrained;
	}

	/**
	 * @param neuralNetworkTrained
	 *            the neuralNetworkTrained to set
	 */
	public void setNeuralNetworkTrained(boolean neuralNetworkTrained) {
		this.neuralNetworkTrained = neuralNetworkTrained;
	}

	/**
	 * @return the numberOfTrainings
	 */
	public int getNumberOfTrainings() {
		return numberOfTrainings;
	}

	/**
	 * @return the currentlyRecognizing
	 */
	public boolean isCurrentlyRecognizing() {
		return currentlyRecognizing;
	}

	/**
	 * @param currentlyRecognizing
	 *            the currentlyRecognizing to set
	 */
	public void setCurrentlyRecognizing(boolean currentlyRecognizing) {
		this.currentlyRecognizing = currentlyRecognizing;
	}

	/**
	 * @return the currentlyTraining
	 */
	public boolean isCurrentlyTraining() {
		return currentlyTraining;
	}

	/**
	 * @param currentlyTraining
	 *            the currentlyTraining to set
	 */
	public void setCurrentlyTraining(boolean currentlyTraining) {
		this.currentlyTraining = currentlyTraining;
	}

	/**
	 * @return the inputNeurons
	 */
	public int getInputNeurons() {
		return inputNeurons;
	}

	/**
	 * @return the supportedSensorList
	 */
	public List<Integer> getSupportedSensorList() {
		return supportedSensorList;
	}

	/**
	 * remove a supported sensor
	 * 
	 * @param sensorName
	 * @return true if the sensor was successfully removed false else
	 */
	public void removeSupportedSensor(int sensorName) {
		supportedSensorList.remove(sensorName);
	}

	/**
	 * add a new sensor to the supported sensor list (neural network might be
	 * retrained)
	 * 
	 * @param sensorID
	 * @return true if the sensor was added else false
	 */
	public boolean addSupportedSensor(int sensorID) {
		try {
			return supportedSensorList.add(sensorID);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @return the supportedActivityList
	 */
	public List<String> getSupportedActivityList() {
		return supportedActivityList;
	}

	/**
	 * remove a supported activity
	 * 
	 * @param activityName
	 * @return true if the sensor was successfully removed false else
	 */
	public boolean removeSupportedActivity(String activityName) {
		if (supportedActivityList.remove(activityName)) {
			return true;
		}
		return false;
	}

	/**
	 * add a new sensor to the supported sensor list (neural network might be
	 * retrained)
	 * 
	 * @param activityName
	 * @return true if the sensor was added else false
	 */
	public boolean addSupportedActivity(String activityName) {
		try {
			return supportedActivityList.add(activityName);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @return the neuralNetworkExisting
	 */
	public boolean isNeuralNetworkExisting() {
		return neuralNetworkExisting;
	}
}
