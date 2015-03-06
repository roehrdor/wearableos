package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.drive.Metadata;
import de.unistuttgart.vis.wearable.os.R;

import java.util.ArrayList;

public class GoogleDriveAdapter extends ArrayAdapter<Metadata>{

    public GoogleDriveAdapter(Context context, ArrayList<Metadata> metadataList){
        super(context, 0, metadataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       Metadata mData = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_google_drive_one_drive,parent,false);
        }
        TextView fileNameView = (TextView)convertView.findViewById(R.id.text_view_file_name);
        ImageView iconView = (ImageView)convertView.findViewById(R.id.image_view_file_icon);

        fileNameView.setText(mData.getTitle());

        iconView.setImageDrawable(getContext().getResources().getDrawable(mData.isFolder()?R.drawable.folder:R.drawable.file));

        return convertView;
    }

}
