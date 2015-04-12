/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.service;

import android.content.Intent;
import android.os.Message;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityRecognitionModule;
import de.unistuttgart.vis.wearable.os.api.BaseCallbackObject;
import de.unistuttgart.vis.wearable.os.api.CallbackHandler;
import de.unistuttgart.vis.wearable.os.api.IGarmentAPI;
import de.unistuttgart.vis.wearable.os.api.IGarmentDriver;
import de.unistuttgart.vis.wearable.os.bluetoothservice.GarmentOSBluetoothService;
import de.unistuttgart.vis.wearable.os.driver.DriverManager;
import de.unistuttgart.vis.wearable.os.privacy.PrivacyManager;
import de.unistuttgart.vis.wearable.os.sensors.InternalSensors;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * The Garment OS Service
 * @author roehrdor
 */
public class GarmentOSService extends android.app.Service {
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
        Message msg = new Message();
        msg.what = Constants.CALLBACK;
        msg.arg1 = flag;
        msg.arg2 = 0x0;
        msg.obj = bco;
        mHandler.sendMessage(msg);
	}

    /**
     * Start the bluetooth service for the given sensor ID
     *
     * @param sensorID the sensor ID to start the service for
     */
    public static void startBTService(int sensorID) {
        Intent startBT = new Intent(context, GarmentOSBluetoothService.class);
        startBT.putExtra("btId", sensorID);
        context.startService(startBT);
    }
	
	
	// =========================================================
	// Getter and setter functions
	// =========================================================
	/**
	 * <p>This functions returns the context of the </p>
	 * @return the context
	 */
	public static android.content.Context getContext() {
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
        if(IGarmentAPI.class.getName().equals(intent.getAction())) {
			String appName = intent.getStringExtra("AppProcess");
			PrivacyManager.instance.registerNewApp(appName);
			return this.APIBinder;
		}					
		else
			return null;
	}

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
	public void onCreate() {
		super.onCreate();		
		mHandler.sendEmptyMessage(Constants.CALLBACK_DEBUG_VALUE);
		if(context == null)
			context = getApplicationContext();
        new InternalSensors(context);
        new Thread(new Runnable() {
			@Override
			public void run() {
				ActivityRecognitionModule.getInstance();
			}
            }).start();
    }
}
