package de.unistuttgart.vis.wearable.os.developmentModule;

import android.content.Context;
import android.view.View;

/**
 * @author Sophie Ogando
 */
public abstract class PopupWindow {

	protected abstract View getPopupContent(final Context context,
			final android.widget.PopupWindow pWindow);
}
