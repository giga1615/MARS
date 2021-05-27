package com.mars.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mars.model.CapsuleDto;
import com.mars.model.CapsuleListDto;

public interface CapsuleService {

    CapsuleDto Create(CapsuleDto capsuleDto, MultipartFile[] files) throws Exception;
    void CreateList(CapsuleDto capsuleDto) throws Exception;
    List<CapsuleListDto> getMyList(String id) throws Exception;
    CapsuleDto readOne(int no) throws Exception;
    void createListUsingR(String id,CapsuleListDto capsuleListDto) throws Exception;

}
