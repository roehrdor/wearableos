LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES:=neural_network.c de_unistuttgart_vis_wearable_os_activityRecognition_NeuralNetwork.c
LOCAL_MODULE:=neuralNetwork
include $(BUILD_SHARED_LIBRARY)
