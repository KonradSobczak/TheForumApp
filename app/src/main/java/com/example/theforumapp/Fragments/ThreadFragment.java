package com.example.theforumapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.theforumapp.Adapter.ThreadAdapter;
import com.example.theforumapp.Adapter.UserAdapter;
import com.example.theforumapp.Model.Thread;
import com.example.theforumapp.Model.User;
import com.example.theforumapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ThreadFragment extends Fragment {

    private RecyclerView recyclerView;
    private ThreadAdapter threadAdapter;
    private List<Thread> threads;

    public ThreadFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_threads);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        readThreads();

        return view;
    }

    private void readThreads() {
        threads = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Threads");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                threads.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Thread thread = snapshot.getValue(Thread.class);
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = firebaseUser.getUid();
                    if (thread.getCustomerId().equals(userId) || thread.getEngineerId().equals(userId)) {
                        threads.add(thread);
                    }
                }


                threadAdapter = new ThreadAdapter(getContext(), threads);
                recyclerView.setAdapter(threadAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
