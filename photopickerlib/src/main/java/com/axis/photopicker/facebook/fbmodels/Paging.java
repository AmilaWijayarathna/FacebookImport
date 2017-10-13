
package com.axis.photopicker.facebook.fbmodels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Paging {

    @SerializedName("cursors")
    @Expose
    private Cursors cursors;

    /**
     * 
     * @return
     *     The cursors
     */
    public Cursors getCursors() {
        return cursors;
    }

    /**
     * 
     * @param cursors
     *     The cursors
     */
    public void setCursors(Cursors cursors) {
        this.cursors = cursors;
    }

}
