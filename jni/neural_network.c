/*
 * name:		neural_network.c
 * date:		2014-09-28
 * creator:		roehrdor
 * description:	This file implements the defined functions for the neural
 * 				network.
 *
 * 	bugs:		none
 * 	history:	-
 * 	finished:	[x]
 *
 * (c) 2014 - roehrdor.com
 */

#include "include/neural_network_global.h"
#include "include/neural_network.h"
#include "include/neural_network_private.h"
#include "include/neural_network_serial.h"

int const neural_net_size = sizeof(neural_net);


/*************** Neural Network functions ***************/
/* public */
/*
 * Construct a new neural network.
 *
 * The neural network consists of different layers, where each layer can have
 * a number of neurons
 *
 * @param topology array which sizes determines the number of layers, on each
 * 			individual position the number of neurons in this layer is stored
 * @param size the size of the given topology array, which specifies the number
 * 			of layers in the neural network
 * @return the neural network
 */
neural_net *new_neural_net(const uint *topology, const uint size)
{
	neural_net *network;
	uint num_outputs, layer_num, neuron_num, current_topology;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* We do not allow a nullpointer as topology, exit creation */
	if(topology == NULL)
		return NULL;
#endif	

	/* Allocate memory for our neural network structure */
	network = malloc(sizeof(neural_net));

	/* Set the number of layers */
	network->num_layers = size;

	/* Allocate memory for the layers of our neural network */
	network->layers = malloc(sizeof(layer) * size);

	network->recent_average_error = 0.0;
	network->error = 0.0;

	/* Iterate through our topology and create the neurons */
	for(layer_num = 0; layer_num != size; ++layer_num)
	{
		/*
		 * We need to know the number of outputs per neuron
		 * If we are already in the output layer there is no need for
		 * any outputs otherwise lookup the number up.
		 */
		num_outputs = layer_num == size - 1 ? 0 : topology[layer_num + 1];

		/*
		 * Cache the number of neurons in the current layer,
		 * including the bias neuron
		 */
		current_topology = topology[layer_num] + 1;

		/* Allocate memory for the neurons in the current layer */
		network->layers[layer_num].num_neurons = current_topology;
		network->layers[layer_num].neurons = malloc(sizeof(neuron*) * (current_topology));

		/* Create all the neurons in the current layer */
		for(neuron_num = 0; neuron_num != current_topology; ++neuron_num)
			network->layers[layer_num].neurons[neuron_num] = new_neuron(num_outputs, neuron_num);

		/* The last neuron is our bias neuron, which has a constant output of 1.0 */
		neuron_set_output_value(network->layers[layer_num].neurons[current_topology - 1], 1.0);
	}

	/* return the created network */
	return network;
}

/*
 * Create a new neural network from an existent one
 *
 * @param source_file the file to load the neural network from
 * @return the loaded neural network
 */
