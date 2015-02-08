/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.api;

import de.unistuttgart.vis.wearable.os.handle.APIHandle;

/**
 * Basically the same as {@link APIFunctions} with the only difference that
 * these functions will be executed asynchronously. Therefore none of the return
 * types of the following functions differs from void. The first parameter of
 * each function is an Object of {@link AsyncResultObject} in which the result
 * of the function call will be saved.
 * 
 * @author roehrdor
 */
public class APIFunctionsAsync {
	
	public static void getTime(final AsyncResultObject aro) {
		if (APIHandle.isServiceBound()) {
			new Thread(new Runnable() {				
				@Override
				public void run() {
					aro.setObject(APIFunctions.getTime());
				}
			}).start();
		} else {
			throw new RuntimeException(
					"Connection to the service could not be established");
		}
	}

	// =============================================================================
	//
	// Public SDK Functions
	//
	// =============================================================================
}
