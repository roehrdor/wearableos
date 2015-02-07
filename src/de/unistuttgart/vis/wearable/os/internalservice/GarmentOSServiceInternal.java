package de.unistuttgart.vis.wearable.os.internalservice;

import de.unistuttgart.vis.wearable.os.app.MainActivity;
import de.unistuttgart.vis.wearable.os.internalapi.IGarmentInternalAPI;
import de.unistuttgart.vis.wearable.os.internalapi.PUserApp;

/**
 * This service shall be bound by the settings app only.
 * 
 * @author roehrdor
 */
public class GarmentOSServiceInternal extends android.app.Service {
	public static android.content.Context context;
	
	//
	// This objects contains and implements all the call able functions called
	// from the internal API Interface
	//
	protected IGarmentInternalAPI.Stub APIInternalBinder = new APIInternalBinder();
	
	public static PUserApp pu = new PUserApp("blub", 1);
	
	
	// =========================================================
	// Getter and setter functions
	// =========================================================
	/**
	 * <p>
	 * This functions returns the context of the
	 * </p>
	 * 
	 * @return
	 */
	public static android.content.Context getContext() {
		if (context == null)
			context = MainActivity.getMainActivityContext();
		return context;
	}

	

	// =========================================================
	// Overriden functions
	// =========================================================
	@Override
	public int onStartCommand(android.content.Intent intent, int flags,
			int startId) {
		return START_STICKY;
	}

	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		if (IGarmentInternalAPI.class.getName().equals(intent.getAction()))
			return this.APIInternalBinder;
		else
			return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (context == null)
			context = getApplicationContext();
		
		Runnable r = new Runnable() {			
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					android.util.Log.d("orDEBUG", "Service: " + pu.getName());
				}
			}
		};
		
		new Thread(r).start();
	}
}