neural_net *new_neural_net_load(const char * const source_file)
{
	FILE *fp;
	char *content;
	long read_hash, computed_hash, size;
	neural_net *network;
	neural_network_serial_header header;
	neuron *neur;
	layer *current_layer;
	uint index, layer_num, neuron_num, k, num_neurons, num_outputs;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* We do not allow a nullpointer as file, exit loading */
	if(source_file == NULL)
		return NULL;
#endif	

	/* Try to open the file */
	fp = fopen(source_file, "rb");

	if(fp == NULL)
	{
		fclose(fp);
		/* return NN_COULD_NOT_OPEN_FILE; */
		return NULL;
	}

	computed_hash = 0x0;

	/* Get the file size */
	fseek(fp, 0, SEEK_END);
	size = ftell(fp);
	fseek(fp, 0, SEEK_SET);

	/* Copy the file into memory */
	content = malloc(size);
	fread(content, sizeof(char), size, fp);
	fclose(fp);

	/* 
	 * Read the stored hash value from the file and compute an hash value 
	 * for the file 
	 */
	read_hash = *(content + sizeof(neural_network_serial_header) - sizeof(long));
	memcpy(&read_hash, content + sizeof(neural_network_serial_header) - sizeof(long), sizeof(long));
	memcpy(content + sizeof(neural_network_serial_header) - sizeof(long), &computed_hash, sizeof(long));
	computed_hash = hash(content, size);

	/* 
	 * Check the beginning of the file for the tag and compare the read and
	 * computed hash value
	 */
	if(computed_hash != read_hash || strncmp(content, "nn", 2) != 0)			
	{
		free(content);

		/* File damaged */
		return NULL;	
	}

	/* No errors detected in the file, so we can proceed reading it */
	network = malloc(sizeof(neural_net));

	/* Copy back the header from the virtual file */
	memcpy(&header, content, sizeof(neural_network_serial_header));
	network->num_layers = header.num_layers;
	network->layers = malloc(sizeof(layer) * network->num_layers);

	/* Set the index to the end of the file header */
	index = sizeof(neural_network_serial_header);

	/* Get back the neural net attributes */
	memcpy(&network->error, content + index, sizeof(double));
	index += sizeof(double);
	memcpy(&network->recent_average_error, content + index, sizeof(double));
	index += sizeof(double);

	/* Now that we know the number of layers we can iterate through the file */
	for(layer_num = 0; layer_num != network->num_layers; ++layer_num)
	{
		/* 
		 * Cache the current layer and get back the number of neurons in this 
		 * layer 
		 */
		current_layer = &network->layers[layer_num];
		memcpy(&current_layer->num_neurons, content + index, sizeof(uint));
		index += sizeof(uint);
		num_neurons = current_layer->num_neurons;
		current_layer->neurons = malloc(sizeof(neuron*) * num_neurons);		

		/* Iterate over all the neurons in this layer */
		for(neuron_num = 0; neuron_num != num_neurons; ++neuron_num)
		{					
			/* Get the number of output neurons */	
			memcpy(&num_outputs, content + index, sizeof(uint));
			index += sizeof(uint);

			/* 
			 * Now that we have the number of output neurons and the neuron 
			 * number we can construct a new neuron
			 */
			current_layer->neurons[neuron_num] = new_neuron(num_outputs, neuron_num);

			/* Cache the current neuron */
			neur = current_layer->neurons[neuron_num];

			/* Set all the parameters */
			neur->num_outputs = num_outputs;
			memcpy(&neur->index, content + index, sizeof(uint));
			index += sizeof(uint);
			memcpy(&neur->value, content + index, sizeof(double));
			index += sizeof(double);
			memcpy(&neur->gradient, content + index, sizeof(double));
			index += sizeof(double);

			/* Iterate over all the outputs and get back their weight */
			for(k = 0; k != num_outputs; ++k)
			{
				memcpy(&neur->output_weights[k].weight, content + index, sizeof(double));
				index += sizeof(double);
				memcpy(&neur->output_weights[k].delta_weight, content + index, sizeof(double));
				index += sizeof(double);
			}
		}
	}
	
	/* Delete the virtual file */
	free(content);

	return network;
}


/*
 * Delete an existent neural network and free the allocated memory.
 *
 * @param net the neural network to deconstruct
 */
void delete_neural_net(neural_net *net)
{
	uint layer_num, num_layers, neuron_num, num_neurons;

	/* Handle nullpointer */
	if(net == NULL)
		return;

	/* Cache the number of layers */
	num_layers = net->num_layers;

	/* Iterate through all the layers and delete them */
	for(layer_num = 0; layer_num != num_layers; ++layer_num)
	{
		/* Cache the number of neurons in the current layer */
		num_neurons = net->layers[layer_num].num_neurons;

		/* Iterate through all the neurons in the current layer and delete them */
		for(neuron_num = 0; neuron_num != num_neurons; ++neuron_num)
			delete_neuron(net->layers[layer_num].neurons[neuron_num]);

		/* Free the memory allocated for the pointer to the neurons */
		free(net->layers[layer_num].neurons);
	}

	/* Free the memory allocated for the layers */
	free(net->layers);

	/* Free the memory allocated for the neural network */
	free(net);
}


