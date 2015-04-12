/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityEnum;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;

public class HARActivityManager extends Activity {

    private Context context = this;
    Button sensorsBtn;
    Button activitiesBtn;
    Button saveBtn;
    Button deleteBtn;

    List<String> sensorsList;
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
        deleteBtn = (Button) findViewById(R.id.har_manager_delete);

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
                createBtn();
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
        PSensor[] pSensors;
        try {
            pSensors = APIFunctions.API_getAllSensors();
        } catch(RuntimeException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder
                    .setTitle("Connection Error")
                    .setMessage("An error occurred while trying to communicate" +
                            " with the internal API. Please try again. If it " +
                            "still won't work an App restart may help.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return;
        }
        sensorsList = new ArrayList<String>();
        for (PSensor pSensor : pSensors) {
            if (pSensor.isEnabled()) {
                sensorsList.add(pSensor.getDisplayedSensorName() + " \nID: "
                        + pSensor.getID() + " (enabled)");
            }
        }
        if(sensorsList.size() < 1) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder
                    .setTitle("Error")
                    .setMessage("No enabled Sensors found. Please enable them first.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return;
        }
        final String[] sensors = Arrays.copyOf(sensorsList.toArray(),
                sensorsList.size(), String[].class);
        sensorsList.clear();
        try {
            sensorsList = APIFunctions.getSensors();
        } catch(RuntimeException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder
                    .setTitle("Connection Error")
                    .setMessage("An error occurred while trying to communicate" +
                            " with the internal API. Please try again. If it " +
                            "still won't work an App restart may help.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return;
        }

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
                                } else {
                                    // Else, if the item is already in
                                    // the array, remove it
                                    checkedSensors[which] = false;
                                }
                            }
                        })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean changed = false;
                        for (int i = 0; i < sensors.length; i++) {
                            try {
                                if (checkedSensors[i]) {
                                    if (!APIFunctions.getSensors().contains(
                                            sensors[i].split(" ")[2])) {
                                        APIFunctions.addSensor(sensors[i]
                                                .split(" ")[2]);
                                        changed = true;
                                    }
                                } else {
                                    APIFunctions.removeSensor((sensors[i]
                                            .split(" ")[2]));
                                }
                            } catch (RuntimeException e) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        context);
                                alertDialogBuilder
                                        .setTitle("Connection Error")
                                        .setMessage("An error occurred while trying to communicate"
                                                + " with the internal API. Please try again. If it "
                                                + "still won't work an App restart may help.")
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                                return;
                            }
                        }
                        if (changed) {
                            makeText("Sensors updated");
                        } else {
                            makeText("Sensors not updated");
                        }
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
        try {
            activitiesList = APIFunctions.getSupportedActivities();
        } catch(RuntimeException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder
                    .setTitle("Connection Error")
                    .setMessage("An error occurred while trying to communicate" +
                            " with the internal API. Please try again. If it " +
                            "still won't work an App restart may help.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return;
        }
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
                        boolean changed = false;
                        for (int i = 0; i < activities.length; i++) {
                            try {
                                if (checkedActivities[i]) {
                                    if (!APIFunctions.getSupportedActivities()
                                            .contains(activities[i])) {
                                        APIFunctions.addActivity(activities[i]);
                                        changed = true;
                                    }
                                } else {
                                    APIFunctions.removeActivity((activities[i]));
                                }
                            } catch (RuntimeException e) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        context);
                                alertDialogBuilder
                                        .setTitle("Connection Error")
                                        .setMessage("An error occurred while trying to communicate"
                                                + " with the internal API. Please try again. If it "
                                                + "still won't work an App restart may help.")
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                                return;
                            }

                        }
                        if (changed) {
                            makeText("Activities updated");
                        } else {
                            makeText("Activities not updated");
                        }
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

    protected void deleteBtn() {
        try {
            APIFunctions.deleteNeuralNetwork();
            makeText("Neural network deleted.");
        } catch (FileNotFoundException e) {
        }
    }

    protected void createBtn() {
        try {
            if(APIFunctions.getSensors().size() == 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder
                        .setTitle("Error")
                        .setMessage("No sensors are selected!")
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return;
            }
            APIFunctions.createNeuralNetwork();
            makeText("Neural network created.");
        } catch (IllegalArgumentException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder
                    .setTitle("Error")
                    .setMessage(e.getLocalizedMessage())
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }

                            }).show();
        } catch(RuntimeException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder
                    .setTitle("Connection Error")
                    .setMessage("An error occurred while trying to communicate" +
                            " with the internal API. Please try again. If it " +
                            "still won't work an App restart may help.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    public void makeText(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
