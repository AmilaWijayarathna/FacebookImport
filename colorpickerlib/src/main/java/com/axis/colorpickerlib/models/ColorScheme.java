
package com.axis.colorpickerlib.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ColorScheme {

    private String name;
    private List<Color> colors = new ArrayList<Color>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The colors
     */
    public List<Color> getColors() {
        return colors;
    }

    /**
     *
     * @param colors
     *     The colors
     */
    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }}
