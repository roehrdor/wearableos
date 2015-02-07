package de.unistuttgart.vis.wearable.os.service;

import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackHandler;
import de.unistuttgart.vis.wearable.os.api.IGarmentAPI;
import de.unistuttgart.vis.wearable.os.app.MainActivity;
import de.unistuttgart.vis.wearable.os.utils.Constants;

/**
 * The Garment OS Kernel Service
 * @author roehrdor
 */
public class GarmentOSSerivce extends android.app.Service {
	public static android.content.Context context;
	
	//
	// This objects contains and implements all the call able functions called
	// from the public API Interface
	//
	protected IGarmentAPI.Stub APIBinder = new APIBinder();
	
	//
	// Callback list, this list stores all the handles of applications that have
	// registered to be called back using the IGarmentServiceCallback
	//
	static final android.os.RemoteCallbackList<CallbackNode> mCallbacks = new android.os.RemoteCallbackList<CallbackNode>();
	static final android.os.Handler mHandler = new CallbackHandler(mCallbacks);
	
	
	//
	// =========================================================
	// Kernel functions
	// =========================================================
	//
	/**
	 * Callback with the given flag and send the given object
	 *
	 * @param flag
	 *            the callback flag
	 * @param bco
	 *            the object to be sent
	 */
	public static void callback(int flag, BaseCallbackObject bco) {
		mHandler.obtainMessage(Constants.CALLBACK, flag, 0x0, bco);
	}
	
	
	
	
	
	// =========================================================
	// Getter and setter functions
	// =========================================================
	/**
	 * <p>This functions returns the context of the </p>
	 * @return 
	 */
	public static android.content.Context getContext() {
		if(context == null)
			context = MainActivity.getMainActivityContext();
		return context;
	}
	
	
	
	// =========================================================
	// Overriden functions
	// =========================================================
	@Override
	public int onStartCommand(android.content.Intent intent, int flags, int startId) {
		return START_STICKY; 
	}
	
	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		if(IGarmentAPI.class.getName().equals(intent.getAction()))
			return this.APIBinder;
		else
			return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mHandler.sendEmptyMessage(Constants.CALLBACK_DEBUG_VALUE);
		if(context == null)
			context = getApplicationContext();		
	}
	
}
