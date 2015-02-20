/*
 * name:		neural_network.h
 * date:		2014-09-28
 * creator:		roehrdor
 * description:	This file is the main header file to include. It shall provide
 * 				all the needed API function calls and structures needed to use
 * 				the neural network.
 *
 * 	bugs:		none
 * 	history:	-
 * 	finished:	[x]
 *
 * (c) 2014 - roehrdor.com
 */

#ifndef NEURONAL_NETWORK_H_
#define NEURONAL_NETWORK_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifndef __cplusplus
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <assert.h>
#else
#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cmath>
#include <cassert>
#endif
#include "neural_network_global.h"

typedef struct s_neural_net neural_net;
extern int const neural_net_size;

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
neural_net 	*new_neural_net(const uint *topology, const uint size);

/*
 * Create a new neural network from an existent one
 *
 * @param source_file the file to load the neural network from
 * @return the loaded neural network
 */
neural_net 	*new_neural_net_load(const char * const source_file);

/*
 * Delete an existent neural network and free the allocated memory.
 *
 * @param net the neural network to deconstruct
 */
void 		delete_neural_net(neural_net *net);

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
void		neural_net_feed_forward(const neural_net *net, const double * const input, const uint size);

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
void 		neural_net_back_prop(neural_net * const net, const double * const target, const uint size);

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
void		neural_net_get_results(const neural_net * const net, double * const res, const uint size);

/*
 * Get the recent average error rate of the neural network.
 *
 * @param the neural network to get the average error rate from
 * @return the average error rate
 */
double		neural_net_recent_average_error(const neural_net * const net);

/*
 * Save the neural network to the given destination file for later use.
 *
 * @param net the neural net to save
 * @param destination_file the file to save the network in
 * @return error code
 */
uint 		neural_net_save(const neural_net * const net, const char *destination_file);

uint 		neural_net_get_input_neurons_number(neural_net *net);
uint 		neural_net_get_output_neurons_number(neural_net *net);

#ifdef __cplusplus
}
#endif

#endif /* NEURONAL_NETWORK_H_ */
