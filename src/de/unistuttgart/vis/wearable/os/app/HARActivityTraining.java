/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class HARActivityTraining extends Activity {

	private boolean live = true;
	private String activity;
	private int windowLength = 2000;
	private Calendar startDate = Calendar.getInstance();
	private Calendar endDate = Calendar.getInstance();
	private DateFormat dateFormat = SimpleDateFormat.getDateInstance();
	private DateFormat timeFormat = SimpleDateFormat
			.getTimeInstance(DateFormat.SHORT);

	final Context context = this;
	private RadioGroup rg;
	private Button cancelBtn;
	private Button startBtn;
	private Button startDateBtn;
	private Button startTimeBtn;
	private Button endDateBtn;
	private Button endTimeBtn;
	private Spinner activitySpinner;
	private EditText millisecondsETx;

	// final ContextThemeWrapper context = new ContextThemeWrapper(this,
	// android.R.style.Theme_Holo_Dialog);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_har_training);

		// get whether live or database training
		rg = (RadioGroup) findViewById(R.id.har_train_radioGroup1);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.har_train_radio0:
					live = true;
					break;
				case R.id.har_train_radio1:
					live = false;
					break;
				}
			}
		});

		// get current date
		final Calendar c = Calendar.getInstance();
		final int cyear = c.get(Calendar.YEAR);
		final int cmonth = c.get(Calendar.MONTH);
		final int cday = c.get(Calendar.DAY_OF_MONTH);
		final int chour = c.get(Calendar.HOUR_OF_DAY);
		final int cminute = c.get(Calendar.MINUTE);

		// set date buttons
		startDateBtn = (Button) findViewById(R.id.har_train_button1);
		startDateBtn.setText(dateFormat.format(c.getTime()));
		startDateBtn.refreshDrawableState();
		startDateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dialog = new DatePickerDialog(context,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								// check if start time is set correct
								if (year > cyear
										|| (year == cyear && monthOfYear > cmonth)
										|| (year == cyear
												&& monthOfYear == cmonth && dayOfMonth > cday)) {

									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
									alertDialogBuilder
											.setTitle("Wrong Date")
											.setMessage(
													"Start date is set in the future!")
											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.cancel();
														}
													}).show();

								} else {
									startDate
											.set(year, monthOfYear, dayOfMonth);
									startDateBtn.setText(dateFormat
											.format(startDate.getTimeInMillis()));
								}
							}
						}, cyear, cmonth, cday);
				dialog.getDatePicker().setCalendarViewShown(false);
				dialog.setTitle("Select start date");
				dialog.show();
			}

		});
		startTimeBtn = (Button) findViewById(R.id.har_train_button2);
		startTimeBtn.setText(timeFormat.format(c.getTime()));
		startTimeBtn.refreshDrawableState();
		startTimeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TimePickerDialog dialog = new TimePickerDialog(context,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								// check if start time is set correct
								if ((startDate.get(Calendar.YEAR) == cyear
										&& startDate.get(Calendar.MONTH) == cmonth
										&& startDate.get(Calendar.DAY_OF_MONTH) == cday && hourOfDay > chour)
										|| startDate.get(Calendar.YEAR) == cyear
										&& startDate.get(Calendar.MONTH) == cmonth
										&& startDate.get(Calendar.DAY_OF_MONTH) == cday
										&& hourOfDay == chour
										&& minute > cminute) {

									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
									alertDialogBuilder
											.setTitle("Wrong Time")
											.setMessage(
													"Start time is set in the future!")
											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.cancel();
														}
													}).show();
								} else {

									startDate.set(Calendar.HOUR_OF_DAY,
											hourOfDay);
									startDate.set(Calendar.MINUTE, minute);
									startTimeBtn.setText(timeFormat
											.format(startDate.getTimeInMillis()));
								}

							}
						}, chour, cminute, true);
				dialog.setTitle("Select start time");
				dialog.show();

			}

		});
		endDateBtn = (Button) findViewById(R.id.har_train_button3);
		endDateBtn.setText(dateFormat.format(c.getTime()));
		endDateBtn.refreshDrawableState();
		endDateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dialog = new DatePickerDialog(context,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								// check if end date is set correct
								if (startDate.get(Calendar.YEAR) > year
										|| (startDate.get(Calendar.YEAR) == year && startDate
												.get(Calendar.MONTH) > monthOfYear)
										|| (startDate.get(Calendar.YEAR) == year
												&& startDate
														.get(Calendar.MONTH) == monthOfYear && startDate
												.get(Calendar.DAY_OF_MONTH) > dayOfMonth)) {

									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
									alertDialogBuilder
											.setTitle("Wrong Date")
											.setMessage(
													"End date is before start date!")
											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.cancel();
														}
													}).show();

								} else {
									endDate.set(year, monthOfYear, dayOfMonth);
									endDateBtn.setText(dateFormat
											.format(endDate.getTimeInMillis()));
								}

							}
						}, cyear, cmonth, cday);
				dialog.getDatePicker().setCalendarViewShown(false);
				dialog.setTitle("Select end date");
				dialog.show();
			}

		});
		endTimeBtn = (Button) findViewById(R.id.har_train_button4);
		endTimeBtn.setText(timeFormat.format(c.getTime()));
		endTimeBtn.refreshDrawableState();
		endTimeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TimePickerDialog dialog = new TimePickerDialog(context,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								// check if end time is set correct
								if ((startDate.get(Calendar.YEAR) == endDate
										.get(Calendar.YEAR)
										&& startDate.get(Calendar.MONTH) == endDate
												.get(Calendar.MONTH)
										&& startDate.get(Calendar.DAY_OF_MONTH) == endDate
												.get(Calendar.DAY_OF_MONTH) && startDate
										.get(Calendar.HOUR_OF_DAY) > hourOfDay)
										|| (startDate.get(Calendar.YEAR) == endDate
												.get(Calendar.YEAR)
												&& startDate
														.get(Calendar.MONTH) == endDate
														.get(Calendar.MONTH)
												&& startDate
														.get(Calendar.DAY_OF_MONTH) == endDate
														.get(Calendar.DAY_OF_MONTH)
												&& startDate
														.get(Calendar.HOUR_OF_DAY) == hourOfDay && startDate
												.get(Calendar.MINUTE) > minute)) {

									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
									alertDialogBuilder
											.setTitle("Wrong Time")
											.setMessage(
													"End time is before start time!")
											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.cancel();
														}
													}).show();

								} else {
									endDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
									endDate.set(Calendar.MINUTE, minute);
									endTimeBtn.setText(timeFormat
											.format(endDate.getTimeInMillis()));
								}

							}
						}, chour, cminute, true);
				dialog.setTitle("Select end time");
				dialog.show();

			}

		});

		// activity
		ArrayAdapter<String> adapter;
		try {
			adapter = new ArrayAdapter<String>(HARActivityTraining.this,
					android.R.layout.simple_spinner_item,
					APIFunctions.getSupportedActivities());
			activitySpinner = (Spinner) findViewById(R.id.har_train_activity_spinner);
			activitySpinner.setAdapter(adapter);
			activitySpinner
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							activitySpinner.setSelection(position);
							activity = activitySpinner.getSelectedItem()
									.toString();
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub

						}
					});
		} catch (RuntimeException e) {
			Log.e("har",
					"RuntimeException in onCreate: " + e.getLocalizedMessage());
		}
		

		// window length
		millisecondsETx = (EditText) findViewById(R.id.har_train_editText1);
		millisecondsETx.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (Integer.valueOf(millisecondsETx.getText().toString()) > 499
							&& Integer.valueOf(millisecondsETx.getText()
									.toString()) < 1440001) {
						windowLength = Integer.valueOf(millisecondsETx
								.getText().toString());
					} else {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								context);
						alertDialogBuilder
								.setTitle("Training not possible")
								.setMessage(
										"Time window length is not set correctly!\n"
												+ "It must be a number between 500 and 1440000")
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
											}
										}).show();
						millisecondsETx.setText("");
					}
					return true;
				}
				return false;
			}
		});

		// Cancel button
		cancelBtn = (Button) findViewById(R.id.har_train_button6);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HARActivityTraining.this.finish();
			}

		});

		// start button
		startBtn = (Button) findViewById(R.id.har_train_button5);
		startBtn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				// if no time window length is set, set it to 2000
				if (millisecondsETx.getText().toString().equals("")) {
					millisecondsETx.setText(millisecondsETx.getHint());
				}

				// if no activity is set, set it to the shown
				if (activity == (null)) {
					activity = activitySpinner.getSelectedItem().toString();
				}

				boolean noData = false;
				for (String sensor : APIFunctions.getSensors()) {
					try {
						APIFunctions.SENSORS_SENSOR_getRawDataII(
								Integer.valueOf(sensor),
								Utils.dateToUnix(startDate.getTime()),
								Utils.dateToUnix(endDate.getTime()));
						// } catch (DatabaseObjectNotClosedException e) {
						// noData = true;
					} catch (NullPointerException e) {
						Log.e("har",
								"[HARActivityTraining]:[onCreate] random NullPointerException");
					}
					if (!APIFunctions.SENSORS_SENSOR_isEnabled(Integer
							.valueOf(sensor))) {
						noData = true;
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								context);
						alertDialogBuilder
								.setTitle("Sensors are diabled!")
								.setMessage(
										"Enable them in the sensor settings first.")
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// HARActivityTraining.this.finish();
												dialog.cancel();
											}
										}).setCancelable(false).show();
						break;
					}
				}
				// check if there is data in the database at the given time
				if (!live && noData) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							context);
					alertDialogBuilder
							.setTitle("Training not possible")
							.setMessage(
									"No sensordata in the selected time periode!")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									}).show();
				}

				// start training if data is available
				if (!noData) {
					if (live) {
						new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {
								try {
									APIFunctions.train(activity, windowLength);
								} catch (RuntimeException e) {
									Log.e("har", "RuntimeException in onCreate: " + e.getLocalizedMessage());
								}
								return null;
							}
						}.execute();
						Toast.makeText(context, "Live training started",
								Toast.LENGTH_SHORT).show();
					} else {
						new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {
								try {
									APIFunctions.train(activity, windowLength,
											startDate.getTime(), endDate.getTime());
								} catch (RuntimeException e) {
									Log.e("har", "RuntimeException in onCreate: " + e.getLocalizedMessage());
								}
								return null;
							}
						}.execute();
						Toast.makeText(context, "Database training started",
								Toast.LENGTH_SHORT).show();
					}
					HARActivityTraining.this.finish();
				}
			}

		});
	};
}
