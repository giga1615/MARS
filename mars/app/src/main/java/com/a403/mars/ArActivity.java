package com.a403.mars;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a403.mars.location.LocationMarker;
import com.a403.mars.location.LocationScene;
import com.a403.mars.model.CapsuleDto;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// AR참고
// https://www.journaldev.com/21479/android-augmented-reality-arcore-example
// https://github.com/silverbullet1472/Amap-GPS-ARLocation
public class ArActivity extends AppCompatActivity {

    private boolean installRequested;
    private boolean hasFinishedLoadingRenderable = false;
    private boolean hasFinishedSetRenderable = false;

    private Snackbar loadingMessageSnackbar = null;
    private ArSceneView arSceneView;
    // Renderables for this example
    private ViewRenderable CapsuleRenderable;   //武汉大学的标识牌
    // Our ARCore-Location scene
    private LocationScene locationScene;
//    private TextView locationText;

    int range = 50; // 캡슐과 나 사이의 거리(=50m)

    // 캡슐 객체 관련 코드
    CapsuleDto capsule;
    public ArrayList<CapsuleDto> capsuleList = null;
    public ArrayList<CapsuleDto> capsuleRangeList = null;
    private ArrayList<ViewRenderable> capsuleRenderableList = new ArrayList<>();
    private ArrayList<CompletableFuture<ViewRenderable>> capsuleLayoutList = new ArrayList<>();
    GPSTracker gpsTracker;

    TextView sub_title;
    ImageView back_btn;

    String jwt;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!Utils.checkIsSupportedDeviceOrFinish(this)) {
            // Not a supported device.
            return;
        }
        Utils.checkPermissions(this);   // 권한 검사

        // jwt 수신
        Intent getIntent = getIntent();
        jwt = getIntent.getExtras().getString("jwt");

        setContentView(R.layout.activity_ar);
        arSceneView = findViewById(R.id.ar_scene_view);
