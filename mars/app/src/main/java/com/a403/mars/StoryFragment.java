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

    // ?????? ??????
    String jwt;

    // ?????????
    int year[][] = new int[6][13];

    // ??? ??????
    int totalCnt = 0;
    TextView total;

    // d-day ??????
    // Millisecond ????????? ??????(24 ??????)
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    // ?????? ????????? ?????? ?????? ??????
    private java.util.GregorianCalendar mCalendar;


    // ??????
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

    // ???????????? ????????? ?????????
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

        // ??? ??????
        total = (TextView) rootView.findViewById(R.id.total_capsules);


        // ????????? ??????
        imageView = (ImageView) rootView.findViewById(R.id.front);
        rotateAnimation();

        // Spinner
        spinnerYear = rootView.findViewById(R.id.spinnerYear);
        populateSpinnerYear();

        // ???????????? ???????????? ??? ??? ?????? ??????
        lock = (ImageView) rootView.findViewById(R.id.lock);
        unlock = (ImageView) rootView.findViewById(R.id.unlock);

        // Bar id ??????
        barChart = rootView.findViewById(R.id.barChart);

        // jwt ?????????
        // ?????? ?????????????????? jwt, ?????? ???????????? (????????? ??????)
        Intent intent_story = getActivity().getIntent();
        jwt = intent_story.getStringExtra("jwt");

        // Volley ??????
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Volley ???????????? ?????? ????????????
        String finalNew_jwt = jwt;
        String url = "http://k4a403.p.ssafy.io:8000/api/capsule/mylist?jwt=" + finalNew_jwt;

        // Recycler
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        CapsuleStoryAdapter adapter = new CapsuleStoryAdapter();

        // d-day
        // ?????? ????????? ?????? ?????? ??????
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
                                // ?????? ?????? ??????
                                String title = capsuleObject.getString("title");
                                String day = capsuleObject.getString("created_date");
                                day = day.substring(0, 10);

                                // d-day ??????
                                String open = capsuleObject.getString("open_date");
                                String strCount = "";

                                if (open.compareTo("null") != 0) {
                                    Log.d("???.....????????? ???", open);
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

                        // ????????? ?????????
                        try {
                            year = new int[6][13];
                            JSONArray obj = response.getJSONArray("list");
                            totalCnt = obj.length();
                            total.setText(totalCnt + "");
                            for (int i = 0; i < obj.length(); i++) {
                                JSONObject j = obj.getJSONObject(i);

                                // key??? ?????????
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

    // ????????? ???????????? ??? ???????????? ????????? ?????????
    private void chartMake(String years) {
        // create new object of bare entries arraylist and labels arraylist
        barEntryArrayList = new ArrayList<>();
        labelsNames = new ArrayList<>();

        fillMonthSales(year[Integer.parseInt(years) - 2016]);   // fillMonthSales ?????? ??????

        for (int i = 0; i < monthDalesDataArrayList.size(); i++) {
            String month = monthDalesDataArrayList.get(i).getMonth();
            int sales = monthDalesDataArrayList.get(i).getSales();
            barEntryArrayList.add(new BarEntry(i, sales));
            labelsNames.add(month);
        }


        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Monthly Capsules");
        // ????????? ??????????????? colors.xml ?????? ??????
        int startColor = ContextCompat.getColor(getActivity(), R.color.startColor);
        int endColor = ContextCompat.getColor(getActivity(), R.color.endColor);
        barDataSet.setGradientColor(startColor, endColor);
//        barDataSet.setColors(R.drawable.gradient_chart);
//        barDataSet.setColors(startColor, endColor);

        barDataSet.setDrawValues(false);    // ????????? ?????? ??? ??????
        barDataSet.setBarBorderWidth(1f);   // ????????? border

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);  // ????????? ??? ??????

        barChart.setData(barData);

        // ve need to set XAxis value formater
        XAxis xAxis = barChart.getXAxis();  // x ??? ??????
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsNames));  // ???????????? "???" ??????
        // set position of labels (month names)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // "???" ????????? ????????? ??????
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(labelsNames.size());    // 1????????? ?????? ????????????
        xAxis.setLabelRotationAngle(0);   // ??? ??????
        xAxis.setTextColor(Color.WHITE);    // x??? ????????? ???

        YAxis yAxisLeft = barChart.getAxisLeft();   // y??? ?????? ??? ??????
        yAxisLeft.setTextColor(Color.WHITE);    // y??? ????????? ???
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0);   // y??? ????????? 0

        // y??? range ??????
        yAxisLeft.setGranularity(1.0f);
        yAxisLeft.setGranularityEnabled(true);

        YAxis yAxisRight = barChart.getAxisRight(); //Y?????? ???????????? ??????
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);

        Legend legend = barChart.getLegend();   // ????????? ??????
        legend.setEnabled(false);   // ?????? ????????? ????????? ?????? ?????????

        Description description = barChart.getDescription();
        description.setEnabled(false);

        barChart.setBackgroundColor(Color.argb(180, 13, 24, 40));

        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(false);    // ????????? ???????????? ?????? ??????
        barChart.animateY(2000);    // ????????? ?????? ???????????? ??????????????? ??????
        barChart.setPinchZoom(false);   // ??? ????????? ?????? ????????? ?????? ??????
        barChart.invalidate();  // ????????? ?????????
    }

    // ?????????
    private void populateSpinnerYear() {
        // ?????? ?????? ?????????
//        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.years));
        // ?????? ?????? ????????? values/array.xml??? ?????? ????????????
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

    // ????????? ??????
    private void rotateAnimation() {

        rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        imageView.startAnimation(rotateAnimation);
    }

    // chart
    // ????????? ?????? ?????? ??????????????? sales ??? ??????????????? ??????.
    private void fillMonthSales(int y[]) {
        monthDalesDataArrayList.clear();
        monthDalesDataArrayList.add(new MonthDalesData("1???", y[1]));
        monthDalesDataArrayList.add(new MonthDalesData("2???", y[2]));
        monthDalesDataArrayList.add(new MonthDalesData("3???", y[3]));
        monthDalesDataArrayList.add(new MonthDalesData("4???", y[4]));
        monthDalesDataArrayList.add(new MonthDalesData("5???", y[5]));
        monthDalesDataArrayList.add(new MonthDalesData("6???", y[6]));
        monthDalesDataArrayList.add(new MonthDalesData("7???", y[7]));
        monthDalesDataArrayList.add(new MonthDalesData("8???", y[8]));
        monthDalesDataArrayList.add(new MonthDalesData("9???", y[9]));
        monthDalesDataArrayList.add(new MonthDalesData("10???", y[10]));
        monthDalesDataArrayList.add(new MonthDalesData("11???", y[11]));
        monthDalesDataArrayList.add(new MonthDalesData("12???", y[12]));
    }
}