package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

import org.json.JSONObject;

import java.util.Comparator;

public class JSONComparator implements Comparator<JSONObject>{
    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {
        if(lhs.optString(Miscellaneous.TYPE).equals("folder")&&rhs.optString(Miscellaneous.TYPE).equals("file")){
            return 1;
        }
        else if(lhs.optString(Miscellaneous.TYPE).equals("file")&&rhs.optString(Miscellaneous.TYPE).equals("folder")){
            return -1;
        }
        else if(lhs.optString(Miscellaneous.TYPE).equals("folder")&&rhs.optString(Miscellaneous.TYPE).equals("folder")){
            if(lhs.optString(Miscellaneous.NAME).compareTo(rhs.optString(Miscellaneous.NAME))>0){
                return 1;
            }
            else if(lhs.optString(Miscellaneous.NAME).compareTo(rhs.optString(Miscellaneous.NAME))<0){
                return -1;
            }
            // Case that should never happen
            else{
                return 0;
            }
        }
        else{
            if(lhs.optString(Miscellaneous.NAME).compareTo(rhs.optString(Miscellaneous.NAME))>0){
                return 1;
            }
            else if(lhs.optString(Miscellaneous.NAME).compareTo(rhs.optString(Miscellaneous.NAME))<0){
                return -1;
            }
            // Case that should never happen
            else{
                return 0;
            }
        }
    }
}
