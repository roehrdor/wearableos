/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.app;

import de.unistuttgart.vis.wearable.os.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;

/**
 * @author Tobias
 *
 */
public class HARActivity extends Activity {

	final Context context = this;
	private TextView harStatusTxt;
	private Button trainBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_har);

		harStatusTxt = (TextView) findViewById(R.id.har_textView_status);
		harStatusTxt.setMovementMethod(new ScrollingMovementMethod());

		trainBtn = (Button) findViewById(R.id.button_har_train);
		try {
			if (APIFunctions.isTraining()) {
				trainBtn.setText(R.string.har_button_stop);
			} else {
				trainBtn.setText(R.string.btn_start_trainingHAR);
			}
		} catch (RuntimeException e) {
			Log.e("har",
					"RuntimeException in onCreate: " + e.getLocalizedMessage());
		}

		try {
			switch (APIFunctions.getNeuralNetworkStatus()) {
			case NOTINITIALIZED:
				trainBtn.setVisibility(View.GONE);
				harStatusTxt.setText(R.string.notinitialized);
				break;
			case INITIALIZED:
				trainBtn.setVisibility(View.VISIBLE);
				harStatusTxt.setText(R.string.initialized);
				break;
			case TRAINED:
				trainBtn.setVisibility(View.VISIBLE);
				harStatusTxt.setText(R.string.initialized);
				break;
			case IDLING:
				trainBtn.setVisibility(View.VISIBLE);
				harStatusTxt.setText(R.string.initialized);
			default:
				break;
			}
		} catch (RuntimeException e) {
			Log.e("har",
					"RuntimeException in onCreate: " + e.getLocalizedMessage());
		}

		trainBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (APIFunctions.getSupportedActivities().size() < 1) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							context);
					alertDialogBuilder
							.setTitle("Error")
							.setMessage("No Activities selected")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}

									}).show();
				} else {
					try {
						if (APIFunctions.isTraining()) {
							APIFunctions.stopTraining();
                            Toast.makeText(context, "Training stopped", Toast.LENGTH_SHORT);
							reBuild();
						} else {
							Intent intent = new Intent(context,
									HARActivityTraining.class);
							startActivity(intent);
						}
					} catch (RuntimeException e) {
                        Toast.makeText(context, "Training not stopped", Toast.LENGTH_SHORT);
						Log.e("har",
								"RuntimeException in onCreate: "
										+ e.getLocalizedMessage());
					}
				}

			}

		});
	}

	protected void reBuild() {
		this.recreate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onRestart() {
		super.onStart();
		this.onCreate(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.action_settings);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = new Intent(context, HARActivityManager.class);
		startActivity(intent);
		return super.onMenuItemSelected(featureId, item);
	}
}
