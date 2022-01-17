package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.entity.Sysparams;
import com.bezkoder.springjwt.repository.SysparamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysparamsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    SysparamsRepository sysparamsRepository;

    public List<Object> findAll() {
//		 Map<String, Object> results = new HashMap<>();
//	        StringBuilder sql1 = new StringBuilder();
//	        sql1.append("select count(*) from sysparams");

//	        results.put("data",sysparamsRepository.findAll());
//	        results.put("count", jdbcTemplate .queryForObject(sql1.toString(), Integer.class));

        return sysparamsRepository.findAll();

    }

    public Object findBySyskey(String sysKey) {
        return sysparamsRepository.findBySysKey(sysKey);
    }

    public List<Object> findBySysTypeAndSyskey(String sysType, String sysKey) {
    	List<Object> sysparams= sysparamsRepository.findBySysTypeAndSysKey(sysType, sysKey);
    	return  sysparams;
    }

    public int updateBySysKey(Sysparams sysparams) {
        int sysParams = sysparamsRepository.updateBySysKey(sysparams);
        return sysParams;
    }

}
