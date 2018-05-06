package com.cmi.bache24.data.model;

/**
 * Created by omar on 9/9/16.
 */
public class Road {

    private String thoroughfare;
    private String[] sections;

    public String getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(String thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public String[] getSections() {
        return sections;
    }

    public void setSections(String[] sections) {
        this.sections = sections;
    }
}
