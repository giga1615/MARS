package com.mars.security;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mars.service.CustomUserDetailService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtFilter {

	@Autowired
	CustomUserDetailService userDetailsService;

	static String secretKey = "mysecurityjwtservicesuccess";
	private static final String SECRET_KEY = Base64.getEncoder().encodeToString(secretKey.getBytes());

	// 최초 로그인시에 생성해서 프론트에서 localstorage나 쿠키에 저장을 해준다.

	// 프론트 측에서 request할때 토큰을 보내고 이 함수를 통해서, 검증이 되면 가능하다.
	// 이걸 인터셉터로 처리하면 더좋다.
	// 토큰 검증

	public Map<String, Object> verifyJWT(String jwt) throws UnsupportedEncodingException {

		Map<String, Object> claimMap = null;

		System.out.println(jwt);
		System.out.println("jwt가 문제없이 인증된경우");
		

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
		
		return claimMap;// 토큰이 검증되면, map을 가져다가 쓸수있음 claim은 map으로 이루어져있음
	}

	// 2021.03.29 수정
	// JWT 토큰에서 인증 정보 조회

	public Authentication getAuthentication(String jwt) throws ExpiredJwtException, UnsupportedJwtException,
			MalformedJwtException, SignatureException, IllegalArgumentException, UnsupportedEncodingException {

		Map<String, Object> claimMap = null;
		Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes("UTF-8")) // Set Key
				.parseClaimsJws(jwt) // 파싱 및 검증, 실패 시 에러
				.getBody();

		claimMap = claims;
		System.out.println("jwt인증후 ID값 잘 로드되는지 체크/ 비밀번호인증이생성됨");
		System.out.println((String) claimMap.get("id"));
		UserDetails userDetails = userDetailsService.loadUserByUsername((String) claimMap.get("id"));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

}