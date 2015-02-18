package de.unistuttgart.vis.wearable.os.activityRecognition;

import java.lang.IllegalArgumentException;

/**
 * <html>
 * <body>
 * <p>The NeuralNetwork class is a Java API for the neural network developed for
 * GarmentOS. </p>
 *
 * <p>The Neural Network needs at least two layer, one for the input
 * and one for the output neurons, otherwise the network can handle an arbitary
 * number of hidden layers with an arbitary number of neurons each (at least 1).
 * The nework creates bias neurons in every hidden layer having a constant 
 * impact on the result and subsequent neurons.</p>
 *
 * <p>This neural network can be saved to disk for later use. A saved network can
 * be loaded to create a new network. A saved network can not be loaded into an
 * already created network but always creates a new one. </p>
 * 
 * <p>In comparison to the original C API the Java API also offers some functions
 * for direct classification which is now done by a single Java function call.
 * The training can now also be one in only one function call.</p>
 * 
 * <p>This class requires the shared neuralnetwork library to be in the same 
 * directory as the binary java NeuralNetwork class file. The shared library
 * is loaded statically to use the native functions.</p>
 * 
 * <p>The C structures (for Java Developers: read instances) are saved in the JNI 
 * source file and explicitly identified by the instance id. Since the memory 
 * for the structures (instances) are allocated in C they must be explicitly 
 * deleted again. Therefore one must call the delete function of this class
 * to free this allocated memory.</p>
 *
 * <p>All values, input and output must and will be in range (-1.0 .. 1.0)</p>
 * 
 * <p>Important:<br/>
 *		Instances that are no longer needed or will never be used again must
 *		be explicitly deleted by the user.</p>
 * 
 * @author Oliver Roehrdanz
 * @version 1.0
 * (c) 2014 - roehrdor.com
 * </body>
 * </html>
 */
public class NeuralNetwork {

	// num_instances count the number of created instances
	private static int num_instances = 0;
	private static int num_existent = 0;

	// instance id to address the JNI structure
	private int instance_id = -1;

	// Also store the number of input and output neurons in the java
	// class, for early error recognition 
	private int numberOfInputNeurons;
	private int numberOfOutputNeurons;

	// Statically load the library
	static {
		System.loadLibrary("neuralNetwork");
	}
	
	/**
	 * Create a new Neural Network with the given topology. The topology is an 
	 * array which size identifies the number of layers in this neura network.
	 * On each array index the number of elements in this layer is stored.
	 * 
	 * There must be at least two layers, one for the input and one for the 
	 * output. If there are less layers in the topology or if there are not 
	 * more than zero Neuron in any layer an  IllegalArgumentException is 
	 * thrown.
	 *
	 * @param topology the topology that describes the layers of the neural
	 *			network
	 */
	public NeuralNetwork(int[] topology) {	
		if(topology.length < 2)
			throw new IllegalArgumentException("There must be at least one input and one output layer");

		for(int i = topology.length - 1; i >= 0; --i)
			if(topology[i] < 1)
				throw new IllegalArgumentException("There must be a neuron in each layer");

		this.numberOfInputNeurons = topology[0];
		this.numberOfOutputNeurons = topology[topology.length - 1];

		if(this.j_new_neural_net(topology, topology.length) == 0)
			this.set_index();
	}
	
	/**
	 * Loads an existing neural network from the given file. If there is no
	 * string passed as argument the constructor throws an 
	 * IllegalArgumentException. 
	 *
	 * @param file the neural network save file to load
	 */
	public NeuralNetwork(String file) {
		if(file == null)
			throw new IllegalArgumentException("File cannot be a nullpointer");

		if(this.j_new_neural_net_from_file(file) == 0)
			this.set_index();
		else
			System.out.println("Could not read file");
	}
	
	/**
	 * Unlike other Java classes this class needs to manually delete its 
	 * instances. Once done this function shall be called on every created
	 * instance, so the allocated memory is freed.
	 */
	public synchronized void delete() {
		this.j_delete_neural_net();
		--num_existent;
		if(num_existent == 0 )
			num_instances = 0;
	}

