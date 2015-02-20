/*
 * name:		neural_network_serial.h
 * date:		2014-09-28
 * creator:		roehrdor
 * description:	This file defines a structure for a file header to save the 
 * 				neural network for later use. 
 *				This file also implements a hash function to verify stored
 *				neural networks.
 *
 * 	bugs:		none
 * 	history:	-
 * 	finished:	[x]
 *
 * (c) 2014 - roehrdor.com
 */

#ifndef NEURONAL_NETWORK_SERIAL_H_
#define NEURONAL_NETWORK_SERIAL_H_

#ifdef __cplusplus	
extern "C" {
#endif

#include "neural_network_global.h"

typedef struct s_neural_network_serial_header neural_network_serial_header;

/*
 * Structure for the header to serialize the neural network.
 * The header stores the number of layers, input neurons and output neurons as
 * well as the number of total hidden neurons and a hash value to verify a 
 * previously stored network.
 */
struct s_neural_network_serial_header
{
	char tag[3];
	char version[5];
	uint num_layers;
	uint num_neurons_input;
	uint num_neurons_output;
	uint num_neurons_hidden_total;
	long hash;
};

/*
 * Calculate the hash for a given string or file content with the given length
 *
 * @param s the string for file content to hash
 * @param len the length of the content to hash
 * @return the computed hash value
 */
INL long hash(char *s, uint len)
{
	unsigned long hash;
	uint i;
	hash = len + 0x734A5E;
	i = -1;

	while(++i != len)
		hash = (((hash << 0x5) ^ (hash >> 0x1B)) ^ s[i]);

	return hash;
}

#ifdef __cplusplus	
}
#endif

#endif /* NEURONAL_NETWORK_SERIAL_H_ */