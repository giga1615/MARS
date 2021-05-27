package com.mars.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	//webSecurityAdapter는 애초에 springSecurity에 내포되어있다. 

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	// authenticationManager를 Bean 등록합니다.
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.httpBasic().disable() // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
				.csrf().disable() // csrf 보안 토큰 disable처리.
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 역시 사용하지															// 않습니다.
				.and().authorizeRequests() // 여기는 그니까 필터는 거치는데, 인증이안되도 접근이 가능하다는 얘기
				// 그니까 필터에서 오류는 나면 안됨
				.antMatchers("/api/Login","/api/member/create","/api/kakaoLogin", "/api/capsule/image/**","/api/capsule/video/**","/api/capsule/voice/**",
						"/api/tempJwt", "/api/capsule/download")
				.permitAll().anyRequest().authenticated().and()
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		// JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
	}
}