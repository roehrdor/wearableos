package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class DigitalViewModule extends PopupModule2 {

	public DigitalViewModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		content = new TextView(context);
		super.createLayout(context, content, R.drawable.graph,
				"Digital View");
	}
}
