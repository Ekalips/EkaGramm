package com.ekalips.ekagramm;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static android.graphics.Typeface.ITALIC;

/**
 * Created by ekalips on 9/7/16.
 */

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {
    List<InstagramMedia> dataset;
    Context context;

    private OnRecyclerViewClickCallBack mOnRecyclerViewClickCallBack;


    public PostsRecyclerViewAdapter(List<InstagramMedia> instagramMediaList,Context context,OnRecyclerViewClickCallBack callback)
    {
        dataset = instagramMediaList; this.context = context; this.mOnRecyclerViewClickCallBack = callback;
    }



    @Override
    public PostsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_recycler_view_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //image
        String url = dataset.get(holder.getAdapterPosition()).getStandardResolution();
        Picasso.with(context).load(url).into(holder.image, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.INVISIBLE);
            }
        });
        //text
        SpannableString hashtagintitle = new SpannableString(dataset.get(holder.getAdapterPosition()).getDescription());
        Matcher matcher = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(hashtagintitle);
        while (matcher.find())
        {
            hashtagintitle.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(), matcher.end(), 0);
            hashtagintitle.setSpan(new StyleSpan(ITALIC),matcher.start(),matcher.end(),0);
        }
        holder.desc.setText(hashtagintitle);
        //likes
        holder.likesCount.setText(String.valueOf(dataset.get(holder.getAdapterPosition()).getLikesCount()));
        //comments
        holder.commentsCount.setText(String.valueOf(dataset.get(holder.getAdapterPosition()).commentsCount));
        //user avatar
        Picasso.with(context).load(dataset.get(holder.getAdapterPosition()).getProfilePic()).into(holder.profilePic);
        //username
        holder.usernameText.setText(dataset.get(holder.getAdapterPosition()).getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (mOnRecyclerViewClickCallBack!=null) mOnRecyclerViewClickCallBack.onMethodCallback(dataset.get(holder.getAdapterPosition()),holder.image);

                else {
                   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                   builder.setTitle("Nope!")
                           .setMessage("Sorry,but you can't open posts with no internet :c")
                           .setIcon(R.mipmap.ic_launcher)
                           .setCancelable(false)
                           .setNegativeButton("Okay",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           dialog.cancel();
                                       }
                                   });
                   AlertDialog alert = builder.create();
                   alert.show();
               }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public void swapItems(List<InstagramMedia> medias) {
        // compute diffs
        final ContactDiffCallback diffCallback = new ContactDiffCallback(this.dataset, medias);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // clear contacts and add
        this.dataset.clear();
        this.dataset.addAll(medias);

        diffResult.dispatchUpdatesTo(this); // calls adapter's notify methods after diff is computed
    }

    public boolean hasCommentsCallback()
    {
        return mOnRecyclerViewClickCallBack!=null;
    }

    public void setmOnRecyclerViewClickCallBack(OnRecyclerViewClickCallBack mOnRecyclerViewClickCallBack) {
        this.mOnRecyclerViewClickCallBack = mOnRecyclerViewClickCallBack;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView desc;
        public TextView likesCount,commentsCount;
        public LinearLayout likesLay,commentsLay;
        public CircleImageView profilePic;
        public TextView usernameText;
        public ProgressBar progressBar;
        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.posts_image_view);  //image
            desc = (TextView) v.findViewById(R.id.posts_desc);  //text
            likesCount = (TextView) v.findViewById(R.id.posts_likes_text);  //count of likes
            commentsCount = (TextView) v.findViewById(R.id.posts_comments_text);    //count of comments
            likesLay = (LinearLayout) v.findViewById(R.id.posts_likes_view);    //view for likes onClick (for future)
            commentsLay = (LinearLayout) v.findViewById(R.id.posts_comments_view);  //view for comments onClick (for future)
            profilePic = (CircleImageView) v.findViewById(R.id.posts_profile_pic);  //user icon
            usernameText = (TextView) v.findViewById(R.id.posts_profile_text);  //username
            progressBar = (ProgressBar) v.findViewById(R.id.item_progress_bar);
            progressBar.setIndeterminate(true);
        }
    }

}
