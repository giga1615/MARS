package com.mars.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class tempJwt{



    static String secretKey = "mysecurityjwtservicesuccess";
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(secretKey.getBytes());

    // 최초 로그인시에 생성해서 프론트에서 localstorage나 쿠키에 저장을 해준다.

    public String createToken1(String subject, String email, String nickName , String profile_image) {
        subject="userInfo";
        email="ybj3@naver.com";
        nickName="충현";
        profile_image="random";
        Long expiredTime = 1000 * 60L * 60L * 5* 12L; // 토큰 유효 시간 (2시간)
        // Header 부분 설정
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        // payload 부분 설정
        Map<String, Object> payloads = new HashMap<>();
        // 여기서 dao로 불러와서 payloads에 삽입
        payloads.put("email", email);
        payloads.put("id", email);
        payloads.put("nickName", nickName);
        payloads.put("profile_image", profile_image);



        Date ext = new Date(); // 토큰 만료 시간
        ext.setTime(ext.getTime() + expiredTime);

        // 토큰 Builder
        String jwt = Jwts.builder().setHeader(headers) // Headers 설정
                .setClaims(payloads) // Claims 설정
                .setSubject(subject) // 토큰 용도
                .setExpiration(ext) // 토큰 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // HS256과 Key로 Sign
                .compact(); // 토큰 생성

        return jwt;// jwt생성된것임 따로 세션이나 저장할 필요없음

    }
}
