package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

import android.util.Log;
import org.json.JSONObject;

import java.util.Comparator;

public class JSONComparator implements Comparator<JSONObject>{
    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {
        if(lhs.optString(Miscellaneous.TYPE).equals("folder")&&!rhs.optString(Miscellaneous.TYPE).equals("folder")){
            Log.d("gosDEBUG", "Branch :"+1);
            return 1;
        }
        else if(!lhs.optString(Miscellaneous.TYPE).equals("folder")&&rhs.optString(Miscellaneous.TYPE).equals("folder")){
            Log.d("gosDEBUG", "Branch :"+2);
            return -1;
        }
        else if(lhs.optString(Miscellaneous.TYPE).equals("folder")&&rhs.optString(Miscellaneous.TYPE).equals("folder")){
            Log.d("gosDEBUG", "Branch :"+3);
            if(lhs.optString(Miscellaneous.NAME).compareTo(rhs.optString(Miscellaneous.NAME))>0){
                Log.d("gosDEBUG", "Branch :"+4);
                return 1;
            }
            else if(lhs.optString(Miscellaneous.NAME).compareTo(rhs.optString(Miscellaneous.NAME))<0){
                Log.d("gosDEBUG", "Branch :"+5);
                return -1;
            }
            // Case that should never happen
            else{
                Log.d("gosDEBUG", "Branch :"+6);
                return 0;
            }
        }
        else{
            Log.d("gosDEBUG", "Branch :"+7);
            if(lhs.optString(Miscellaneous.NAME).compareTo(rhs.optString(Miscellaneous.NAME))>0){
                Log.d("gosDEBUG", "Branch :"+8);
                return 1;
            }
            else if(lhs.optString(Miscellaneous.NAME).compareTo(rhs.optString(Miscellaneous.NAME))<0){
                Log.d("gosDEBUG", "Branch :"+9);
                return -1;
            }
            // Case that should never happen
            else{
                Log.d("gosDEBUG", "Branch :"+10);
                return 0;
            }
        }
    }
}
