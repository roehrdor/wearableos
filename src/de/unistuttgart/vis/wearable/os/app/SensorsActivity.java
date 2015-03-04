package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;

public class SensorsActivity extends Activity {


    PSensor[] sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensorlist);

        ListView listView = (ListView) findViewById(R.id.listView1);
        sensors = APIFunctions.API_getAllSensors();
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
            View sensorsLayout = view;
            if (sensorsLayout == null) {
                sensorsLayout = getLayoutInflater().inflate(R.layout.custom_list_layout, parent, false);
            }
            TextView txtTitle = (TextView) sensorsLayout.findViewById(R.id.txt);
            TextView subTitle1 = (TextView) sensorsLayout
                    .findViewById(R.id.txt2);
            TextView subTitle2 = (TextView) sensorsLayout
                    .findViewById(R.id.txt3);
            ImageView imageView = (ImageView) sensorsLayout
                    .findViewById(R.id.img);
            txtTitle.setText(sensor.getDisplayedSensorName());

            subTitle1.setText("smoothness: " + (int) (sensor.getSmoothness() * 100));
            int tmp = (int) (sensor.getSavePeriod() / SensorDetailActivity.SAVE_PERIOD_FACTOR);
            subTitle2.setText("power options: " + String.valueOf(tmp));

            mySwitch = (Switch) sensorsLayout.findViewById(R.id.switch3);
            mySwitch.setChecked(sensor.isEnabled());
            mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    sensor.setEnabled(isChecked);
                }
            });

            imageView.setImageResource(sensor.getSensorType().getIconID());
            return sensorsLayout;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.onCreate(null);
    }

    public void listViewOptions(ListView listView) {
        @SuppressWarnings("rawtypes")
        ArrayAdapter adapter = new SensorListAdapter(this, sensors);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                openSensor(position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!sensors[position].isInternalSensor()) {
                    showDialogDelete(position);
                }
                return true;
            }
        });


    }

    public void showDialogDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SensorsActivity.this);

        builder.setMessage("Wollen sie diesen Sensor löschen?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Sobald die Funktion deleteSensor existiert kann dieser Code einkommentiert werden

                //APIFunctions.deleteSensor(sensors[position].getID());
                //onCreate(null);
                dialog.dismiss();
                onResume();
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

    public void openSensor(int position) {

        if (sensors[position].isInternalSensor()) {
            Intent intent = new Intent(this, SensorDetailActivity.class);
            intent.putExtra("sensorId", sensors[position].getID());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SensorDetailBluetoothActivity.class);
            intent.putExtra("sensorId", sensors[position].getID());
            startActivity(intent);
        }
    }

    public void addSensor(View view) {
        Intent intent = new Intent(this, AddSensorActivity.class);
        startActivity(intent);
    }


}