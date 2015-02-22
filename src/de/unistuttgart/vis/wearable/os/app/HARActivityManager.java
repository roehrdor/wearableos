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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HARActivityManager extends Activity {

	private Context context = this;
	private Button sensorsBtn;
	private Button activitiesBtn;
	private Button saveBtn;
	private Button loadBtn;
	private Button deleteBtn;
	private TextView numerOfTrainings;
	private TextView numerOfSkippedTrainings;

	private List<Integer> supportedSensorList;
	private List<String> supportedActivityList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_har_manager);

		sensorsBtn = (Button) findViewById(R.id.har_manager_button1);
		sensorsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<String> sensorsList = new ArrayList<String>();
				for(PSensor sensor : APIFunctions.API_getAllSensors()) {
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
				supportedSensorList = ActivityRecognitionModule.getInstance()
						.getSupportedSensorList();
				boolean[] checkedSensors = new boolean[sensors.length];
				int i = 0;
				for (String s : sensors) {
					for (int l : supportedSensorList) {
						if (s.split(" ")[2].equals(String.valueOf(l))) {
							checkedSensors[i] = true;
						}
					}
					i++;
				}
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder
						.setTitle("Sensors")
						.setMultiChoiceItems(
								sensors,
								checkedSensors,
								new DialogInterface.OnMultiChoiceClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {
										if (isChecked) {
											// If the user checked the item, add
											// it to the selected items
											supportedSensorList.add(Integer
													.valueOf(sensors[which]
															.split(" ")[2]));
											Toast.makeText(context,
													sensors[which] + " added",
													Toast.LENGTH_SHORT).show();
										} else if (supportedSensorList.contains(Integer
												.valueOf(sensors[which]
														.split(" ")[2]))) {
											// Else, if the item is already in
											// the array, remove it
											supportedSensorList.remove(Integer
													.valueOf(sensors[which]
															.split(" ")[2]));
											Toast.makeText(
													context,
													sensors[which] + " removed",
													Toast.LENGTH_SHORT).show();
										}
									}
								})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
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

		});

		activitiesBtn = (Button) findViewById(R.id.har_manager_button2);
		activitiesBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String[] activities = new String[ActivityEnum.values().length - 1];
				int i = 0;
				for(ActivityEnum ae : ActivityEnum.values()) {
					if (!ae.equals(ActivityEnum.NOACTIVITY)) {
						activities[i] = ae.toString();
						i++;
					}
				}
				supportedActivityList = ActivityRecognitionModule.getInstance()
						.getSupportedActivityList();
				boolean[] checkedActivities = new boolean[activities.length];
				i = 0;
				for (String s : activities) {
					for (String l : supportedActivityList) {
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
						.setMultiChoiceItems(
								activities,
								checkedActivities,
								new DialogInterface.OnMultiChoiceClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {
										if (isChecked) {
											// If the user checked the item, add
											// it to the selected items
											supportedActivityList
													.add(activities[which]);
											Toast.makeText(context, 
													activities[which] + " added", 
													Toast.LENGTH_SHORT).show();
										} else if (supportedActivityList
												.contains(activities[which])) {
											// Else, if the item is already in
											// the array, remove it
											supportedActivityList.remove(activities[which]);
											Toast.makeText(context, 
													activities[which] + " added", 
													Toast.LENGTH_SHORT).show();
										}
									}
								})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
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
				updateUI();
			}

		});
		
		saveBtn = (Button) findViewById(R.id.har_manager_button3);
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityRecognitionModule.getInstance().saveNeuralNetworkToFile();
				Toast.makeText(context, "Neural network saved",
						Toast.LENGTH_SHORT).show();
				updateUI();
			}
			
		});
		
		loadBtn = (Button) findViewById(R.id.har_manager_button4);
		loadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityRecognitionModule.getInstance().loadNeuralNetwork();
				Toast.makeText(context, "Neural network loaded",
						Toast.LENGTH_SHORT).show();
				updateUI();
			}
			
		});
		
		deleteBtn = (Button) findViewById(R.id.har_manager_button5);
		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityRecognitionModule.getInstance().deleteNeuralNetworkFromMemory();
				Toast.makeText(context, "Neural network deleted",
						Toast.LENGTH_SHORT).show();
				updateUI();
			}
			
		});
		
		numerOfTrainings = (TextView) findViewById(R.id.har_manager_textView2);	
		numerOfSkippedTrainings = (TextView) findViewById(R.id.har_manager_textView4);
		
		updateUI();
	}
	
	private void updateUI() {
		numerOfTrainings.setText(String.valueOf(ActivityRecognitionModule
				.getInstance().getNumberOfTrainings()));
		numerOfSkippedTrainings.setText(String.valueOf(ActivityRecognitionModule
				.getInstance().getTotalskippedTrainings()) + " maximum: "
				+ String.valueOf(ActivityRecognitionModule.getInstance()
						.getMaximumSkippedTrainings()));
	}

}
