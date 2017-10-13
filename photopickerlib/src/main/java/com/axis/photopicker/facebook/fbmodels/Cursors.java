
package com.axis.photopicker.facebook.fbmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Cursors {

    @SerializedName("before")
    @Expose
    private String before;
    @SerializedName("after")
    @Expose
    private String after;

    /**
     * 
     * @return
     *     The before
     */
    public String getBefore() {
        return before;
    }

    /**
     * 
     * @param before
     *     The before
     */
    public void setBefore(String before) {
        this.before = before;
    }

    /**
     * 
     * @return
     *     The after
     */
    public String getAfter() {
        return after;
    }

    /**
     * 
     * @param after
     *     The after
     */
    public void setAfter(String after) {
        this.after = after;
    }

}
