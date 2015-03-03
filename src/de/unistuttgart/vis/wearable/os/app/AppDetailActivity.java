package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.internalapi.PUserApp;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

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

        ArrayAdapter adapter = new AppListAdapter();
        listView.setAdapter(adapter);
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
            fillSpinner(sensorTypes[position], itemView);

            imageView.setImageResource(sensorTypes[position].getIconID());
            textView.setText(sensorTypes[position].toString());

            return itemView;
        }
    }

    private void fillSpinner(final SensorType sensorType, View itemView) {
        spinner = (Spinner) itemView.findViewById(R.id.spinner_app_list_sensor_detail);
        pSensors = APIFunctions.getAllSensors(sensorType);

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

    @Override
    public void onBackPressed(){
        //App aktualisieren!

    }
}