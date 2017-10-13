
package com.axis.photopicker.facebook.fbmodels;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photos {

    @SerializedName("data")
    @Expose
    private List<Photo> data = new ArrayList<Photo>();
    @SerializedName("paging")
    @Expose
    private Paging paging;

    /**
     * 
     * @return
     *     The data
     */
    public List<Photo> getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    public void setData(List<Photo> data) {
        this.data = data;
    }

    /**
     * 
     * @return
     *     The paging
     */
    public Paging getPaging() {
        return paging;
    }

    /**
     * 
     * @param paging
     *     The paging
     */
    public void setPaging(Paging paging) {
        this.paging = paging;
    }

}
