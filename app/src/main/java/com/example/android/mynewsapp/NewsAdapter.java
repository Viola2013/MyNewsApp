package com.example.android.mynewsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Mervi on 1.7.2017.
 */

public class NewsAdapter extends ArrayAdapter<NewsListing> {
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    NewsAdapter(Context context, ArrayList<NewsListing> articles) {
        super(context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.article_title);
            holder.sectionName = convertView.findViewById(R.id.section);
            holder.date = convertView.findViewById(R.id.date);
            holder.author = convertView.findViewById(R.id.author);
            holder.trailText = convertView.findViewById(R.id.trail_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NewsListing currNewslist = getItem(position);

        if (!currNewslist.getTitle().isEmpty())
            holder.title.setText(currNewslist.getTitle());
        if (!currNewslist.getSectionName().isEmpty())
            holder.sectionName.setText(currNewslist.getSectionName());
        if (!currNewslist.getDate().isEmpty()) {
            Date parsedDate = parseDate(currNewslist.getDate());
            if (parsedDate != null) holder.date.setText(formatDate(parsedDate));
        }
        if (!currNewslist.getAuthor().isEmpty())
            holder.author.setText(currNewslist.getAuthor());
        if (!currNewslist.getTrailText().isEmpty())
            holder.trailText.setText(currNewslist.getTrailText());


        return convertView;
    }

    private String formatDate(Date dateObj) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(dateObj);
    }

    private Date parseDate(String strDate) {

        if (strDate == null) return null;
        SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = parser.parse(strDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing date", e);
        }
        return date;
    }

    private static class ViewHolder {
        TextView title;
        TextView sectionName;
        TextView date;
        TextView trailText;
        TextView author;

    }
}