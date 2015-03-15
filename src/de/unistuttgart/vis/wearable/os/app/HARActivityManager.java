package de.unistuttgart.vis.wearable.os.app;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.activity.ActivityEnum;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class HARActivityManager extends Activity {

	private Context context = this;
	private Button sensorsBtn;
	private Button activitiesBtn;
	private Button saveBtn;
	private Button loadBtn;
	private Button deleteBtn;

	private List<String> sensorsList;
	private List<String> activitiesList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_har_manager);

		sensorsBtn = (Button) findViewById(R.id.har_manager_sensors);
		activitiesBtn = (Button) findViewById(R.id.har_manager_activities);
		saveBtn = (Button) findViewById(R.id.har_manager_save);
		loadBtn = (Button) findViewById(R.id.har_manager_load);
		deleteBtn = (Button) findViewById(R.id.har_manager_delete);
		if (APIFunctions.getNeuralNetworkStatus().toString()
				.equals("NOTINITIALIZED")) {
			saveBtn.setText(R.string.har_button_manager_create);
			deleteBtn.setVisibility(View.GONE);
		} else {
			saveBtn.setText(R.string.button_manager_save);
			deleteBtn.setVisibility(View.VISIBLE);
		}

		sensorsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sensorBtn();
			}
		});
		activitiesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activitiesBtn();
			}
		});
		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteBtn = (Button) findViewById(R.id.har_manager_delete);
				if (APIFunctions.getNeuralNetworkStatus().toString()
						.equals("NOTINITIALIZED")) {
					createBtn();
				} else {
					saveBtn();
				}
			}
		});
		loadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadBtn();
			}
		});
		deleteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteBtn();
			}
		});

	}

	protected void sensorBtn() {
		sensorsList = new ArrayList<String>();
		for (PSensor pSensor : APIFunctions.API_getAllSensors()) {
			if (pSensor.isEnabled()) {
				sensorsList.add(pSensor.getDisplayedSensorName() + " \nID: "
						+ pSensor.getID() + " (enabled)");
			} else {
				sensorsList.add(pSensor.getDisplayedSensorName() + " \nID: "
						+ pSensor.getID() + " (disabled)");
			}
		}
		final String[] sensors = Arrays.copyOf(sensorsList.toArray(),
				sensorsList.size(), String[].class);
		sensorsList.clear();
		sensorsList = APIFunctions.getSensors();
		final boolean[] checkedSensors = new boolean[sensors.length];
		int i = 0;
		for (String s : sensors) {
			for (String l : sensorsList) {
				if (s.split(" ")[2].equals(l)) {
					checkedSensors[i] = true;
				}
			}
			i++;
		}

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder
				.setTitle("Sensors")
				.setMultiChoiceItems(sensors, checkedSensors,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									// If the user checked the item, add
									// it to the selected items
									checkedSensors[which] = true;
									// sensorsList.add(sensors[which].split(" ")[2]);
								} else {
									// Else, if the item is already in
									// the array, remove it
									checkedSensors[which] = false;
									// sensorsList.remove(sensors[which]
									// .split(" ")[2]);
								}
							}
						})
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < sensors.length; i++) {
							if (checkedSensors[i]) {
								if (!APIFunctions.getSensors().contains(
										sensors[i].split(" ")[2])) {
									APIFunctions.addSensor(
											sensors[i].split(" ")[2]);
								}
							} else {
//								APIFunctions.removeSensor(
//										(sensors[i].split(" ")[2]));
							}
						}
						Log.i("har", "currently supported sensors: " + APIFunctions.getSensors().toString());
						dialog.cancel();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).show();
	}

	protected void activitiesBtn() {
		final String[] activities = new String[ActivityEnum.values().length - 1];
		int i = 0;
		for (ActivityEnum ae : ActivityEnum.values()) {
			if (!ae.equals(ActivityEnum.NOACTIVITY)) {
				activities[i] = ae.toString();
				i++;
			}
		}
		activitiesList = APIFunctions.getSupportedActivities();
		final boolean[] checkedActivities = new boolean[activities.length];
		i = 0;
		for (String s : activities) {
			for (String l : activitiesList) {
				if (s.equals(l)) {
					checkedActivities[i] = true;
				}
			}
			i++;
		}
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder
				.setTitle("Activities")
				.setMultiChoiceItems(activities, checkedActivities,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									// If the user checked the item, add
									// it to the selected items
									checkedActivities[which] = true;
								} else if (activitiesList
										.contains(activities[which])) {
									// Else, if the item is already in
									// the array, remove it
									checkedActivities[which] = false;
								}
							}
						})
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < activities.length; i++) {
							if (checkedActivities[i]) {
								if (!APIFunctions.getSupportedActivities().contains(
										activities[i])) {
									APIFunctions.addActivity(
											activities[i]);
								}
							} else {
//								APIFunctions.removeActivity(
//										(activities[i]));
							}
						}
						Log.i("har", "currently supported activities: " + APIFunctions.getSupportedActivities().toString());
						dialog.cancel();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).show();
	}

	protected void saveBtn() {
		try {
			APIFunctions.saveNeuralNetwork();
		} catch (FileNotFoundException e) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setTitle("ERROR").setMessage(e.getLocalizedMessage())
					.show();
		}
	}

	protected void loadBtn() {
		try {
			APIFunctions.loadNeuralNetwork();
			this.recreate();
		} catch (FileNotFoundException e) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setTitle("ERROR").setMessage(e.getLocalizedMessage())
					.show();
		}
	}

	protected void deleteBtn() {
		try {
			APIFunctions.deleteNeuralNetwork();
			this.recreate();
		} catch (FileNotFoundException e) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setTitle("ERROR").setMessage(e.getLocalizedMessage())
					.show();
		}
	}

	protected void createBtn() {
		try {
			if (APIFunctions.createNeuralNetwork()) {
				this.recreate();
				Toast.makeText(context, "Neural network created",
						Toast.LENGTH_SHORT);
			} else {
				Toast.makeText(context, "Neural network not created",
						Toast.LENGTH_SHORT);
			}
		} catch (IllegalArgumentException e) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setTitle("ERROR").setMessage(e.getLocalizedMessage())
					.show();
		}
	}
}
