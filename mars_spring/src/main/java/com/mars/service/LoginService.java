package com.mars.service;

import java.sql.SQLException;

public interface LoginService {


	String androidKakao(String authorizationToken, String android_token) throws SQLException;
	
}
