
package com.axis.photopicker.facebook.fbmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Albums {

    @SerializedName("data")
    @Expose
    private List<Album> data = new ArrayList<Album>();
    @SerializedName("paging")
    @Expose
    private Paging paging;

    /**
     * 
     * @return
     *     The data
     */
    public List<Album> getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    public void setData(List<Album> data) {
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
