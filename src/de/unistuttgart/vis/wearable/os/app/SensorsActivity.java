package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;

public class SensorsActivity extends Activity {



    PSensor[] sensors;
//    private String[] sensorNames;
//    private Float[] smoothness;
//    private Integer[] powerOption;

//    Integer[] imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensorlist);

        ListView listView = (ListView)findViewById(R.id.listView1);
        sensors = APIFunctions.API_getAllSensors();
//        int numberOfSensors = sensors.length;

//        if(sensors != null) {
//
//            this.sensorNames = new String[numberOfSensors];
//            this.smoothness = new Float[numberOfSensors];
//            this.powerOption = new Integer[numberOfSensors];
//            this.imageId = new Integer[numberOfSensors];
//
//            int count = 0;
//            for(PSensor sensor : sensors) {
//                this.sensorNames[count] = sensor.getDisplayedSensorName();
//                this.smoothness[count] = sensor.getSmoothness();
//                this.imageId[count] = sensor.getSensorType().getIconID();
//                int powerOption = (int)(sensor.getSampleRate() / SensorDetailActivity.SAMPLE_RATE_FACTOR);
//                powerOption = powerOption == 0 ? 1 : powerOption;
//                this.powerOption[count++] = powerOption;
//            }
//        }

        listViewOptions(listView);
    }

    private class SensorListAdapter extends ArrayAdapter<PSensor> {

        private final Activity context;
        private final PSensor[] sensors;
        private Switch mySwitch;

        public SensorListAdapter(Activity context, PSensor[] sensors) {
            super(context, R.layout.custom_list_layout, sensors);
            this.context = context;
            this.sensors = sensors;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final PSensor sensor = sensors[position];
            LayoutInflater inflater = context.getLayoutInflater();
            LinearLayout sensorsLayout = (LinearLayout) inflater.inflate(
                    R.layout.custom_list_layout, parent, false);
            TextView txtTitle = (TextView) sensorsLayout.findViewById(R.id.txt);
            TextView subTitle1 = (TextView) sensorsLayout
                    .findViewById(R.id.txt2);
            TextView subTitle2 = (TextView) sensorsLayout
                    .findViewById(R.id.txt3);
            ImageView imageView = (ImageView) sensorsLayout
                    .findViewById(R.id.img);
            txtTitle.setText(sensor.getDisplayedSensorName());

            subTitle1.setText("smoothness: " + (int)(sensor.getSmoothness() * 100));
            int tmp = (int)(sensor.getSavePeriod() / SensorDetailActivity.SAVE_PERIOD_FACTOR);
            subTitle2.setText("power options: " + String.valueOf(tmp));

            mySwitch = (Switch) sensorsLayout.findViewById(R.id.switch3);

            if(sensors != null) {
                mySwitch.setChecked(sensor.isEnabled());
                mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        sensor.setEnabled(isChecked);
                    }
                });
            }

            imageView.setImageResource(sensor.getSensorType().getIconID());

            // Workaround for on item click listener working with switch object
            sensorsLayout
                    .setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            return sensorsLayout;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.onCreate(null);
    }

    public void listViewOptions(ListView listView) {
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                openSensor(position);
            }
        });

        @SuppressWarnings("rawtypes")
        ArrayAdapter adapter = new SensorListAdapter(this, sensors);
        listView.setAdapter(adapter);
    }

    public void openSensor(int position) {
        Intent intent = new Intent(this, SensorDetailActivity.class);
        intent.putExtra("sensorId", sensors[position].getID());
        intent.putExtra("newSensor", false);
        startActivity(intent);
    }

    public void addSensor(View view) {
        Intent intent = new Intent(this, AddSensorActivity.class);
        startActivity(intent);
    }


}