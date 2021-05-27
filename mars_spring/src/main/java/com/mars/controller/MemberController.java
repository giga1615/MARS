package com.mars.controller;

import com.mars.model.MemberDto;
import com.mars.service.JwtService;
import com.mars.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    MemberService memberService;

    @Autowired
    JwtService jwtService;


    @GetMapping("/test")
    public String hello(String id){

        return "hello";
    }



    @GetMapping("/read")
    public ResponseEntity<Map<String, Object>> readMember(@RequestParam(value = "jwt", required = false) String jwt) throws Exception
    {
        Map<String, Object> resultMap = new HashMap<>();

        HttpStatus status = null;

        try{
            String id = jwtService.getIdByJWT(jwt); // jwt로 내 아이디 찾아서
            MemberDto member = memberService.read(id);  // 내 정보 읽기
            status = HttpStatus.ACCEPTED;
            resultMap.put("member",member);
            resultMap.put("message", "SUCCESS");
        }catch (Exception e){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            resultMap.put("message", "SERVER_ERROR");
            e.printStackTrace();
        }

        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    @GetMapping("/readall")
    public ResponseEntity<Map<String, Object>> readAll (@RequestParam(value = "jwt", required = false) String jwt) throws Exception
    {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = null;

        try{
           List<MemberDto> list = memberService.getAllMember();
            status = HttpStatus.ACCEPTED;
            resultMap.put("list",list);
            resultMap.put("message", "SUCCESS");
        }catch (Exception e){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            resultMap.put("message", "SERVER_ERROR");
            e.printStackTrace();
        }

        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }


}