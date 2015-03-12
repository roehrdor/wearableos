/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;

interface IGarmentCallback {
	void callback(in BaseCallbackObject value);
}