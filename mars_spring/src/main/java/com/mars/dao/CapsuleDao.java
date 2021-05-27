package com.mars.dao;


import com.mars.model.CapsuleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapsuleDao extends JpaRepository<CapsuleDto, Integer> {

    //CapsuleDto findById(String no) throws SQLException;


    //CapsuleDto findByKakaoId(String id) throws SQLException;
}
