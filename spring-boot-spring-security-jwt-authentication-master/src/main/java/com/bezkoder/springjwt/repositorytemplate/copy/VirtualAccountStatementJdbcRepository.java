package com.bezkoder.springjwt.repositorytemplate.copy;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bezkoder.springjwt.entity.Sysparams;
import com.bezkoder.springjwt.entity.VirtualAccountStatement;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.repository.VirtualAccountRepository;
import com.bezkoder.springjwt.repository.VirtualAccountStatementRepository;

@Repository
public class VirtualAccountStatementJdbcRepository implements VirtualAccountStatementRepository {

	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Object> findByaccountid(String accountid, int days) {
		
		if(days==1)
		{
		return jdbcTemplate.query(
				 "select * from virtual_account_statement where transDateTime>= DATE_ADD(CURDATE(), INTERVAL 0 DAY) AND  accountid = ? order by transDateTime DESC",
				 new Object[]{accountid},
				 (rs, rowNum) ->
				 new VirtualAccountStatement(rs.getString("merchantid"),
						      				 rs.getString("accountid"),
						      				 rs.getString("referenceid"),
						      				 rs.getDouble("creditamount"),
						      				 rs.getDouble("debitamount"), 
						      				 rs.getDouble("balance"),
						      				 rs.getDate("transDateTime"),
						      				 rs.getString("batchID"), 
						      				 rs.getString("description"))
				 );
		}
		
		else if(days==7)
		{
			return jdbcTemplate.query(
					 "select * from virtual_account_statement where transDateTime>= DATE_ADD(CURDATE(), INTERVAL -7 DAY) AND  accountid = ? order by transDateTime DESC",
					 new Object[]{accountid},
					 (rs, rowNum) ->
					 new VirtualAccountStatement(rs.getString("merchantid"),
							      				 rs.getString("accountid"),
							      				 rs.getString("referenceid"),
							      				 rs.getDouble("creditamount"),
							      				 rs.getDouble("debitamount"), 
							      				 rs.getDouble("balance"),
							      				 rs.getDate("transDateTime"),
							      				 rs.getString("batchID"), 
							      				 rs.getString("description"))
					 );
		}
		
		else if(days==30)
		{
			return jdbcTemplate.query(
					 "select * from virtual_account_statement where transDateTime>= DATE_ADD(CURDATE(), INTERVAL -30 DAY) AND  accountid = ? order by transDateTime DESC",
					 new Object[]{accountid},
					 (rs, rowNum) ->
					 new VirtualAccountStatement(rs.getString("merchantid"),
							      				 rs.getString("accountid"),
							      				 rs.getString("referenceid"),
							      				 rs.getDouble("creditamount"),
							      				 rs.getDouble("debitamount"), 
							      				 rs.getDouble("balance"),
							      				 rs.getDate("transDateTime"),
							      				 rs.getString("batchID"), 
							      				 rs.getString("description"))
					 );
			
		}
		
		else {
			throw new GenericCustomException("No data found", new Date());
		}
	}

}
