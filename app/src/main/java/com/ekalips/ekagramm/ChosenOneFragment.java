package com.ekalips.ekagramm;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static android.graphics.Typeface.ITALIC;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChosenOneFragment extends Fragment implements OnCommentsLoadedCallback,Serializable{


    public LinearLayout linearLayout;
    public ImageView image;
    public TextView desc;
    public TextView likesCount,commentsCount;
    public LinearLayout likesLay,commentsLay;
    public CircleImageView profilePic;
    public TextView usernameText;
    RecyclerView commentsRecyclerView;
    InstagramMedia media;

    public ChosenOneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle args) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chosen_one, container, false);

        if (this.getArguments() != null )  {media = (InstagramMedia)this.getArguments().getSerializable("media");
        this.getArguments().clear();
        }



        image = (ImageView) v.findViewById(R.id.chosen_image_view);  //image
        desc = (TextView) v.findViewById(R.id.chosen_desc);  //text
        likesCount = (TextView) v.findViewById(R.id.chosen_likes_text);  //count of likes
        commentsCount = (TextView) v.findViewById(R.id.chosen_comments_text);    //count of comments
        likesLay = (LinearLayout) v.findViewById(R.id.chosen_likes_view);    //view for likes onClick (for future)
        commentsLay = (LinearLayout) v.findViewById(R.id.chosen_comments_view);  //view for comments onClick (for future)
        profilePic = (CircleImageView) v.findViewById(R.id.chosen_profile_pic);  //user icon
        usernameText = (TextView) v.findViewById(R.id.chosen_profile_text);  //username
        linearLayout = (LinearLayout) v.findViewById(R.id.scroll_layout);

        if (args!=null)
        {
            media = args.getParcelable("media");
            onMethodCallback();
        }

        if (media != null)
            if (media.getComments() == null || !(media.getComments().size() > 0))
            {
                media.setCallback(this); requestComments(media.getMediaID());
            }

        String url = media.getStandardResolution();
        Picasso.with(getContext()).load(url).into(image);
        //text
        SpannableString hashtagintitle = new SpannableString(media.getDescription());
        Matcher matcher = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(hashtagintitle);
        while (matcher.find())
        {
            hashtagintitle.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(), matcher.end(), 0);
            hashtagintitle.setSpan(new StyleSpan(ITALIC),matcher.start(),matcher.end(),0);
        }
        desc.setText(hashtagintitle);
        //likes
        likesCount.setText(String.valueOf(media.getLikesCount()));
        //comments
        commentsCount.setText(String.valueOf(media.commentsCount));
        //user avatar
        Picasso.with(getContext()).load(media.getProfilePic()).into(profilePic);
        //username
        usernameText.setText(media.getUsername());




        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Photo");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("media",media);
    }

    public void requestComments(String mediaID)
    {
        String request = "https://api.instagram.com/v1/media/" + mediaID + "/comments?access_token=" + getString(R.string.token);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            media.initiateComments(new JSONObject(response).getJSONArray("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 400:
                            String json = new String(response.data);
                          //  json = trimMessage(json, "error_message");
                            Log.d("INITIAL REQUEST", "ERROR: " + (json));
                          //  Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                          //  Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onMethodCallback() {
        Log.d("COMMENTS","LOADED");
        if (commentsRecyclerView!=null)
        {
            if (commentsRecyclerView.getAdapter() == null)
            {
                commentsRecyclerView.setAdapter(new CommentsRecyclerViewAdapter(media.getComments(),getContext()));
            }
            else {  }
        }
        else {
            if (getContext() != null) {
                commentsRecyclerView = new RecyclerView(getContext());
                commentsRecyclerView.setHasFixedSize(false);
                commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                commentsRecyclerView.setAdapter(new CommentsRecyclerViewAdapter(media.getComments(), getContext()));
                linearLayout.addView(commentsRecyclerView);
            }
        }
    }
}
