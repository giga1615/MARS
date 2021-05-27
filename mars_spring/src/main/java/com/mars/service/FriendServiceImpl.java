package com.mars.service;

import com.mars.dao.FriendDao;
import com.mars.model.FriendDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    @Autowired
    FriendDao friendDao;

    @Transactional
    @Override
    public void add(FriendDto friend){
        friendDao.save(friend);
    } // 친구등록

    @Transactional
    @Override
    public List<FriendDto> read(String id) {
       // 아이디로 친구 목록 가져오기
       List<FriendDto> list = friendDao.findAllByMyid(id);
       return list;
    }

    @Transactional
    @Override
    // 내 아이디, 친구아이디 (frienddto)이용해서 삭제
    public void delete(FriendDto friend) {
        friendDao.delete(friend);
    }

    @Transactional
    @Override
    public void update(FriendDto frienddto, String yourid) {
        List<FriendDto> friend = friendDao.findByYourid(yourid);
        for(int i=0;i<friend.size();i++){
            friend.get(i).update(frienddto.getProfileimage());
        }

    }

}