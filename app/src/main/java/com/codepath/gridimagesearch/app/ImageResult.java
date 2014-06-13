package com.codepath.gridimagesearch.app;

import android.media.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ImageResult implements Serializable {

    private String fullUrl;
    private String thumbnailUrl;

    public ImageResult(JSONObject jsonObject) {
        try {
            this.fullUrl = jsonObject.getString("url");
            this.thumbnailUrl = jsonObject.getString("tbUrl");
        } catch (JSONException e) {
            this.fullUrl = null;
            this.thumbnailUrl = null;
        }
    }


    public String getFullUrl() {
        return fullUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String toString() {
        return this.thumbnailUrl;
    }

    public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();
        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new ImageResult(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
