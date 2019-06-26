package com.example.awarex;

import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.awarex.Adapter.TvShowAdapter;
import com.example.awarex.Model.TvShow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView tvShow_recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TvShow> tvShowList = new ArrayList<>();
    private ArrayList<TvShow> tvShowListFull = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private TextView errorLayout, loadingLayout;
    private Boolean visibility = false;
    private TvShowAdapter tvShowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        errorLayout = findViewById(R.id.errorLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        tvShow_recyclerView = findViewById(R.id.tvShowList);


        layoutManager = new LinearLayoutManager(this);
        tvShow_recyclerView.setLayoutManager(layoutManager);

        //recyclerView adapter setup
        tvShowAdapter = new TvShowAdapter(tvShowList, tvShowListFull, this);
        tvShow_recyclerView.setAdapter(tvShowAdapter);

        //Swipe to refresh functionality
        refreshLayout = findViewById(R.id.swipeRefresh);
        loadingLayout.setVisibility(View.VISIBLE);

        refreshLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        initiateRequest();


        //This function gets called for each refresh call.
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!tvShowList.isEmpty())
                {
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                }
                else
                {
                    initiateRequest();
                }
            }
        });

    }


    private void initiateRequest()
    {
        refreshLayout.setRefreshing(true);
        JsonArrayRequest request = new JsonArrayRequest(getString(R.string.api_url), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                refreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);
                visibility = true;
                try {
                    for(int i = 0; i<response.length(); i++)
                    {
                        TvShow tvShow = new TvShow();
                        tvShow.setName(response.getJSONObject(i).getString("name"));
                        tvShow.setAir(response.getJSONObject(i).getString("air"));
                        tvShow.setImg(response.getJSONObject(i).getString("img"));
                        tvShowList.add(tvShow);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Cloning Arraylist to make deep copy
                for (TvShow show : tvShowList) {
                    try {
                        tvShowListFull.add((TvShow) show.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }

                loadingLayout.setVisibility(View.GONE);
                tvShowAdapter.notifyDataSetChanged();



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "onErrorResponse: "+ error.toString());
                refreshLayout.setRefreshing(false);
                loadingLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(4000, 2, 2f));
        MySingleton.getInstance(this).addToRequestQueue(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            if(!visibility) {
                if (!refreshLayout.isRefreshing()) {
                    errorLayout.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(true);
                }

                // Start our refresh background task
                initiateRequest();
            }
            return true;
        }
        if (id == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    tvShowAdapter.getFilter().filter(newText);
                    return false;
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

}
