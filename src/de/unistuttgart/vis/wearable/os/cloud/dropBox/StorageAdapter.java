package de.unistuttgart.vis.wearable.os.cloud.dropBox;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.unistuttgart.vis.wearable.os.R;

/**
 * Created by marti_000 on 04.03.2015.
 */
public class StorageAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    public StorageAdapter(Activity context,
                      String[] web, Integer[] imageId) {
        super(context, R.layout.custom_list_view_apps, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.custom_list_view_apps, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_app_list_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView_app_list);
        txtTitle.setText(web[position]);
        if (web[position].endsWith(".zip")) {
            imageView.setImageResource(imageId[1]);
        } else {
            imageView.setImageResource(imageId[0]);
        }
        return rowView;
    }
}
