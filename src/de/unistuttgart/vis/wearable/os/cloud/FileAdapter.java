package de.unistuttgart.vis.wearable.os.cloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.unistuttgart.vis.wearable.os.R;
import java.io.File;
import java.util.ArrayList;

public class FileAdapter extends ArrayAdapter<File>{
    public FileAdapter(Context context, ArrayList<File> metadataList){
        super(context, 0, metadataList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        File mData = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_google_drive_one_drive,parent,false);
        }
        TextView fileNameView = (TextView)convertView.findViewById(R.id.text_view_file_name);
        ImageView iconView = (ImageView)convertView.findViewById(R.id.image_view_file_icon);
        fileNameView.setText(mData.getName());
        iconView.setImageDrawable(getContext().getResources().getDrawable(!mData.isFile()?R.drawable.folder:R.drawable.file));

        return convertView;
    }

}
