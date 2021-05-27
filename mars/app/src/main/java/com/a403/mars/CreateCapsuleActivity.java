package com.a403.mars;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.a403.mars.location.LocationMarker;
import com.a403.mars.location.LocationScene;
import com.a403.mars.model.CapsuleDto;
import com.a403.mars.model.Friend;
import com.a403.mars.retrofit.ApiService;
import com.a403.mars.retrofit.FileUtils;
import com.a403.mars.retrofit.InternetConnection;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.daimajia.swipe.SwipeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateCapsuleActivity extends AppCompatActivity {
    private static final String TAG = CreateCapsuleActivity.class.getSimpleName();

    String selectedImagePath;

    // 스와이프(갤러리에서 사진 추가 버튼)
    private SwipeLayout swipe_add_img;
    ImageView back_btn;

    // 이미지 뷰 페이저
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    private String[] images;
    private String[] viewPager_images;
    int img_size = 0;   // 선택된 이미지 개수
    final int PICTURE_REQUEST_CODE = 100;
    View viewFadingEdge;

    TextInputEditText capsule_open_date;
    TextInputEditText capsule_tag_friend;
    TextInputEditText capsule_title;
    TextInputEditText capsule_where;
    TextInputEditText capsule_story;
    TextInputEditText capsule_music;

    // 친구 태그
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Friend> friends_list = new ArrayList<>();
    SpinnerDialog spinnerDialog;
    ImageView clear_btn;

    // 저장버튼
    RelativeLayout save_btn;
    GPSTracker mGPS;
    String selected_friends;

    String jwt_;
    private ArrayList<Uri> arrayList;
    private final int REQUEST_CODE_PERMISSIONS = 1;
    private final int REQUEST_CODE_READ_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_capsule);

        arrayList = new ArrayList<>();
        mGPS = new GPSTracker(this);
        selected_friends = ",";
        images = new String[100];
        viewFadingEdge = (View) findViewById(R.id.viewFadingEdge);
        capsule_title = (TextInputEditText) findViewById(R.id.capsule_title);
        capsule_where = (TextInputEditText) findViewById(R.id.capsule_where);
        capsule_story = (TextInputEditText) findViewById(R.id.capsule_story);
        capsule_music = (TextInputEditText) findViewById(R.id.capsule_music);

        capsule_open_date = (TextInputEditText) findViewById(R.id.capsule_open_date);
        capsule_open_date.setEnabled(true);
        capsule_open_date.setTextIsSelectable(true);
        capsule_open_date.setFocusable(false);
        capsule_open_date.setFocusableInTouchMode(false);
        capsule_tag_friend = (TextInputEditText) findViewById(R.id.capsule_tag_friend);
        capsule_tag_friend.setEnabled(true);
        capsule_tag_friend.setTextIsSelectable(true);
        capsule_tag_friend.setFocusable(false);
        capsule_tag_friend.setFocusableInTouchMode(false);

        // 데이터 수신
        Intent getIntent = getIntent();
        boolean isCapture = getIntent.getExtras().getBoolean("isSkip");
        jwt_ = getIntent.getExtras().getString("jwt");

        if (!isCapture) {
            selectedImagePath = getIntent.getExtras().getString("imageURI");
            images[img_size++] = selectedImagePath;
            viewFadingEdge.setVisibility(View.VISIBLE);
        }

        clear_btn = (ImageView) findViewById(R.id.clear_btn);
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capsule_tag_friend.setText("");
                selected_friends = ",";
                clear_btn.setVisibility(View.INVISIBLE);
            }
        });

        // Volley 사용
        RequestQueue queue = Volley.newRequestQueue(this);

        // Volley 내가만든 캡슐 불러오기
        String url = "http://k4a403.p.ssafy.io:8000/friend/read?jwt=" + jwt_;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String msg = response.getString("message");
                            if (msg.equals("SUCCESS")) {
                                JSONArray friendArray = response.getJSONArray("friendlist");
                                for (int k = 0; k < friendArray.length(); k++) {
                                    JSONObject friendObject = friendArray.getJSONObject(k);
                                    int no = friendObject.getInt("no");
                                    String myname = friendObject.getString("myname");
                                    String yourname = friendObject.getString("yourname");
                                    String myid = friendObject.getString("myid");
                                    String yourid = friendObject.getString("yourid");
                                    String profileimage = friendObject.getString("profileimage");
                                    friends_list.add(new Friend(no, myname, yourname, myid, yourid, profileimage)); // 프렌드리스트
                                    items.add(yourname); // 스피너
                                }
                            }
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

        spinnerDialog = new SpinnerDialog(CreateCapsuleActivity.this, items,
                "Select in FriendsList");

        spinnerDialog.setTitleColor(getResources().getColor(R.color.mars_navy));
        spinnerDialog.setSearchIconColor(getResources().getColor(R.color.grey));
        spinnerDialog.setSearchTextColor(getResources().getColor(R.color.purple_200));
        spinnerDialog.setItemColor(getResources().getColor(R.color.deep_grey));
        spinnerDialog.setItemDividerColor(getResources().getColor(R.color.grey));
        spinnerDialog.setCloseColor(getResources().getColor(R.color.mars_navy_l));

        spinnerDialog.setCancellable(true);
        spinnerDialog.setShowKeyboard(false);

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
//                Toast.makeText(CreateCapsuleActivity.this, item + "  " + position + "", Toast.LENGTH_SHORT).show();
                if (capsule_tag_friend.getText().toString().compareTo("") == 0) {
                    capsule_tag_friend.setText(item);
                    selected_friends += friends_list.get(position).getYourid();
                } else {
                    capsule_tag_friend.setText(capsule_tag_friend.getText().toString() + "," + item);
                    selected_friends += "," + friends_list.get(position).getYourid();
                }

                clear_btn.setVisibility(View.VISIBLE);
            }
        });

        capsule_tag_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

        // 뒤로가기 버튼
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateCapsuleActivity.this, DrawActivity.class);
                intent.putExtra("jwt", jwt_);
                startActivity(intent);
                finish();
            }
        });

        swipe_add_img = (SwipeLayout) findViewById(R.id.swipe_sample1);
        swipe_add_img.setShowMode(SwipeLayout.ShowMode.LayDown);

        // 오른쪽에서 나오는 drag (tag로 설정한 HideTag가 보여짐
        swipe_add_img.addDrag(SwipeLayout.DragEdge.Right, swipe_add_img.findViewWithTag("HideTag"));
        swipe_add_img.addView(swipe_add_img.findViewWithTag("HideTag"));

        // swipe_layout을 클릭한 경우
        swipe_add_img.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CreateCapsuleActivity.this, "Click on surface", Toast.LENGTH_SHORT).show();
            }
        });

        // add_img_btn버튼을 클릭한 경우
        swipe_add_img.findViewById(R.id.add_img_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/* video/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST_CODE);
            }
        });

        // 이미지 슬라이드
        // https://android-dev.tistory.com/12
        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        viewPager_images = new String[img_size];
        System.arraycopy(images, 0, viewPager_images, 0, img_size);

        sliderViewPager.setOffscreenPageLimit(1);
        sliderViewPager.setAdapter(new ImageSliderAdapter(this, viewPager_images));

        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        setupIndicators(viewPager_images.length);

        // 저장버튼
        save_btn = (RelativeLayout) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 유효성 검사
                boolean isPossible = true;
                if (capsule_title.getText().toString().equals("")) {
                    isPossible = false;
                    showDialog("제목");
                }
                if (capsule_where.getText().toString().equals("")) {
                    isPossible = false;
                    showDialog("장소");
                }
                if (capsule_open_date.getText().toString().equals("")) {
                    isPossible = false;
                    showDialog("캡슐 오픈일");
                }

                if (isPossible) {
                    int img_num = viewPager_images.length;
                    for (int i = 0; i < img_num; i++) {
                        Uri img_uri = Uri.parse(viewPager_images[i]);
                        arrayList.add(img_uri);
                    }
                    uploadImagesToServer();
                }
            }
        });
    }

    private void uploadImagesToServer() {
        if (InternetConnection.checkConnection(CreateCapsuleActivity.this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            showProgress();

            // create list of file files (photo, video, ...)
            MultipartBody.Part[] files = new MultipartBody.Part[viewPager_images.length];

            // create upload service client
            ApiService service = retrofit.create(ApiService.class);

            if (arrayList != null) {
                // create part for file (photo, video, ...)
                for (int i = 0; i < arrayList.size(); i++) {
                    files[i] = prepareFilePart("image" + i, arrayList.get(i));
                }
            }

            // JWT 디코딩하여, 나의 이메일 같이 보내기
            JWT jwt_decode = new JWT(jwt_);
            Claim claim_email = jwt_decode.getClaim("email");
            String my_email = claim_email.asString();

            // create a map of data to pass along
            RequestBody jwt = createPartFromString(jwt_);
            RequestBody title = createPartFromString(capsule_title.getText().toString());
            RequestBody music_title = createPartFromString(capsule_music.getText().toString());
            RequestBody memo = createPartFromString(capsule_story.getText().toString());
            RequestBody gps_x = createPartFromString(Double.toString(mGPS.getLatitude()));
            RequestBody gps_y = createPartFromString(Double.toString(mGPS.getLongitude()));
            RequestBody open_date = createPartFromString(capsule_open_date.getText().toString());
            RequestBody address = createPartFromString(capsule_where.getText().toString());
            RequestBody capsule_friends;
            if (selected_friends.compareTo(",") == 0) {
                capsule_friends = createPartFromString(my_email);
            } else {
                capsule_friends = createPartFromString(my_email + selected_friends);
            }

            // finally, execute the request
            Call<ResponseBody> call = service.uploadMultiple(jwt, title, music_title, memo, gps_x, gps_y, open_date, address, capsule_friends, files);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    hideProgress();
                    try {
                        Log.d("TEST : >>>>>>> ", response.body().string());
                        if (response.isSuccessful()) {
                            Toast.makeText(CreateCapsuleActivity.this, "캡슐 생성 완료!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateCapsuleActivity.this, ArActivity.class);
                            intent.putExtra("jwt", jwt_);
                            startActivity(intent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    Log.e(TAG, "Image upload failed!", t);
                    Snackbar.make(findViewById(android.R.id.content),
                            "캡슐 생성 실패!", Snackbar.LENGTH_LONG).show();
                }
            });

        } else {
            hideProgress();
            Toast.makeText(CreateCapsuleActivity.this,
                    R.string.string_internet_connection_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress() {
        save_btn.setEnabled(false);
    }

    private void hideProgress() {
        save_btn.setEnabled(true);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());
        if (!fileUri.toString().contains("storage")) {
            // use the FileUtils to get the actual file by uri
            file = FileUtils.getFile(this, fileUri);
        }

        Log.d("파일테스트", file.toString());

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("files", file.getName(), requestFile);
    }

    /**
     * Runtime Permission
     */
    private void askForPermission() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                != PackageManager.PERMISSION_GRANTED) {
            /* Ask for permission */
            // need to request permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please grant permissions to write data in sdcard",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        v -> ActivityCompat.requestPermissions(CreateCapsuleActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(CreateCapsuleActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted

            } else {
                // Permission Denied
                Toast.makeText(CreateCapsuleActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateCapsuleActivity.this);
        final AlertDialog dialog = builder.setMessage("You need to grant access to Read External Storage")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(CreateCapsuleActivity.this, android.R.color.holo_blue_light));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(CreateCapsuleActivity.this, android.R.color.holo_red_light));
        });

        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //ClipData 또는 Uri를 가져온다
                Uri uri = data.getData();
                ClipData clipData = data.getClipData();

                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        images[img_size++] = clipData.getItemAt(i).getUri().toString();
                    }
                }
            }
            viewPager_images = new String[img_size];
            System.arraycopy(images, 0, viewPager_images, 0, img_size);

            sliderViewPager.setOffscreenPageLimit(1);
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, viewPager_images));
            setupIndicators(viewPager_images.length);
        }
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        if (count != 0) viewFadingEdge.setVisibility(View.VISIBLE);
        else viewFadingEdge.setVisibility(View.INVISIBLE);

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

    // 오픈일 input 클릭메서드
    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // 오픈일 설정
    public void processDatePickerResult(int year, int month, int day) {
        String month_string = getMonth(month + 1);
        String day_string = getMonth(day);
        String year_string = Integer.toString(year);
        String dateMessage = (year_string + "-" + month_string + "-" + day_string);

        capsule_open_date.setText(dateMessage);
    }

    // 날짜 두자리 수로 만들기(월, 일 동일)
    public static String getMonth(int month) {
        if (month > 0 && month < 10)
            return "0" + String.valueOf(month);
        else
            return String.valueOf(month);
    }

    // 다이얼로그
    void showDialog(String msg) {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(CreateCapsuleActivity.this)
                .setTitle(msg)
                .setMessage(msg + "을(를) 채워주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateCapsuleActivity.this, DrawActivity.class);
        intent.putExtra("jwt", jwt_);
        startActivity(intent);
        finish();
    }

}