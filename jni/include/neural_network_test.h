/*
 * name:		neural_network_test.h
 * date:		2014-09-29
 * creator:		roehrdor
 * description:	This file defines all the test routines to test this neural
 *				network.
 *
 * 	bugs:		none
 * 	history:	-
 * 	finished:	[x]
 *
 * (c) 2014 - roehrdor.com
 */

#ifndef NEURONAL_NETWORK_TEST_H_
#define NEURONAL_NETWORK_TEST_H_

#include "neural_network.h"
#include "neural_network_private.h"

void test_neural_network_compare(neural_net *net, neural_net *net2);
void test_neural_network_save_load();


#endif /* NEURONAL_NETWORK_TEST_H_ */