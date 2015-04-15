package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementSystems;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

public class SensorDetailBluetoothActivity extends Activity {

    private PSensor sensor;
    private SeekBar seekBarSmoothness;
    private SeekBar seekBarPowerOptions;
    private Spinner spinner;
    private Spinner spinner2;
    private Spinner spinner3;
    private Spinner spinner4;
    private Switch mySwitch;
    SensorType[] sensorTypes;
    MeasurementSystems[] measurementSystems;
    TextView textView;
    private int seekBarSmoothnessMax = 98; // between 0 and 1
    private int seekBarPowerOptionsMax = 99; // bigger than 0
    private float smoothness = 0;
    private int powerOption = 0;
    private MeasurementSystems measurementSystem = MeasurementSystems.TEMPERATURE;
    private SensorType sensorType = SensorType.TEMPERATURE;
    private int sensorId;
    public static final int SAVE_PERIOD_FACTOR = 125;
    public static final double SAMPLE_RATE_FACTOR = 1.2;
    private boolean enabled;
    private String sensorDriver;
    private BluetoothDevice btDevice;
    private String btMac;
    private BluetoothAdapter myBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail_bluetooth);
        mySwitch = (Switch) findViewById(R.id.sensorDetail_switch);
        buildSensorProperties();
        buildSeekBars();
        fillSpinner();
        setViews();
    }

    public void showGraph(View view) {
        Intent intent = new Intent(getBaseContext(), GraphActivity.class);
        intent.putExtra("sensorId", getIntent().getIntExtra("sensorId", -1));
        startActivity(intent);
    }

    private void buildSensorProperties() {
        Intent intent = getIntent();

        sensorId = intent.getIntExtra("sensorId", -1);
        sensor = APIFunctions.API_getSensorById(sensorId);
        Log.d("gosDEBUG",
                "SensorDetailActivity:buildSensorProperties() - sensor "
                        + sensor);
        if (sensor != null) {
            smoothness = sensor.getSmoothness();
            powerOption = (int) (sensor.getSampleRate() / SAMPLE_RATE_FACTOR);
            enabled = sensor.isEnabled();
            mySwitch.setChecked(enabled);
            mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    enabled = isChecked;

                }
            });
            if (powerOption == 0) {
                powerOption = 1; // min value
            }
            measurementSystem = sensor.getDisplayedMeasurementSystem();
            sensorType = sensor.getSensorType();
        }

    }

    private void buildSeekBars() {
        seekBarSmoothness = (SeekBar) findViewById(R.id.sensorDetail_seekBar_Smoothness);
        seekBarPowerOptions = (SeekBar) findViewById(R.id.sensorDetail_seekBar_PowerOptions);
        seekBarSmoothness.setMax(seekBarSmoothnessMax);
        seekBarPowerOptions.setMax(seekBarPowerOptionsMax);
        seekBarSmoothness
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        // smoothness is between 0 and 1
                        smoothness = (float) (seekBarSmoothness.getProgress() + 1) / 100;
                    }
                });
        seekBarPowerOptions
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        // powerOption > 0
                        powerOption = seekBarPowerOptions.getProgress() + 1;

                    }
                });

    }

    private void setViews() {

        textView = (TextView) findViewById(R.id.sensorDetail_textView_title);
        textView.setText(sensor.getDisplayedSensorName());

        seekBarSmoothness.setProgress((int) (smoothness * 100 - 1));
        seekBarPowerOptions.setProgress(powerOption - 1);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void fillSpinner() {
        sensorTypes = SensorType.values();
        spinner = (Spinner) findViewById(R.id.sensorDetail_spinner_SensorType);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sensorTypes);
        spinner.setAdapter(adapter);
        spinner.setSelection(getPosition(sensorTypes, sensorType));
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sensorType = sensorTypes[position];
                measurementSystems = sensorType.getMeasurementSystems();
                spinner2 = (Spinner) findViewById(R.id.sensorDetail_spinner_MeasurmentSystem);
                ArrayAdapter adapter2 = new ArrayAdapter(parent.getContext(),
                        android.R.layout.simple_spinner_item,
                        measurementSystems);
                spinner2.setAdapter(adapter2);
                spinner2.setSelection(getPosition(measurementSystems,
                        measurementSystem));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });


        measurementSystems = sensorType.getMeasurementSystems();
        spinner2 = (Spinner) findViewById(R.id.sensorDetail_spinner_MeasurmentSystem);
        ArrayAdapter adapter3 = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, measurementSystems);
        spinner2.setAdapter(adapter3);
        spinner2.setSelection(getPosition(measurementSystems, measurementSystem));

        spinner3 = (Spinner) findViewById(R.id.sensorAdd_spinner_Bluetooth);
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, 1);
        }

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        ArrayAdapter<BluetoothDevice> BTArrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_spinner_item);
        for (BluetoothDevice device : pairedDevices) {
            BTArrayAdapter.add(device);
        }
        spinner3.setAdapter(BTArrayAdapter);


        spinner3.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                btDevice = (BluetoothDevice) spinner3.getSelectedItem();
                btMac = btDevice.getAddress();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        spinner4 = (Spinner) findViewById(R.id.sensorAdd_spinner_SensorDriver);
        ArrayAdapter adapter1 = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        adapter1.add("LightDriver");
        adapter1.add("StepDriver");

        spinner4.setAdapter(adapter1);
        spinner4.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sensorDriver = (String) spinner4.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });
    }

    public void refreshBluetoothSpinner(View view) {
        loadBTDevices();
    }

    private void loadBTDevices() {
        spinner3 = (Spinner) findViewById(R.id.sensorAdd_spinner_Bluetooth);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, 1);
        }

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        ArrayAdapter<BluetoothDevice> BTArrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_spinner_item);
        for (BluetoothDevice device : pairedDevices) {
            BTArrayAdapter.add(device);
        }
        spinner3.setAdapter(BTArrayAdapter);
    }


    public int getPosition(SensorType[] sensorTypes, SensorType sensorType) {
        int i = 0;
        for (SensorType sT : sensorTypes) {
            if (sT == sensorType) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public int getPosition(MeasurementSystems[] measurementSystems,
                           MeasurementSystems measurementSystem) {
        int i = 0;
        for (MeasurementSystems mS : measurementSystems) {
            if (mS == measurementSystem) {
                return i;
            }
            i++;
        }
        return 0;
    }

    @Override
    public void onBackPressed() {

        if (!(smoothness > 0) || !(powerOption > 0)) {
            Toast.makeText(getBaseContext(),
                    "Please check your SensorProperties. No null values allowed.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        sensor.setEnabled(enabled);
        sensor.setSavePeriod(powerOption * SAVE_PERIOD_FACTOR);
        sensor.setSampleRate((int) (powerOption * SAMPLE_RATE_FACTOR));
        sensor.setSmoothness(smoothness);
        sensor.setDisplayedMeasurementSystem(measurementSystems[spinner2.getSelectedItemPosition()]);
        sensor.setSensorType(sensorTypes[spinner.getSelectedItemPosition()]);
        sensor.setDisplayedSensorName(textView.getText().toString());

        this.finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // open GraphActivity
            showGraph(null);
        }
    }

}

