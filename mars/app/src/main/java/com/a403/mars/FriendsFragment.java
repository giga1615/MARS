package com.a403.mars;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.layout.simple_list_item_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.a403.mars.model.Friend;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import kotlin.jvm.Synchronized;

public class FriendsFragment extends Fragment {

    MainActivity activity;
    GridView listView;
    MyBaseAdapter adapter;
    TextInputEditText friends_search;
    static RequestQueue queue1;
    String[] yourIdFull;
    static String[] yourId;
    static String[] fullId2;
    static String[] yourName;
    static String[] profileImage;

    String jwt_;

    static int list_cnt1 = 0;
    static int list_cnt2 = 0;

    ArrayList<Friend> friends_list = new ArrayList<>();

    String my_email;

    // 친구 태그
    ArrayList<String> allName = new ArrayList<>();
    SpinnerDialog spinnerDialog;

    public FriendsFragment() {
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

    public String[] volleyCycle(ViewGroup rootView, String finalNew_jwt) {

        String[] temp = new String[100];
        volley1(rootView, finalNew_jwt);
//        volley2(rootView ,finalNew_jwt, fullId2);

        return fullId2;


    }

    public void volley1(ViewGroup rootView, String finalNew_jwt) {
        // 어댑터 객체 생성
        Resources res = getResources();

        // Volley 사용(친구목록 받아오기)
        queue1 = Volley.newRequestQueue(getActivity());
        String url = "http://k4a403.p.ssafy.io:8000/friend/read?jwt=" + finalNew_jwt;


        JsonObjectRequest jsonRequest1 = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    public void onResponse(JSONObject response) {
                        // recyclerview
                        try {

                            JSONArray friendsArray = response.getJSONArray("friendlist");

                            list_cnt1 = friendsArray.length();
                            for (int i = 0; i < friendsArray.length(); i++) {
                                JSONObject friendObject = friendsArray.getJSONObject(i);
                                // 키값 꺼내쓰기
                                String myId = friendObject.getString("myid");
                                yourIdFull[i] = friendObject.getString("yourid").toString();
                                StringTokenizer st1 = new StringTokenizer(friendObject.getString("yourid").toString(),
                                        "@");
                                yourId[i] = st1.nextToken();
                                yourName[i] = friendObject.getString("yourname").toString();
                                profileImage[i] = friendObject.getString("profileimage").toString();

                            }

                            //여기
                            fullId2 = yourIdFull;

                            // 항목별 이미재 한 개와, 텍스트 2개를 어댑터에 넣는다.
                            for (int i = 0; i < list_cnt1; i++) {
                                adapter.addItem(new ItemDataBox(profileImage[i], yourName[i], yourId[i]));
                            }

                            // 리스트뷰에 어댑터 설정
                            listView.setAdapter(adapter);
                            volley2(rootView, finalNew_jwt, fullId2);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue1.add(jsonRequest1);


    }

    public void volley2(ViewGroup rootView, String finalNew_jwt, String[] yourIdFull) {
        // 어댑터 객체 생성
        Resources res = getResources();
        RequestQueue queue2 = Volley.newRequestQueue(getActivity());
        String url_search = "http://k4a403.p.ssafy.io:8000/member/readall?jwt=" + finalNew_jwt;
        JsonObjectRequest jsonRequest2 = new JsonObjectRequest(Request.Method.GET, url_search, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // recyclerview
                        try {
                            allName.clear();
                            // 리스트뷰에 어댑터 설정
                            listView.setAdapter(adapter);

                            JSONArray allArray = response.getJSONArray("list");
                            list_cnt2 = allArray.length();

                            // 친구 태그할 목록 데이터 추가
                            for (int i = 0; i < list_cnt2; i++) {

                                JSONObject allObject = allArray.getJSONObject(i);
                                int no = allObject.getInt("no");
                                String name = allObject.getString("name");
                                String id = allObject.getString("id");
                                String profile_image = allObject.getString("profile_image");

                                if(!id.equals(my_email)) {
                                    allName.add(name);

                                    friends_list.add(new Friend(no, name, id, profile_image)); // 프렌드리스트
                                    Log.d("friends", friends_list.toString());
                                }
                            }

                            spinnerDialog = new SpinnerDialog(getActivity(), allName, "전체 멤버 리스트 (클릭하여 친구추가)");

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
                                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                                    String url = "http://k4a403.p.ssafy.io:8000/friend/add";

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    response = response.trim();
                                                    if (response.contains("SUCCESS")) {
                                                        StringTokenizer st = new StringTokenizer(friends_list.get(position).getMyid(), "@");
                                                        String id_ = st.nextToken();
                                                        Log.d("msg 받아오기", id_);
                                                        adapter.addItem(new ItemDataBox(friends_list.get(position).getProfileimage(), friends_list.get(position).getMyname(), id_));
                                                        listView.setAdapter(adapter);
                                                    } else {
                                                        Toast.makeText(getActivity(), "이미 등록된 친구입니다.", Toast.LENGTH_SHORT).show();
                                                    }
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
                                            params.put("jwt", finalNew_jwt);
                                            params.put("your_id", friends_list.get(position).getMyid());
                                            return params;
                                        }
                                    };
                                    stringRequest.setShouldCache(false);
                                    queue.add(stringRequest);
                                }

                            });
                            friends_search = (TextInputEditText) rootView.findViewById(R.id.friends_search);
                            friends_search.setEnabled(true);
                            friends_search.setTextIsSelectable(true);
                            friends_search.setFocusable(false);
                            friends_search.setFocusableInTouchMode(false);
                            friends_search.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    spinnerDialog.showSpinerDialog();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue2.add(jsonRequest2);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friends, container, false);
        // 리스트뷰 객체 참조
        listView = (GridView) rootView.findViewById(R.id.ListView);
        adapter = new MyBaseAdapter(getContext());
        Resources res = getResources();
        yourId = new String[1000];
        yourIdFull = new String[1000];
        yourName = new String[1000];
        profileImage = new String[1000];

        /// 황윤호
        // jwt 다루기
        // 메인 액티비티에서 jwt, 토큰 받아오기 (인텐트 이용)
        Intent intent_story = getActivity().getIntent();
        jwt_ = intent_story.getStringExtra("jwt");

        // JWT 디코딩하여
        JWT jwt_decode = new JWT(jwt_);
        Claim claim_email = jwt_decode.getClaim("email");
        my_email = claim_email.asString();

        //함수 cycle
        volleyCycle(rootView, jwt_);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemDataBox currentItem = (ItemDataBox) adapter.getItem(position);
                String[] currentData = currentItem.getData();

                // 상세페이지로 이동
                Intent intent = new Intent(getActivity(), FriendsDetailActivity.class);

                intent.putExtra("jwt", jwt_);
                intent.putExtra("yourName", yourName[position]);
                intent.putExtra("yourId", yourId[position]);
                intent.putExtra("profileImage", profileImage[position]);
                intent.putExtra("yourIdFull", yourIdFull[position]);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return rootView;
    }
}