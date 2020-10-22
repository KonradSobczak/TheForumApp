package com.example.theforumapp.Adapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theforumapp.Model.Thread;
import com.example.theforumapp.R;
import com.example.theforumapp.ThreadActivity;

import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    private Context context;
    private List<Thread> threads;


    public ThreadAdapter(Context context, List<Thread> threads) {
        this.context = context;
        this.threads = threads;
    }


        @NonNull
        @Override
    public ThreadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.thread_item, parent, false);

            return new ThreadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Thread thread = threads.get(position);
        int count = position+1;
        holder.count.setText(String.valueOf(count));
        holder.issueTitle.setText(thread.getIssueTitle());
        holder.issueType.setText(thread.getIssueType());
        holder.currentBinary.setText(thread.getBinary());
        holder.osVersion.setText(thread.getOsVersion());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ThreadActivity.class);
                intent.putExtra("threadId", thread.getId());
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return threads.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView count;
        private TextView issueTitle;
        private TextView issueType;
        private TextView osVersion;
        private TextView currentBinary;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.count);
            issueTitle = itemView.findViewById(R.id.issueTitle);
            issueType = itemView.findViewById(R.id.issueType);
            currentBinary = itemView.findViewById(R.id.binaryView);
            osVersion = itemView.findViewById(R.id.osView);


        }
    }
}
