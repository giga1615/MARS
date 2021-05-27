package com.mars.security;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;



@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends GenericFilterBean {

	@Autowired
	private JwtFilter jwtFilter;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// System.out.println("로그인으로보내면 들어오긴 하는데 예외처리 else로해줌");
		// 헤더에서 JWT 를 받아옵니다.

		String jwt = request.getParameter("jwt");

		if (jwt != null) {
			jwt = request.getParameter("jwt");

			if (jwtFilter.verifyJWT(jwt) != null) {
				// 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
				Authentication authentication = jwtFilter.getAuthentication(jwt);
				// SecurityContext 에 Authentication 객체를 저장합니다.
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				//여기에 인증이생성되면서, memberDto를 보면 그안에 어떤것이 허용되는지에 대한 권한들이 들어있음
				//그냥 쉽게 권한정보가 확장되어있는 memberTable이라고 생각하면됨, 그리고 그게 세션에 저장되어서
				//securiyContextHolder에서 가져다 쓸수있음
				System.out.println("sercurity에 잘 저장되었는지 "+authentication.getName() );
				System.out.println("securityContextHolder에 인증생성됨");
			} else {
				System.out.println("just login or 인증불가");
			}

		} else if (jwt == null) {
			System.out.println("회원가입");
		}
		//다음 필터에게 계속 request와 reponse를 전달
		chain.doFilter(request, response);
	}

}