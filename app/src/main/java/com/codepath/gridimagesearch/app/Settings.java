package com.codepath.gridimagesearch.app;

public class Settings {

    private String mColor;
    private String mSize;
    private String mType;
    private String mSiteFilter;

    public Settings(String color) {
        this.mColor = color;
    }

    public void setColor(String color) {
        mColor = color;
    }

    public String getColor() {
        return this.mColor;
    }
}
