package com.ekalips.ekagramm;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnScrollCallback, MenuItemCompat.OnActionExpandListener {



    Context context;
    RecyclerView recyclerView;
    Parcelable statesList;
    Fragment mContent;
    Toolbar mToolbar;
    MenuItem searchMenuItem;
    boolean search = false;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "Content");
            return;
        }

        PostsFragment postsFragment = new PostsFragment();
        postsFragment.setOnScrollCallback(this);
        postsFragment.setArguments(getIntent().getExtras());
        mContent = postsFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.activity_main,postsFragment).commit();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "Content", mContent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

//        // Retrieve list state and list/item positions
//        if(state != null)
//            statesList = state.getParcelable("RecyclerViewState");
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (statesList != null) {
//            recyclerView.getLayoutManager().onRestoreInstanceState(statesList);
//        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onMethodCallback(String direction,int dY) {
        //TODO: MAKE HIDING
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchMenuItem = searchItem;

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ((PostsFragment)mContent).getPostsRequest(query);
            search = true;
        }
        else if (Intent.ACTION_DEFAULT.equals(intent.getAction()))
        {
            ((PostsFragment)mContent).getPostsRequest("test");
            search = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (search)
        {
            Intent intent = new Intent(this,MainActivity.class);
            intent.setAction(Intent.ACTION_DEFAULT);
            startActivity(intent);
        }
    }
}
