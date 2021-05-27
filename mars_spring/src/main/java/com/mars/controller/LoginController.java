package com.mars.controller;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.mars.service.tempJwt;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.mars.service.JwtService;
import com.mars.service.LoginService;



@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RestController
@RequestMapping("/api")
public class LoginController {

	@Autowired
	LoginService loginService;

	@Autowired
	JwtService jwtService;

	@Autowired
	tempJwt tempJwt1;

	@GetMapping(value = "/tempJwt")
	public String tempJwt() throws MalformedURLException {
		String jwt = tempJwt1.createToken1("d", "d", "d", "d");
		return jwt;
	}

	@RequestMapping(value = "/kakaoLogin")
	public ResponseEntity<Map<String, Object>> kakaoLogin(
			HttpServletRequest ho,
			@RequestParam(value = "code", required = false) String authorizationToken,
			@RequestParam(value = "android_token", required = false) String android_token

	) throws MalformedURLException, SQLException {
		System.out.println("kakao");
//		
//		Enumeration params = ho.getParameterNames();
//		System.out.println("----------------------------");
//		while (params.hasMoreElements()){
//		    String name = (String)params.nextElement();
//		    System.out.println(name + " : " +ho.getParameter(name));
//		}
//		System.out.println("----------------------------");
		
		
		
		Map<String, Object> resultmap = new HashMap<>();
		String jwt = loginService.androidKakao(authorizationToken, android_token);
		
		
		
		String check;
		try {
			check = jwtService.getIdByJWT(jwt);
			System.out.println(check);
			if(check.equals("")) {
				resultmap.put("jwt","fail");
				return new ResponseEntity<>(resultmap, HttpStatus.BAD_REQUEST);
			}else {
				resultmap.put("jwt",jwt);
				return new ResponseEntity<>(resultmap, HttpStatus.ACCEPTED);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultmap.put("jwt","fail");
			return new ResponseEntity<>(resultmap, HttpStatus.BAD_REQUEST);
		}
	
		
		//System.out.println(authorizationToken);
		

		//return new ResponseEntity<>(resultmap, HttpStatus.BAD_REQUEST);
	}


		

}
