package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.entity.Syserrorcode;
import com.bezkoder.springjwt.repository.SyserrorcodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyserrorcodeService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    SyserrorcodeRepository syserrorcodeRepository;

    public List<Object> findAll() {

//		    Map<String, Object> results = new HashMap<>();
//	        StringBuilder sql1 = new StringBuilder();
//	        sql1.append("select count(*) from syserrorcode");

//	        results.put("data",syserrorcodeRepository.findAll());
//	        results.put("count", jdbcTemplate .queryForObject(sql1.toString(), Integer.class));

        return syserrorcodeRepository.findAll();

    }

    public Object findBySysId(String sysID) {
        return syserrorcodeRepository.findBySysID(sysID);

    }

    public Syserrorcode findBySysIDAndErrorCode(String sysID, String errorCode) {
        return syserrorcodeRepository.findBySysIDAndErrorCode(sysID, errorCode);

    }

    public int updateBySysId(Syserrorcode syserrorcode) {
        int sysErrorcode = syserrorcodeRepository.updateBySysId(syserrorcode);
        return sysErrorcode;
    }


}
