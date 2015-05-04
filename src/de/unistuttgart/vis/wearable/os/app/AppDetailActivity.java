package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.internalapi.PUserApp;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * Created by Lucas on 01.03.2015.
 */
public class AppDetailActivity extends Activity {
    private SensorType[] sensorTypes;
    private Spinner spinner;
    private PSensor[] pSensors;
    private PUserApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        app = getIntent().getParcelableExtra("PUserApp");
        sensorTypes = APIFunctions.getAvailableSensorTypes();
        ListView listView = (ListView) findViewById(R.id.listView_app_detail);
        listViewOptions(listView);
    }

    private class AppListAdapter extends ArrayAdapter<SensorType> {

        public AppListAdapter() {
            super(AppDetailActivity.this, R.layout.custom_list_view_apps_detail, sensorTypes);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            View itemView = view;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.custom_list_view_apps_detail, parent, false);
            }

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView_app_list_detail);
            TextView textView = (TextView) itemView.findViewById(R.id.textView_app_list_name_detail);
            spinner = (Spinner) itemView.findViewById(R.id.spinner_app_list_sensor_detail);
            pSensors = APIFunctions.getAllSensors(sensorTypes[position]);
            fillSpinner(sensorTypes[position]);

            //Wählt den aktuell in PUserApp gespeicherten Sensor im Spinner aus
            int i = 0;
            if(app.getDefaultSensor(sensorTypes[position]) != null) {
                    if (!app.sensorTypeGranted(Utils.permissionFlagFromSensorType(sensorTypes[position]))) {
                        itemView.setBackgroundColor(Color.parseColor("#86959f"));
                    }else{
                        itemView.setBackgroundColor(Color.parseColor("#c0d6e4"));
                    }

                for(PSensor pSensor : pSensors) {
                    if(pSensor.equals(app.getDefaultSensor(sensorTypes[position]))) {
                        spinner.setSelection(i);
                    }
                    i++;
                }
            }


            imageView.setImageResource(sensorTypes[position].getIconID());
            textView.setText(sensorTypes[position].toString());

            return itemView;
        }
    }

    private void fillSpinner(final SensorType sensorType) {
        ArrayAdapter adapter = new ArrayAdapter(AppDetailActivity.this, android.R.layout.simple_spinner_item, pSensors);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                app.setDefaultSensor(sensorType, pSensors[position].getID());
                spinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }



    public void listViewOptions(ListView listView) {
        @SuppressWarnings("rawtypes")
        ArrayAdapter adapter = new AppListAdapter();
        listView.setAdapter(adapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                showDialogDelete(position, view);
                return true;
            }
        });


    }

    public void showDialogDelete(final int position, final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AppDetailActivity.this);

        if(app.sensorTypeGranted(Utils.permissionFlagFromSensorType(sensorTypes[position]))) {
            builder.setMessage("Would you like to disable this sensor?");
        }else{
            builder.setMessage("Would you like to enable this sensor?");
        }

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ImageView imageView = (ImageView) view.findViewById(R.id.imageView_app_list_detail);
                if(app.sensorTypeGranted(Utils.permissionFlagFromSensorType(sensorTypes[position]))) {
                    app.denySensorType(Utils.permissionFlagFromSensorType(sensorTypes[position]));
                    view.setBackgroundColor(Color.parseColor("#86959f"));
                    Toast.makeText(getBaseContext(), "Sensor access denied", Toast.LENGTH_LONG).show();
                }else{
                    app.allowSensorType(Utils.permissionFlagFromSensorType(sensorTypes[position]));
                    view.setBackgroundColor(Color.parseColor("#c0d6e4"));
                    Toast.makeText(getBaseContext(), "Sensor access granted", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}