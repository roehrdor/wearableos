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
 * TODO update comments
 * 
 * @author Tobias
 *
 */
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
	private final String NN_FILE = "nn";
	private final String NNM_FILE = "nnm";
	private final String N_FILE = "n";

	/**
	 * Tries to load an existing neural network with the given configuration.
	 * 
	 * @param file
	 *            String with the path
	 */
	public NeuralNetworkManager(String file) {
		this.file = file + "/NeuralNetwork/";
		try {
			load(true);
		} catch (FileNotFoundException e) {
			Log.e("har",
					"FileNotFoundException in constructor: "
							+ e.getLocalizedMessage());
		}
	}

	/**
	 * Creates a hash from all sensor IDs.
	 * 
	 * @return
	 */
	private int sensorsHash() {
		String hash = "";
		for (String s : sensors) {
			hash += hash + s + "_";
		}
		return hash.hashCode();
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
		for (File files : (new File(file)).listFiles()) {
			if (sensorsHash() == Integer.valueOf(files.getName().split("_")[0])) {
				try {
					load(false);
					return true;
				} catch (FileNotFoundException e) {
					Log.e("har",
							"FileNotFoundException in create: "
									+ e.getLocalizedMessage());
				}
			}
		}
		this.inputNeurons = inputNeurons;
		neuralNetwork = new NeuralNetwork(new int[] { inputNeurons,
				inputNeurons / 2, 1 });
		status = Status.INITIALIZED;
		try {
			save();
		} catch (FileNotFoundException e) {
			Log.e("har",
					"FileNotFoundException in create: "
							+ e.getLocalizedMessage());
			close();
			return false;
		}
		return true;
	}

	/**
	 * Saves the neural network and the information in two separate files (nn
	 * and nnm)
	 * 
	 * @return true if the data was saved correctly false else
	 */
	public boolean save() throws FileNotFoundException {
		Log.i("har", "save path: " + file + sensorsHash());
		neuralNetwork.saveNetwork(file + sensorsHash() + NN_FILE);
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file
					+ sensorsHash() + NNM_FILE));
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
		try {
			Log.i("har", "save current nn");
			oos = new ObjectOutputStream(new FileOutputStream(file + N_FILE));
			oos.writeInt(sensorsHash());
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
	@SuppressWarnings("unchecked")
	public boolean load(boolean startUp) throws FileNotFoundException {
		int sensorsHash = sensorsHash();
		// If the System just got started, read the last used neural network in
		// the N file
		if (startUp) {
			if ((new File(file + N_FILE)).exists()) {
				ObjectInputStream ois;
				try {
					Log.i("har", "load current nn");
					ois = new ObjectInputStream(new FileInputStream(file
							+ N_FILE));
					sensorsHash = ois.readInt();
				} catch (IOException e) {
					Log.e("har",
							"IOException in load: " + e.getLocalizedMessage());
				}
			}
		}
		if (!(new File(file + sensorsHash + NN_FILE)).exists()) {
			Log.e("har", "File not found: " + file + sensorsHash + NN_FILE);
			throw new FileNotFoundException();
		}
		Log.i("har", "load path: " + file + sensorsHash + NN_FILE);
		neuralNetwork = new NeuralNetwork(file + sensorsHash + NN_FILE);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(file + sensorsHash
					+ NNM_FILE));
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
		ObjectOutputStream oos;
		try {
			Log.i("har", "save current nn");
			oos = new ObjectOutputStream(new FileOutputStream(file + N_FILE));
			oos.writeInt(sensorsHash());
			oos.flush();
			oos.close();
		} catch (IOException e) {
			Log.e("har", "IOException in save: " + e.getLocalizedMessage());
			return false;
		}
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
		if (savePeriode == Byte.MAX_VALUE) {
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
		activities.clear();
		sensors.clear();
		status = Status.NOTINITIALIZED;
		new File(file + N_FILE).delete();
		new File(file + sensorsHash() + NN_FILE).delete();
		new File(file + sensorsHash() + NNM_FILE).delete();
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
		Log.i("har", "activity added " + activity);
		activities.add(activity);
		trainings.add(0);
	}

	/**
	 * @param activity
	 *            to remove
	 */
	public void removeActivity(String activity) {
		Log.i("har", "activity removed " + activity);
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
		if (sensors.contains(sensor)) {
			return;
		}
		Log.i("har", "sensor added " + sensor);
		sensors.add(sensor);
		Collections.sort(sensors);
	}

	/**
	 * @param activity
	 *            to remove
	 */
	public void removeSensor(String sensor) {

		if (activities.remove(sensor)) {
			Log.i("har", "sensor removed " + sensor);
		} else {
			Log.i("har", "sensor not removed " + sensor);
		}
	}
}
