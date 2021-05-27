package com.a403.mars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.a403.mars.model.Capsule;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mmin18.widget.RealtimeBlurView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

public class CapsuleDetailActivity extends AppCompatActivity {

    TextView sub_title, content, dday_, title, create_date, location, with_friend, music;
    ImageView back_btn;
    RealtimeBlurView blur_view;
    ScrollView scroll_view;

    // 이미지 뷰 페이저
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    // d-day 계산
    // Millisecond 형태의 하루(24 시간)
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    // 현재 날짜를 알기 위해 사용
    private java.util.GregorianCalendar mCalendar;

    private String[] images;
    int no;
    String jwt;

    Capsule capsule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capsule_detail);

        Intent getIntent = getIntent();
        jwt = getIntent.getExtras().getString("jwt");
        no = getIntent.getExtras().getInt("no");
//        Toast.makeText(this, no+"", Toast.LENGTH_SHORT).show();
        capsule = new Capsule();

        sub_title = (TextView) findViewById(R.id.sub_title);
        dday_ = (TextView) findViewById(R.id.dday);
        title = (TextView) findViewById(R.id.title);
        create_date = (TextView) findViewById(R.id.create_date);
        location = (TextView) findViewById(R.id.location);
        with_friend = (TextView) findViewById(R.id.with_friend);
        content = (TextView) findViewById(R.id.content);
        music = (TextView) findViewById(R.id.music);
        blur_view = (RealtimeBlurView) findViewById(R.id.blur_view);
        blur_view.setVisibility(View.INVISIBLE);
        scroll_view = (ScrollView) findViewById(R.id.scroll_view);

        sub_title.setVisibility(View.INVISIBLE);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        // d-day
        // 현재 날짜를 알기 위해 사용
        mCalendar = new GregorianCalendar();

        // Volley 사용
        RequestQueue queue = Volley.newRequestQueue(this);

        // Volley 내가만든 캡슐 불러오기
        String url = "http://k4a403.p.ssafy.io:8000/api/capsule/readOne?jwt=" + jwt + "&no=" + no;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("캡슐", response.toString());

                        try {
                            JSONObject capsuleObject = response.getJSONObject("CAPSULE");
                            capsule.setNo(capsuleObject.getInt("no"));
                            capsule.setId(capsuleObject.getString("id"));
                            capsule.setTitle(capsuleObject.getString("title"));
                            capsule.setMusic_title(capsuleObject.getString("music_title"));
                            capsule.setWrite(capsuleObject.getString("memo"));
                            capsule.setPhoto_url(capsuleObject.getString("photo_url"));
                            capsule.setVoice_url(capsuleObject.getString("voice_url"));
                            capsule.setVideo_url(capsuleObject.getString("video_url"));
                            capsule.setGps_x(capsuleObject.getDouble("gps_x"));
                            capsule.setGps_y(capsuleObject.getDouble("gps_y"));
                            capsule.setCreated_date(capsuleObject.getString("created_date"));
                            capsule.setOpen_date(capsuleObject.getString("open_date"));
                            capsule.setAddress(capsuleObject.getString("address"));
                            capsule.setCapsule_friends(capsuleObject.getString("capsule_friends"));
                            capsule.setCapsule_friends_by_name(capsuleObject.getString("capusle_frineds_by_name"));

                            // 데이터 할당
                            title.setText(capsule.getTitle());
                            StringTokenizer create_day = new StringTokenizer(capsule.getCreated_date(), " ");
                            create_date.setText(create_day.nextToken());
                            location.setText(capsule.getAddress());
                            content.setText(capsule.getWrite());
                            with_friend.setText("@" + capsule.getCapsule_friends_by_name());
                            music.setText(capsule.getMusic_title());

                            // d-day 계산
                            String open = capsuleObject.getString("open_date");
                            String strCount = "";

                            if (open.compareTo("null") != 0) {
                                Log.d("널.....어쩌면 좋", open);
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
                                    blur_view.setVisibility(View.VISIBLE);
                                    scroll_view.stopNestedScroll();
                                } else if (result == 0) {
                                    strFormat = "D-Day";
                                } else {
                                    result *= -1;
                                    strFormat = "D + %d";
                                }
                                strCount = (String.format(strFormat, result));
                                dday_.setText(strCount);
                            }

                            // 이미지 슬라이드
                            // https://android-dev.tistory.com/12
                            StringTokenizer st = new StringTokenizer(capsule.getPhoto_url(), ",");
                            StringTokenizer st1 = new StringTokenizer(capsule.getVideo_url(), ",");
                            int image_num = st.countTokens();
                            int video_num = st1.countTokens();
                            int total_size = image_num + video_num;
                            Log.d("이미지 수 : " + image_num, "비디오 수 : " + video_num);
                            images = new String[total_size];
                            for (int i = 0; i < image_num; i++) {
                                StringBuilder img_url = new StringBuilder(st.nextToken().replaceAll("\\\"", ""));
                                images[i] = img_url.toString();
                                Log.d("이미지" + i, images[i]);
                            }
                            for (int i = image_num; i < video_num; i++) {
                                images[i] = st1.nextToken();
                            }

                            sliderViewPager = findViewById(R.id.sliderViewPager);
                            layoutIndicator = findViewById(R.id.layoutIndicators);

                            sliderViewPager.setOffscreenPageLimit(1);
                            sliderViewPager.setAdapter(new ImageSliderAdapter(getApplication(), images));

                            sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                @Override
                                public void onPageSelected(int position) {
                                    super.onPageSelected(position);
                                    setCurrentIndicator(position);
                                }
                            });

                            setupIndicators(images.length);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );
        queue.add(jsonRequest);
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        layoutIndicator.removeAllViews();
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(CapsuleDetailActivity.this, MainActivity.class);
        intent.putExtra("jwt", jwt);
        intent.putExtra("tab", "map");
        startActivity(intent);
        finish();
    }
}