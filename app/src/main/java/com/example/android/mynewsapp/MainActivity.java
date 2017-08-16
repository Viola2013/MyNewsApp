package com.example.android.mynewsapp;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsListing>> {
    private static final int LOADER_ID = 1;
    private static final String SEARCH_QUERY_KEY = "query";
    private TextView mEmptyStateView;
    private NewsAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyStateView = (TextView) findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mAdapter = new NewsAdapter(this, new ArrayList<NewsListing>());

        ListView NewsLoaderListView = (ListView) findViewById(R.id.list);
        NewsLoaderListView.setEmptyView(mEmptyStateView);
        NewsLoaderListView.setAdapter(mAdapter);
        NewsLoaderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        mAdapter.getItem(position).getWebUrl()
                ));
                if (browserIntent.resolveActivity(view.getContext().getPackageManager()) != null)
                    view.getContext().startActivity(browserIntent);
            }
        });

        if (getIntent() != null) handleIntent(getIntent());
    }

    //helpers
    private void handleIntent(Intent intent) {
        final String queryAction = intent.getAction();
        if (Intent.ACTION_SEARCH.equals(queryAction)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle bundle = new Bundle();
            bundle.putString(SEARCH_QUERY_KEY, query);
            lookupNewsLoaders(bundle);
        } else if (Intent.ACTION_MAIN.equals(queryAction)) {
            lookupNewsLoaders(null);
        }
    }

    private void lookupNewsLoaders(Bundle bundle) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            mProgressBar.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(LOADER_ID, bundle, this);
        } else {
            mEmptyStateView.setText(getString(R.string.no_internet));
        }
    }

    private String createUri(Bundle bundle) {
        String queryString;
        if (bundle != null) queryString = bundle.getString(SEARCH_QUERY_KEY);
        else queryString = "null";

        final String QUERY_URL = "http://content.guardianapis.com/search";
        final String ARG_QUERY = "q";
        final String ARG_API = "api-key";
        final String API_KEY = "test";
        final String ARG_SHOW_FIELDS = "show-fields";

        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(ARG_QUERY, queryString);

        StringBuilder fieldsBuilder = new StringBuilder();

        if (fieldsBuilder.length() > 0) {
            fieldsBuilder.deleteCharAt(fieldsBuilder.length() - 1);
            uriBuilder.appendQueryParameter(ARG_SHOW_FIELDS, fieldsBuilder.toString());
        }
        uriBuilder.appendQueryParameter(ARG_API, API_KEY);
        return uriBuilder.toString();

    }

    //loader stuff
    @Override
    public Loader<List<NewsListing>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, createUri(args));

    }

    public boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    @Override
    public void onLoadFinished(Loader<List<NewsListing>> loader, List<NewsListing> data) {
        mProgressBar.setVisibility(View.GONE);
        mEmptyStateView.setText(getString(R.string.no_results));
        mAdapter.clear();
        if (data != null && !data.isEmpty()) mAdapter.addAll(data);

    }

    @Override
    public void onLoaderReset(Loader<List<NewsListing>> loader) {
        mAdapter.clear();
    }

    //intents
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

}