	/**
	 * Update the input for the neural network.<br/> 
	 * The input value must be in range (-1.0 .. 1.0)
	 *
	 * @param input the input to feed forward
	 */
	public void input(double[] input) {
		if(input.length != this.numberOfInputNeurons)
//			throw new IllegalArgumentException("The number of input neurons must equal the input numbers of the neural network");
		this.j_neural_net_feed_forward(input, input.length);
	}

	/**
	 * Train the neural network with the given input and target data.<br/>
	 * This function calls {@link NeuralNetwork#input} for the input data and
	 * {@link NeuralNetwork#backProp} for the target data.<br/>
	 * The input and target value must be in range (-1.0 .. 1.0)
	 *
	 *
	 * @param input the inputs to feed forward
	 * @param the target result
	 */
	public void train(double[] input, double[] target) {
//		if(input.length != this.numberOfInputNeurons)
//			throw new IllegalArgumentException("The number of input neurons must equal the input numbers of the neural network");
//		if(target.length != this.numberOfOutputNeurons)
//			throw new IllegalArgumentException("The number of target neurons must equal the output numbers of the neural network");

		this.j_neural_net_feed_forward(input, input.length);
		this.j_neural_net_back_prop(target, target.length);
	}

	/**
	 * Train the network with an already given input.<br/>
	 * The target value must be in range (-1.0 .. 1.0) 
	 *
	 * @param target the target value
	 */
	public void backProp(double[] target) {
//		if(target.length != this.numberOfOutputNeurons)
//			throw new IllegalArgumentException("The number of target neurons must equal the output numbers of the neural network");

		this.j_neural_net_back_prop(target, target.length);	
	}

	/**
	 * Get the result of the neural network classification.<br/>
	 * The output value will be in range (-1.0 .. 1.0)
	 *
	 * @param result result output array to store the results in
	 */
	public void result(double[] result) {
//		if(result.length != this.numberOfOutputNeurons)
//			throw new IllegalArgumentException("The number of result neurons must equal the output numbers of the neural network");

		this.j_neural_net_get_results(result, result.length);
	}

	/**
	 * Feed forward the input and fetch the output.<br/>
	 * This function calls {@link NeuralNetwork#input} for the input data
	 * and {@link NeuralNetwork#result} for the output data.<br/>
	 * The input value must be in range (-1.0 .. 1.0). <br/>
	 * The output value will be in range (-1.0 .. 1.0). <br/>
	 * 
	 * @param input the input to feed forward
	 * @param output the result of the neural network
	 */
	public void classifiy(double[] input, double[] output) {
//		if(input.length != this.numberOfInputNeurons)
//			throw new IllegalArgumentException("The number of input neurons must equal the input numbers of the neural network");
//		if(output.length != this.numberOfOutputNeurons)
//			throw new IllegalArgumentException("The number of result neurons must equal the output numbers of the neural network");

		this.j_neural_net_feed_forward(input, input.length);
		this.j_neural_net_get_results(output, output.length);
	}

	/**
	 * Get the recent error rate
	 *
	 * @return the recent error rate
	 */
	public double recentError() {
		return this.j_neural_net_get_recent_average_error();
	}

	/**
	 * Save the network for later use
	 *
	 * @param the file to save the network in
	 * @return errno
	 */
	public int saveNetwork(String file) {
		if(file == null)
			throw new IllegalArgumentException("File cannot be a nullpointer");
		return this.j_neural_net_save(file);
	}

	/**
	 * Set the current index to identify this obect in the JNI Code
	 */
	private synchronized void set_index() {
		this.instance_id = num_instances++;
		++num_existent;
	}
	
	/* 
	 * Native functions that are defined using JNI, 
	 * calling the Neural Network API 
	 */
	private native int j_new_neural_net(int[] topology, int size);
	private native int j_new_neural_net_from_file(String file);
	private native void j_delete_neural_net();
	private native void j_neural_net_feed_forward(double[] input, int size);
	private native void j_neural_net_back_prop(double[] target, int size);
	private native void j_neural_net_get_results(double[] res, int size);
	private native double j_neural_net_get_recent_average_error();
	private native int j_neural_net_save(String file);	
}
