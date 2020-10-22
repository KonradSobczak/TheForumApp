package com.example.theforumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.theforumapp.Adapter.ArchievedThreadAdapter;
import com.example.theforumapp.Adapter.ThreadAdapter;
import com.example.theforumapp.Model.ArchievedThread;
import com.example.theforumapp.Model.Thread;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ArchievedThreadListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArchievedThreadAdapter threadAdapter;
    private List<ArchievedThread> archievedThreads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archieved_thread_list);
        recyclerView = findViewById(R.id.recycler_view_threads);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        readThreads();
    }

    private void readThreads() {
        archievedThreads = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Archieved Threads");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                archievedThreads.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final ArchievedThread thread = snapshot.getValue(ArchievedThread.class);
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        archievedThreads.add(thread);

                }


                threadAdapter = new ArchievedThreadAdapter(ArchievedThreadListActivity.this, archievedThreads);
                recyclerView.setAdapter(threadAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
