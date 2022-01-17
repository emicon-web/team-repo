package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.entity.Syserrorcode;

import java.util.List;

public interface SyserrorcodeRepository {

    List<Object> findAll();

    Object findBySysID(String sysID);

    Syserrorcode findBySysIDAndErrorCode(String sysID, String errorCode);

    int updateBySysId(Syserrorcode syserrorcode);

}