/*
 * Feed the neural network with new input values.
 * This function will classify the new input values and set the output values
 * accordingly. This function asserts the size of the given input array (size)
 * equals the number of input neurons in the neural network.
 *
 * @param net the neural network to feed with the given input values
 * @param input the input values for the neural network
 * @param size the size of the input values array which must equal the number
 * 			of input neurons in the given neural network
 */
void neural_net_feed_forward(const neural_net *net, const double * const input, const uint size)
{
	uint num_input_neurons, layer_num, i, n, num_layers, num_neurons_no_bias;
	layer *previous_layer;
	num_input_neurons = net->layers[0].num_neurons;
	num_layers = net->num_layers;	

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(net == NULL || input == NULL)
		return;
#endif

	/* The input's array size must equal the number of input neurons (not bias) */
	if(size != num_input_neurons - 1)
	{
		/* Print error and return */

		return;
	}

	/* Assign the given input values to the neural network */
	for(i = 0; i != size; ++i)
		neuron_set_output_value(net->layers[0].neurons[i], input[i]);

	/* Iterate over all layers (despite input) and feed forwad the given input */
	for(layer_num = 1; layer_num != num_layers; ++layer_num)
	{
		/* Cache the previous layer and the number of neurons without bias */
		previous_layer = &net->layers[layer_num-1];
		num_neurons_no_bias = net->layers[layer_num].num_neurons - 1;

		/* Iterate over all neurons and feed them */
		for(n = 0; n != num_neurons_no_bias; ++n)
			neuron_feed_forward(net->layers[layer_num].neurons[n], previous_layer);
	}
}


/*
 * Train the network using back propagation. Before calling this function new
 * input values should have been set. This function will update the neuron
 * values and connection weights according to the given target values.
 *
 * This function asserts the size of the target values array which is given
 * by the parameter size must equal the number of output neurons in the neural
 * network
 *
 * @param net the neural network to train according to the given target values
 * @param target target values which are the desired output values for the
 * 			already set input values
 * @param size the size of the target values array
 */
void neural_net_back_prop(neural_net * const net, const double * const target, const uint size)
{
	layer *output_layer, *layer, *previous_layer, *hidden_layer, *next_layer;
	uint n, layer_num, num_layer, output_neurons_no_bias;
	double delta;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(net == NULL || target == NULL)
		return;
#endif

	/*
	 * Set the error to 0 and cache the number of layers, the output layer
	 * and the number of neurons in the output layer
	 */
	net->error = 0.0;
	num_layer = net->num_layers;
	output_layer = &net->layers[num_layer - 1];
	output_neurons_no_bias = output_layer->num_neurons - 1;

	/* The number of output neurons not including the bias must equal the size */
	if(output_neurons_no_bias != size)
	{
		/* Print error and return */

		return;
	}

	/* Calculate the overall error */
	for(n = 0; n != output_neurons_no_bias; ++n)
	{
		delta = target[n] - output_layer->neurons[n]->value;
		net->error += delta * delta;
	}

	/* Calculate average error */
	net->error /= output_neurons_no_bias;

	/* RMS */
	net->error = sqrt(net->error);

	/* Implement a recent average measurement */
	net->recent_average_error = (net->recent_average_error
			* recent_average_error_smoothing_factor + net->error)
			/ (recent_average_error_smoothing_factor + 1.0);

	/* Compute output layer gradients */
	for(n = 0; n != output_neurons_no_bias; ++n)
		neuron_compute_output_gradients(output_layer->neurons[n], target[n]);

	/* Compute hidden layer gradients */
	for(layer_num = num_layer - 2; layer_num != 0; --layer_num)
	{
		hidden_layer = &net->layers[layer_num];
		next_layer = &net->layers[layer_num + 1];

		for(n = 0; n != hidden_layer->num_neurons; ++n)
			neuron_compute_hidden_gradients(hidden_layer->neurons[n], next_layer);
	}

	/* Update connection weights */
	for(layer_num = num_layer - 1; layer_num != 0; --layer_num)
	{
		layer = &net->layers[layer_num];
		previous_layer = &net->layers[layer_num - 1];

		for(n = 0; n != layer->num_neurons - 1; ++n)
			neuron_update_input_weights(layer->neurons[n], previous_layer);
	}
}


