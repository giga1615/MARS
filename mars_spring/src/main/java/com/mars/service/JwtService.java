package com.mars.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface JwtService {

	
	String createToken(String subject, String email, String nickName,String profile_image);

	Map<String, Object> verifyJWT(String jwt) throws UnsupportedEncodingException;

	public String getIdByJWT(String jwt) throws UnsupportedEncodingException;
}
