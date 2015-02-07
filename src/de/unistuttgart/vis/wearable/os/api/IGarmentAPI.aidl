package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;

interface IGarmentAPI {
	long currentTime();
	
	void registerCallback(IGarmentCallback callback, int ID);
	void unregisterCallback(IGarmentCallback callback, int ID);
}