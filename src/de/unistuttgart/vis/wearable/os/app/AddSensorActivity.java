package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementSystems;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

public class AddSensorActivity extends Activity {

    private SeekBar seekBarSmoothness;
    private SeekBar seekBarPowerOptions;
    private Spinner spinner;
    private Spinner spinner2;
    private Spinner spinner3;
    SensorType[] sensorTypes;
    MeasurementSystems[] measurementSystems;
    // ToDo from Manu Lorenz
    // BluetoothDevice[] bluetoothDevices;
    // BluetoothDevice[] bluetoothDevice
    TextView textView;
    private int seekBarSmoothnessMax = 98; // between 0 and 1
    private int seekBarPowerOptionsMax = 99; // bigger than 0
    private float smoothness = 1;
    private int powerOption = 1;
    private MeasurementSystems measurementSystem = MeasurementSystems.TEMPERATURE;
    private SensorType sensorType = SensorType.TEMPERATURE;
    private String sensorName = "New Sensor";
    public static final int SAVE_PERIOD_FAKTOR = 125;
    public static final double SAMPLE_RATE_FAKTOR = 1.2;
    Boolean newSensor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);

        buildSeekBars();
        fillSpinner();
        setViews();
    }

    private void buildSeekBars() {
        seekBarSmoothness = (SeekBar) findViewById(R.id.sensorAdd_seekBar_Smoothness);
        seekBarPowerOptions = (SeekBar) findViewById(R.id.sensorAdd_seekBar_PowerOptions);
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
                        ;
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

    private void setViews(){

        textView = (TextView) findViewById(R.id.sensorAdd_textView_title);
        textView.setText(sensorName);

        seekBarSmoothness.setProgress(1);
        seekBarPowerOptions.setProgress(1);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void fillSpinner() {
        sensorTypes = SensorType.values();
        spinner = (Spinner) findViewById(R.id.sensorAdd_spinner_SensorType);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, sensorTypes);
        spinner.setAdapter(adapter);
        spinner.setSelection(getPosition(sensorTypes, sensorType));
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sensorType = sensorTypes[position];
                measurementSystems = sensorType.getMeasurementSystems();
                spinner2 = (Spinner) findViewById(R.id.sensorAdd_spinner_MeasurmentSystem);
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
        spinner2 = (Spinner) findViewById(R.id.sensorAdd_spinner_MeasurmentSystem);
        ArrayAdapter adapter3 = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, measurementSystems);
        spinner2.setAdapter(adapter3);
        spinner2.setSelection(getPosition(measurementSystems, measurementSystem));

        // ToDo fill Bluetooth spinner with all available Bluetooth Devices
        // ToDo from Manu Lorenz

        spinner3 = (Spinner) findViewById(R.id.sensorAdd_spinner_Bluetooth);
        // bluetoothDevices = ;
        // ArrayAdapter adapter4 = new ArrayAdapter(this,
        // android.R.layout.simple_spinner_item, measurementSystems);
        // spinner3.setAdapter(adapter4);
    }

    public void refreshBluetoothSpinner() {
        // bluetoothDevice = spinner3.getSelectedItem();
        // bluetoothDevices = ;
        // ArrayAdapter adapter5 = new ArrayAdapter(this,
        // android.R.layout.simple_spinner_item, measurementSystems);
        // spinner3.setAdapter(adapter4);
        // spinner3.setSelection(getPosition(bluetoothDevices,
        // bluetoothDevice));
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

    // public int getPosition(bluetoothDevices[] bluetoothDevices,
    // bluetoothDevice bluetoothDevice) {
    // int i = 0;
    // for (bluetoothDevices mS : bluetoothDevices) {
    // if (mS == bluetoothDevice) {
    // return i;
    // }
    // i++;
    // }
    // return 0;
    // }

    @Override
    public void onBackPressed() {
//        if (textView.getText().toString().equalsIgnoreCase("New Sensor")
//                || textView.getText().toString().isEmpty()) {
//            Toast.makeText(getBaseContext(), "Please change your Sensor Name!",
//                    Toast.LENGTH_SHORT).show();
//        } else if (APIFunctions.newSensor(
//                MeasurementUnits.NONE,
//                measurementSystems[spinner2.getSelectedItemPosition()],
//                (int) (powerOption * SAMPLE_RATE_FAKTOR), powerOption
//                        * SAVE_PERIOD_FAKTOR, textView.getText().toString(),
//                sensorTypes[spinner.getSelectedItemPosition()], smoothness)) {
//            this.finish();
//        } else {
//
//            Toast.makeText(getBaseContext(),
//                    "Please check your sensor properties!", Toast.LENGTH_SHORT)
//                    .show();
//        }
        this.finish();
    }

    public void goBack(View view) {
        this.finish();
    }
}
