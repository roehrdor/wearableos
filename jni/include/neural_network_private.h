/*
 * name:		neural_network_private.h
 * date:		2014-09-28
 * creator:		roehrdor
 * description:	This file provides the definitions of the needed private,
 * 				closed API function calls which are not available for public
 * 				use (comparable to private functions in a class) as well as
 * 				the declaration of the needed structures for the neural
 * 				network.
 *
 * 				Later one might decide whether or not the neuron structure
 * 				shall be available public. Therefore we need a typedef in
 * 				neural_network.h for it and to define the public  API functions
 * 				there.
 *
 * 	bugs:		none
 * 	history:	-
 * 	finished:	[x]
 *
 * (c) 2014 - roehrdor.com
 */

#ifndef NEURONAL_NETWORK_PRIVATE_H_
#define NEURONAL_NETWORK_PRIVATE_H_

#ifdef __cplusplus
extern "C" {
#endif

#include "neural_network_global.h"
#include "neural_network.h"

#define NN_ENABLE_NULLPTR_CHECK	

/* Neuron structure for the neural network */
typedef struct s_neuron neuron;

/* Layer structure for the neural network, consists of neurons */
typedef struct s_layer layer;

/* Connection structure to store connection detail between neurons */
typedef struct s_connection connection;

/* net learning rate, [0.0 ... 1.0] */
static double eta = 0.15;

/* momentum [0.0 ... 1.0] */
static double alpha = 0.5;

static double recent_average_error_smoothing_factor = 100.0;

/*
 * Structure implementation of our neural network.
 * Functions for the neural net are defined in the non private header so they
 * are available as API calls.
 *
 * The basic neural network consists of several layers (at least 2) and stores
 * and error as well as an recent_average_error value.
 */
struct s_neural_net
{
	uint num_layers;
	layer *layers;
	double error;
	double recent_average_error;
};

/*
 * Structure for layer of the neural network.
 * A Layer consists of number of neurons as well as the neurons themselves.
 */
struct s_layer
{
	uint num_neurons;
	neuron **neurons;
};

/*
 * Structure for neurons of the neural network.
 * These neurons consist of an index in the current layer as well as the number
 * of output layers and the output weights to all of the neurons in the
 * subsequent layer. The neurons also have a value and a gradient to store.
 */
struct s_neuron
{
	uint index;
	uint num_outputs;
	connection *output_weights;
	double value;
	double gradient;
};

/*
 * Structure for connection between neurons in the neural network.
 * These connections are pretty simple and does not store the target neuron,
 * these connections only store the weight and the delta weight of the
 * connection to neurons in the subsequent layer.
 */
struct s_connection
{
	double weight;
	double delta_weight;
};


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
neuron 		*new_neuron(uint num_outputs, uint index);

/*
 * Deconstruct and delete the created neuron and free the allocated memory.
 *
 * @param n the neuron to deconstruct
 */
void		delete_neuron(neuron *n);

/*
 * Set the output value of the neuron.
 *
 * @param n the neuron to change
 * @param val the new output value for the given neuron
 */
void 		neuron_set_output_value(neuron * const n, double val);

/*
 * Get the output value of the neuron.
 *
 * @param n the neuron to get the output value from
 * @return the output value of the given neuron
 */
double 		neuron_get_output_value(const neuron * const n);

/*
 * Feed previous values forward in the neural network to classify an given
 * input.
 *
 * @param n the neuron to get feeded forward
 * @param previous_layer the previous layer that feeds forward the given neuron
 */
void 		neuron_feed_forward(neuron * const n, const layer * const previous_layer);

/*
 * Compute the gradient of the output layer neuron.
 *
 * @param n the neuron to compute the gradient for
 * @param target the output target value
 */
void	 	neuron_compute_output_gradients(neuron * const n, const double target);

/*
 * Compute the gradient of the hidden layer neuron.
 *
 * @param n the neuron to compute the gradient for
 * @param next_layer the subsequent layer of the one the neuron lays in
 */
void		neuron_compute_hidden_gradients(neuron * const n, const layer * const next_layer);

/*
 * Update the input weights of the given neuron from the previous layer.
 *
 * @param n the neuron to update the input layer in
 * @param previous_layer the layer to update the input weights from
 */
void 		neuron_update_input_weights(const neuron *n, const layer *previous_layer);

/* private */
/*
 * Compute the sum of the outgoing weights multiplied with the pointed to
 * neuron values.
 *
 * @param n the neuron to compute the dow for
 * @param next_layer the next layer to get the target neurons
 * @return the dow
 */
double		neuron_sum_dow(const neuron * const n, const layer * const next_layer);

/*
 * Transfer learning function, in our case tanh(x) with the range
 * from [-1.0 ... 1.0]
 *
 * @param x input value for the formula
 * @return the result in range [-1.0 ... 1.0] for the given input
 */
double		transfer_function(const double x);

/*
 * The derivative of the transfer function
 *
 * @param x input value for the formula
 * @return result of the formula for the given input
 */
double 		transfer_function_dx(const double x);

/*
 * Return a random value in rage [0.0 ... 1.0)
 *
 * @return random double value
 */
double 		random_weight(void);


#ifdef __cplusplus	
}
#endif

#endif /* NEURONAL_NETWORK_PRIVATE_H_ */
