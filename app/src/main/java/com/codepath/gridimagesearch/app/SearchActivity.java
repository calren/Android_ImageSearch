package com.codepath.gridimagesearch.app;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends Activity {

    StaggeredGridView gvResults;
    ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
    ImageResultArrayAdapter imageAdapter;
    TextView tvPlaceHolder;
    String query;

    int imageCount;

    SharedPreferences sharedPreferences;

    String apiRequest = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tvPlaceHolder = (TextView)findViewById(R.id.tvPlaceHolder);
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

                callApi(apiRequest);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String text) {
                return false;
            }
            public boolean onQueryTextSubmit(String searchQuery) {
                query = searchQuery;
                onImageSearch(searchQuery);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void setUpView() {
        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
    }

    public void onImageSearch(String url) {
        tvPlaceHolder.setVisibility(View.INVISIBLE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String query = url;
        Toast.makeText(this, "Searching for: " + url, Toast.LENGTH_SHORT).show();
        imageResults.clear();
        imageCount = 0;
        apiRequest = buildApiRequest(url);
        callApi(apiRequest);
    }

    public String buildApiRequest(String query) {
        String apiRequest = "https://ajax.googleapis.com/ajax/services/search/images?rsz=8&v=1.0&q=" + Uri.encode(query);

        String color = sharedPreferences.getString("colorPref", "any");
        String size = sharedPreferences.getString("sizePref", "any");
        String imgType = sharedPreferences.getString("fileTypePref", "any");
        String site = sharedPreferences.getString("sitePref", "google.com");

        if (!color.equals("any")) {
            apiRequest = apiRequest + "&imgcolor=" + color;
        }

        if (!size.equals("any")) {
            apiRequest = apiRequest + "&imgsz=" + size;
        }

        if (!imgType.equals("any")) {
            apiRequest = apiRequest + "&imgtype=" + imgType;
        }

        if (!site.equals("google.com")) {
            apiRequest = apiRequest + "&as_sitesearch=" + site;
        }

        return apiRequest;
    }


    public void callApi(String apiRequest) {
        AsyncHttpClient client = new AsyncHttpClient();
        apiRequest = apiRequest + "&start=" + imageCount;
        imageCount = imageCount + 8;
//        System.out.println("apiRequest = " + apiRequest);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettingsView();
            return true;
        }
        if (id == R.id.action_refresh) {
            onImageSearch(query);
        }
        return super.onOptionsItemSelected(item);
    }


}


