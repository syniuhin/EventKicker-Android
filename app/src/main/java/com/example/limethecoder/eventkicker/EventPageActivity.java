package com.example.limethecoder.eventkicker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.limethecoder.eventkicker.model.Comment;
import com.example.limethecoder.eventkicker.model.Like;
import com.example.limethecoder.eventkicker.net.ApiResponse;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.gson.Gson;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.ArrayList;

public class EventPageActivity extends AppCompatActivity {

  MaterialViewPager mViewPager;
  int mLikesCnt;
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
  private Button mLikeButton;
  private Integer mLikesCount = -1;
  private Boolean mIsLiked = false;
  private EventItem mEventItem;
  private ArrayList<Comment> mComments;
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

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
        (this);
    String username = prefs.getString("username", "");
    String passwd = prefs.getString("userPassword", "");

    mService = ServiceManager.newService(username, passwd);

    findViews();
    loadDataToView();
    setListeners();


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
    mAuthorNameTextView = (TextView) findViewById(R.id.name); // Don't forget
    // about Stead (John Stead)
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

    mLikeButton = (Button) findViewById(R.id.likes_button);
    mLikeButton.setEnabled(false);
  }

  private void loadDataToView() {
    mDescriptionTextView.setText(mEventItem.getDescription());
    mAuthorNameTextView.setText(mEventItem.getAuthorName());
    mTimeCreatedTextView.setText(mEventItem.getParsedCreateDate());
    mTimeScheduledTextView.setText(mEventItem.getSchParsedDate());
    getLikesCount();
    isLiked();
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
        Intent i = new Intent(EventPageActivity.this,
                              CommentsListActivity.class);
        i.putExtra("id", mEventItem.getId());
        startActivity(i);
      }
    });

    mLikeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Like like = new Like();
        like.eventId = (long) mEventId;
        like.authorId = (long) PreferenceManager.getDefaultSharedPreferences
            (EventPageActivity.this).getInt("userId", -1);
        postLike(like);
      }
    });
  }

  private void loadCommentsToView() {
    if (mComments == null || mComments.size() < 3)
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

  private void postLike(final Like like) {
    Call<ApiResponse<Like>> call = mService.postLike(like);
    call.enqueue(new Callback<ApiResponse<Like>>() {
      @Override
      public void onResponse(Response<ApiResponse<Like>> response,
                             Retrofit retrofit) {
        if (response.body().success) {
          isLiked();
          getLikesCount();
        }
      }

      @Override
      public void onFailure(Throwable t) {

      }
    });
  }

  private void getLikesCount() {
    Call<ApiResponse<Integer>> call = mService.getLikesCount(mEventId);
    call.enqueue(new Callback<ApiResponse<Integer>>() {
      @Override
      public void onResponse(Response<ApiResponse<Integer>> response,
                             Retrofit retrofit) {
        if (response.body().success) {
          mLikesCount = response.body().single;
          updateLikeButton();
        }
      }

      @Override
      public void onFailure(Throwable t) {
        // Log error here since request failed
        Log.e("UserData", t.getMessage());
      }
    });
  }

  private void isLiked() {
    Call<ApiResponse<Boolean>> call = mService.isLiked(mEventId);
    call.enqueue(new Callback<ApiResponse<Boolean>>() {

      @Override
      public void onResponse(Response<ApiResponse<Boolean>> response,
                             Retrofit retrofit) {
        if (response.body().success) {
          mIsLiked = response.body().single;
          updateLikeButton();
        }
      }

      @Override
      public void onFailure(Throwable t) {

      }
    });
  }

  private void updateLikeButton() {
    mLikeButton.setText(
        ((mIsLiked) ? "Liked " : "Like ") +
            ((mLikesCount > 0) ? mLikesCount.toString() : ""));
    mLikeButton.setEnabled(!mIsLiked);
  }
}
