package com.example.limethecoder.eventkicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.gson.Gson;

public class EventPageActivity extends AppCompatActivity {

    private int mEventId;
    private EventItem mEventItem;

    private TextView mDescriptionTextView;
    private TextView mAuthorNameTextView;
    private TextView mTimeCreatedTextView;
    private TextView mTimeScheduledTextView;

    MaterialViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        String json = getIntent().getStringExtra("json");
        Gson gson = new Gson();
        mEventItem = gson.fromJson(json, EventItem.class);

        TextView header_logo = (TextView) mViewPager.findViewById(R.id.logo_white);
        header_logo.setText(mEventItem.getName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Jesus loves you", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViews();
        loadDataToView();
        setListeners();

        Toolbar toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }

    }

    private void findViews() {
          mDescriptionTextView = (TextView) findViewById(R.id.event_descr);
          mAuthorNameTextView = (TextView) findViewById(R.id.name);
          mTimeCreatedTextView = (TextView) findViewById(R.id.created_date);
          mTimeScheduledTextView = (TextView) findViewById(R.id.scheduled_date);
    }

    private void loadDataToView() {
        mDescriptionTextView.setText(mEventItem.getDescription());
        mAuthorNameTextView.setText(mEventItem.getAuthorName());
        mTimeCreatedTextView.setText(mEventItem.getParsedCreateDate());
        mTimeScheduledTextView.setText(mEventItem.getSchParsedDate());
    }

    private void setListeners() {
        mAuthorNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventPageActivity.this, UserPage.class);
                i.putExtra("id", mEventItem.getAuthorId());
                startActivity(i);
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                mEventItem.getPictureUrl()
                        );
                }

                return null;
            }
        });
    }

}
