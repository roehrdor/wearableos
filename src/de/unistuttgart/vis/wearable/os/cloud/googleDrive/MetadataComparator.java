package de.unistuttgart.vis.wearable.os.cloud.googleDrive;


import com.google.android.gms.drive.Metadata;

import java.util.Comparator;

public class MetadataComparator implements Comparator<Metadata>{
    @Override
    public int compare(Metadata lhs, Metadata rhs) {
        if(lhs.isFolder()&&!rhs.isFolder()){
            return 1;
        }
        else if(!lhs.isFolder()&&rhs.isFolder()){
            return -1;
        }
        else if(!lhs.isFolder()&&!rhs.isFolder()){
            if(lhs.getTitle().compareTo(rhs.getTitle())>0){
                return 1;
            }
            else if(lhs.getTitle().compareTo(rhs.getTitle())<0){
                return -1;
            }
            else{
                if(lhs.getModifiedDate().after(rhs.getModifiedDate())){
                    return 1;
                }
                else if(lhs.getModifiedDate().before((rhs.getModifiedDate()))){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        }
        else{
            if(lhs.getTitle().compareTo(rhs.getTitle())>0){
                return 1;
            }
            else if(lhs.getTitle().compareTo(rhs.getTitle())<0){
                return -1;
            }
            else{
                if(lhs.getModifiedDate().after(rhs.getModifiedDate())){
                    return 1;
                }
                else if(lhs.getModifiedDate().before((rhs.getModifiedDate()))){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        }
    }
}
