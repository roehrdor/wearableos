package de.unistuttgart.vis.wearable.os.developmentModule;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

public abstract class PopupModule2 extends BasisModule {
	public PopupModule2(Context context) {
		super(context);
	}

	public PopupModule2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PopupModule2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected View getPopupContent(final Context context,
			final PopupWindow pWindow) {

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(VERTICAL);

		LinearLayout downLayout = new LinearLayout(context);
		downLayout.setOrientation(HORIZONTAL);
		downLayout.setGravity(Gravity.BOTTOM);

		LinearLayout upperLayout = new RadioGroup(context);
		upperLayout.setOrientation(VERTICAL);
		upperLayout.setGravity(Gravity.TOP);

		TextView textview = new TextView(context);
		textview.setText("Choose Sensor:");
		textview.setPadding(0, 0, 0, 200);

		textview.setTextColor(Color.WHITE);
		textview.setTextSize(22);

		upperLayout.addView(textview);

		layout.addView(upperLayout);
		layout.addView(downLayout);

		Button cancelButton = new Button(context);
		cancelButton.setText("cancel");
		downLayout.addView(cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				pWindow.dismiss();
			}
		});

		Button saveButton = new Button(context);
		saveButton.setText("save");
		downLayout.addView(saveButton);
		return layout;
	}
}