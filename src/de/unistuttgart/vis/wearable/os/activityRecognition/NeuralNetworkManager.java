package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;
import de.unistuttgart.vis.wearable.os.activityRecognition.TimeWindow;

/**
 * This class holds the neural network and additional data which is needed for
 * the neural network to work.
 * 
 * @author Tobias
 *
 */
public class NeuralNetworkManager {

	public static enum Status {
		NOTINITIALIZED, INITIALIZED, TRAINED, IDLING;
	}

	private final double LONG_MAX = 4294967296.0;

	private NeuralNetwork neuralNetwork;
	// the current status of the neural network
	private Status status = Status.NOTINITIALIZED;
	// supported activities
	List<String> activities = new ArrayList<String>();
	// number of training for activities
	List<Integer> trainings = new ArrayList<Integer>();
	// list with all currently supported sensors
	private List<String> sensors = new ArrayList<String>();
	private int inputNeurons = 0;
	private short savePeriode = 0;
	private String file = "";
	private final String NN_FILE = "_nn";
	private final String NNM_FILE = "_nnm";

	/**
	 * Tries to load an existing neural network with the given configuration.
	 * 
	 * @param file
	 *            String with the path
	 */
	public NeuralNetworkManager(File file) {
		this.file = file.getAbsolutePath() + File.separator;
		try {
			load();
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Creates a new neural network with the given number of input neurons, a
	 * hidden layer with half the number of input neurons and one output neuron.
	 * 
	 * @param inputNeurons
	 *            number of the features
	 * @return true if the neural network was created, false else
	 * @throws IllegalArgumentException
	 */
	public boolean create(int inputNeurons) throws IllegalArgumentException {
		if (neuralNetwork != null) {
			return false;
		}
		if (inputNeurons < 1) {
			throw new IllegalArgumentException(
					"The number of input neurons must be higher than 1!");
		}
		this.inputNeurons = inputNeurons;
		neuralNetwork = new NeuralNetwork(new int[] { inputNeurons,
				inputNeurons / 2, 1 });
		status = Status.INITIALIZED;
		try {
			if(save()) {
				close();
			}
		} catch (FileNotFoundException e) {
			Log.e("har",
					"FileNotFoundException in create: "
							+ e.getLocalizedMessage());
		}
		return true;
	}

	/**
	 * Saves the neural network and the information in two separate files
	 * 
	 * @return true if the data was saved correctly false else
	 */
	public boolean save() throws FileNotFoundException {
		if(status == Status.IDLING) {
			load();
		}
		neuralNetwork.saveNetwork(file + NN_FILE);
		close();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file + NNM_FILE));
			oos.writeInt(inputNeurons);
			oos.writeObject(status);
			oos.writeObject(activities);
			oos.writeObject(trainings);
			oos.writeObject(sensors);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			Log.e("har", "IOException in save: " + e.getLocalizedMessage());
			return false;
		}
		return true;
	}

	/**
	 * Loads the neural network and the information.
	 * 
	 * @return true if the data was loaded correctly false else
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	public boolean load() throws FileNotFoundException {
		if (!(new File(file + NN_FILE)).exists()) {
			throw new FileNotFoundException();
		}
		neuralNetwork = new NeuralNetwork(file + NN_FILE);
		if (status == Status.IDLING) {
			if (!trainings.contains(0)) {
				status = Status.TRAINED;
			} else {
				status = Status.INITIALIZED;
			}
			return true;
		}
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(file + NNM_FILE));
			this.inputNeurons = ois.readInt();
			this.status = (Status) ois.readObject();
			this.activities = (List<String>) ois.readObject();
			this.trainings = (List<Integer>) ois.readObject();
			this.sensors = (List<String>) ois.readObject();
			ois.close();
			if (!trainings.contains(0)) {
				status = Status.TRAINED;
			} else {
				status = Status.INITIALIZED;
			}
		} catch (IOException e) {
			Log.e("har", "IOException in load: " + e.getLocalizedMessage());
			return false;
		} catch (ClassNotFoundException e) {
			Log.e("har",
					"ClassNotFoundException in load: "
							+ e.getLocalizedMessage());
			return false;
		}
		close();
		return true;
	}

	/**
	 * Recognizes the running activity based on the given time window.
	 * 
	 * @param timeWindow
	 *            Should contain all relevant data
	 * @return A hash which matches a previously trained activity
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public double recognise(TimeWindow timeWindow) throws NullPointerException,
			IllegalArgumentException {
		if (status == Status.NOTINITIALIZED) {
			throw new NullPointerException("No neural network found!");
		}
		if (status == Status.INITIALIZED) {
			throw new NullPointerException("No trained neural network found!");
		}
		if (activities.size() == 0) {
			throw new NullPointerException("No activities found!");
		}
		if (sensors.size() == 0) {
			throw new NullPointerException("No sensors found!");
		}
		if (timeWindow.getFeatureSet().size() != inputNeurons) {
			throw new IllegalArgumentException(
					"Feature set size does not match number of input neurons!");
		}

		double[] input = new double[inputNeurons];
		double[] output = new double[1];

		int i = 0;
		for (Entry<String, Double> entry : timeWindow.getFeatureSet()
				.entrySet()) {
			input[i] = entry.getValue() / LONG_MAX;
			if (Math.abs(input[i]) >= 1) {
				throw new IllegalArgumentException("Feature " + i
						+ " did not match -1 <= x <= 1!");
			}
			if (Double.isNaN(input[i])) {
				throw new IllegalArgumentException("Feature " + i + " is NaN!");
			}
			if (Double.isInfinite(input[i])) {
				throw new IllegalArgumentException("Feature " + i
						+ " is infinity!");
			}
			i++;
		}
		neuralNetwork.input(input);
		neuralNetwork.result(output);
		return output[0];
	}

	/**
	 * Trains the neural network with the given time window and increments the
	 * number of training from the trained activity.
	 * 
	 * @param timeWindow
	 *            Should contain all relevant data
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public void train(TimeWindow timeWindow) throws NullPointerException,
			IllegalArgumentException {
		if (status == Status.NOTINITIALIZED) {
			throw new NullPointerException("No neural network found!");
		}
		if (activities.size() == 0) {
			throw new NullPointerException("No activities found!");
		}
		if (sensors.size() == 0) {
			throw new NullPointerException("No sensors found!");
		}
		if (timeWindow.getFeatureSet().size() != inputNeurons) {
			throw new IllegalArgumentException(
					"Feature set size does not match number of input neurons!");
		}
		double[] features = new double[inputNeurons];
		double[] output = new double[1];
		double[] target = { timeWindow.getActivityLabel().hashCode() / LONG_MAX };

		int i = 0;
		for (Entry<String, Double> entry : timeWindow.getFeatureSet()
				.entrySet()) {
			features[i] = entry.getValue() / LONG_MAX;
			if (Math.abs(features[i]) >= 1) {
				throw new IllegalArgumentException("Feature " + i
						+ " did not match -1 <= x <= 1!");
			}
			if (Double.isNaN(features[i])) {
				throw new IllegalArgumentException("Feature " + i + " is NaN!");
			}
			if (Double.isInfinite(features[i])) {
				throw new IllegalArgumentException("Feature " + i
						+ " is infinity!");
			}
			i++;
		}
		neuralNetwork.input(features);
		neuralNetwork.result(output);
		neuralNetwork.backProp(target);
		int currentActivity = activities.indexOf(timeWindow.getActivityLabel());
		trainings.set(currentActivity, trainings.get(currentActivity) + 1);
		if (status.equals(Status.INITIALIZED) && !trainings.contains(0)) {
			status = Status.TRAINED;
		}
		if (savePeriode == 256) {
			try {
				save();
			} catch (FileNotFoundException e) {
				Log.e("har",
						"FileNotFoundException in train: "
								+ e.getLocalizedMessage());
			}
			savePeriode = 0;
		} else {
			savePeriode++;
		}
	}

	/**
	 * Deletes the neural network from the memory.
	 */
	public void close() {
		if (!(neuralNetwork == null)) {
			neuralNetwork.delete();
		}
		neuralNetwork = null;
		status = Status.IDLING;
	}

	/**
	 * Deletes the neural network and additional information from the storage
	 * and memory.
	 * 
	 * @throws FileNotFoundException
	 */
	public void delete() throws FileNotFoundException {
		(new File(file + NN_FILE)).delete();
		(new File(file + NNM_FILE)).delete();
		if (!(neuralNetwork == null)) {
			neuralNetwork.delete();
		}
		neuralNetwork = null;
		activities.clear();
		trainings.clear();
		sensors.clear();
		status = Status.NOTINITIALIZED;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the activities
	 */
	public List<String> getActivities() {
		return activities;
	}

	/**
	 * @param activity
	 *            to add
	 */
	public void addActivity(String activity) {
		if (activities.contains(activity)) {
			return;
		}
		activities.add(activity);
		trainings.add(0);
	}

	/**
	 * @param activity
	 *            to remove
	 */
	public void removeActivity(String activity) {
		if (activities.size() == 0) {
			return;
		}
		int location = activities.indexOf(activity);
		if (activities.remove(activity)) {
			trainings.remove(location);
		}
	}

	/**
	 * @return the supported sensors
	 */
	public List<String> getSensors() {
		return sensors;
	}

	/**
	 * @param activity
	 *            to add
	 */
	public void addSensor(String sensor) {
		if (sensors.contains(sensor)) {
			return;
		}
		try {
			delete();
		} catch (FileNotFoundException e) {
			Log.e("har",
					"FileNotFoundException in addSensor: "
							+ e.getLocalizedMessage());
		}
		sensors.add(sensor);
		Collections.sort(sensors);
	}

	/**
	 * @param activity
	 *            to remove
	 */
	public void removeSensor(String sensor) {
		if (sensors.size() == 0) {
			return;
		}
		if (sensors.remove(sensor)) {
			try {
				delete();
			} catch (FileNotFoundException e) {
				Log.e("har",
						"FileNotFoundException in removeSensor: "
								+ e.getLocalizedMessage());
			}
		}
	}
}
