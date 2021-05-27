package com.mars.service;

import com.mars.dao.MemberDao;
import com.mars.model.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberDao memberDao;

    @Transactional
    @Override
    public void sign(MemberDto member) {

        byte[] array = new byte[15]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        String id = member.getId();
        String profileImage = member.getProfile_image();
        String android_token = member.getAndroid_token();
        String name = member.getName();

        member.setId(id);
        member.setName(name);
        member.setPassword(generatedString);
        member.setProfile_image(profileImage);
        member.setAndroid_token(android_token);

        memberDao.save(member);
    } // 회원등록

    @Transactional
    @Override
    public MemberDto read(String id) {
        MemberDto member = memberDao.findById(id);  // 아이디로 정보 읽기
        return member;                                  // ID가 디비에 있는지도 이걸로 체크하면 될 듯 합니다
    }

    @Transactional
    @Override
    public String getToken(String id){
    	System.out.println("id 제발좀" + id);
        MemberDto member = memberDao.findById(id);
        String token = member.getAndroid_token();
        return token;
    }

    @Transactional
    @Override
    public String getName(String id) {
        MemberDto member = memberDao.findById(id);
        String name = member.getName();
        return name;
    }

    @Override
    public String getProfile(String id) {
        MemberDto member = memberDao.findById(id);
        String profileImage = member.getProfile_image();
        return profileImage;
    }

    @Override
    public List<MemberDto> getAllMember() {
        List<MemberDto> list = memberDao.findAll();
        return list;
    }


}