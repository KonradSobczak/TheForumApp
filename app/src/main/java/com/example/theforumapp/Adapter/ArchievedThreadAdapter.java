package com.example.theforumapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theforumapp.Model.ArchievedThread;
import com.example.theforumapp.Model.Thread;
import com.example.theforumapp.R;
import com.example.theforumapp.ThreadActivity;

import java.util.List;

public class ArchievedThreadAdapter extends RecyclerView.Adapter<ArchievedThreadAdapter.ViewHolder>{



    private Context context;
    private List<ArchievedThread> threads;

    public ArchievedThreadAdapter(Context context, List<ArchievedThread> threads) {
        this.context = context;
        this.threads = threads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.archieved_thread_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ArchievedThread archievedThread = threads.get(position);
        int count = position+1;
        holder.count.setText(String.valueOf(count));
        holder.issueTitle.setText(archievedThread.getIssueTitle());
        holder.issueType.setText((archievedThread.getIssueType()));
        holder.currentBinary.setText((archievedThread.getBinary()));
        holder.osVersion.setText((archievedThread.getOsVersion()));
        holder.customerView.setText(archievedThread.getCustomerName());
        holder.engineerView.setText(archievedThread.getEngineerName());
        holder.resolutionView.setText(archievedThread.getResolution());


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
        private TextView customerView;
        private TextView engineerView;
        private TextView resolutionView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.count);
            issueTitle = itemView.findViewById(R.id.issueTitle);
            issueType = itemView.findViewById(R.id.issueType);
            currentBinary = itemView.findViewById(R.id.binaryView);
            osVersion = itemView.findViewById(R.id.osView);
            customerView = itemView.findViewById(R.id.customerNameView);
            engineerView = itemView.findViewById(R.id.engineerNameView);
            resolutionView = itemView.findViewById(R.id.resolutionView);


        }
    }

}
