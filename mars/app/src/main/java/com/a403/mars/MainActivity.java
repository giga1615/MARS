package com.a403.mars;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    MapFragment mapFragment;
    FriendsFragment friendsFragment;
    StoryFragment storyFragment;

    String jwt, tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mapFragment = new MapFragment();
        friendsFragment = new FriendsFragment();
        storyFragment = new StoryFragment();

        // jwt 수신
        Intent getIntent = getIntent();
        jwt = getIntent.getExtras().getString("jwt");
        tab = getIntent.getExtras().getString("tab");

        switch (tab) {
            case "map":
                getSupportFragmentManager().beginTransaction().replace(R.id.addFragmentLayout, mapFragment, mapFragment.getTag()).commit();
                bottomNavigationView.setSelectedItemId(R.id.navigation_map);
                break;
            case "friend":
                getSupportFragmentManager().beginTransaction().replace(R.id.addFragmentLayout, friendsFragment, friendsFragment.getTag()).commit();
                bottomNavigationView.setSelectedItemId(R.id.navigation_friends);
                break;
            case "story":
                getSupportFragmentManager().beginTransaction().replace(R.id.addFragmentLayout, storyFragment, storyFragment.getTag()).commit();
                bottomNavigationView.setSelectedItemId(R.id.navigation_story);
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // 로그인 엑티비티에서 넘어온 것
            switch (item.getItemId()) {
                case R.id.navigation_map:
//                    Toast.makeText(getApplicationContext(), "MAP", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.addFragmentLayout, mapFragment, mapFragment.getTag()).commit();
                    Intent intent_map = new Intent(getApplicationContext(), MapFragment.class);
                    intent_map.putExtra("jwt", jwt);
                    return true;

                case R.id.navigation_ar:
//                    Toast.makeText(getApplicationContext(), "AR", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ArActivity.class);
                    intent.putExtra("jwt", jwt);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.navigation_friends:
//                    Toast.makeText(getApplicationContext(), "FRIENDS", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager2 = getSupportFragmentManager();
                    fragmentManager2.beginTransaction().replace(R.id.addFragmentLayout, friendsFragment, friendsFragment.getTag()).commit();
                    Intent intent_friend = new Intent(getApplicationContext(), FriendsFragment.class);
                    intent_friend.putExtra("jwt", jwt);
                    return true;

                case R.id.navigation_story:
//                    Toast.makeText(getApplicationContext(), "STORY", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager3 = getSupportFragmentManager();
                    fragmentManager3.beginTransaction().replace(R.id.addFragmentLayout, storyFragment, storyFragment.getTag()).commit();

                    // 다시 보내기
                    Intent intent_story = new Intent(getApplicationContext(), StoryFragment.class);
                    intent_story.putExtra("jwt", jwt);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "백버튼누름", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MARS 종료")
                .setMessage("정말 종료하시겠습니까?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}