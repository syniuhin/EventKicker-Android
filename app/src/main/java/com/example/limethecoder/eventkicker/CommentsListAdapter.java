package com.example.limethecoder.eventkicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.limethecoder.eventkicker.model.Comment;

import java.util.List;

/**
 * infm created it with love on 12/26/15. Enjoy ;)
 */
public class CommentsListAdapter extends RecyclerView
    .Adapter<CommentsListAdapter.ViewHolder> {

  private List<Comment> mComments;

  public CommentsListAdapter(List<Comment> comments) {
    mComments = comments;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent,
                                       int viewType) {
    View v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.comment_layout, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder,
                               int position) {
    Comment comment = mComments.get(position);
    holder.getAuthorView().setText(comment.authorName);
    holder.getContentView().setText(comment.content);
    holder.getDateView().setText(comment.getParsedDate());

  }

  @Override
  public int getItemCount() {
    return mComments.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private TextView authorView;
    private TextView contentView;
    private TextView dateView;


    public ViewHolder(View itemView) {
      super(itemView);
      authorView = (TextView) itemView.findViewById(R.id.comment_author);
      contentView = (TextView) itemView.findViewById(R.id.comment_content);
      dateView = (TextView) itemView.findViewById(R.id.comment_date);
    }

    public TextView getAuthorView() {
      return authorView;
    }


    public TextView getContentView() {
      return contentView;
    }


    public TextView getDateView() {
      return dateView;
    }
  }

  }
