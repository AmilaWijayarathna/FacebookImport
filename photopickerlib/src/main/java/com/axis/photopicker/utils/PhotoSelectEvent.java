package com.axis.photopicker.utils;

/**
 * Created by Sithmal on 1/20/16.
 */


public class PhotoSelectEvent {

    private boolean isSelected;

    public PhotoSelectEvent (boolean select){
        this.isSelected = select;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
