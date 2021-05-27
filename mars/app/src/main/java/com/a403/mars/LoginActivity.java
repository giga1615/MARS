package com.a403.mars;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    Context context;

    // 1. 변수선언
    Animation wave;
    ImageView imageButton3;
    ImageView imageView8;
    ImageView imageView9;
    static String codeToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        byte[] sha1 = {(byte) 0xC7, (byte) 0x18, (byte) 0x36, (byte) 0xCE, (byte) 0x62, (byte) 0x30, (byte) 0x62, (byte) 0x07, (byte) 0x6C, (byte) 0xD9, (byte) 0xF7, (byte) 0xC4, (byte) 0xBD, (byte) 0x5B, (byte) 0xB5, (byte) 0xCE, (byte) 0x4B, (byte) 0x67, (byte) 0x2D, (byte) 0xF7};
        Log.d("keyhash : ", Base64.encodeToString(sha1, Base64.NO_WRAP));

        context = this;

        // 2. 리소스 할당
        wave = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wave);
        imageButton3 = findViewById(R.id.imageButton3);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);

        // 3. 버튼 누르면 애니매이션 실행
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OAuthToken token;
                Error error;

                UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this,
                        new Function2<OAuthToken, Throwable, Unit>() {
                            @Override
                            public Unit invoke(OAuthToken token, Throwable throwable) {

                                if (token != null) {
                                    Log.i("LoginActivity", "success");

                                    String code = token.getAccessToken();

                                    codeToken = code;

                                    // Firebase FCM 토큰 생성
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.w("FCM Log", "getInstanceId failed", task.getException());
                                                        return;
                                                    }
                                                    // 여기서 생성된 토큰을 로그인 할 때 백으로 넘겨줘야함 !!!
                                                    String token = task.getResult().getToken();

                                                    // 볼리 함수 실행
                                                    volley(codeToken, token);
                                                    Log.d("FCM Log", "FCM 토큰: " + token);
                                                }
                                            });
                                }
                                if (throwable != null) {
                                    Log.i("LoginActivity", "fail");
                                }
                                Log.i("LoginActivity", "shit");
                                return null;
                            }
                        }
                );
            }
        });

    }   // oncreate 끝

    // Volley(로그인)
    public void volley(String codeKakao, String fcmToken1) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://k4a403.p.ssafy.io:8000/api/kakaoLogin";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String jwt = response;

                        // jwt 다루기
                        int idx = jwt.indexOf(":");
                        jwt = jwt.substring(idx + 2);
                        jwt = jwt.replaceAll("\\\"", "");
                        jwt = jwt.replace("}", "");

                        // 메인 엑티비티로 전달
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        intent1.putExtra("jwt", jwt);
                        Log.d("전송JWT", jwt);
                        intent1.putExtra("tab", "map");
                        startActivity(intent1);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "[" + error.getMessage() + "]");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", codeKakao);
                params.put("android_token", fcmToken1);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
}