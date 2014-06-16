package com.codepath.gridimagesearch.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends Activity {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
    ImageResultArrayAdapter imageAdapter;

    int imageOffLoad = 0;

    SharedPreferences sharedPreferences;

    String apiRequest = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpView();
        imageAdapter = new ImageResultArrayAdapter(this, imageResults);
        gvResults.setAdapter(imageAdapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
                ImageResult imageResult = imageResults.get(position);
                i.putExtra("result", imageResult);
                startActivity(i);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                callApi(apiRequest, imageAdapter.getCount());

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    public void setUpView() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
    }

    public void onImageSearch(View view) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String query = etQuery.getText().toString();
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
        imageResults.clear();
        apiRequest = "https://ajax.googleapis.com/ajax/services/search/images?rsz=8&v=1.0&q=" + Uri.encode(query) +
                "&imgcolor=" + sharedPreferences.getString("colorPref", ""); // + "&imgtype=" + type;
        System.out.println(apiRequest);
        callApi(apiRequest, imageOffLoad);
    }

    public void callApi(String apiRequest, int imageOffLoad) {
        AsyncHttpClient client = new AsyncHttpClient();
        apiRequest = apiRequest + "&start=" + imageAdapter.getCount();

        client.get(apiRequest,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        JSONArray imageJsonresults = null;
                        try {
                            imageJsonresults = response.getJSONObject("responseData").getJSONArray("results");
                            imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonresults));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showSettingsView() {
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettingsView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}


