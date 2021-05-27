package com.a403.mars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

public class PlaceActivity extends AppCompatActivity implements OnMapReadyCallback, Overlay.OnClickListener {

    private MapView mapView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    String gps_x_s, gps_y_s;
    double gps_x, gps_y;

    TextView sub_title;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        Intent intent = getIntent();
        gps_x_s = intent.getStringExtra("gps_x");
        gps_y_s = intent.getStringExtra("gps_y");
        gps_x = Double.parseDouble(gps_x_s);
        gps_y = Double.parseDouble(gps_y_s);

        sub_title = (TextView) findViewById(R.id.sub_title);
        sub_title.setVisibility(View.INVISIBLE);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


    }

    //위치정보 권한 설정
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // 지도상에 마커 표시
        Marker marker = new Marker();
        marker.setPosition(new LatLng(gps_x, gps_y));
        marker.setMap(naverMap);

        marker.setWidth(150);
        marker.setHeight(150);
        marker.setIcon(OverlayImage.fromResource(R.drawable.map_whale));
        marker.setOnClickListener(this);

        this.naverMap = naverMap;
        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
//        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true); // 나침반
        uiSettings.setScaleBarEnabled(true); // 거리
        uiSettings.setZoomControlEnabled(true); // 줌
        uiSettings.setLocationButtonEnabled(false); // 내가 있는곳

        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(gps_x, gps_y),   // 위치 지정
                14,  // 줌 레벨
                0,  // 기울임 각도
                0   // 방향
        );
        naverMap.setCameraPosition(cameraPosition);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        return false;
    }
}
