package com.example.bookapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.RecycleViewHolder> {

    private final String TAB = BookAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Book> mlist;
    private LayoutInflater mInflater;
//private OnItemListener mOnItemClickListener;

    BookAdapter(Context context, ArrayList<Book> list) {
        this.context = context;
        this.mlist = list;
        this.mInflater = LayoutInflater.from(context);
    }

//        void SetOnItemClickListener(OnItemListener OnClickListener){
//        this.mOnItemClickListener = OnClickListener;
//        }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
        Book currentitem = mlist.get(position);
        holder.mTitleTextView.setText("Title: "+currentitem.getTitle());
        holder.mAuthoeTextView.setText("Author: "+currentitem.getAuthorNames());
        holder.mDescriptionTextView.setText("Description: "+currentitem.getDescription());
//        holder.bind(mlist.get(position),mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class RecycleViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mAuthoeTextView;
        private TextView mDescriptionTextView;

        RecycleViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.book_title);
            mAuthoeTextView = itemView.findViewById(R.id.book_authors);
            mDescriptionTextView = itemView.findViewById(R.id.book_description);

        }

//    void bind(Book books,OnItemListener OnItemListener){
//        itemView.setOnClickListener(view->{
//            OnItemListener.OnItemClick(books);
//        });
//    }
    }
}