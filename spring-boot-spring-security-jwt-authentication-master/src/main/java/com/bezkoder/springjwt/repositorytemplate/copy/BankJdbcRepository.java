package com.bezkoder.springjwt.repositorytemplate.copy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bezkoder.springjwt.entity.Bank;
import com.bezkoder.springjwt.entity.Ifscbankmaster;
import com.bezkoder.springjwt.entity.Payout;
import com.bezkoder.springjwt.repository.BankRepository;

@Repository
public class BankJdbcRepository implements BankRepository{
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Object> finBankName(String ifscprefix) {
		
		
		
		return jdbcTemplate.query(
				"SELECT  bank.bankname FROM bank,ifscbankmaster WHERE ifscbankmaster.bankid = bank.sysBankCode AND  ifscprefix = ?",
				new Object[]{ifscprefix},
				(rs, rowNum) ->
				 new Bank(
                 		rs.getString("bankName"))
		);
		
		
		
	}

}
