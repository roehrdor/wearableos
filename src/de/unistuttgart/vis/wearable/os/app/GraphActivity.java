package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.graph.AbstractLiveGraph;

public class GraphActivity extends Activity {

	protected LinearLayout chart;
    PSensor sensor;
    IGarmentCallback igcb;
    LiveGraph liveGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		chart = (LinearLayout) findViewById(R.id.graphLayout);

        sensor = APIFunctions.getSensorById(getIntent().getExtras()
                .getInt("sensorId"));
        liveGraph = new LiveGraph(chart, sensor, 300, GraphActivity.this, 10);
	}

    class LiveGraph extends AbstractLiveGraph {
//        LinearLayout chart;
        protected LiveGraph (LinearLayout chart, PSensor pSensor, int numberOfValuesToShow,
                        Context context, int graphsPerSecond) {
            super(pSensor, numberOfValuesToShow, context, graphsPerSecond);
//            this.chart = chart;
        }

        @Override
        public void acceptGraph(View graph) {
            if (chart != null) {
                chart.removeAllViews();
                chart.addView(graph);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        liveGraph.unRegisterCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        liveGraph.reregisterCallback();
    }
}