/*
 * Get the classification results from the neural network. Before the input
 * values must have been set using the feed forward function.
 *
 * This function asserts the size of the result array which is given by the
 * parameter size must equal the number of output neurons in the neural network
 *
 * @param net the neural network to extract the output values from
 * @param res the result values array to store the results in
 * @param size the size of the result values array
 */
void neural_net_get_results(const neural_net * const net, double * const res, const uint size)
{
	uint n, num_output_neurons_no_bias;
	layer *output_layer;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(net == NULL || res == NULL)
		return;
#endif

	/*
	 * Cache the output layer and the number of neurons in the output layer
	 * without the bias neuron
	 */
	output_layer = &net->layers[net->num_layers - 1];
	num_output_neurons_no_bias = output_layer->num_neurons - 1;

	/* The number of output values must equal the res array size */
	if(num_output_neurons_no_bias != size)
	{
		/* Print error and return */

		return;
	}

	/* Iterate over all output neurons and store the value */
	for(n = 0; n != num_output_neurons_no_bias; ++n)
		res[n] = neuron_get_output_value(output_layer->neurons[n]);
}


/*
 * Get the recent average error rate of the neural network.
 *
 * @param the neural network to get the average error rate from
 * @return the average error rate
 */
double neural_net_recent_average_error(const neural_net * const net)
{
#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(net == NULL)
		return NAN;
#endif

	return net->recent_average_error;
}


/*
 * Save the neural network to the given destination file for later use.
 *
 * @param net the neural net to save
 * @param destination_file the file to save the network in
 * @return error code
 */
