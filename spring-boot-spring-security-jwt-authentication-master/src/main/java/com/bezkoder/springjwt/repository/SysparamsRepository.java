package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.entity.Sysparams;

import java.util.List;

public interface SysparamsRepository {

    List<Object> findAll();

    Object findBySysKey(String sysKey);

    List<Object> findBySysTypeAndSysKey(String sysType, String sysKey);

    int updateBySysKey(Sysparams sysparams);

}
