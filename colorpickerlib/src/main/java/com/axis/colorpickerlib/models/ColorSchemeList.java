
package com.axis.colorpickerlib.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ColorSchemeList {

    private Boolean fail;
    private Data data;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     *     The fail
     */
    public Boolean getFail() {
        return fail;
    }

    /**
     *
     * @param fail
     *     The fail
     */
    public void setFail(Boolean fail) {
        this.fail = fail;
    }

    /**
     *
     * @return
     *     The data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     *     The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
