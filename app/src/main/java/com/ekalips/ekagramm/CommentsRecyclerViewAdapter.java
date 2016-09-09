package com.ekalips.ekagramm;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

/**
 * Created by ekalips on 9/8/16.
 */

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder> {

    List<InstaComment> comments;
    Context context;
    public CommentsRecyclerViewAdapter(List<InstaComment> comments, Context context)
    {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public CommentsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentsRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentsRecyclerViewAdapter.ViewHolder holder, int position) {
        //make name bold
        int nameLenght = comments.get(holder.getAdapterPosition()).getAuthorUsername().length();
        String text = comments.get(holder.getAdapterPosition()).getAuthorUsername() + " " + comments.get(holder.getAdapterPosition()).getText();
        SpannableString hashtagintitle = new SpannableString(text);
        hashtagintitle.setSpan(new StyleSpan(BOLD),0,nameLenght-1,0);

        //find hashtags and make them italic
        Matcher matcher = Pattern.compile("(#|@)([A-Za-z0-9_-]+)").matcher(hashtagintitle);
        while (matcher.find())
        {
            hashtagintitle.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(), matcher.end(), 0);
            hashtagintitle.setSpan(new StyleSpan(ITALIC),matcher.start(),matcher.end(),0);
        }
        //set
        holder.text.setText(hashtagintitle);

        //date
        Date d = new Date(comments.get(holder.getAdapterPosition()).getCreatedTime() * 1000);
        if (d.getDay() == Calendar.getInstance().DAY_OF_MONTH && d.getMonth() == Calendar.getInstance().MONTH &&
                d.getYear() == Calendar.getInstance().YEAR )
            holder.date.setText(new SimpleDateFormat("hh:mm").format(d));
        else
            holder.date.setText( new SimpleDateFormat("d MMM yyyy").format(d));
        //prof pic
        Picasso.with(context).load(comments.get(holder.getAdapterPosition()).getProfilePictureUrl()).into(holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView text,date;
        CircleImageView profilePic;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.comment_item_text); //username + comment text (like instagram)
            date = (TextView) itemView.findViewById(R.id.comment_item_date); //message date
            profilePic = (CircleImageView) itemView.findViewById(R.id.comment_item_pic); //author picture
        }
    }
}
