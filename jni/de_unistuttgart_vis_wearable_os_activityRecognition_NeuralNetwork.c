#include "include/de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork.h"
#include "include/neural_network.h"

static void **networks;
static uint instances = 0;
static uint num_existent = 0;
static uint num_elements = 0;
static uint max_current_instances = 4;

#define ILLEGAL_INSTANCE_ID 0xFFFFFFFF

inline int get_instance_id(JNIEnv *env, jobject thiz)
{
	jclass thiz_class;
	jfieldID fidnumber;

	/* Get the instance id from the object */
	thiz_class = (*env)->GetObjectClass(env, thiz);
	fidnumber = (*env)->GetFieldID(env, thiz_class, "instance_id", "I");
	if(fidnumber == NULL)
	{
		printf("Fatal, could not read instance_id!\n");
		return ILLEGAL_INSTANCE_ID;
	}
	return (int)(*env)->GetIntField(env, thiz, fidnumber);
}

inline void keep_network(neural_net *network)
{
	//TODO save pointer to neural network in array
	if(networks == NULL)
	{
		max_current_instances = 4;
		networks = malloc(sizeof(void*) * max_current_instances);
	}

	/* Enough memory available */
	if(instances >= max_current_instances)
	{
		void **p;
		max_current_instances *= 2;
		p = realloc(network, max_current_instances);
		if(p == NULL)
		{
			printf("damn\n");
			return;
		}
	}
	networks[instances] = network;
	++instances;
	++num_existent;
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_new_neural_net
 * Signature: ([II)I
 */
JNIEXPORT jint JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1new_1neural_1net(JNIEnv *env, jobject thiz, jintArray topology, jint size)
{	
	uint *c_topology;
	uint i;

	c_topology = (uint*)(*env)->GetIntArrayElements(env, topology, NULL);
	if(NULL == c_topology)
	{
		printf("Error, could not read topology!\n");
		return 0x1;
	}	
	keep_network(new_neural_net(c_topology, size));
	(*env)->ReleaseIntArrayElements(env, topology, (jint*)c_topology, 0);
	return 0x0;
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_new_neural_net_from_file
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1new_1neural_1net_1from_1file(JNIEnv *env, jobject thiz, jstring file_name)
{
	int instance_id;
	const char *str;
	void *instance;
	neural_net *net;

	str = (*env)->GetStringUTFChars(env, file_name, NULL);
	if(str == NULL)
		return 0x1;


	instance = net = new_neural_net_load(str);
	(*env)->ReleaseStringUTFChars(env, file_name, str);		
	
	/* File could not be read or has been damaged */
	if(instance == NULL)
		return 0x1;

	keep_network(instance);	

	//
	// Set the number of neurons in the object
	//
	jclass thizClass = (*env)->GetObjectClass(env, thiz);
	
	//
	// Set the number of input neurons
	//
	jfieldID fidNumberOfInputNeurons = (*env)->GetFieldID(env, thizClass, "numberOfInputNeurons", "I");
	if(fidNumberOfInputNeurons == NULL)
		return 0x1;
	jint numberOfInputNeurons = (*env)->GetIntField(env, thiz, fidNumberOfInputNeurons);
	numberOfInputNeurons = neural_net_get_input_neurons_number(net);
	printf("%d\n", numberOfInputNeurons);
	(*env)->SetIntField(env, thiz, fidNumberOfInputNeurons, numberOfInputNeurons);

	//
	// Set the number of output neurons
	//
	jfieldID fidNumberOfOutputNeurons = (*env)->GetFieldID(env, thizClass, "numberOfOutputNeurons", "I");
	if(fidNumberOfOutputNeurons == NULL)
		return 0x1;
	jint numberOfOutputNeurons = (*env)->GetIntField(env, thiz, fidNumberOfOutputNeurons);
	numberOfOutputNeurons = neural_net_get_output_neurons_number(net);
	(*env)->SetIntField(env, thiz, fidNumberOfOutputNeurons, numberOfOutputNeurons);

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
	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_elements - 1 < instance_id)
		return;
	
	/* Now that we got our ID we can delete the neural network */
	delete_neural_net((neural_net*)networks[instance_id]);
	--num_existent;
	if(num_existent == 0)
	{
		free(networks);
		networks = NULL;
		num_elements = 0;
		max_current_instances = 0;
		instances = 0;
	}
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_neural_net_feed_forward
 * Signature: ([DI)V
 */
JNIEXPORT void JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1neural_1net_1feed_1forward(JNIEnv *env, jobject thiz, jdoubleArray input, jint size)
{
	int instance_id;
	uint i;
	double *dinput;		

	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_elements - 1 < instance_id)
	{
		printf("Fatal, could not get instance_id\n");
		return;
	}
	dinput = (double*)(*env)->GetDoubleArrayElements(env, input, NULL);
	neural_net_feed_forward((neural_net*)networks[instance_id], dinput, (int)size);
	(*env)->ReleaseDoubleArrayElements(env, input, (jdouble*)dinput, 0);
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_neural_net_back_prop
 * Signature: ([DI)V
 */
JNIEXPORT void JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1neural_1net_1back_1prop(JNIEnv *env, jobject thiz, jdoubleArray target, jint size)
{
	int instance_id;
	uint i;
	double *dtarget;

	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_elements - 1 < instance_id)
		return;

	dtarget = (double*)(*env)->GetDoubleArrayElements(env, target, NULL);
	neural_net_back_prop((neural_net*)networks[instance_id], dtarget, (int)size);
	(*env)->ReleaseDoubleArrayElements(env, target, (jdouble*)dtarget, 0);

}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_neural_net_get_results
 * Signature: ([DI)V
 */
JNIEXPORT void JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1neural_1net_1get_1results(JNIEnv *env, jobject thiz, jdoubleArray res, jint size)
{
	int instance_id;
	uint i;
	double *dresult;

	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_elements - 1 < instance_id)
		return;

	dresult = (double*)(*env)->GetDoubleArrayElements(env, res, NULL);
	neural_net_get_results((neural_net*)networks[instance_id], dresult, size);
	(*env)->ReleaseDoubleArrayElements(env, res, (jdouble*)dresult, 0);
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_neural_net_get_recent_average_error
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1neural_1net_1get_1recent_1average_1error(JNIEnv *env, jobject thiz)
{
	int instance_id;

	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_elements - 1 < instance_id)
		return ILLEGAL_INSTANCE_ID;

	return (jdouble)neural_net_recent_average_error((neural_net*)networks[instance_id]);
}

/*
 * Class:     de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork
 * Method:    j_neural_net_save
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork_j_1neural_1net_1save(JNIEnv *env, jobject thiz, jstring file_name)
{
	const char *str;
	int instance_id;
	jint return_value;

	if((instance_id = get_instance_id(env, thiz)) == ILLEGAL_INSTANCE_ID || num_elements - 1 < instance_id)
		return ILLEGAL_INSTANCE_ID;

	str = (*env)->GetStringUTFChars(env, file_name, NULL);

	if(str == NULL)
		return;

	return_value = (jint)neural_net_save((neural_net*)networks[instance_id], str);
	(*env)->ReleaseStringUTFChars(env, file_name, str);	
	return return_value;
}

