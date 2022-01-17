package com.bezkoder.springjwt.repositorytemplate.copy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bezkoder.springjwt.entity.Sysparams;
import com.bezkoder.springjwt.repository.SysparamsRepository;

@Repository
public class SysParamsJdbcRepository implements SysparamsRepository {
	
	@Autowired
	 private JdbcTemplate jdbcTemplate;

	@Override
	public List<Object> findAll() {
//		StringBuilder sql = new StringBuilder();
//		sql.append("select * from sysparams" +  " LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
//		Map<String, Object> results = new HashMap<>();
		return jdbcTemplate.query(
				 "SELECT * FROM sysparams",
				 (rs, rowNum) ->
				 new Sysparams(rs.getString("sysType"), rs.getString("sysKey"), rs.getString("sysValue"), rs.getString("description"), rs.getString("addProp"), rs.getString("createdBy"), rs.getString("lastUpdatedBy"), rs.getDate("createdAt"), rs.getDate("updatedAt"))

				 );
		
	}

	@Override
	public Object findBySysKey(String sysKey) {
		
	return jdbcTemplate.queryForObject(
				 "select * from sysparams where sysKey = ?",
				 new Object[]{sysKey},
				 (rs, rowNum) ->
				 new Sysparams(rs.getString("sysType"), rs.getString("sysKey"), rs.getString("sysValue"), rs.getString("description"), rs.getString("addProp"), rs.getString("createdBy"), rs.getString("lastUpdatedBy"), rs.getDate("createdAt"), rs.getDate("updatedAt"))
				 );
	}

	@Override
	public List<Object> findBySysTypeAndSysKey(String sysType, String sysKey) {

		return jdbcTemplate.query(
				"select * from sysparams where sysType = ? and sysKey = ?",
				new Object[]{sysType, sysKey},
				(rs, rowNum) ->
						new Sysparams(rs.getString("sysType"),
								rs.getString("sysKey"),
								rs.getString("sysValue"),
								rs.getString("description"),
								rs.getString("addProp"),
								rs.getString("createdBy"),
								rs.getString("lastUpdatedBy"),
								rs.getDate("createdAt"),
								rs.getDate("updatedAt"))
		);
	}

	@Override
	public int updateBySysKey(Sysparams sysparams) {
		return jdbcTemplate.update(
				"update sysparams set sysValue = ?, description = ?, addProp = ?, createdBy = ?, lastUpdatedBy = ?, createdAt = ? , updatedAt = ?  where sysKey = ? and  sysType = ?",
				 sysparams.getSysValue(), sysparams.getDescription(), sysparams.getAddProp(),sysparams.getCreatedBy(), sysparams.getLastUpdatedBy(),sysparams.getCreatedAt(),sysparams.getUpdatedAt(),sysparams.getSysKey(),sysparams.getSysType());
		
	}

}
