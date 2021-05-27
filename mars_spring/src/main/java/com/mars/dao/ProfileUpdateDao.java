package com.mars.dao;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mars.model.MemberDto;

@Repository
public class ProfileUpdateDao {
	 @Autowired
	    EntityManager entityManager;

	    @Transactional
	    public void save(String image,String id, String android_token){
	    	MemberDto memberDto = new MemberDto();
	    	memberDto=entityManager.find(MemberDto.class, id);
	    	memberDto.setProfile_image(image);
	    	memberDto.setAndroid_token(android_token);
	        entityManager.persist(memberDto);
	        //entityManager.merge(userEntity); //비추 전체 변경됨
	    }

}
