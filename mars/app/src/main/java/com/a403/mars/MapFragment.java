package com.a403.mars;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;




public class MapFragment extends Fragment implements OnMapReadyCallback, Overlay.OnClickListener{

    MainActivity activity;
    String jwt;

    // Double 담는 List 선언
    List<Double> latitude = new ArrayList<>();
    List<Double> longitude = new ArrayList<>();

    // infowindow 사용
    JSONArray arr;


    // d-day 계산
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    // 현재 날짜를 알기 위해 사용
    private java.util.GregorianCalendar mCalendar;

    // 잠금 이미지
    ImageView lock;
    ImageView unlock;

    // infowindow 객체생성
    private InfoWindow infoWindow;

    // GPS 관련
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;


    public MapFragment() { }

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        //
        lock = (ImageView) rootView.findViewById(R.id.lock);
        unlock = (ImageView) rootView.findViewById(R.id.unlock);

        // 지도 객체 생성
        FragmentManager fm = getChildFragmentManager();
        com.naver.maps.map.MapFragment mapFragment = (com.naver.maps.map.MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = com.naver.maps.map.MapFragment.newInstance(); // 내 위치 임의로 넣을 때(마커)
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        // getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this);

        // 위치를 반환하는 구현체인 FusedLocationSource 생성
        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        // json 받아오기
        // 메인 액티비티에서 jwt, 토큰 받아오기 (인텐트 이용)
        Intent intent_map = getActivity().getIntent();
        jwt = intent_map.getStringExtra("jwt");


        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!mLocationSource.isActivated()) {   // 권한 거부됨
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
        );
    }





    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        // 내 위치 추적 가능
        mNaverMap = naverMap;

        naverMap.setLocationSource(mLocationSource);
        naverMap.setIndoorEnabled(true); // 실내지도
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);


        // 지도 세팅
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setScaleBarEnabled(true); // 거리
        uiSettings.setZoomControlEnabled(true); // 줌
        uiSettings.setLocationButtonEnabled(true); // 내 위치

        // Volley 사용
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Volley 내가만든 캡슐 불러오기
        String finalNew_jwt = jwt;
        String url = "http://k4a403.p.ssafy.io:8000/api/capsule/mylist?jwt=" + finalNew_jwt;

        // d-day
        // 현재 날짜를 알기 위해 사용
        mCalendar = new GregorianCalendar();

        GPSTracker mGPS = new GPSTracker(getActivity());


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray capsuleArray = response.getJSONArray("list");
                            arr = capsuleArray;
                            latitude.clear();
                            longitude.clear();

                            // gps 정보로 마커 찍기
                            for (int i=0; i<capsuleArray.length(); i++)
                            {
                                JSONObject capsuleObject = capsuleArray.getJSONObject(i);

                                Double sLAT = capsuleObject.getDouble("gps_x");
                                Double sLNG = capsuleObject.getDouble("gps_y");

                                // 위도 경도 담기
                                latitude.add(sLAT);
                                longitude.add(sLNG);
                            }

                            if (latitude.size() > 0) {
//                                CameraPosition cameraPosition = new CameraPosition(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), 10);
//                                mNaverMap.setCameraPosition(cameraPosition);

                                for (int i = 0; i < latitude.size(); i++) {
                                    // 마커 찍기
                                    Marker marker = new Marker();

                                    try {
                                        JSONObject jo = arr.getJSONObject(i);
                                        // Tag 설정

                                        marker.setTag(jo);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    marker.setPosition(new LatLng(latitude.get(i), longitude.get(i)));
                                    marker.setMap(naverMap);

                                    // 마커 꾸미기
                                    marker.setWidth(150);
                                    marker.setHeight(150);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.map_whale));

                                     // 마커 클릭
                                    marker.setOnClickListener(activity.mapFragment);
                                }

                                // 클릭했을 때 infowindow 설정
                                infoWindow = new InfoWindow();
                                infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(getActivity()) {
                                    @NonNull
                                    @Override
                                    protected View getContentView(@NonNull InfoWindow infoWindow) {
                                        Marker marker = infoWindow.getMarker();
                                        JSONObject jo = (JSONObject) marker.getTag();

                                        View view = View.inflate(MapFragment.this.getContext(), R.layout.view_info_window, null);

                                        try {
                                            ((TextView) view.findViewById(R.id.title)).setText(jo.getString("title"));
                                            // 생성날짜 년/월/일
                                            String date = jo.getString("created_date");
                                            date = date.substring(0, 10);
                                            ((TextView) view.findViewById(R.id.create_date)).setText(date);
                                            ((TextView) view.findViewById(R.id.address)).setText(jo.getString("address"));
                                            ((TextView) view.findViewById(R.id.capsule_friends)).setText(jo.getString("capsule_friends"));

                                            // d-day 계산
                                            String open = jo.getString("open_date");

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
                                            }   else if (result == 0) {
                                                strFormat = "D-Day";
                                            }   else {
                                                result *= -1;
                                                strFormat = "D + %d";
                                            }
                                            final String strCount = (String.format(strFormat, result));
                                            ((TextView) view.findViewById(R.id.open_date)).setText(strCount);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return view;
                                    }
                                });
                            }
                            else {
//                                CameraPosition cameraPosition = new CameraPosition(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), 10);
//                                mNaverMap.setCameraPosition(cameraPosition);
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

    }

    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if (overlay instanceof Marker) {
            Marker marker = (Marker) overlay;
            if (marker.getInfoWindow() != null) {
                infoWindow.close();
            } else {
                infoWindow.open(marker);
            }
            return true;
        }
        return false;
    }
}
