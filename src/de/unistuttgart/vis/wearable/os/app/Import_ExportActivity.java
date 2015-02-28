package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Martin on 26.02.2015.
 */
public class Import_ExportActivity extends Activity{

        @Override
        public void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
        }

        public void startExportActivity(View view) {
                Intent intent = new Intent(getBaseContext(), StorageActivity.class);
                intent.putExtra("isExport", true);
                startActivity(intent);
        }

        public void startImportActivity(View view) {
                Intent intent = new Intent(getBaseContext(), StorageActivity.class);
                intent.putExtra("isExport", false);
                startActivity(intent);
        }
}