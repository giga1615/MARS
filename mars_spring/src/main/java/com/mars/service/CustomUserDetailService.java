package com.mars.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mars.dao.LoginDao;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	LoginDao loginDao;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		
		//이거는 이제 그냥 login함수내에서 작용
		try {
			return loginDao.findById(id);
		} catch (SQLException e) {
			return null;
		}
		
	}
}