package com.axis.colorpickerlib;

/**
 * Created by DDA_Admin on 5/5/16.
 */
public class ColorCategory {

    String title;
    String[] colors = new String[5];

    public ColorCategory(String title,String[] colors) {
        this.title = title;
        this.colors = colors;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getColors() {
        return colors;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
    }
}
