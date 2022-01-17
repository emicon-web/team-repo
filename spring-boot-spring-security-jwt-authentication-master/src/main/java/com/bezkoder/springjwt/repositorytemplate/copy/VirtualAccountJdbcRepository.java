package com.bezkoder.springjwt.repositorytemplate.copy;

import com.bezkoder.springjwt.enums.VirtualAccountStatusEnum;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.VirtualAccountDto;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.repository.VirtualAccountRepository;
import com.bezkoder.springjwt.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class VirtualAccountJdbcRepository implements VirtualAccountRepository {

    private static final Logger logger = LogManager.getLogger(VirtualAccountJdbcRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    UserRepository userRepository;

    @Override
    public List<VirtualAccountDto> findAll(final String merchantId, final String status) {

        String sql;
        Object[] data;
        if (Utils.nullOrEmptyString(status)) {
            sql = "SELECT * FROM virtual_account WHERE merchantid = ? ";
            data = new Object[]{merchantId};
        } else {
            sql = "SELECT * FROM virtual_account WHERE merchantid = ? AND status = ? ";
            data = new Object[]{merchantId, status};
        }
        logger.info("findAll() - query : " + sql);
        List<VirtualAccountDto> virtualAccounts = null;
        try {
            virtualAccounts = jdbcTemplate.query(sql, data, (rs, rowNum) -> mapVirtualAccounts(rs));
        } catch (Exception e) {
            logger.error("Error while fetching all virtual accounts {}", e);
            throw new GenericCustomException("Error while fetching all virtual accounts ", new Date());
        }
        return virtualAccounts;
    }

    private VirtualAccountDto mapVirtualAccounts(final ResultSet rs) throws SQLException {
        VirtualAccountDto virtualAccount = new VirtualAccountDto();
        virtualAccount.setVirtualAccountID(rs.getString("virtualAccountID"));
        virtualAccount.setBalance(rs.getDouble("balance"));
        virtualAccount.setApprovedBy(rs.getString("approvedBy"));
        virtualAccount.setClosedBy(rs.getString("closedBy"));
        virtualAccount.setClosedDate(rs.getTimestamp("closedDate"));
        virtualAccount.setCreatedBy(rs.getString("createdBy"));
        virtualAccount.setDescription(rs.getString("description"));
        virtualAccount.setMerchantid(rs.getString("merchantid"));
        virtualAccount.setStatus(rs.getString("status"));
        virtualAccount.setCreatedDate(rs.getTimestamp("createdDate"));
        virtualAccount.setUpdatedAt(rs.getTimestamp("updatedAt"));
        return virtualAccount;
    }

    @Override
    public int updateStatus(VirtualAccountDto virtualAccountDto,String action) {
        String abc = "select status from virtual_account where virtualAccountID = \"" + virtualAccountDto.getVirtualAccountID() + "\" ";
        String selectdata = jdbcTemplate.queryForObject(abc, String.class);
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        String sql1="SELECT createdBy FROM virtual_account where status= 'PENDINGCLOSURE' AND virtualAccountID = \"" + virtualAccountDto.getVirtualAccountID() + "\" ";
    	String name = jdbcTemplate.queryForObject(sql1, String.class);
    	System.out.println(name);
    	if(action.equals("APPROVE") && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to approve the record as you are the creator of the same", new Date());

    	}
    	if(action.equals("REJECT") && name.equals(user.getUserName()))
    	{
            throw new GenericCustomException("you are not authorized to reject the record as you are the creator of the same", new Date());
    	}

        if (selectdata.equals("PENDINGCLOSURE")) {
           if(action.equals("APPROVE"))
           {
             return jdbcTemplate.update(
                    "update virtual_account set status='CLOSED' , approvedBy = ?  WHERE virtualAccountID = ?",

                    virtualAccountDto.getApprovedBy(),
                    virtualAccountDto.getVirtualAccountID());
           } 
           
           if(action.equals("REJECT"))
           {
        	   return jdbcTemplate.update(
                       "update virtual_account set status='ACTIVE' , approvedBy = ?  WHERE virtualAccountID = ?",

                       virtualAccountDto.getApprovedBy(),
                       virtualAccountDto.getVirtualAccountID());
           }
        }
        
       else if (selectdata.equals("CLOSED")) {
            throw new GenericCustomException("The Account is already closed", new Date());

        } else {
            throw new GenericCustomException("The Account is ACTIVE please process it before sending for clouser ", new Date());
        }
		return 0;
    }

    @Override
    public int updatevirtualAccountDto(VirtualAccountDto virtualAccountDto) {
        String abc = "select status from virtual_account where virtualAccountID = \"" + virtualAccountDto.getVirtualAccountID() + "\" AND merchantid = \""+virtualAccountDto.getMerchantid()+"\" ";
        String selectdata = jdbcTemplate.queryForObject(abc, String.class);
        
        String mrerchat = "Select enableCloseAcctChecker from merchant_configuration where merchantid = \"" +virtualAccountDto.getMerchantid()+"\"";
        boolean selectdata1 = jdbcTemplate.queryForObject(mrerchat, Boolean.class);
        System.out.println(selectdata1);


        System.out.println(selectdata);
        if (selectdata.equals("CLOSED")) {
            throw new GenericCustomException("The Account is already closed", new Date());
        }
        
        if(selectdata1==false)
        {
        	 return jdbcTemplate.update(
                     "update virtual_account set status='CLOSED' , closedDate = ? , closedBy =?, closeReason = ?  WHERE virtualAccountID = ?",

                     virtualAccountDto.getClosedDate(),
                     virtualAccountDto.getClosedBy(),
                     virtualAccountDto.getCloseReason(),
                     virtualAccountDto.getVirtualAccountID());
        	
        }
        
        else {
        return jdbcTemplate.update(
                "update virtual_account set status='PENDINGCLOSURE' , closedDate = ? , closedBy =?, closeReason = ?  WHERE virtualAccountID = ?",

                virtualAccountDto.getClosedDate(),
                virtualAccountDto.getClosedBy(),
                virtualAccountDto.getCloseReason(),
                virtualAccountDto.getVirtualAccountID());
        
        }
    }

    public VirtualAccountDto getVirtualAccount(final String virtualAccountId, String status) {
        if (Utils.nullOrEmptyString(virtualAccountId)) {
            return null;
        }

        String sql;
        Object[] data;
        if (Utils.nullOrEmptyString(status)) {
            sql = "SELECT * FROM virtual_account WHERE virtualAccountID = ? ";
            data = new Object[]{virtualAccountId};
        } else {
            sql = "SELECT * FROM virtual_account WHERE virtualAccountID = ? AND status = ? ";
            data = new Object[]{virtualAccountId, status};
        }
        try {
            logger.info("getVirtualAccount() - query : " + sql);
            return jdbcTemplate.queryForObject(sql, data, (rs, rowNum) -> mapVirtualAccounts(rs));
        } catch (Exception e) {
            logger.error("Error while fetching virtual account {}", e);
//            throw new GenericCustomException("Error while fetching virtual account ", new Date());
            return null;
        }
    }

    public boolean existsByVirtualAccountId(final String virtualAccountId) {
        if (Utils.nullOrEmptyString(virtualAccountId)) {
            return false;
        }
        try {
            final String query = "SELECT EXISTS ( SELECT * from virtual_account WHERE virtualAccountID = \"" + virtualAccountId + "\" )";
            logger.info("existsByVirtualAccountId() - query : " + query);
            return jdbcTemplate.queryForObject(query, Boolean.class);
        } catch (Exception e) {
            logger.error("Error while fetching/checking virtualAccountId {}", e);
            throw new GenericCustomException("Error while fetching/checking virtualAccountId ", new Date());
        }
    }

    @Override
    public int createVirtualAccount(VirtualAccountDto virtualAccountDto) {
        String sql = "INSERT INTO virtual_account (virtualAccountID, merchantid, description, balance, status, createdDate, updatedAt, createdBy) " +
                " VALUES (?, ?, ?, ?, ?, NOW(), NOW(), ?) ";

        try {
            logger.info("createVirtualAccount() - query : " + sql);
            int result = jdbcTemplate.update(sql, virtualAccountDto.getVirtualAccountID(), virtualAccountDto.getMerchantid(), virtualAccountDto.getDescription(),
                  virtualAccountDto.getBalance(), VirtualAccountStatusEnum.ACTIVE.name(), virtualAccountDto.getMerchantid());
            logger.info(" Virtual account created successfully ");
            return result;
        } catch (Exception e) {
            logger.error("Error while creating virtual account {}", e);
            throw new GenericCustomException("Error while creating virtual account ", new Date());
        }
    }

    @Override
    public List<Object> findallPendingAccounts(String merchantid) {
        return jdbcTemplate.query(
                "SELECT * FROM virtual_account where status= 'PENDINGCLOSURE' AND merchantid = ? order by updatedAt DESC",
				new Object[]{merchantid},
				(rs, rowNum) ->
                        new VirtualAccountDto(
                                rs.getString("virtualAccountID"),
                                rs.getString("merchantid"),
                                rs.getString("description"),
                                rs.getDouble("balance"),
                                rs.getString("status"),
                                rs.getTimestamp("closedDate"),
                                rs.getString("closedBy"),
                                rs.getTimestamp("createdDate"),
                                rs.getTimestamp("updatedAt"),
                                rs.getString("createdBy"),
                                rs.getString("closeReason"),
                                rs.getString("approvedBy"))
        );
    }
}