//        locationText = findViewById(R.id.tv_location);
        GPSTracker mGPS = new GPSTracker(this);
        sub_title = (TextView) findViewById(R.id.sub_title);
        sub_title.setVisibility(View.INVISIBLE);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArActivity.this, MainActivity.class);
                intent.putExtra("jwt", jwt);
                intent.putExtra("tab", "map");
                startActivity(intent);
                finish();
            }
        });

        // 캡슐에 임시로 데이터 넣어둠
        capsuleList = new ArrayList<>();

        // Volley 사용
        RequestQueue queue = Volley.newRequestQueue(this);

        // Volley 내가만든 캡슐 불러오기
        String finalNew_jwt = jwt;
        String url = "http://k4a403.p.ssafy.io:8000/api/capsule/mylist?jwt=" + finalNew_jwt;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // recyclerview
                        try {
                            JSONArray capsuleArray = response.getJSONArray("list");

                            for (int k = 0; k < capsuleArray.length(); k++) {
                                JSONObject capsuleObject = capsuleArray.getJSONObject(k);
                                int no = capsuleObject.getInt("no");
                                double gps_x = capsuleObject.getDouble("gps_x");
                                double gps_y = capsuleObject.getDouble("gps_y");

                                capsuleList.add(new CapsuleDto(no, gps_x, gps_y));
                            }
                            capsuleRangeList = new ArrayList<>();   // range내에있는 캡슐만 담기
                            if (capsuleList != null) {
                                capsuleRangeList.clear();
                                // 선택한 반경 안의 캡슐만 list에 add
                                for (int j = 0; j < capsuleList.size(); j++) {
                                    capsule = capsuleList.get(j);
                                    if (getDistance(mGPS.getLatitude(), mGPS.getLongitude(), capsule.getGps_x(), capsule.getGps_y()) < range) {
                                        capsuleRangeList.add(capsule);
                                    }
                                }
                                Log.d("캡슐 수", capsuleRangeList.size() + "");
                            }

                            // 캡슐 레이아웃 설정 from a 2D View.
                            if (capsuleRangeList.size() != 0) {
                                // 반경 안에 있는 캡슐 개수 만큼 capsuleLayoutList 생성
                                for (int i = 0; i < capsuleRangeList.size(); i++) {
                                    capsuleLayoutList.add(ViewRenderable.builder().setView(getApplication(), R.layout.card).build());
                                }

                                CompletableFuture<ViewRenderable> objCapsule = new CompletableFuture<>();
                                for (int i = 0; i < capsuleRangeList.size(); i++) {
                                    objCapsule = capsuleLayoutList.get(i);
                                }

                                CompletableFuture.allOf(
                                        objCapsule)
                                        .handle(
                                                (notUsed, throwable) -> {
                                                    // When you build a Renderable, Sceneform loads its resources in the background while
                                                    // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                                                    // before calling get().
                                                    if (throwable != null) {
                                                        Utils.displayError(getApplication(), "Unable to load renderables", throwable);
                                                        return null;
                                                    }
                                                    try {
                                                        if (capsuleRangeList.size() != 0) {
                                                            for (int i = 0; i < capsuleRangeList.size(); i++) {
                                                                capsuleRenderableList.add(capsuleLayoutList.get(i).get());
                                                            }
                                                        }
                                                        hasFinishedLoadingRenderable = true;

                                                    } catch (InterruptedException | ExecutionException ex) {
                                                        Utils.displayError(getApplication(), "Unable to load renderables", ex);
                                                    }
                                                    return null;
                                                });

                                arSceneView
                                        .getScene()
                                        .addOnUpdateListener(
                                                (FrameTime frameTime) -> {
                                                    // 모형을 로딩한 후에 다시 다음 작업을 진행
                                                    if (!hasFinishedLoadingRenderable) {
                                                        return;
                                                    }
                                                    // 상속받은 LocationScene 개체 새로 만들기
                                                    if (locationScene == null) {
                                                        locationScene = new LocationScene(getApplication(), getParent(), arSceneView);
                                                        locationScene.setOffsetOverlapping(false);  // 겹치는 모형에 이동량을 더할 지 설정

                                                        Toast.makeText(getApplicationContext(), "내 주변에 캡슐이 " + capsuleRangeList.size() + "개 있습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                    // ArFrame 가져오기
                                                    Frame frame = arSceneView.getArFrame();
                                                    if (frame == null) {
                                                        return;
                                                    }
                                                    // Frame이 추적 중일 때 다시 시작
                                                    if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                                        return;
                                                    }
                                                    // locationScene이 비어 있지 않고 모형이 설치되어 있지 않다면
                                                    if (locationScene != null && !hasFinishedSetRenderable) {
                                                        LocationMarker[] locationMarker = new LocationMarker[100];

                                                        try {
                                                            Thread.sleep(13);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }

                                                        for (int i = 0; i < capsuleRangeList.size(); i++) {
                                                            // node 객체 생성
                                                            Node base = new Node();

                                                            base.setRenderable(capsuleRenderableList.get(i));
                                                            base.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));

                                                            Context c = getBaseContext();
                                                            // 캡슐 클릭 -> 상세보기 페이지로 이동
                                                            View eView = capsuleRenderableList.get(i).getView();
                                                            int capsule_no = capsuleRangeList.get(i).getNo();
                                                            eView.setOnTouchListener((v, event) -> {
                                                                Intent intent = new Intent(ArActivity.this, CapsuleDetailActivity.class);
                                                                Log.d("캡슐번호", capsule_no + "");
                                                                intent.putExtra("jwt", jwt);
                                                                intent.putExtra("no", capsule_no);
                                                                startActivity(intent);
                                                                finish();
                                                                return false;
                                                            });

                                                            locationMarker[i] = new LocationMarker(
                                                                    capsuleRangeList.get(i).getGps_y(), capsuleRangeList.get(i).getGps_x(), base);

                                                            locationScene.mLocationMarkers.add(locationMarker[i]);
                                                            // 캡슐 배치 완료
                                                            hasFinishedSetRenderable = true;
                                                        }
                                                    }

                                                    if (locationScene != null) {
                                                        locationScene.processFrame(frame);
                                                        if (locationScene.locationManager.currentLocation != null) {
//                                        String deviceInfo = "WGS Longitude:" + this.locationScene.locationManager.currentLocation.getLongitude() + "\n"
//                                                + "WGS Latitude:" + this.locationScene.locationManager.currentLocation.getLatitude() + "\n"
//                                                + "AMap Longitude:" + this.locationScene.locationManager.currentAmapLocation.getLongitude() + "\n"
//                                                + "AMap Latitude:" + this.locationScene.locationManager.currentAmapLocation.getLatitude() + "\n"
//                                                + "Location Type:" + this.locationScene.locationManager.currentAmapLocation.getLocationType() + "\n"
//                                                + "Accuracy:" + this.locationScene.locationManager.currentAmapLocation.getAccuracy() + "\n"
//                                                + "Address:" + this.locationScene.locationManager.currentAmapLocation.getAddress();
//                                        locationText.setText(deviceInfo);
                                                        }

                                                    }

                                                    if (loadingMessageSnackbar != null) {
                                                        for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                                            if (plane.getTrackingState() == TrackingState.TRACKING) {
//                                      // 트래킹을 할 때 평면을 측정해야 끝이나, 비tracKing 상태에서는 이미 모형을 표시할 수 있음
                                                                hideLoadingMessage();
                                                            }
                                                        }
                                                    }


                                                });


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

        // 캡슐 생성 버튼
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArActivity.this, DrawActivity.class);
                intent.putExtra("jwt", jwt);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Make sure we call locationScene.resume();
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = Utils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = true;
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                Utils.handleSessionException(this, e);
            }
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Utils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            //showLoadingMessage();
        }
    }

    // gps(캡슐과 나 사이) 거리 구하기
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double distance;

        Location locationA = new Location("pointA");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    /**
     * Make sure we call locationScene.pause();
     */
    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == Utils.PERMISSION_REQUESTCODE) {
            if (!Utils.verifyPermissions(paramArrayOfInt)) {
                finish();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ArActivity.this.findViewById(android.R.id.content),
                        "seeking plane!!!",
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ArActivity.this, MainActivity.class);
        intent.putExtra("jwt", jwt);
        intent.putExtra("tab", "map");
        startActivity(intent);
        finish();
    }
}