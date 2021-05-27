package com.mars.service;

import com.mars.model.MemberDto;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MemberService {
    void sign(MemberDto member);
    MemberDto read(String id);
    String getToken(String id); // 토큰 찾기
    String getName(String id);
    String getProfile(String id);
    List<MemberDto> getAllMember();

}