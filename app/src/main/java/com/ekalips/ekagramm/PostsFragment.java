package com.ekalips.ekagramm;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment implements OnRecyclerViewClickCallBack {

    RecyclerView recyclerView;
    Parcelable statesList;
    Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    OnScrollCallback onScrollCallback;
    public PostsFragment() {
        // Required empty public constructor
    }

    public void setOnScrollCallback(OnScrollCallback onScrollCallback) {
        this.onScrollCallback = onScrollCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);
        final String tag = "test";
        context = getContext();
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipePosts);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPostsRequest(tag);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.posts_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT)
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.addItemDecoration(new SpacesItemDecoration(16,2));
        }

        if (hasSavedData())
        {
            //swipeRefreshLayout.setRefreshing(true);
            initializeRecyclerView(loadPostsData());
            getPostsRequest(tag);
        }
        else {
            getPostsRequest(tag);
        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        if (savedInstanceState != null) {
            statesList = savedInstanceState.getParcelable("RecyclerViewState");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (recyclerView!=null) statesList = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("RecyclerViewState", statesList);
    }



    private void savePostsData(List<InstagramMedia> posts)
    {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(posts);
        prefsEditor.putString("Posts", json);
        prefsEditor.apply();
    }

    private List<InstagramMedia> loadPostsData()
    {
        List<InstagramMedia> posts;
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("Posts", "");
        posts = gson.fromJson(json,new TypeToken<List<InstagramMedia>>() {}.getType());
        return posts;
    }

    private boolean hasSavedData()
    {
        return !PreferenceManager.getDefaultSharedPreferences(context).getString("Posts","").equals("");
    }

    public String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    public void getPostsRequest(String tag)
    {
        swipeRefreshLayout.setRefreshing(true);
        String request = "https://api.instagram.com/v1/tags/" + tag + "/media/recent?access_token=" + getString(R.string.token);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<InstagramMedia> posts = initializeList(response);
                            swipeRefreshLayout.setRefreshing(false);
                            savePostsData(posts);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(new PostsRecyclerViewAdapter(new ArrayList<InstagramMedia>(),context,null));

                if (hasSavedData())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Error!")
                            .setMessage("Probably you have no internet!\nLoad last viewed posts?")
                            .setIcon(R.mipmap.ic_launcher)
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            initializeRecyclerView(loadPostsData());
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("No",null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }



                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 400:
                            String json = new String(response.data);
                            json = trimMessage(json, "error_message");
                            if (json != null) Log.d("INITIAL REQUEST", "ERROR: " + (json));
                            Toast.makeText(context, "Something went wrong on server", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, "Something went wrong on server", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        queue.add(stringRequest);
    }


    private List<InstagramMedia> initializeList(String JSONRawData) throws JSONException {

        List<InstagramMedia> medias = new ArrayList<>();
        JSONArray jsonArray = new JSONObject(JSONRawData).getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            medias.add(new InstagramMedia(jsonArray.getJSONObject(i)));
        }
        initializeRecyclerView(medias);
        return medias;
    }

    private void initializeRecyclerView(List<InstagramMedia> medias)
    {
        if (recyclerView.getAdapter()==null) recyclerView.setAdapter(new PostsRecyclerViewAdapter(medias,context,this));
        else
        {
            ((PostsRecyclerViewAdapter)recyclerView.getAdapter()).swapItems(medias);
            if (!((PostsRecyclerViewAdapter) recyclerView.getAdapter()).hasCommentsCallback())
            {
                ((PostsRecyclerViewAdapter) recyclerView.getAdapter()).setmOnRecyclerViewClickCallBack(this);
            }
        }
    }

    @Override
    public void onMethodCallback(InstagramMedia media, ImageView holder) {
        ChosenOneFragment fragment = new ChosenOneFragment();
        Bundle args = new Bundle();
        args.putSerializable("media",media);
        fragment.setArguments(args);
            FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.activity_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

   }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount = 2;
        private int spacing;

        public SpacesItemDecoration(int space, int spanCount) {
            this.spacing = space;
            this.spanCount = spanCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column


            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        }
    }
}
