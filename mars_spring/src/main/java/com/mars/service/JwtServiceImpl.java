package com.mars.service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServiceImpl implements JwtService {

	static String secretKey = "mysecurityjwtservicesuccess";
	private static final String SECRET_KEY = Base64.getEncoder().encodeToString(secretKey.getBytes());

	// 최초 로그인시에 생성해서 프론트에서 localstorage나 쿠키에 저장을 해준다.
	@Override
	public String createToken(String subject, String email, String nickName , String profile_image) {
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

	
	
	// 프론트 측에서 request할때 토큰을 보내고 이 함수를 통해서, 검증이 되면 가능하다.
	// 이걸 인터셉터로 처리하고 있다.
	// 토큰 검증
	@Override
	public Map<String, Object> verifyJWT(String jwt) throws UnsupportedEncodingException {
		Map<String, Object> claimMap = null;

		System.out.println(jwt);
		System.out.println("jwt가 문제없이 인증된경우");

		try {
			Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes("UTF-8")) // Set Key
					.parseClaimsJws(jwt) // 파싱 및 검증, 실패 시 에러
					.getBody();

			claimMap = claims;

		} catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
			System.out.println(e);

		} catch (Exception e) { // 그외 에러났을 경우
			System.out.println(e);

		}
		return claimMap;// 토큰이 검증되면, map을 가져다가 쓸수있음 claim은 map으로 이루어져있음
	}




	@Override
	public String getIdByJWT(String jwt) throws UnsupportedEncodingException {

		Map<String, Object> claimMap = null;

		try {
			Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes("UTF-8")) // Set Key
					.parseClaimsJws(jwt) // 파싱 및 검증, 실패 시 에러
					.getBody();

			claimMap = claims;

			// Date expiration = claims.get("exp", Date.class);
			// String data = claims.get("data", String.class);

		} catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우

			System.out.println(e);

		} catch (Exception e) { // 그외 에러났을 경우

			System.out.println(e);

		}
		return (String) claimMap.get("id");// 토큰이 검증되면, map을 가져다가 쓸수있음 claim은 map으로 이루어져있음
	}
}
