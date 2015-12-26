package com.example.limethecoder.eventkicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.example.limethecoder.eventkicker.model.Comment;
import com.example.limethecoder.eventkicker.net.ApiResponse;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

import java.util.ArrayList;


public class CommentsListActivity extends AppCompatActivity {


  RecyclerView mCommentsView;
  private ServiceManager.MyApiEndpointInterface mService;
  int eventId;
  private ArrayList<Comment> mComments;
  RecyclerView.LayoutManager layoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comments_list);

    eventId = getIntent().getIntExtra("id", 1);

    mCommentsView = (RecyclerView) findViewById(R.id.commentsList);
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

}
