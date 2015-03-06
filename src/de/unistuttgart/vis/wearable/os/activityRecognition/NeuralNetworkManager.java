package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;
import de.unistuttgart.vis.wearable.os.activityRecognition.TimeWindow;

public class NeuralNetworkManager {

	public static enum Status {
		NOTINITIALIZED, INITIALIZED, TRAINED;
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
	private byte savePeriode = 0;
	private String file = "";
	private final String NN_FILE = "nn.data";
	private final String NNM_FILE = "nn.data";

	public NeuralNetworkManager(String file) {
		Log.i("har", "NeuralNetworkManager loaded");
		this.file = file;
		try {
			load();
		} catch(FileNotFoundException e) {
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
		return true;
	}

	/**
	 * Saves the neural network and the information in two separate files
	 * (nn.data and nnm.data)
	 * 
	 * @return true if the data was saved correctly false else
	 */
	public boolean save() throws FileNotFoundException {
		if(!(new File(file + NN_FILE)).exists()) {
			throw new FileNotFoundException();
		}
		neuralNetwork.saveNetwork(file + NN_FILE);
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(
					new FileOutputStream(file + NNM_FILE));
			oos.writeObject(status);
			oos.writeObject(activities);
			oos.writeObject(trainings);
			oos.writeObject(sensors);
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Loads the neural network and the information.
	 * 
	 * @return true if the data was loaded correctly false else
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public boolean load() throws FileNotFoundException {
		if(!(new File(file + NN_FILE)).exists()) {
			throw new FileNotFoundException();
		}
		neuralNetwork = new NeuralNetwork(file + NN_FILE);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(file + NNM_FILE));
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
			return true;
		} catch (IOException e) {

		} catch (ClassNotFoundException e) {

		}
		return false;
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
		if(status == Status.NOTINITIALIZED) {
			throw new NullPointerException("No neural network found!");
		}
		if(status == Status.INITIALIZED) {
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

		double[] features = new double[inputNeurons];
		double[] target = new double[1];

		int i = 0;
		for (Entry<String, Double> entry : timeWindow.getFeatureSet()
				.entrySet()) {
			features[i] = entry.getValue() / LONG_MAX;
			if (Math.abs(features[i]) >= 1) {
				throw new IllegalArgumentException(
						"A feature did not match -1 <= x <= 1!");
			}
			neuralNetwork.classifiy(features, target);
		}
		return target[0];
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
		if(status == Status.NOTINITIALIZED) {
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
		double[] target = { timeWindow.getActivityLabel().hashCode() / LONG_MAX };

		int i = 0;
		for (Entry<String, Double> entry : timeWindow.getFeatureSet()
				.entrySet()) {
			features[i] = entry.getValue() / LONG_MAX;
			if (Math.abs(features[i]) >= 1) {
				throw new IllegalArgumentException(
						"A feature did not match -1 <= x <= 1!");
			}
			neuralNetwork.train(features, target);
			i++;
		}
		int currentActivity = activities.indexOf(timeWindow.getActivityLabel());
		trainings.set(currentActivity, trainings.get(currentActivity) + 1);
		if (status.equals(Status.NOTINITIALIZED) && !trainings.contains(0)) {
			status = Status.TRAINED;
		}
		if(savePeriode == Byte.MAX_VALUE) {
			try {
				save();
			} catch (FileNotFoundException e) {
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
		neuralNetwork.delete();
		status = Status.NOTINITIALIZED;
	}

	/**
	 * Deletes the neural network and additional information from the storage
	 * and memory.
	 * 
	 * @throws FileNotFoundException
	 */
	public void delete() throws FileNotFoundException {
		neuralNetwork.delete();
		status = Status.NOTINITIALIZED;
		new File(file + NN_FILE).delete();
		new File(file + NNM_FILE).delete();
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
		activities.add(activity);
		trainings.add(0);
	}

	/**
	 * @param activity
	 *            to remove
	 */
	public void removeActivity(String activity) {
		int location = activities.indexOf(activity);
		activities.remove(activity);
		trainings.remove(location);
	}

	/**
	 * @return the activities
	 */
	public List<String> getSensors() {
		return sensors;
	}

	/**
	 * @param activity
	 *            to add
	 */
	public void addSensor(String sensor) {
		sensors.add(sensor);
	}

	/**
	 * @param activity
	 *            to remove
	 */
	public void removeSensor(String sensor) {
		activities.remove(sensor);
	}
}
