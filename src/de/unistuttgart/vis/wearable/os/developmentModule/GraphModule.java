package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;

//import com.garmentos.garmentOSLib.GraphData;
//import com.garmentos.garmentOSLib.GraphType;
//
//import org.achartengine.ChartFactory;

import de.unistuttgart.vis.wearable.os.graph.GraphType;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
//import android.view.Gravity;

import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.GridLayout.LayoutParams;

public class GraphModule extends PopupModule2 {

	public GraphModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		// TextView content = new TextView(context);
		// content.setText("100 Steps today...");

//		LinearLayout chart = new LinearLayout(context);
//
//		 Date[] x = new Date[100];
//		 double[] y = new double[100];
//		 double[] z = new double[100];
//		 for (int i = 0; i != 100; ++i) {
//		 x[i] = new Date(i);
//		 y[i] = (float) Math.sin((float) i * (float) (Math.random() + 1));
//		 }
//		 for (int i = 0; i != 100; ++i) {
//		 z[i] = i * i * 0.0005f;
//		 }
//		
//		 com.garmentos.garmentOSLib.GraphData gd = new GraphData("sample", x,
//		 y);
//		 com.garmentos.garmentOSLib.GraphData gd2 = new GraphData("sample", x,
//		 z);
//
//		 View view = GraphRenderer.createView(
//		 getContext(), "Samples", GraphType.LINE, gd, gd2);
//		
//		 chart.addView(view);
//		
//		 LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
//		 view
//		 .getLayoutParams();
//		 layoutParams.width = 250;
//		 layoutParams.height = 170;
//
//		 super.createLayout(context, content, R.drawable.statistics,
//		 "Graphs");

//		super.createLayout(context, chart, R.drawable.graph, "Graphs");
	}

}
