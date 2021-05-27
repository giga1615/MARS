package com.mars.dao;

import com.mars.model.MemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberDao extends JpaRepository<MemberDto, Integer> {
    MemberDto findById(String id);
    MemberDto findByName(String name);
    List<MemberDto> findAll();
}