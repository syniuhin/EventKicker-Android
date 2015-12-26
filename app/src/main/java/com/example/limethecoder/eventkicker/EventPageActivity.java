package com.example.limethecoder.eventkicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.limethecoder.eventkicker.model.Comment;
import com.example.limethecoder.eventkicker.net.ApiResponse;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.gson.Gson;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

import java.util.ArrayList;

public class EventPageActivity extends AppCompatActivity {

  MaterialViewPager mViewPager;
  private int mEventId;
  private TextView mDescriptionTextView;
  private TextView mAuthorNameTextView;
  private TextView mTimeCreatedTextView;
  private TextView mTimeScheduledTextView;
  private TextView mCommentAuthor1;
  private TextView mCommentAuthor2;
  private TextView mCommentAuthor3;

  private TextView mCommentContent1;
  private TextView mCommentContent2;
  private TextView mCommentContent3;

  private TextView mCommentDate1;
  private TextView mCommentDate2;
  private TextView mCommentDate3;

  private TextView mCommentsMore;

  private EventItem mEventItem;
  private ArrayList<Comment> mComments;

  int mLikesCnt;

  private ServiceManager.MyApiEndpointInterface mService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_page);

    mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);


    String json = getIntent().getStringExtra("json");
    Gson gson = new Gson();
    mEventItem = gson.fromJson(json, EventItem.class);
    mEventId = mEventItem.getId();

    TextView header_logo = (TextView) mViewPager.findViewById(R.id.logo_white);
    header_logo.setText(mEventItem.getName());

    findViews();
    loadDataToView();
    setListeners();
    mService = ServiceManager.newService();

    //getLikesCount();
    getComments();

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

    mCommentAuthor1 = (TextView) findViewById(R.id.comment_author);
    mCommentAuthor2 = (TextView) findViewById(R.id.comment_author2);
    mCommentAuthor3 = (TextView) findViewById(R.id.comment_author3);

    mCommentContent1 = (TextView) findViewById(R.id.comment_content);
    mCommentContent2 = (TextView) findViewById(R.id.comment_content2);
    mCommentContent3 = (TextView) findViewById(R.id.comment_content3);

    mCommentDate1 = (TextView) findViewById(R.id.comment_date);
    mCommentDate2 = (TextView) findViewById(R.id.comment_date2);
    mCommentDate3 = (TextView) findViewById(R.id.comment_date3);

    mCommentsMore = (TextView) findViewById(R.id.comments_more);
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

    mCommentsMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(EventPageActivity.this, CommentsListActivity.class);
        i.putExtra("id", mEventItem.getId());
        startActivity(i);
      }
    });
  }

  private void loadCommentsToView() {
    if(mComments == null || mComments.size() < 3)
      return;

    mCommentAuthor1.setText(mComments.get(0).authorName);
    mCommentAuthor2.setText(mComments.get(1).authorName);
    mCommentAuthor3.setText(mComments.get(2).authorName);

    mCommentContent1.setText(mComments.get(0).content);
    mCommentContent2.setText(mComments.get(1).content);
    mCommentContent3.setText(mComments.get(2).content);

    mCommentDate1.setText(mComments.get(0).getParsedDate());
    mCommentDate2.setText(mComments.get(1).getParsedDate());
    mCommentDate3.setText(mComments.get(2).getParsedDate());
  }

  private void getComments() {
    Call<ApiResponse<Comment>> call = mService.getEventComments(mEventId, 3);
    call.enqueue(new Callback<ApiResponse<Comment>>() {
      @Override
      public void onResponse(retrofit.Response<ApiResponse<Comment>> response,
                             Retrofit retrofit) {

        if (response.body().success) {
          mComments = response.body().multiple;
          loadCommentsToView();
        }
      }

      @Override
      public void onFailure(Throwable t) {
        // Log error here since request failed
        Log.e("UserData", t.getMessage());
      }
    });
  }

  private void getLikesCount() {
    Call<ApiResponse> call = mService.getLikesCnt(mEventId);
    call.enqueue(new Callback<ApiResponse>() {
      @Override
      public void onResponse(retrofit.Response<ApiResponse> response,
                             Retrofit retrofit) {

        if (response.body().success) {
          //mLikesCnt = (int)response.body().single; ??
        }
      }

      @Override
      public void onFailure(Throwable t) {
        // Log error here since request failed
        Log.e("UserData", t.getMessage());
      }
    });
  }

}
