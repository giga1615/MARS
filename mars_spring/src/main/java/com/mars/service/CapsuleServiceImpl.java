package com.mars.service;


import com.mars.dao.*;
import com.mars.model.CapsuleListDto;
import com.mars.model.CapsuleDto;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CapsuleServiceImpl implements CapsuleService {

    @Autowired
    CapsuleDao capsuleDao;

//    @Autowired
//    CapsuleListDao capsuleListDao;

    @Autowired
    CapsuleJpqlDao capsuleJpqlDao;

//    @Autowired
//    CapsuleListJpqlDao capsuleListJpqlDao;


    @Autowired
    MemberDao memberDao;

    @Autowired
    RedisTemplate redisTemplate;


    //transactional로 해줘야 도중에 오류가 나면 알아서 rollback해준다.
    //사용안하면, 실행된거까지 그냥 처리됨 일괄처리를위해서 이렇게
    //null인거는 null에 들어가게하면됨
    //transactional은 꼭 바로 set으로 안하고 method단위로 처리해줘도 된다.
    //@Transactional
    @Override
    public CapsuleDto Create(CapsuleDto capsuleDto, MultipartFile[] files) throws Exception {

        //Map<String, String> urlmap = new HashMap<>();
        List videoList = new ArrayList();
        List imageList = new ArrayList();
        List voiceList = new ArrayList();
        List<List> list = new ArrayList<>();
        
        capsuleDto.setCapusle_frineds_by_name(friendsCoverter(capsuleDto.getCapsule_friends()));


        if(files!=null) {
        //urlmap=getUrlAndSaveFiles(files);
            list=getUrlAndSaveFiles(files);
            videoList = list.get(0);
            imageList = list.get(1);
            voiceList = list.get(2);

            String video_url = convertMultiString(videoList);
            String image_url = convertMultiString(imageList);
            String voice_url = convertMultiString(voiceList);

        capsuleDto.setVideo_url(video_url);
        capsuleDto.setPhoto_url(image_url);
        capsuleDto.setVoice_url(voice_url);

        }

        if(capsuleDto.getCreated_date()==null) capsuleDto.setCreated_date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));//아이디랑 시간값으로, no찾아서 createList에 넘겨줌
        capsuleDao.saveAndFlush(capsuleDto);
        return capsuleJpqlDao.findById_date(capsuleDto.getId(), capsuleDto.getCreated_date());



    }




    private String convertMultiString(List list) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<list.size();i++){
            sb.append(list.get(i));
            if(i!=list.size()-1){
            sb.append(",");
            }
        }
        return sb.toString();
    }






    @Override
    public void CreateList(CapsuleDto capsuleDto) throws Exception {

        String friendsList = capsuleDto.getCapsule_friends();

        if(friendsList==null) return;

        StringTokenizer stringTokenizer = new StringTokenizer(friendsList, ",");
        int count =stringTokenizer.countTokens();
        for(int i=0 ; i<count;i++){

            String temp = stringTokenizer.nextToken();
            System.out.println(temp);
            CapsuleListDto capsuleListDto = new CapsuleListDto();
            capsuleListDto.setNo(capsuleDto.getNo());
            capsuleListDto.setTitle(capsuleDto.getTitle());
            capsuleListDto.setId(temp);//여기는이름
            capsuleListDto.setGps_x(capsuleDto.getGps_x());//오류나면 null이아니라 0이라서
            capsuleListDto.setGps_y(capsuleDto.getGps_y());
            capsuleListDto.setCreated_date(capsuleDto.getCreated_date());
            capsuleListDto.setOpen_date(capsuleDto.getOpen_date());
            capsuleListDto.setAddress(capsuleDto.getAddress());//주소추가 앞단에서 입력받음
            capsuleListDto.setCapsule_friends(friendsCoverter(capsuleDto.getCapsule_friends()));

            //R에 넣어줌
            this.createListUsingR(temp,capsuleListDto);
//
//            //Rdb에 넣어줌
//            capsuleListDao.save(capsuleListDto);

        }





    }


    public String friendsCoverter(String friends){

        StringTokenizer st= new StringTokenizer(friends, ",");
        StringBuilder sb = new StringBuilder();

        int stMax = st.countTokens();

        for(int i=1; i<=stMax;i++){

        	String friend_email = st.nextToken();
        	System.out.println("id :: "+friend_email);
            String friendName = memberDao.findById(friend_email).getName();
            System.out.println("name :: "+ friendName);

            if(i==stMax) sb.append(friendName);
            else sb.append(friendName).append(",");

        }

        return sb.toString();
    }




    @Override
    public List<CapsuleListDto> getMyList(String id) throws Exception {
        List<CapsuleListDto> list = null;


        ListOperations<String, CapsuleListDto> stringStringListOperations = redisTemplate.opsForList();
        Long size = stringStringListOperations.size(id);
        list = stringStringListOperations.range(id, 0, size-1);


//       List<CapsuleDto> list = null;
//       list = capsuleListJpqlDao.findById(id);
//        System.out.println(list.toString());
        return list;
    }



    @Override
    public CapsuleDto readOne(int no) throws Exception {


        return capsuleDao.findById(no).orElse(null);
    }

    @Override
    public void createListUsingR(String id, CapsuleListDto capsuleListDto) throws Exception {

        ListOperations<String, CapsuleListDto> stringStringListOperations = redisTemplate.opsForList();
        stringStringListOperations.rightPush(id, capsuleListDto);
    }

    List<List> getUrlAndSaveFiles(MultipartFile[] files){
    	
        List videoList = new ArrayList();
        List imageList = new ArrayList();
        List voiceList = new ArrayList();
        List<List> list = new ArrayList<>();

        for(int i = 0; i<files.length;i++) {
            String fileName = StringUtils.cleanPath(files[i].getOriginalFilename());
            String ext = FilenameUtils.getExtension(fileName).toLowerCase();
            System.out.println("파일이름 디버거 :: "+fileName);
            System.out.println(ext);
            //확장자별로 파일정리
            if (ext.equals("avi") || ext.equals("mp4")) {
                String url = convertUrlAndSetFile(files[i], "video");
                videoList.add(url);
                //videoMap.put("video_url", url);
            } else if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif")) {
                String url = convertUrlAndSetFile(files[i], "image");
                imageList.add(url);
                //imageMap.put("image_url", url);
            } else if (ext.equals("m4a") || ext.equals("mp3") || ext.equals("wav")) {
                String url = convertUrlAndSetFile(files[i], "voice");
                voiceList.add(url);
                //voiceMap.put("voice_url", url);
            } else {
                forbiddenExt();
            }

        }

        list.add(videoList);
        list.add(imageList);
        list.add(voiceList);

        return list;
    }


    String convertUrlAndSetFile(MultipartFile file,String extDir){

        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        String plusTime = format1.format(time);

        //String dir = "/src/main/resources/static/"+extDir;
        String dir = "/home/ubuntu/src/"+extDir;
        //String path = System.getProperty("user.dir");

        //String rootPath = path + dir;
        String rootPath = dir;


        String filePath = rootPath + "/";

        Path directory = Paths.get(filePath).toAbsolutePath().normalize();

        plusTime.replaceAll(":", "");
        String fileName = StringUtils.cleanPath(plusTime + file.getOriginalFilename());

        //String pathDB = "http://localhost:8000/api/capsule/"+extDir+"/" + fileName;
        String pathDB = "http://k4a403.p.ssafy.io:8000/api/capsule/"+extDir+"/" + fileName;

        Assert.state(!fileName.contains(".."), "Name of file cannot contain '..'");
        // 파일을 저장할 경로를 Path 객체로 받는다.
        Path targetPath = directory.resolve(fileName).normalize();

        // 파일이 이미 존재하는지 확인하여 존재한다면 오류를 발생하고 없다면 저장한다.
        Assert.state(!Files.exists(targetPath), fileName + " File alerdy exists.");
        try {
            file.transferTo(targetPath);
        } catch (IllegalStateException e) {

            e.printStackTrace();
            return "Error";
        } catch (IOException e) {

            e.printStackTrace();
            return "Error";
        }

        return pathDB;

    }

    void forbiddenExt(){ throw new IllegalArgumentException("금지된 확장자 입니다."); }




}
