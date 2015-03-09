package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

import android.util.Log;
import org.json.JSONObject;

import java.util.Comparator;

public class JSONComparator implements Comparator<JSONObject>{
    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {

        if(lhs.optString(Miscellaneous.TYPE).equals("folder")){
            if(rhs.optString(Miscellaneous.TYPE).equals("folder")){
                return lhs.optString(Miscellaneous.NAME).toLowerCase().compareTo(rhs.optString(Miscellaneous.NAME).toLowerCase());
                }
            else if(rhs.optString(Miscellaneous.TYPE).equals("file")) {
                return 1;
            }
            else{
                return 0;
            }
        }
        else if(lhs.optString(Miscellaneous.TYPE).equals("file")){
            if(rhs.optString(Miscellaneous.TYPE).equals("folder")){
                return -1;
            }
            else if(rhs.optString(Miscellaneous.TYPE).equals("file")) {
                return lhs.optString(Miscellaneous.NAME).toLowerCase().compareTo(rhs.optString(Miscellaneous.NAME).toLowerCase());
            }
            else return 0;
        }
        else {
            return 0;
        }
    }
}