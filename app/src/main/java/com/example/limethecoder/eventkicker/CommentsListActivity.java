package com.example.limethecoder.eventkicker;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.limethecoder.eventkicker.model.Comment;
import com.example.limethecoder.eventkicker.net.ApiResponse;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.ArrayList;


public class CommentsListActivity extends AppCompatActivity {


  RecyclerView mCommentsView;
  private ServiceManager.MyApiEndpointInterface mService;
  int eventId;
  private ArrayList<Comment> mComments;
  RecyclerView.LayoutManager layoutManager;

  private EditText mCommentEditText;
  private Button mSubmitButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comments_list);

    eventId = getIntent().getIntExtra("id", 1);

    mCommentsView = (RecyclerView) findViewById(R.id.commentsList);
    mCommentEditText = (EditText) findViewById(R.id.comment_list_form);
    mSubmitButton = (Button) findViewById(R.id.comment_list_submit_button);
    mSubmitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String content = mCommentEditText.getText().toString();
        Comment comment = new Comment();
        comment.authorId = PreferenceManager.getDefaultSharedPreferences
            (CommentsListActivity.this).getInt("userId", -1);
        comment.eventId = eventId;
        comment.content = content;
        postComment(comment);
      }
    });

    layoutManager = new LinearLayoutManager(this);
    mCommentsView.setLayoutManager(layoutManager);
    mService = ServiceManager.newService();
    getComments();
  }

  private void getComments() {
    Call<ApiResponse<Comment>> call = mService.getAllEventComments(eventId);
    call.enqueue(new Callback<ApiResponse<Comment>>() {
      @Override
      public void onResponse(retrofit.Response<ApiResponse<Comment>> response,
                             Retrofit retrofit) {

        if (response.body().success) {
          mComments = response.body().multiple;
          mCommentsView.setAdapter(new CommentsListAdapter(mComments));
        }
      }

      @Override
      public void onFailure(Throwable t) {
        // Log error here since request failed
        Log.e("UserData", t.getMessage());
      }
    });
  }

  private void postComment(Comment comment) {
    Call<ApiResponse<Comment>> call = mService.postComment(comment);
    call.enqueue(new Callback<ApiResponse<Comment>>() {
      @Override
      public void onResponse(Response<ApiResponse<Comment>> response,
                             Retrofit retrofit) {
        if (response.body().success) {
          getComments();
        }
      }

      @Override
      public void onFailure(Throwable t) {
      }
    });
  }
}
