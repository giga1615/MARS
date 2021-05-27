package com.a403.mars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class StoryFragment extends Fragment {

    MainActivity activity;

    // 변수 선언
    String jwt;

    // 그래프
    int year[][] = new int[6][13];

    // 총 개수
    int totalCnt = 0;
    TextView total;

    // d-day 계산
    // Millisecond 형태의 하루(24 시간)
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    // 현재 날짜를 알기 위해 사용
    private java.util.GregorianCalendar mCalendar;


    // 회전
    Animation rotateAnimation;
    ImageView imageView;

    // BarChart
    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> labelsNames;
    ArrayList<MonthDalesData> monthDalesDataArrayList = new ArrayList<>();

    // Spinner
    Spinner spinnerYear;

    // RecyclerView
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    // 캡슐목록 자물쇠 이미지
    ImageView lock;
    ImageView unlock;

    public StoryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_story, container, false);

        // 총 개수
        total = (TextView) rootView.findViewById(R.id.total_capsules);


        // 이미지 회전
        imageView = (ImageView) rootView.findViewById(R.id.front);
        rotateAnimation();

        // Spinner
        spinnerYear = rootView.findViewById(R.id.spinnerYear);
        populateSpinnerYear();

        // 버튼으로 추가하는 법 및 캡슐 목록
        lock = (ImageView) rootView.findViewById(R.id.lock);
        unlock = (ImageView) rootView.findViewById(R.id.unlock);

        // Bar id 찾기
        barChart = rootView.findViewById(R.id.barChart);

        // jwt 다루기
        // 메인 액티비티에서 jwt, 토큰 받아오기 (인텐트 이용)
        Intent intent_story = getActivity().getIntent();
        jwt = intent_story.getStringExtra("jwt");

        // Volley 사용
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Volley 내가만든 캡슐 불러오기
        String finalNew_jwt = jwt;
        String url = "http://k4a403.p.ssafy.io:8000/api/capsule/mylist?jwt=" + finalNew_jwt;

        // Recycler
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        CapsuleStoryAdapter adapter = new CapsuleStoryAdapter();

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
                                    } else if (result == 0) {
                                        strFormat = "D-Day";
                                    } else {
                                        result *= -1;
                                        strFormat = "D + %d";
                                    }
                                    strCount = (String.format(strFormat, result));
                                }

                                String add = capsuleObject.getString("address");
                                String friends = capsuleObject.getString("capsule_friends");
                                double gps_x = capsuleObject.getDouble("gps_x");
                                double gps_y = capsuleObject.getDouble("gps_y");

                                adapter.addItem(new CapsuleStory(title, day, strCount, add, friends, gps_x, gps_y));
                                recyclerView.setAdapter(adapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 그래프 그리기
                        try {
                            year = new int[6][13];
                            JSONArray obj = response.getJSONArray("list");
                            totalCnt = obj.length();
                            total.setText(totalCnt + "");
                            for (int i = 0; i < obj.length(); i++) {
                                JSONObject j = obj.getJSONObject(i);

                                // key값 꺼내기
                                StringTokenizer st = new StringTokenizer(j.getString("created_date"), "-");
                                int y = Integer.parseInt(st.nextToken());
                                int m = Integer.parseInt(st.nextToken());
                                year[y - 2016][m]++;
                            }
                            chartMake("2021");
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

        return rootView;
    }

    // 스피너 선택했을 때 해당년도 그래프 그리기
    private void chartMake(String years) {
        // create new object of bare entries arraylist and labels arraylist
        barEntryArrayList = new ArrayList<>();
        labelsNames = new ArrayList<>();

        fillMonthSales(year[Integer.parseInt(years) - 2016]);   // fillMonthSales 함수 실행

        for (int i = 0; i < monthDalesDataArrayList.size(); i++) {
            String month = monthDalesDataArrayList.get(i).getMonth();
            int sales = monthDalesDataArrayList.get(i).getSales();
            barEntryArrayList.add(new BarEntry(i, sales));
            labelsNames.add(month);
        }


        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Monthly Capsules");
        // 그래프 그라데이션 colors.xml 에서 변경
        int startColor = ContextCompat.getColor(getActivity(), R.color.startColor);
        int endColor = ContextCompat.getColor(getActivity(), R.color.endColor);
        barDataSet.setGradientColor(startColor, endColor);
//        barDataSet.setColors(R.drawable.gradient_chart);
//        barDataSet.setColors(startColor, endColor);

        barDataSet.setDrawValues(false);    // 그래프 상단 값 제거
        barDataSet.setBarBorderWidth(1f);   // 그래프 border

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);  // 그래프 바 두께

        barChart.setData(barData);

        // ve need to set XAxis value formater
        XAxis xAxis = barChart.getXAxis();  // x 축 설정
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsNames));  // 그래프에 "월" 표기
        // set position of labels (month names)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // "월" 그래프 아래로 이동
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(labelsNames.size());    // 1월부터 모두 표기해줌
        xAxis.setLabelRotationAngle(0);   // 월 각도
        xAxis.setTextColor(Color.WHITE);    // x축 텍스트 색

        YAxis yAxisLeft = barChart.getAxisLeft();   // y축 왼쪽 면 설정
        yAxisLeft.setTextColor(Color.WHITE);    // y축 텍스트 색
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0);   // y축 최솟값 0

        // y축 range 조절
        yAxisLeft.setGranularity(1.0f);
        yAxisLeft.setGranularityEnabled(true);

        YAxis yAxisRight = barChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);

        Legend legend = barChart.getLegend();   // 레전드 설정
        legend.setEnabled(false);   // 하단 색깔별 그래프 표기 없애기

        Description description = barChart.getDescription();
        description.setEnabled(false);

        barChart.setBackgroundColor(Color.argb(180, 13, 24, 40));

        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(false);    // 그래프 터치해도 변화 없음
        barChart.animateY(2000);    // 밑에서 부터 올라오는 애니메이션 적용
        barChart.setPinchZoom(false);   // 두 손으로 줌인 줌아웃 효과 제거
        barChart.invalidate();  // 그래프 보여줌
    }

    // 스피너
    private void populateSpinnerYear() {
        // 기본 제공 스피너
//        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.years));
        // 직접 만든 스피너 values/array.xml에 연도 정의했음
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.years));
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(5);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newItem = spinnerYear.getSelectedItem().toString();
                chartMake(newItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // 이미지 회전
    private void rotateAnimation() {

        rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        imageView.startAnimation(rotateAnimation);
    }

    // chart
    // 서버와 연동 되면 조건문으로 sales 만 추가해주면 된다.
    private void fillMonthSales(int y[]) {
        monthDalesDataArrayList.clear();
        monthDalesDataArrayList.add(new MonthDalesData("1월", y[1]));
        monthDalesDataArrayList.add(new MonthDalesData("2월", y[2]));
        monthDalesDataArrayList.add(new MonthDalesData("3월", y[3]));
        monthDalesDataArrayList.add(new MonthDalesData("4월", y[4]));
        monthDalesDataArrayList.add(new MonthDalesData("5월", y[5]));
        monthDalesDataArrayList.add(new MonthDalesData("6월", y[6]));
        monthDalesDataArrayList.add(new MonthDalesData("7월", y[7]));
        monthDalesDataArrayList.add(new MonthDalesData("8월", y[8]));
        monthDalesDataArrayList.add(new MonthDalesData("9월", y[9]));
        monthDalesDataArrayList.add(new MonthDalesData("10월", y[10]));
        monthDalesDataArrayList.add(new MonthDalesData("11월", y[11]));
        monthDalesDataArrayList.add(new MonthDalesData("12월", y[12]));
    }
}