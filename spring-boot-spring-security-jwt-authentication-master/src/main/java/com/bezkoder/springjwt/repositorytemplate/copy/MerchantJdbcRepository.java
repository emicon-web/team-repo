package com.bezkoder.springjwt.repositorytemplate.copy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bezkoder.springjwt.entity.Merchant;
import com.bezkoder.springjwt.entity.Syserrorcode;
import com.bezkoder.springjwt.repository.MerchantRepository;

@Repository
public class MerchantJdbcRepository implements MerchantRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Object> findMerchantAggList(String merchantid) {
		return jdbcTemplate.query(
                "select merchantid,name,displayName from merchant where merchantid = ? AND merchantid=aggMerchantID",
                new Object[]{merchantid},
                (rs, rowNum) ->
                        new Merchant(rs.getString("merchantid"),
                        		rs.getString("name"), 
                        		rs.getString("displayName"))
        );
	}

}
