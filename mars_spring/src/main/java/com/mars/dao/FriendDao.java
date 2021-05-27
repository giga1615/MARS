package com.mars.dao;

import com.mars.model.FriendDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendDao extends JpaRepository<FriendDto,Integer> {
    List<FriendDto> findAllByMyid(String myId);
    List<FriendDto> findByYourid(String yourId);
}