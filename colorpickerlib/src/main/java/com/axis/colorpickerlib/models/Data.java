package com.axis.colorpickerlib.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DDA_Admin on 7/7/16.
 */
public class Data {

    private List<ColorScheme> themes = new ArrayList<ColorScheme>();
    private String version;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     *     The themes
     */
    public List<ColorScheme> getThemes() {
        return themes;
    }

    /**
     *
     * @param themes
     *     The themes
     */
    public void setThemes(List<ColorScheme> themes) {
        this.themes = themes;
    }

    /**
     *
     * @return
     *     The version
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @param version
     *     The version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
