package com.mars.controller;

import com.mars.model.FriendDto;
import com.mars.service.FriendService;
import com.mars.service.JwtService;
import com.mars.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    JwtService jwtService;

    @Autowired
    FriendService friendService;

    @Autowired
    MemberService memberService;

    @PostMapping("/add")
    public ResponseEntity<Map<String,Object>> AddFriend(@RequestParam(value = "jwt", required = false) String jwt,
                                                        @RequestParam(value="your_id",required = false)String yourId) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = null;
        try{
            FriendDto friend = new FriendDto();
            String myId = jwtService.getIdByJWT(jwt); // jwt로 내 아이디 찾기

            String myName = memberService.getName(myId); // 내 아이디로 내 이름 찾기
            String yourName = memberService.getName(yourId); // 친구 아이디로 친구 이름 찾기
            String profileImage = memberService.getProfile(yourId);
            friend.setMyname(myName);
            friend.setYourname(yourName);
            friend.setMyid(myId);
            friend.setYourid(yourId);
            friend.setProfileimage(profileImage);
            List<FriendDto> list = friendService.read(myId);
            boolean check = false;
            for(int i=0;i<list.size();i++){
                if(list.get(i).getYourname().equals(yourName)){
                    check = true;
                    break;
                }
            }
            if(!check)
            {  friendService.add(friend);  // 친구 추가
            status = HttpStatus.ACCEPTED;
            resultMap.put("message", "SUCCESS");}
            else  { status = HttpStatus.ACCEPTED;
            resultMap.put("message", "FAIL");}
        }catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            resultMap.put("message", "SERVER_ERROR");
            e.printStackTrace();
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    @GetMapping("/read")
    public ResponseEntity<Map<String, Object>> readFriend(@RequestParam(value = "jwt", required = false) String jwt) throws Exception
    {
        Map<String, Object> resultMap = new HashMap<>();

        HttpStatus status = null;

        try{
            String myId = jwtService.getIdByJWT(jwt);   // jwt로 내 아이디 찾기
            List<FriendDto> list = friendService.read(myId); // 친구 리스트 읽기
            for(int i=0;i<list.size();i++){
                String id = list.get(i).getYourid();                // 친구 아이디로
                String profile = memberService.getProfile(id);      // 멤버테이블에서 프로필 확인
                if(profile!=list.get(i).getProfileimage()){         // 현재 프로필과 멤버 테이블의 프로필 비교
                    list.get(i).setProfileimage(profile);           // 다르면 멤버테이블의 프로필으로 업데이트
                    friendService.update(list.get(i),id);
                }

            }
            status = HttpStatus.ACCEPTED;
            resultMap.put("friendlist",list);
            resultMap.put("message", "SUCCESS");
        }catch (Exception e){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            resultMap.put("message", "SERVER_ERROR");
            e.printStackTrace();
        }

        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteMember(@RequestParam(value = "jwt", required = false) String jwt,
                                                            @RequestParam(value="your_id",required = false) String yourId) throws Exception
    {
        Map<String, Object> resultMap = new HashMap<>();

        HttpStatus status = null;

        try{
            String myId = jwtService.getIdByJWT(jwt);   // jwt로 내 아이디 찾기
            FriendDto friend = new FriendDto();
            friend.setMyid(myId);
            friend.setYourid(yourId);
            friendService.delete(friend);
            status = HttpStatus.ACCEPTED;
            resultMap.put("message", "SUCCESS");
        }catch (Exception e){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            resultMap.put("message", "SERVER_ERROR");
            e.printStackTrace();
        }

        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

}