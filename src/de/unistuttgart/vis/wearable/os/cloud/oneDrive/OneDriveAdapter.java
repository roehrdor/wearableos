package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.unistuttgart.vis.wearable.os.R;
import org.json.JSONObject;

import java.util.ArrayList;

public class OneDriveAdapter extends ArrayAdapter<JSONObject>{
    public OneDriveAdapter(Context context, ArrayList<JSONObject> jsonList){
        super(context, 0, jsonList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject jsonData = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_google_drive_one_drive,parent,false);
        }
        TextView fileNameView = (TextView)convertView.findViewById(R.id.text_view_file_name);
        ImageView iconView = (ImageView)convertView.findViewById(R.id.image_view_file_icon);

        fileNameView.setText(jsonData.optString(Miscellaneous.NAME));

        iconView.setImageDrawable(getContext().getResources().getDrawable(jsonData.optString(Miscellaneous.TYPE).equals("folder")?R.drawable.folder:R.drawable.file));

        return convertView;
    }

}
