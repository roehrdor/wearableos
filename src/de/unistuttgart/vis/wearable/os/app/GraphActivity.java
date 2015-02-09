package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PSensor;

public class GraphActivity extends Activity {

    private LinearLayout chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        chart = (LinearLayout) findViewById(R.id.graphLayout);
        fillChart();
    }

    private void fillChart(){
        int numberOfValues = 100;

        // muss noch auf neue Umgebung angepasst werden
//        PSensor sensor = APIFunctions.getSensorData(getIntent()
//                .getExtras().getString("sensorName"), true, numberOfValues);
//        chart.addView(com.garmentos.garmentOSLib.GraphRenderer.createGraph(
//                sensor, this));
    }
}
