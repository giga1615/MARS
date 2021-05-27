//package com.mars.dao;
//
//import com.mars.model.CapsuleDto;
//import com.mars.model.CapsuleListDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//import java.util.List;
//
//@Repository
//public class CapsuleListJpqlDaoImpl implements CapsuleListJpqlDao<CapsuleDto> {
//    @Autowired
//    EntityManager entityManager;
//
//    @Override
//    public List<CapsuleListDto> findById(String id) {
//        String qstr = "from CapsuleListDto where id =:id";
//        Query query = entityManager.createQuery(qstr).setParameter("id",id);
//        entityManager.close();
//        return (List<CapsuleListDto>)query.getResultList();
//
//    }
//}