uint neural_net_save(const neural_net * const net, const char *destination_file)
{
	FILE *fp;
	neural_network_serial_header header;
	layer *current_layer;
	neuron *neur;
	char *content;
	uint i, j, k, num_neurons, num_connections, index, size;	

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(net == NULL || destination_file == NULL)
		return NN_ILLEGAL_PARAMTER;
#endif

	/* Try to open the destination file to write the network to */
	fp = fopen(destination_file, "wb");

	/* Check whether we have a valid file handle */
	if(fp == NULL)
	{
		/* If not close just in case and return the error */
		fclose(fp);
		return NN_COULD_NOT_WRITE_FILE;
	}
	
	/* Create the serial header contents */
	strcpy(header.tag, "nn"); 
	header.num_layers = net->num_layers;
	strcpy(header.version, "b001");
	header.num_neurons_input = net->layers[0].num_neurons;
	header.num_neurons_output = net->layers[header.num_layers - 1].num_neurons;
	header.num_neurons_hidden_total = 0;
	for(i = 1; i != header.num_layers - 1; ++i)
		header.num_neurons_hidden_total += net->layers[i].num_neurons;
	header.hash = 0x0;

	/* Compute file size that needs to be written */	
	size = sizeof(neural_network_serial_header) + sizeof(double) * 5 + net->num_layers * sizeof(uint);
	for(i = 0; i != net->num_layers; ++i)
	{	 		 	
	 	num_neurons = net->layers[i].num_neurons;
	 	for(j = 0; j != num_neurons; ++j)	 
	 		size += sizeof(double) * 2 + sizeof(uint) * 2 + sizeof(double) * 2 * net->layers[i].neurons[j]->num_outputs;
	 		
	}

	/* Allocate enough memory for the file size */
	content = malloc(size);

	/* Copy the header to virutal file */	
	memcpy(content, &header, sizeof(neural_network_serial_header));
	index = sizeof(neural_network_serial_header);

	/* Copy the error number and recent average error rate to the virtual file */
	memcpy(content + index, &net->error, sizeof(double));
	index += sizeof(double);
	memcpy(content + index, &net->recent_average_error, sizeof(double));
	index += sizeof(double);

	/* Iterate over all layers to write them and their content down */
	for(i = 0; i != net->num_layers; ++i)
	{
		/* Cache the current layer and number of neurons */
		current_layer = &net->layers[i];
		num_neurons = current_layer->num_neurons;

		/* Copy the number of neurons in that layer to the virtual file */
		memcpy(content + index, &current_layer->num_neurons, sizeof(uint));
		index += sizeof(uint);

		/* Iterate over all neurons in the current layer */
		for(j = 0; j != num_neurons; ++j)
		{
			/* Cache the current neuron */
			neur = current_layer->neurons[j];

			/* Copy all its attributes to the virutal file */
			memcpy(content + index, &neur->num_outputs, sizeof(uint));
			index += sizeof(uint);
			memcpy(content + index, &neur->index, sizeof(uint));
			index += sizeof(uint);
			memcpy(content + index, &neur->value, sizeof(double));
			index += sizeof(double);
			memcpy(content + index, &neur->gradient, sizeof(double));
			index += sizeof(double);

			/* Cache number of outgoing connections */
			num_connections = neur->num_outputs;

			/* Iterate over all the connections and write them to the virtual file */
			for(k = 0; k != num_connections; ++k)
			{
				memcpy(content + index, &neur->output_weights[k].weight, sizeof(double));
				index += sizeof(double);
				memcpy(content + index, &neur->output_weights[k].delta_weight, sizeof(double));
				index += sizeof(double);
			}
		}
	}

	/* Copy eta, alpha and recent average error smoothing factor value to the file */
	memcpy(content + index, &eta, sizeof(double));
	index += sizeof(double);
	memcpy(content + index, &alpha, sizeof(double));
	index += sizeof(double);
	memcpy(content + index, &recent_average_error_smoothing_factor, sizeof(double));
	index += sizeof(double);

	/* Compute the header */
	header.hash = hash(content, size);

	/* Copy the header to the file */
	memcpy(content + sizeof(neural_network_serial_header) - sizeof(long), &header.hash, sizeof(long));

	/* Finally write the contents of the virtual file to disk */
	fwrite(content, sizeof(char), size, fp);

	/* Delete the virtual file */
	free(content);

	/* Close the file handle */
	fclose(fp);

	return NN_SUCCESS;
}


/*************** Neuron functions ***************/
/* public */
/*
 * Create a new neuron with the given number of output neurons and the given
 * index. The index specifies the number of the neuron within its layer, this
 * is needed to specify the outputs from the previous layers to this neuron.
 *
 * @param num_outputs the number of outputs from this neuron
 * @param index the index in the current layer
 * @return the neuron
 */
neuron *new_neuron(uint num_outputs, uint index)
{
	neuron *n;
	uint c;

	n = malloc(sizeof(neuron));
	n->output_weights = malloc(sizeof(connection) * num_outputs);
	n->num_outputs = num_outputs;
	n->index = index;
	n->value = 0.0;
	n->gradient = 0.0;

	for(c = 0; c != num_outputs; ++c)
	{
		n->output_weights[c].weight = random_weight();
		n->output_weights[c].delta_weight = 0.0;
	}

	return n;
}


/*
 * Deconstruct and delete the created neuron and free the allocated memory.
 *
 * @param n the neuron to deconstruct
 */
void delete_neuron(neuron *n)
{
	/* Handle nullpointer */
	if(n == NULL)
		return;

	free(n->output_weights);
	free(n);
}


/*
 * Set the output value of the neuron.
 *
 * @param n the neuron to change
 * @param val the new output value for the given neuron
 */
void neuron_set_output_value(neuron * const n, double val)
{
#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(n == NULL)
		return;
#endif
	n->value = val;
}


/*
 * Get the output value of the neuron.
 *
 * @param n the neuron to get the output value from
 * @return the output value of the given neuron
 */
double neuron_get_output_value(const neuron * const n)
{
#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(n == NULL)
		return NAN;
#endif
	return n->value;
}


