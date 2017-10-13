package com.axis.colorpickerlib;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by DDA_Admin on 5/5/16.
 */
public class RecentColorDb {

    ArrayList<String> recentColors;

    public RecentColorDb() {
        recentColors = new ArrayList<String>();
    }

    public static RecentColorDb instance=null;

    public static RecentColorDb getInstance() {
        if(instance==null){
            instance=new RecentColorDb();}

        return instance;

    }
    public void setRecentColors(String color){
        if (!recentColors.contains(color)) {
            recentColors.add(color);
        }
    }

    public ArrayList<String> getRecentColors() {
        //Collections.reverse(recentColors);
        return recentColors;
    }
}
