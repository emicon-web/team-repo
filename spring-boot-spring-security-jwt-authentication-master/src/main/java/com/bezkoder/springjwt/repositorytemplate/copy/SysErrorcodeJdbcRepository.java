package com.bezkoder.springjwt.repositorytemplate.copy;

import com.bezkoder.springjwt.entity.Syserrorcode;
import com.bezkoder.springjwt.repository.SyserrorcodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SysErrorcodeJdbcRepository implements SyserrorcodeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Object> findAll() {
//		StringBuilder sql = new StringBuilder();
//		sql.append("select * from syserrorcode" +  " LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
//		Map<String, Object> results = new HashMap<>();

        return jdbcTemplate.query(
                "SELECT * FROM syserrorcode",
                (rs, rowNum) ->
                        new Syserrorcode(rs.getString("sysID"), rs.getString("errorCode"), rs.getString("mappedErrorCode"), rs.getDate("createdAt"), rs.getDate("updatedAt"), rs.getString("createdBy"), rs.getString("lastUpdatedBy"))

        );

    }

    @Override
    public Object findBySysID(String sysID) {
        return jdbcTemplate.queryForObject(
                "select * from syserrorcode where sysID = ?",
                new Object[]{sysID},
                (rs, rowNum) ->
                        new Syserrorcode(rs.getString("sysID"), rs.getString("errorCode"), rs.getString("mappedErrorCode"), rs.getDate("createdAt"), rs.getDate("updatedAt"), rs.getString("createdBy"), rs.getString("lastUpdatedBy"))
        );
    }

    @Override
    public Syserrorcode findBySysIDAndErrorCode(String sysID, String errorCode) {
        return jdbcTemplate.queryForObject(
                "select * from syserrorcode where sysID = ? and errorCode = ?",
                new Object[]{sysID, errorCode},
                (rs, rowNum) ->
                        new Syserrorcode(rs.getString("sysID"),
                                rs.getString("errorCode"),
                                rs.getString("mappedErrorCode"),
                                rs.getDate("createdAt"),
                                rs.getDate("updatedAt"),
                                rs.getString("createdBy"),
                                rs.getString("lastUpdatedBy"))
        );
    }

    @Override
    public int updateBySysId(Syserrorcode syserrorcode) {
        return jdbcTemplate.update(
                "update syserrorcode set  mappedErrorCode = ?, createdAt = ?, updatedAt = ?, createdBy = ?, lastUpdatedBy = ?  where sysID = ? AND errorCode = ? ",
                 syserrorcode.getMappedErrorCode(), syserrorcode.getCreatedAt(), syserrorcode.getUpdatedAt(), syserrorcode.getCreatedBy(), syserrorcode.getLastUpdatedBy(), syserrorcode.getSysID(),syserrorcode.getErrorCode());

    }

}
