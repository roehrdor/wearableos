package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass.Device;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Array;
import java.util.Set;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.bluetoothservice.GarmentOSBluetoothService;
import de.unistuttgart.vis.wearable.os.handle.APIHandle;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementSystems;
import de.unistuttgart.vis.wearable.os.sensors.MeasurementUnits;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
import de.unistuttgart.vis.wearable.os.sensors.SensorType;

public class AddSensorActivity extends Activity {

    private SeekBar seekBarSmoothness;
    private SeekBar seekBarPowerOptions;
    private Spinner spinner;
    private Spinner spinner2;
    private Spinner spinner3;
    private BluetoothDevice btDevice;
    private String btMac;
    SensorType[] sensorTypes;
    MeasurementSystems[] measurementSystems;
    BluetoothDevice[] bluetoothDevices;
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
    private Boolean newSensor = false;
    private Spinner spinner4;
    private String sensorDriver;


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

    private void setViews() {

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
        // niy
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

    // Hier muss noch herausgefunden werden wie man zwei BluetoothDevices miteinander vergleicht

//    public int getPosition(BluetoothDevice[] bluetoothDevices, BluetoothDevice bluetoothDevice) {
//        int i = 0;
//        for (bluetoothDevices bd : bluetoothDevices) {
//            if (bd == bluetoothDevice) {
//                return i;
//            }
//            i++;
//        }
//        return 0;
//    }

    @Override
    public void onBackPressed() {
        if (textView.getText().toString().equalsIgnoreCase("New Sensor") || textView.getText().toString().isEmpty()) {
            Toast.makeText(getBaseContext(), "Please change your Sensor Name!",
                    Toast.LENGTH_SHORT).show();
        }  else {
            de.unistuttgart.vis.wearable.os.internalapi.APIFunctions.addNewSensor((int) (powerOption * SAMPLE_RATE_FAKTOR), powerOption * SAVE_PERIOD_FAKTOR, (int) smoothness, textView.getText().toString(), sensorTypes[spinner.getSelectedItemPosition()], btMac, measurementSystems[spinner2.getSelectedItemPosition()], MeasurementUnits.NONE, MeasurementSystems.LUX, MeasurementUnits.NONE);
            Intent startBT = new Intent(this, GarmentOSBluetoothService.class);
            startBT.putExtra("btDevice", btMac);
            startBT.putExtra("btId", textView.getText().toString());
            startBT.putExtra("btDriver", sensorDriver);
            startService(startBT);
            Toast.makeText(getBaseContext(), "Sensor saved and BluetoothService started", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    public void goBack(View view) {
        this.finish();
    }

}
