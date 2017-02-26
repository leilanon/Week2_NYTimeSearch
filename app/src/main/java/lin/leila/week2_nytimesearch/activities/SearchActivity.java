package lin.leila.week2_nytimesearch.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import lin.leila.week2_nytimesearch.Article;
import lin.leila.week2_nytimesearch.ArticleArrayAdapter;
import lin.leila.week2_nytimesearch.EndlessScrollListener;
import lin.leila.week2_nytimesearch.R;

public class SearchActivity extends AppCompatActivity {

    GridView gvResults;
    String strQuery;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

    }

    public void setupViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

                // get the article to display
                Article article = articles.get(position);

                // pass in that article into intent
                i.putExtra("article", article);

                // launch the activity
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }
    public void loadNextDataFromApi(int offset) {
        onArticleSearch(offset);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override

            public boolean onQueryTextSubmit(String query) {
                adapter.clear();
                strQuery = query;
                onArticleSearch(0);
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(SearchActivity.this, SettingActivity.class);
            startActivity(i);
            return true;
        } else {

        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(int page) {
        String query = strQuery;
        String strOldDate;
        String strNewDate;
        String strDate = "";
        String strSort = "";
        String strDesk = "";
        SimpleDateFormat fmtOld = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtNew = new SimpleDateFormat("yyyyMMdd");
        SharedPreferences spref = getSharedPreferences(SettingActivity.KEY, MODE_PRIVATE);

        strOldDate = spref.getInt("dateDD", 1) + "/" +
                spref.getInt("dateMM", 1) + "/" +
                spref.getInt("dateYYYY", 2000);
        try {
            Date fmtDate = fmtOld.parse(strOldDate);
            strNewDate = fmtNew.format(fmtDate);
            strDate = strNewDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] sortArray = getResources().getStringArray(R.array.sort_array);
        strSort = sortArray[spref.getInt("sortOption", 0)];

        if (!spref.getBoolean("deskArtsChk", false) &&
                !spref.getBoolean("deskSportsChk", false) &&
                !spref.getBoolean("deskStyleChk", false)) {
        } else {
            strDesk = "news_desk:(\"";
            if (spref.getBoolean("deskArtsChk", false)) {
                strDesk += "Arts ";
            }
            if (spref.getBoolean("deskSportsChk", false)) {
                strDesk += "Sports ";
            }
            if (spref.getBoolean("deskStyleChk", false)) {
                strDesk += "Fashion & Style";
            }
            strDesk += "\")";
        }

//        Toast.makeText(this, "Searching for" + query, Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "25a4ca6ca4ac4df59d15d24198818d9b");
        params.put("page", page);
        params.put("q", query);
        params.put("begin_date", strDate);
        params.put("sort", strSort);
        if (strDesk.length() > 0) {
            params.put("fq", strDesk);
        }


//        Log.d("DEBUG", "Query" + params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
//                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
