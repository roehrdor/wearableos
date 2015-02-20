/*
 * name:		neural_network_global.h
 * date:		2014-09-28
 * creator:		roehrdor
 * description:	This file provides some global typedefs and Preprocessor
 * 				definitions that might be useful.
 *
 * 	bugs:		none
 * 	history:	-
 * 	finished:	[x]
 *
 * (c) 2014 - roehrdor.com
 */

#ifndef NEURONAL_NETWORK_GLOBAL_H_
#define NEURONAL_NETWORK_GLOBAL_H_

/*
 * Define some actually useless _IN_OUT_, _IN_ and _OUT_
 * macros which could make the source code a little bit easier
 * to read or at least to understand, since we can now
 * determine on the first sight, whether a parameter is used as:
 *	- _IN_OUT_	input value and return value
 *	- _IN_ 		only as input value (will not change)
 * 	- _OUT_ 	only as a return value (will not look at it but change)
 */
#ifndef _IN_OUT_
#define _IN_OUT_
#else
#undef _IN_OUT_
#define _IN_OUT_
#endif
#ifndef _IN_
#define _IN_
#else
#undef _IN_
#define _IN_
#endif
#ifndef _OUT_
#define _OUT_
#else
#undef _OUT_
#define _OUT_
#endif

typedef unsigned int uint;

#define NN_SUCCESS					0x0
#define NN_COULD_NOT_OPEN_FILE		0x1
#define	NN_COULD_NOT_WRITE_FILE		0x2
#define NN_FALSE_NUMBER_OF_LAYERS	0x4
#define NN_FALSE_NUMBER_OF_NEURONS	0x8
#define NN_FILE_DAMAGED				0x10
#define NN_ILLEGAL_PARAMTER			0x20
#define NN_NOT_YET_IMPLEMENTED		0xFFFFFFFF

#define INL inline

#ifndef NULL
#define NULL						((void *)0)
#endif

#endif /* NEURONAL_NETWORK_GLOBAL_H_ */
