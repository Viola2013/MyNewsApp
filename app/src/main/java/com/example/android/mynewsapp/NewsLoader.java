package com.example.android.mynewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

/**
 * Created by Mervi on 1.7.2017.
 */

class NewsLoader extends AsyncTaskLoader<List<NewsListing>> {
    private final String mQueryUrl;

    public NewsLoader(Context context, String queryUrl) {
        super(context);
        mQueryUrl = queryUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsListing> loadInBackground() {
        if (mQueryUrl == null) return null;
        return QueryUtils.fetchData(mQueryUrl);
    }
}