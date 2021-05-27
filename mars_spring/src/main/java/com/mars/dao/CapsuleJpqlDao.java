package com.mars.dao;

import com.mars.model.CapsuleDto;

import java.io.Serializable;

public interface CapsuleJpqlDao<T> extends Serializable {

    public CapsuleDto findById_date(String id, String created_date);

}
