package com.a403.mars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

public class FriendsDetailActivity extends AppCompatActivity {

    // 현재 날짜를 알기 위해 사용
    private java.util.GregorianCalendar mCalendar;

    // d-day 계산
    // Millisecond 형태의 하루(24 시간)
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    TextView sub_title;
    ImageView back_btn;

    TextView kakaoNickName;
    TextView kakaoEmail;
    ImageView kakaoImage;
    Bitmap bitmap;

    String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);

        sub_title = (TextView) findViewById(R.id.sub_title);
        sub_title.setText("Shared Capsule");
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        kakaoNickName = (TextView) findViewById(R.id.kakaoNickName);
        kakaoEmail = (TextView) findViewById(R.id.kakaoEmail);
        kakaoImage = (ImageView) findViewById(R.id.profileImage);


        // FriendsFragment에서 데이터 받아오기
        Intent intent = getIntent();
        jwt = intent.getStringExtra("jwt");
        String nickName = intent.getStringExtra("yourName");
        String email = intent.getStringExtra("yourId");
        String image = intent.getStringExtra("profileImage");
        String yourIdFull = intent.getStringExtra("yourIdFull");

        // FriendsFragment에서 클릭 이벤트 했을 때 받아온 데이터
        kakaoNickName.setText(nickName);
        kakaoEmail.setText(email);
        Glide.with(this).load(image).into(kakaoImage);

        // Volley 사용
        RequestQueue queue = Volley.newRequestQueue(this);

        // Volley 내가만든 캡슐 불러오기
        String finalNew_jwt = jwt;
        String url = "http://k4a403.p.ssafy.io:8000/api/capsule/sharedlist?jwt=" + finalNew_jwt + "&yourid=" + yourIdFull;

        // Recycler
        RecyclerView recyclerView = findViewById(R.id.rv_main2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        CapsuleFriendAdapter adapter = new CapsuleFriendAdapter();

        // d-day
        // 현재 날짜를 알기 위해 사용
        mCalendar = new GregorianCalendar();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // recyclerview
                        try {
                            JSONArray capsuleArray = response.getJSONArray("list");

                            for (int i = 0; i < capsuleArray.length(); i++) {
                                JSONObject capsuleObject = capsuleArray.getJSONObject(i);
                                // 날짜 범위 조정
                                String title = capsuleObject.getString("title");
                                String day = capsuleObject.getString("created_date");
                                day = day.substring(0, 10);

                                // d-day 계산
                                String open = capsuleObject.getString("open_date");

                                StringTokenizer st1 = new StringTokenizer(open, "-");
                                int a_year = Integer.parseInt(st1.nextToken());
                                int a_monthOfYear = Integer.parseInt(st1.nextToken());
                                int a_dayOfMonth = Integer.parseInt(st1.nextToken().trim());

                                Calendar ddayCalendar = Calendar.getInstance();

                                a_monthOfYear -= 1;
                                ddayCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

                                final long dday = ddayCalendar.getTimeInMillis() / ONE_DAY;

                                final long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;

                                long result = dday - today;

                                final String strFormat;
                                if (result > 0) {
                                    strFormat = "D - %d";
                                } else if (result == 0) {
                                    strFormat = "D-Day";
                                } else {
                                    result *= -1;
                                    strFormat = "D + %d";
                                }
                                final String strCount = (String.format(strFormat, result));

                                String add = capsuleObject.getString("address");
                                String friends = capsuleObject.getString("capsule_friends");
                                double gps_x = capsuleObject.getDouble("gps_x");
                                double gps_y = capsuleObject.getDouble("gps_y");

                                adapter.addItem(new CapsuleStory(title, day, strCount, add, friends, gps_x, gps_y, jwt));
                                recyclerView.setAdapter(adapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );
        queue.add(jsonRequest);

    } // oncreate 마지막

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FriendsDetailActivity.this, MainActivity.class);
        intent.putExtra("jwt", jwt);
        intent.putExtra("tab", "friend");
        startActivity(intent);
        finish();
    }
}