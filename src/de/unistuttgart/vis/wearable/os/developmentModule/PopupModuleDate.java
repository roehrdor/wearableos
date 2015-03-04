package de.unistuttgart.vis.wearable.os.developmentModule;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

public abstract class PopupModuleDate extends BasisModule {
	public PopupModuleDate(Context context) {
		super(context);
	}

	public PopupModuleDate(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PopupModuleDate(Context context, AttributeSet attrs) {
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
		textview.setText("Select Date:");
		textview.setPadding(0, 0, 0, 20);

		textview.setTextColor(Color.WHITE);
		textview.setTextSize(22);

		layout.addView(upperLayout);
		layout.addView(downLayout);

		upperLayout.addView(textview);
		
		
		DatePicker datePicker = new DatePicker(context);
		datePicker.setPadding(10, 10, 10, 10);
		datePicker.setScaleX((float) 0.8);
		datePicker.setScaleY((float) 0.8);
		
		TimePicker timePicker = new TimePicker(context);
		datePicker.setPadding(10, 10, 10, 10);
		datePicker.setScaleX((float) 0.8);
		datePicker.setScaleY((float) 0.8);
		
		
		
		upperLayout.addView(datePicker);
		upperLayout.addView(timePicker);

//		RadioButton radioButtonToday = new RadioButton(context);
//
//		radioButtonToday.setText("Today");
//
//		radioButtonToday.setTextColor(Color.WHITE);
//
//		upperLayout.addView(radioButtonToday);
//
//		RadioButton radioButtonWeek = new RadioButton(context);
//
//		radioButtonWeek.setText("Last Week");
//
//		radioButtonWeek.setTextColor(Color.WHITE);
//
//		upperLayout.addView(radioButtonWeek);
//
//		RadioButton radioButtonMonth = new RadioButton(context);
//
//		radioButtonMonth.setText("LastMonth");
//
//		radioButtonMonth.setTextColor(Color.WHITE);
//
//		upperLayout.addView(radioButtonMonth);

//		upperLayout.setPadding(0, 0, 0, 25);

//		Button cancelButton = new Button(context);
//		cancelButton.setText("cancel");
//
//		downLayout.addView(cancelButton);
//
//		cancelButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(final View view) {
//				pWindow.dismiss();
//
//			}
//		});
//
//		Button saveButton = new Button(context);
//		saveButton.setText("save");
//
//		downLayout.addView(saveButton);

		return layout;
	}
}