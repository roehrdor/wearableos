#include "include/de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork.h"
#include "fann/include/doublefann.h"
#include <android/log.h>

#define LOG_TAG		"GOS_NN"
#define LOG(...)	__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define ILLEGAL_INSTANCE_ID 0xFFFFFFFF

static struct fann **networks;
static int num_existent = 0;
static int max_current_instances = 0x4;

/*
 * Every instance of the neural netowrk java class has an uniquee instance id to access
 * the underluying native neural network
 * @return the instance id of the neural network or ILLEGAL_INSTANCE_ID if not existent
 */
inline int get_instance_id(JNIEnv *env, jobject thiz)
{
	jclass thiz_class;
	jfieldID fidnumber;

	/* Get the instance id from the object */
	thiz_class = (*env)->GetObjectClass(env, thiz);
	fidnumber = (*env)->GetFieldID(env, thiz_class, "instance_id", "I");
	if(fidnumber == NULL)
	{
		LOG("Fatal, could not read instance_id!\n");
		return ILLEGAL_INSTANCE_ID;
	}
	return (int)(*env)->GetIntField(env, thiz, fidnumber);
}

/*
 * Save the network in memory
 */
inline int keep_network(JNIEnv *env, jobject thiz, struct fann *pann)
{
	int i;
	jclass thiz_class;
	jfieldID fidnumber;

	/* If we do not have any array at the moment allocate memory and create it */
	if(networks == NULL) 
	{
		max_current_instances = 0x4;
		networks = malloc(sizeof(struct fann*) * max_current_instances);
		for(i = 0; i != max_current_instances; ++i)
		    networks[i] = NULL;
	}

	/* 
	 * Check whether we have enough memory other wise reallocate the memory with
	 * twice as much memory 
	 */
	if(num_existent >= max_current_instances)
	{
		struct fann **p;
		max_current_instances *= 2;
		p = realloc(networks, max_current_instances);
		if(p == NULL)
		{
			LOG("Could not reallocate memory for resizing\n");
			return 0x1;
		}
		networks = p;

		for(i = max_current_instances / 2; i != max_current_instances; ++i) 
		{
			networks[i] = NULL;
		}
	}

	/* Increase the counter */
	++num_existent;

	/* Check for the first free array index, this is the instance id */
	for(i = 0; i != max_current_instances; ++i)	 	
		if(networks[i] == NULL)
			break;

	/* Save the network at this position in the array */
	networks[i] = pann;

	/*  Set the internal instance id */
	/* Get the instance id from the object */
	thiz_class = (*env)->GetObjectClass(env, thiz);
	fidnumber = (*env)->GetFieldID(env, thiz_class, "instance_id", "I");
	if(fidnumber == NULL)
	{
		LOG("Fatal, could not get instance_id %d field!\n", i);
		return ILLEGAL_INSTANCE_ID;
	}
	(*env)->SetIntField(env, thiz, fidnumber, i);

	/* Success */
	return 0x0;
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_new_neural_net
 * Signature: ([II)I
 */
JNIEXPORT jint JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1new_1neural_1net(JNIEnv *env, jobject thiz, jintArray topology, jint size)
{		
	int *c_topology;
	unsigned int i;
	struct fann *ann;

	/* Gain access to the int array elements through JVM */
	c_topology = (int*)(*env)->GetIntArrayElements(env, topology, NULL);

	/* Check whether we got access to the array elements */
	if(c_topology == NULL)
	{
		/* If not Log and return with error code */
		LOG("Error, could not read topology!\n");
		return 0x1;
	}

	/* Create the neural network */
	ann = fann_create_standard_array(size, c_topology);

	fann_set_activation_function_hidden(ann, FANN_SIGMOID_SYMMETRIC);
    fann_set_activation_function_output(ann, FANN_SIGMOID_SYMMETRIC);

	/* Now keep the neural entwork */
	keep_network(env, thiz, ann);
	
	/* Release the int array elements */
	(*env)->ReleaseIntArrayElements(env, topology, (jint*)c_topology, 0);

	/* Success */
	return 0x0;
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_new_neural_net_from_file
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1new_1neural_1net_1from_1file(JNIEnv *env, jobject thiz, jstring file_name)
{
	const char *file;
	struct fann *ann;

	file = (*env)->GetStringUTFChars(env, file_name, NULL);
	if(file == NULL)
		return 0x1;

	ann = fann_create_from_file(file);
	(*env)->ReleaseStringUTFChars(env, file_name, file);	
	keep_network(env, thiz, ann);

	/* Success */
	return 0x0;
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_delete_neural_net
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1delete_1neural_1net(JNIEnv *env, jobject thiz)
{	
	int instance_id;
	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_existent - 1 < instance_id)
		return;

	fann_destroy((struct fann *)networks[instance_id]);
	networks[instance_id] = NULL;
	if(num_existent == 0)
	{
		free(networks);
		networks = NULL;
		max_current_instances = 0x0;
	}
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_train
 * Signature: ([DI[DI)V
 */
JNIEXPORT void JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1train(JNIEnv *env, jobject thiz, jdoubleArray input, jint inputSize, jdoubleArray target, jint targetSize)
{
	fann_type *finput, *ftarget;
	double *dinput;
	struct fann *ann;
	int instance_id;
	int i;

	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_existent - 1 < instance_id)
	{
	    LOG("W %d\n", instance_id);
	    return;
	}

	ann = networks[instance_id];

	finput = (double*)(*env)->GetDoubleArrayElements(env, input, NULL);
	ftarget = (double*)(*env)->GetDoubleArrayElements(env, target, NULL);

	fann_train(ann, finput, ftarget);
    (*env)->ReleaseDoubleArrayElements(env, input, (jdouble*)finput, 0);
    (*env)->ReleaseDoubleArrayElements(env, target, (jdouble*)ftarget, 0);
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_run
 * Signature: ([DI[DI)V
 */
JNIEXPORT void JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1run(JNIEnv *env, jobject thiz, jdoubleArray input, jint inputSize, jdoubleArray output, jint outputSize)
{
	fann_type *finput, *fresult;
	double *dresult;
	struct fann *ann;
	int instance_id;
	int i;

	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_existent - 1 < instance_id)
		return;
	ann = networks[instance_id];

	finput = (fann_type*)(*env)->GetDoubleArrayElements(env, input, NULL);
	dresult = (*env)->GetDoubleArrayElements(env, output, NULL);
	fresult = (double*)fann_run(ann, finput);
	memcpy(dresult, fresult, sizeof(double) * outputSize);

#ifdef _NN_DEBUG_
	LOG("-- START OF RUN--");
    LOG("Input data");
    for(i = 0; i != inputSize; ++i)
    {
        LOG("\t%f", finput[i]);
    }
    LOG("Result data");
    for(i = 0; i != outputSize; ++i)
    {
        LOG("\t%f", fresult[i]);
    }
    LOG("-- END OF RUN--");
#endif

	(*env)->ReleaseDoubleArrayElements(env, input, (jdouble*)finput, 0);
	(*env)->ReleaseDoubleArrayElements(env, output, (jdouble*)dresult, 0);
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_neural_net_save
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1neural_1net_1save(JNIEnv *env, jobject thiz, jstring fileName)
{
	struct fann *ann;
	const char *file;
	int instance_id;

	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_existent - 1 < instance_id)
    		return;
    	ann = networks[instance_id];

    file = (*env)->GetStringUTFChars(env, fileName, NULL);
    if(file == NULL)
        return 0x1;

	fann_save(ann, file);
	(*env)->ReleaseStringUTFChars(env, fileName, file);

	return 0x0;
}