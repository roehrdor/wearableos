package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CyclingModule extends PopupModuleDate {

	public CyclingModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		TextView content = new TextView(context);
		content.setText("100 Steps today...");

		super.createLayout(context, content, R.drawable.bike, "Cycling");
	}

}
