package com.example.theforumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.theforumapp.Fragments.ChatsFragment;
import com.example.theforumapp.Fragments.MessagesFragment;
import com.example.theforumapp.Fragments.ThreadInfoFragment;
import com.example.theforumapp.Fragments.UsersFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ThreadActivity extends AppCompatActivity {


    private ThreadActivity.ViewPagerAdapter viewPagerAdapter;
    private String threadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        threadId = getIntent().getStringExtra("threadId");
        Bundle bundle = new Bundle();
        bundle.putString("thread", threadId);
        getSupportActionBar().setTitle("");
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
        ThreadInfoFragment threadInfoFragment = new ThreadInfoFragment();
        MessagesFragment messagesFragment = new MessagesFragment();
        threadInfoFragment.setArguments(bundle);
        messagesFragment.setArguments(bundle);
        viewPagerAdapter.addFragments(threadInfoFragment, "Information");
        viewPagerAdapter.addFragments(messagesFragment, "Messages");
        viewPagerAdapter.notifyDataSetChanged();

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