/*
 * Feed previous values forward in the neural network to classify an given
 * input.
 *
 * @param n the neuron to get feeded forward
 * @param previous_layer the previous layer that feeds forward the given neuron
 */
void neuron_feed_forward(neuron * const n, const layer * const previous_layer)
{	
	double sum;
	uint i;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(n == NULL || previous_layer == NULL)
		return;
#endif

	sum = 0.0;

	for(i = 0; i != previous_layer->num_neurons; ++i)
		sum += previous_layer->neurons[i]->value * previous_layer->neurons[i]->output_weights[n->index].weight;

	n->value = transfer_function(sum);
}


/*
 * Compute the gradient of the output layer neuron.
 *
 * @param n the neuron to compute the gradient for
 * @param target the output target value
 */
void neuron_compute_output_gradients(neuron * const n, const double target)
{
	double delta;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(n == NULL)
		return;
#endif

	delta = target - n->value;
	n->gradient = delta * transfer_function_dx(n->value);
}


/*
 * Compute the gradient of the hidden layer neuron.
 *
 * @param n the neuron to compute the gradient for
 * @param next_layer the subsequent layer of the one the neuron lays in
 */
void neuron_compute_hidden_gradients(neuron * const n, const layer * const next_layer)
{
	double dow;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(n == NULL || next_layer == NULL)
		return;
#endif

	dow = neuron_sum_dow(n, next_layer);
	n->gradient = dow * transfer_function_dx(n->value);
}


/*
 * Update the input weights of the given neuron from the previous layer.
 *
 * @param n the neuron to update the input layer in
 * @param previous_layer the layer to update the input weights from
 */
void neuron_update_input_weights(const neuron *n, const layer *previous_layer)
{
	uint i;
	neuron *neur;
	double old_delta_weight, new_delta_weight;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(n == NULL || previous_layer == NULL)
		return;
#endif

	for(i = 0; i != previous_layer->num_neurons; ++i)
	{
		neur = previous_layer->neurons[i];
		old_delta_weight = neur->output_weights[n->index].delta_weight;
		new_delta_weight = eta * neur->value * n->gradient + alpha * old_delta_weight;

		neur->output_weights[n->index].delta_weight = new_delta_weight;
		neur->output_weights[n->index].weight += new_delta_weight;
	}
}


/* private */
/*
 * Compute the sum of the outgoing weights multiplied with the pointed to
 * neuron values.
 *
 * @param n the neuron to compute the dow for
 * @param next_layer the next layer to get the target neurons
 * @return the dow
 */
double neuron_sum_dow(const neuron * const n, const layer * const next_layer)
{
	double sum = 0.0;
	uint i;

#ifdef NN_ENABLE_NULLPTR_CHECK
	/* Handle nullpointer */
	if(n == NULL || next_layer == NULL)
		return NAN;
#endif

	for(i = 0; i != next_layer->num_neurons - 1; ++i)
		sum += n->output_weights[i].weight * next_layer->neurons[i]->gradient;

	return sum;
}


/*
 * Transfer learning function, in our case tanh(x) with the range
 * from [-1.0 ... 1.0]
 *
 * @param x input value for the formula
 * @return the result in range [-1.0 ... 1.0] for the given input
 */
double transfer_function(const double x)
{
	return tanh(x);
}


/*
 * The derivative of the transfer function
 *
 * @param x input value for the formula
 * @return result of the formula for the given input
 */
double transfer_function_dx(const double x)
{
	double n;
	n = transfer_function(x);
	return 1.0 - n * n;
}


/*
 * Return a random value in rage [0.0 ... 1.0)
 *
 * @return random double value
 */
double random_weight(void)
{
	return rand() / (double)RAND_MAX;
}

uint neural_net_get_input_neurons_number(neural_net *net) 
{
	return net->layers[0].num_neurons - 1;
}

uint neural_net_get_output_neurons_number(neural_net *net)
{
	return net->layers[net->num_layers - 1].num_neurons - 1;
}
