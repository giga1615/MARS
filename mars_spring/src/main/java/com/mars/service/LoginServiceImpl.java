package com.mars.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mars.dao.LoginDao;
import com.mars.dao.ProfileUpdateDao;
import com.mars.model.MemberDto;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LoginServiceImpl implements LoginService {
	private final static String REST_API_KEY = "0530ead261a6f23c9a61fdba73622fb7";
	private final static String REDIRECT_URI = "http://k4a403.p.ssafy.io:8000/api/kakaoLogin";
	// private final static String REDIRECT_URI =
	// "http://localhost:8000/api/kakaoLogin";

	@Autowired
	JwtService jwtService;

	@Autowired
	LoginDao loginDao;

	@Autowired
	MemberService memberService;
	
	@Autowired
	ProfileUpdateDao profileUpdateDao;

	@Override
	public String androidKakao(String kakao_access_Json, String android_token) throws SQLException {
		String jwt = null;

		final String RequestUrl = "https://kapi.kakao.com/v2/user/me";
		final HttpClient client = HttpClientBuilder.create().build();
		final HttpPost post = new HttpPost(RequestUrl);

		post.addHeader("Authorization", "Bearer " + kakao_access_Json);
		JsonNode returnNode = null;
		try {
			final HttpResponse response = client.execute(post);
			// JSON 형태 반환값 처리
			ObjectMapper mapper = new ObjectMapper();
			returnNode = mapper.readTree(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

		System.out.println(returnNode);

		String kakao_email = null;
		String kakao_id = null;
		String kakao_name = null;
		String profile_image = getImage(kakao_access_Json);

		JsonNode profile = returnNode.path("properties");
		JsonNode kakao_account = returnNode.path("kakao_account");
		kakao_id = returnNode.path("id").asText();
		kakao_email = kakao_account.path("email").asText();
		kakao_name = kakao_account.path("profile").path("nickname").asText();
		//profile_image = profile.path("profile_image").asText();

		System.out.println(kakao_email);
		System.out.println(kakao_name);
		System.out.println(profile_image);

		// 아이디 없으면 회원가입 진행
		if (loginDao.findById(kakao_email) == null) {
			MemberDto memberDto = new MemberDto();
			memberDto.setId(kakao_email);
			memberDto.setName(kakao_name);
			memberDto.setProfile_image(profile_image);
			memberDto.setAndroid_token(android_token);
			memberService.sign(memberDto);

		}else {
			//for 프로필 업데이트
			MemberDto memberDto = new MemberDto();
			memberDto.setAndroid_token(android_token);
			memberDto.setProfile_image(profile_image);
			profileUpdateDao.save(profile_image, kakao_email, android_token);
			
		}
		
		
		
		
		if (kakao_email != null && kakao_id != null) {

			jwt = jwtService.createToken("userInfo", kakao_email, kakao_name, profile_image);
			// 만들어주면 알아서 서버에저장됨

			System.out.println(jwt);

			return jwt;

		} else if (kakao_email == null && kakao_id != null) {
			jwt = jwtService.createToken("userInfo", kakao_id, kakao_name, profile_image);
			// 만들어주면 알아서 서버에저장됨

			System.out.println(jwt);

			return jwt;

		} else {
			return "fail";
		}

	}
	
	public String getImage(String kakao_access_Json) {
		
		final String RequestUrl = "https://kapi.kakao.com/v1/api/talk/profile";
		final HttpClient client = HttpClientBuilder.create().build();
		final HttpGet get = new HttpGet(RequestUrl);

		get.addHeader("Authorization", "Bearer " + kakao_access_Json);
		JsonNode returnNode = null;
		try {
			final HttpResponse response = client.execute(get);
			// JSON 형태 반환값 처리
			ObjectMapper mapper = new ObjectMapper();
			returnNode = mapper.readTree(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		
		String temp = returnNode.path("profileImageURL").asText();
		
		return temp;
	}
	
	
	
}
