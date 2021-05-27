package com.mars.service;

import com.mars.model.FriendDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FriendService {
    void add(FriendDto friend);
    List<FriendDto> read(String id);
    void delete(FriendDto friend);
    void update(FriendDto friend,String yourid);
}