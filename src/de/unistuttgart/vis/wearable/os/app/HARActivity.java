package de.unistuttgart.vis.wearable.os.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.activity.ActivityEnum;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityRecognitionModule;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HARActivity extends Activity {

	// status of the HAR System
	private String[] harStatus = { "not trained", "currently training",
			"currently recognizing", "idling" };
	String selectedActivity = "";

	final Context context = this;
	private TextView harStatusTxt;
	private TextView currentActivityTxt;
	private Button trainBtn;
	private Button testBtn;
	private Button manageBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_har);

		if (!ActivityRecognitionModule.getInstance().isNeuralNetworkExisting()) {
			initalizeNeuralNetworkManager();
		}

		harStatusTxt = (TextView) findViewById(R.id.har_textView_status_detail);
		currentActivityTxt = (TextView) findViewById(R.id.har_textView_show_activity);

		// creating train button
		trainBtn = (Button) findViewById(R.id.button_har_train);
		trainBtn.setOnClickListener(new OnClickListener() {
			/**
			 * stops the training if the HAR System is currently training, else
			 * loads the train activity
			 */
			@Override
			public void onClick(View v) {

				if (ActivityRecognitionModule.getInstance()
						.isCurrentlyTraining()) {
					ActivityRecognitionModule.getInstance().stopTraining();
					updateHARStatus();

				} else if (ActivityRecognitionModule.getInstance()
						.getSupportedSensorList().size() == 0) {
					// if there are no registered sensors, show error dialog
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							context);
					alertDialogBuilder
							.setTitle("Training not possible")
							.setMessage(
									"No supported Sensors found!\n"
											+ "Make sure that sensors are connected and try again.")
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
					Intent intent = new Intent(context,
							HARActivityTraining.class);
					startActivity(intent);
				}
			}
		});

		// creating test button
		testBtn = (Button) findViewById(R.id.button_har_test);
		testBtn.setText("Start Test");
		testBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ActivityRecognitionModule.getInstance()
						.isCurrentlyRecognizing()) {
					ActivityRecognitionModule.getInstance().stopRecognising();
					updateHARStatus();

				} else {
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							ActivityRecognitionModule.getInstance()
									.startRecognising(2000);
							return null;
						}
					}.execute();
				}

			}
		});

		// neural manager button
		manageBtn = (Button) findViewById(R.id.button_har_nnmanager);
		manageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HARActivityManager.class);
				startActivity(intent);
			}

		});
		updateHARStatus();
	}

	/**
	 * on first start set neural network features
	 */
	private void initalizeNeuralNetworkManager() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder
				.setTitle("First Start")
				.setMessage(
						"This is your first start, pleas follow the introductions"
								+ "in the following dialogs to set up yout activity recognition"
								+ " system.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						setSensors();
						dialog.cancel();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								HARActivity.this.finish();
								dialog.cancel();

							}
						}).setCancelable(false).show();
	}

	private void setSensors() {
		List<String> sensorsList = new ArrayList<String>();
		for (PSensor sensor : APIFunctions.API_getAllSensors()) {
			// TODO look if it makes sense
			if (APIFunctions.SENSORS_SENSOR_isEnabled(sensor.getID())) {
				sensorsList.add(sensor.getDisplayedSensorName() + " \nID: "
						+ sensor.getID() + " (enabled)");
			} else {
				sensorsList.add(sensor.getDisplayedSensorName() + " \nID: "
						+ sensor.getID() + " (disabled)");
			}
		}
		final String[] sensors = Arrays.copyOf(sensorsList.toArray(),
				sensorsList.size(), String[].class);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder
				.setTitle("Select Sensors")
				.setMultiChoiceItems(sensors, null,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									if (ActivityRecognitionModule
											.getInstance()
											.addSupportedSensor(
													Integer.valueOf(sensors[which]
															.split(" ")[2]))) {
									}
								} else if (ActivityRecognitionModule
										.getInstance()
										.getSupportedActivityList()
										.contains(
												Integer.valueOf(sensors[which]
														.split(" ")[2]))) {
									ActivityRecognitionModule
											.getInstance()
											.removeSupportedSensor(
													Integer.valueOf(sensors[which]
															.split(" ")[2]));
								}
							}
						})
				.setPositiveButton("Next",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (ActivityRecognitionModule.getInstance()
										.getSupportedSensorList().size() == 0) {
									new AlertDialog.Builder(context)
											.setMessage("No sensors selected!")
											.setNeutralButton(
													"Ok",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog2,
																int which) {
															setSensors();
															dialog2.cancel();

														}
													}).show();
								} else {
									setActivities();
									dialog.cancel();
								}

							}
						})
				.setNegativeButton("Back",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								initalizeNeuralNetworkManager();
								dialog.cancel();
							}
						}).setCancelable(false).show();
	}

	private void setActivities() {
		final String[] activities = new String[ActivityEnum.values().length - 1];
		int i = 0;
		for (ActivityEnum ae : ActivityEnum.values()) {
			if (!ae.equals(ActivityEnum.NOACTIVITY)) {
				activities[i] = ae.toString();
				i++;
			}
		}

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder
				.setTitle("Select Activities")
				.setMultiChoiceItems(activities, null,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									// If the user checked the item, add
									// it to the selected items
									ActivityRecognitionModule.getInstance()
											.addSupportedActivity(
													activities[which]);
								} else if (ActivityRecognitionModule
										.getInstance()
										.getSupportedActivityList()
										.contains(activities[which])) {
									// Else, if the item is already in
									// the array, remove it
									ActivityRecognitionModule.getInstance()
											.removeSupportedActivity(
													activities[which]);
								}
							}
						})
				.setPositiveButton("Set",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (ActivityRecognitionModule.getInstance()
										.getSupportedActivityList().size() == 0) {
									new AlertDialog.Builder(context)
											.setMessage(
													"No activities selected!")
											.setNeutralButton(
													"Ok",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog2,
																int which) {
															setActivities();
															dialog2.cancel();

														}
													}).show();
								} else {
									if (ActivityRecognitionModule.getInstance()
											.createNeuralNetwork()) {
										dialog.cancel();
									} else {
										new AlertDialog.Builder(context)
												.setMessage(
														"Error while creating the neural Network, please"
																+ " try again!")
												.setNeutralButton(
														"Ok",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog2,
																	int which) {
																dialog2.cancel();
															}
														});
									}
								}
							}
						})
				.setNegativeButton("Back",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setSensors();
								dialog.cancel();
							}
						}).setCancelable(false).show();
	}

	@Override
	public void onStart() {
		super.onStart();
		updateHARStatus();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateHARStatus();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * updates the HAR status text field
	 */
	private void updateHARStatus() {
		currentActivityTxt.setText(ActivityRecognitionModule.getInstance()
				.getCurrentActivity().getActivityEnum().toString());
		if (!ActivityRecognitionModule.getInstance().isNeuralNetworkTrained()
				&& ActivityRecognitionModule.getInstance()
						.isCurrentlyTraining()) {
			harStatusTxt.setText(harStatus[0]);
			trainBtn.setText("Stop training");
			testBtn.setText("Start training first");
			testBtn.setEnabled(false);
		} else if (!ActivityRecognitionModule.getInstance()
				.isNeuralNetworkTrained()) {
			harStatusTxt.setText(harStatus[0]);
			trainBtn.setText("Start training");
			testBtn.setText("Start training first");
			testBtn.setEnabled(false);
		} else if (ActivityRecognitionModule.getInstance()
				.isCurrentlyTraining()) {
			harStatusTxt.setText(harStatus[1]);
			trainBtn.setText("Stop training");
			testBtn.setText("Stop training first");
			testBtn.setEnabled(false);
		} else if (ActivityRecognitionModule.getInstance()
				.isCurrentlyRecognizing()) {
			harStatusTxt.setText(harStatus[2]);
			trainBtn.setText("Stop recognizing first");
			testBtn.setText("Stop recognizing");
			trainBtn.setEnabled(false);
		} else if (ActivityRecognitionModule.getInstance()
				.isNeuralNetworkTrained()
				&& !ActivityRecognitionModule.getInstance()
						.isCurrentlyRecognizing()
				&& !ActivityRecognitionModule.getInstance()
						.isCurrentlyTraining()) {
			harStatusTxt.setText(harStatus[3]);
			trainBtn.setText("Start training");
			testBtn.setText("Start recognizing");
			testBtn.setEnabled(true);
			trainBtn.setEnabled(true);
		}

	}

}
