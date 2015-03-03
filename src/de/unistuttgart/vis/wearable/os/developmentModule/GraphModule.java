package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.graph.GraphRenderer;
import de.unistuttgart.vis.wearable.os.graph.GraphType;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class GraphModule extends PopupModule2 {

	private LinearLayout chart;
	private Thread chartUpdateThread;

	public GraphModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		chart = new LinearLayout(context);
//		fillChart();

		super.createLayout(context, chart, R.drawable.graph, "Graphs");
	}

	private void fillChart() {

	

//		GraphRenderer.ChartThreadTuple tuple = GraphRenderer.createGraph(
//				 null, getContext());
//		chart.addView(tuple.getChart());
//		chartUpdateThread = tuple.getThread();
//		chartUpdateThread.start();
	}

}
