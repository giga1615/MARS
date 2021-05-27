package com.mars.dao;

import java.sql.SQLException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mars.model.MemberDto;

@Repository
public interface LoginDao extends JpaRepository<MemberDto, Long> {
	//primary key로 검색해준다는 의미임, id가
	MemberDto findById(String id) throws SQLException;
 
}
