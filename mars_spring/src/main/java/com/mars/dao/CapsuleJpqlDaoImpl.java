package com.mars.dao;

import com.mars.model.CapsuleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Repository
public class CapsuleJpqlDaoImpl implements CapsuleJpqlDao<CapsuleDto> {
    @Autowired
    EntityManager entityManager;

    @Override
    public CapsuleDto findById_date(String id, String created_date) {
        String qstr = "from CapsuleDto where id =:id and created_date =:created_date";
        Query query = entityManager.createQuery(qstr).setParameter("id",id).setParameter("created_date",created_date);
        entityManager.close();
        return (CapsuleDto)query.getSingleResult();

    }
}
