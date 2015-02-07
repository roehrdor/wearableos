package de.unistuttgart.vis.wearable.os.app;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.utils.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	//
	// We actually have two Intent names here, one is for the public SDK the
	// other one is for the private SDK. For more information regarding 
	// public and private SDK please read the project documentation
	//
	public static final String SERVICE_INTENT_NAME = "de.unistuttgart.vis.wearable.os.service.GarmentOSSerivce";
	public static final String SERVICE_INTERNAL_INTENT_NAME = "de.unistuttgart.vis.wearable.os.internalservice.GarmentOSServiceInternal";
	
	//
	// Static fields to store the context of the settings application.
	//
	private static android.content.Context context;
	private static boolean serviceStarted = false;
	private static boolean servicInternalStarted = false;

	//
	// These attributes are used to save the database path and password
	//
	private static final String PREFS_NAME = "prefPWPath";
	private static final String PASSWORD = "DatabasePassword";
	private static final String PATH = "DatabasePath";
	private static String mPath;	
	private static String mPassword;
	
	// =====================================================================
	//
	// Functions to save database path and password preferences
	//
	// =====================================================================
	/**
	 * Read the password for the database from the file. If the file does not
	 * exists this function returns null.
	 * 
	 * @return the password or null if not set yet
	 */
	private static String readPWFile() {
		android.content.SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return (mPassword = settings.getString(PASSWORD, null));
	}

	/**
	 * Return the path for the database from the file. If the file does not
	 * exist the function will return null.
	 * 
	 * @return the path or null if not set yet
	 */
	private static String readPathFile() {
		android.content.SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return (mPath = settings.getString(PATH, null));
	}

	/**
	 * This function writes the given path and password to the corresponding
	 * files. If a parameter is null the corresponding file will not be modified
	 * or if not yet existent created.
	 * 
	 * @param path
	 *            the path of the database, null if not to change
	 * @param pw
	 *            the password of the database, null if not to change
	 */
	private static void writeToFile(String path, String pw) {
		android.content.SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		android.content.SharedPreferences.Editor editor = settings.edit();
		if (pw != null) {
			editor.putString(PASSWORD, pw);
		}

		if (path != null) {
			editor.putString(PATH, path);
		}
		editor.commit();
	}
	

	/**
	 * Set the path for the database and save it for later use
	 * 
	 * @param path
	 *            the database path
	 */
	public static void setPath(String path) {
		mPath = path;
		writeToFile(path, null);
	}

	/**
	 * Set the password for the database and save it for later use
	 * 
	 * @param pw
	 *            the database password
	 */
	public static void setPW(String pw) {
		mPassword = pw;
		writeToFile(null, pw);
	}

	/**
	 * Returns the database password that is currently saved
	 * 
	 * @return the current database password
	 */
	public static String getPW() {
		android.util.Log.d("gosDEBUG", "MainActivity:getPW() - " + mPassword);
		return mPassword;
	}

	/**
	 * Returns the database path that is currently saved
	 * 
	 * @return the current database path that
	 */
	public static String getPath() {
		android.util.Log.d("gosDEBUG", "MainActivity:getPath() - " + mPath);
		return mPath;
	}

	// =====================================================================
	//
	// Activity Functions
	//
	// =====================================================================
	/**
	 * <p>
	 * Get the context of the main activity.
	 * </p>
	 * <p>
	 * Note that this function can return null if the context has not yet been
	 * set, which is quite unlikely.
	 * </p>
	 * 
	 * @return the context of the main activity
	 */
	public static android.content.Context getMainActivityContext() {
		return context;
	}
		
	@Override
	protected void onStart() {
		super.onStart();			
		
		//
		// Start the public service 
		//
		if(!serviceStarted) {
			android.content.Intent serviceIntent = Utils.explicitFromImplicit(context, new android.content.Intent(SERVICE_INTENT_NAME));
			serviceStarted = startService(serviceIntent) != null;
			android.util.Log.d("orDEBUG", "MainActivity:onStart() - started public service");
		}
		
		//
		// Start the Settings application exclusive service 
		//
		if(!servicInternalStarted) {
			android.content.Intent serviceInternalIntent = Utils.explicitFromImplicit(context, new android.content.Intent(SERVICE_INTERNAL_INTENT_NAME));
			servicInternalStarted = startService(serviceInternalIntent) != null;
			android.util.Log.d("orDEBUG", "MainActivity:onStart() - started internal service");
		}	
		
		final TextView textView = (TextView) findViewById(R.id.textView1);
		
		new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				while(true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					final long time = APIFunctions.getTime();
					runOnUiThread(new Runnable() {						
						@Override
						public void run() {
							textView.setText(String.valueOf(time));	
						}
					});					
				}				
			}
		}).start();
	}

	TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//
		// We need to store the application context in the static context field
		//
		context = this.getApplicationContext();
		readPathFile();
		readPWFile();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
