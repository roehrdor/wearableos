package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.graph.GraphRenderer;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;

import de.unistuttgart.vis.wearable.os.api.PSensor;

public class GraphActivity extends Activity {

	private LinearLayout chart;
	private Thread chartUpdateThread;
    private final int NUMBER_OF_VALUES = 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		chart = (LinearLayout) findViewById(R.id.graphLayout);
		fillChart();
	}

	private void fillChart() {

		PSensor sensor = APIFunctions.API_getSensorById(getIntent().getExtras()
				.getInt("sensorId"));

		GraphRenderer.ChartThreadTuple tuple = GraphRenderer.createGraph(
				sensor, this, NUMBER_OF_VALUES);
		chart.addView(tuple.getChart());
        //TODO callback
		chartUpdateThread = tuple.getThread();
		chartUpdateThread.start();
	}

	@Override
	protected void onDestroy() {
        //TODO wird nicht ausgelöst/ bzw callback deregistrieren
		if (chartUpdateThread != null)
			chartUpdateThread.interrupt();

		super.onDestroy();
	}
}
