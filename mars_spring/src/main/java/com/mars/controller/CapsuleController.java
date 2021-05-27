package com.mars.controller;



import com.mars.model.CapsuleDto;
import com.mars.model.CapsuleListDto;
import com.mars.pushhandle.AndroidPushPeriodicNotifications;
import com.mars.service.AndroidPushNotificationsService;
import com.mars.service.CapsuleService;
import com.mars.service.JwtService;
import com.mars.service.MemberService;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.imageio.spi.ImageReaderSpi;
import javax.servlet.http.HttpServletRequest;


@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RestController
@RequestMapping("/api/capsule")
public class CapsuleController {

    @Autowired
    JwtService jwtService;
    @Autowired
    CapsuleService capsuleService;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;
    @Autowired
    MemberService memberService;

    @Autowired
    RedisTemplate redisTemplate;

//캡슐 크리에이트를 만들어야됨 일단

    //캡슐 설계, 음악, 사진, 영상, 음성, 제목, 글, 음악제목 넣을 수 있도록
    //DB에는 각각에 따른 주소가 들어가고, 글과 음악제목은 DB자체에 넣는다.
    //Capsule Create를 controller에서 만들어주고
    //비니지스 로직에서 Capsule 관련 method를 나눠준다.
    //dto로 받으면, 훨씬 깔끔하고, 필요한값만 받을 수 있음
    @PostMapping(value = "/create")
    public ResponseEntity<Map<String, Object>> createCapsule(
    		HttpServletRequest ho,
    		@RequestParam(value = "jwt", required = false) String jwt,
                                                             @RequestParam(value = "files", required = false) MultipartFile[] files,
                                                             CapsuleDto capsuleDto
    ) throws Exception {
    	
   
    	
//		
//		Enumeration params = ho.getParameterNames();
//		System.out.println("----------------------------");
//		while (params.hasMoreElements()){
//		    String name = (String)params.nextElement();
//		    System.out.println(name + " : " +ho.getParameter(name));
//		}
//		System.out.println("----------------------------");
//
//        System.out.println("files찍어보기");
//        System.out.println(files);

        Map<String, Object> resultmap = new HashMap<>();

        String id = null;
        try {
            id = jwtService.getIdByJWT(jwt);
            capsuleDto.setId(id);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
            System.out.println("jwt만료");
            return new ResponseEntity<>(resultmap, HttpStatus.ACCEPTED);
        }

        try {
            CapsuleDto capsuleFromCreate = capsuleService.Create(capsuleDto,files);
            capsuleDto.setNo(capsuleFromCreate.getNo());
            capsuleService.CreateList(capsuleDto);
            resultmap.put("MESSAGE", "SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        }
        
        
     // 친구 목록
        String friends = capsuleDto.getCapsule_friends();
        List<String> friendsList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(friends,",");
        int temp_size=st.countTokens();
        for(int i=0;i<temp_size;i++){
            friendsList.add(st.nextToken());
        }
        System.out.println("친구목록" + friendsList);
        send(friendsList);
        
        

        return new ResponseEntity<>(resultmap, HttpStatus.ACCEPTED);
    }
    
    // FCM 전송
    public @ResponseBody ResponseEntity<String> send(List<String> friendList) throws Exception {
        AndroidPushPeriodicNotifications androidPushPeriodicNotifications = new AndroidPushPeriodicNotifications();

        List<String> tokens = new ArrayList<>();
        
        String token = null;
        // 친구 목록에서 token 목록 얻기
        for(int i=0;i<friendList.size();i++){
            String id = friendList.get(i);
            
            token = memberService.getToken(id);
            // System.out.println("토큰 : "+token);
            tokens.add(token);
        }

        String notifications = androidPushPeriodicNotifications.PeriodicNotificationJson(tokens);
        System.out.println("##########notifications#########" +notifications);
        HttpEntity<String> request = new HttpEntity<>(notifications);
        System.out.println("##########request#########" +request);
        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();


        try{
            String firebaseResponse = pushNotification.get();
            return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
        }
        catch (InterruptedException e){
            logger.debug("got interrupted!");
            throw new InterruptedException();
        }
        catch (ExecutionException e){
            logger.debug("execution error!");
        }

        return new ResponseEntity<>("Push Notification ERROR!", HttpStatus.BAD_REQUEST);
    }
    

    @GetMapping(value = "/image/{imagename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> image(@PathVariable("imagename") String imagename) throws IOException {


        //String dir = StringUtils.cleanPath("src/main/resources/static/image/");
        String dir = StringUtils.cleanPath("/home/ubuntu/src/image/");
        InputStream imageStream = new FileInputStream(dir + imagename);

        byte[] imageByteArray = IOUtils.toByteArray(imageStream);

        imageStream.close();
        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);

        
    }

    @GetMapping(value = "/video/{videoname}", produces = "video/mp4")
    public FileSystemResource video(@PathVariable("videoname") String videoname) throws IOException {


        //File video =new File("src/main/resources/static/video/"+videoname);
        File video =new File("/home/ubuntu/src/video/"+videoname);

        return new FileSystemResource(video);

    }

    @GetMapping(value = "/voice/{audioname}", produces = "audio/mp3")
    public FileSystemResource sound(@PathVariable("audioname") String audioname) throws IOException {


        //File sound =new File("src/main/resources/static/voice/"+audioname);
        File sound =new File("/home/ubuntu/src/voice/"+audioname);

        return new FileSystemResource(sound);

    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource download() throws IOException {


        //File sound =new File("src/main/resources/static/voice/"+audioname);
        File apk =new File("/home/ubuntu/src/apk/"+ "mars.apk");

        return new FileSystemResource(apk);

    }




    @GetMapping(value = "/readOne")
    public ResponseEntity<Map<String, Object>> readOne(@RequestParam(value = "no", required = false) int no) throws MalformedURLException {
        Map<String, Object> resultmap = new HashMap<>();
        try {
            CapsuleDto capsuleDto = capsuleService.readOne(no);
            resultmap.put("CAPSULE", capsuleDto);
        } catch (Exception e) {
            resultmap.put("MESSAGE", "FAIL");
            e.printStackTrace();
        }


        return new ResponseEntity<>(resultmap, HttpStatus.OK);
    }


    @DeleteMapping(value = "/delete")
    public ResponseEntity<Map<String, Object>> deleteCapsule() throws MalformedURLException {


        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/mylist")
    public ResponseEntity<Map<String, Object>> myCapsuleList(@RequestParam(value = "jwt", required = false) String jwt
    ){
        Map<String, Object> resultmap = new HashMap<>();
        String id = null;
        //List<CapsuleDto> list = null;
        List<CapsuleListDto> list = null;
        try {
            id = jwtService.getIdByJWT(jwt);

            list=capsuleService.getMyList(id);
            resultmap.put("list", list);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        }
        return new ResponseEntity<>(resultmap, HttpStatus.OK);
    }


    @PostMapping("/admin/onlyR/make")
    public ResponseEntity<Map<String, Object>> makeredis(
            @RequestParam(value = "jwt", required = false) String jwt,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "created_date", required = false) String created_date,
            @RequestParam(value = "no", required = false) int no,
            CapsuleDto capsuleDto
    ){
        Map<String, Object> resultmap = new HashMap<>();

        //List<CapsuleDto> list = null;
        List<CapsuleListDto> list = null;

        CapsuleListDto capsuleListDto = null;
        try {



            capsuleService.CreateList(capsuleDto);
            list=capsuleService.getMyList(id);

            resultmap.put("list", list);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        }
        return new ResponseEntity<>(resultmap, HttpStatus.OK);
    }


    @GetMapping("/admin/onlyR/read")
    public ResponseEntity<Map<String, Object>> makeredis(
            @RequestParam(value = "jwt", required = false) String jwt,
            @RequestParam(value = "id", required = false) String id

    ){
        Map<String, Object> resultmap = new HashMap<>();

        //List<CapsuleDto> list = null;
        List<CapsuleListDto> list = null;

        CapsuleListDto capsuleListDto = null;
        try {

            list=capsuleService.getMyList(id);

            resultmap.put("list", list);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        }
        return new ResponseEntity<>(resultmap, HttpStatus.OK);
    }




    @PostMapping("/admin/timechange")
    public ResponseEntity<Map<String, Object>> updateList(
            @RequestParam(value = "jwt", required = false) String jwt,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "created_date", required = false) String created_date,
            @RequestParam(value = "no", required = false) int no,
            @RequestParam(value = "gps_x", required = false) double gps_x,
            @RequestParam(value = "gps_y", required = false) double gps_y
    ){
        Map<String, Object> resultmap = new HashMap<>();

        //List<CapsuleDto> list = null;
        List<CapsuleListDto> list = null;
        List<CapsuleListDto> copy_list = new ArrayList<>();
        CapsuleListDto capsuleListDto = null;
        try {

            list=capsuleService.getMyList(id);

            int size =list.size();
            ListOperations<String, CapsuleListDto> operations = redisTemplate.opsForList();

            //모두삭제
            for(int i=0;i<size;i++){
                operations.rightPop(id);
            }

            for(int i=0;i<size;i++){
                capsuleListDto = list.get(i);

                if(no==capsuleListDto.getNo()){
                    if(created_date!=null){
                    capsuleListDto.setCreated_date(created_date);}

                    if(gps_x!=0 && gps_y !=0){
                    capsuleListDto.setGps_x(gps_x);
                    capsuleListDto.setGps_y(gps_y);
                    }
                }
                copy_list.add(capsuleListDto);

                operations.rightPush(id, capsuleListDto);
            }



            resultmap.put("list", copy_list);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        }
        return new ResponseEntity<>(resultmap, HttpStatus.OK);
    }


    
    @GetMapping("/sharedlist")
    public ResponseEntity<Map<String, Object>> sharedCapsuleList(@RequestParam(value = "jwt", required = false) String jwt
            ,@RequestParam(value = "yourid", required = false) String yourid
            
    ){
        Map<String, Object> resultmap = new HashMap<>();
        String id = null;
        //List<CapsuleDto> list = null;
        List<CapsuleListDto> list = null;
        List<CapsuleListDto> origin_list = null;
        List<CapsuleListDto> result = new ArrayList<>();
        try {
            id = jwtService.getIdByJWT(jwt);
            origin_list = capsuleService.getMyList(id);
            list=capsuleService.getMyList(yourid);

            for(int i=0; i<origin_list.size();i++) {
                int myNo = origin_list.get(i).getNo();
                int yourNo = 0;
                for(int j=0;j<list.size();j++){

                    yourNo = list.get(j).getNo();
                    if(myNo==yourNo){
                        result.add(origin_list.get(i));
                        //result.add(list.get(j));
                    }
                }

            }
            
            resultmap.put("list", result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("MESSAGE", "FAIL");
        }
        return new ResponseEntity<>(resultmap, HttpStatus.OK);
    }
    
    
    
    

}
