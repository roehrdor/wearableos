package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.internalapi.PUserApp;

public class PrivacyActivity extends Activity {
    PUserApp[] apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        apps = APIFunctions.API_getRegisteredUserApplications();
        ListView listView = (ListView) findViewById(R.id.listView_apps);
        ArrayAdapter adapter = new AppListAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(parent.getContext(), AppDetailActivity.class);
                intent.putExtra("PUserApp", apps[position]);
                startActivity(intent);
            }
        });
    }

    private class AppListAdapter extends ArrayAdapter<PUserApp> {

        public AppListAdapter() {
            super(PrivacyActivity.this, R.layout.custom_list_view_apps, apps);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            View itemView = view;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.custom_list_view_apps, parent, false);
            }

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView_app_list);
            TextView textView = (TextView) itemView.findViewById(R.id.textView_app_list_name);
            try {
                imageView.setImageDrawable(getPackageManager().getApplicationIcon(apps[position].getName()));
            } catch (PackageManager.NameNotFoundException e) {
                imageView.setImageResource(R.drawable.ic_launcher);
            }

            //
            // Try to get the name of the application from the Android Package Manager
            //
            try {
                // if we can access the name using the Package Manager set it this way
                textView.setText(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(apps[position].getName(), 0)));
            } catch (PackageManager.NameNotFoundException nameNotFoundException) {
                // Otherwise set the package name of the application
                textView.setText(apps[position].getName());
            }

            return itemView;
        }
    }
}
