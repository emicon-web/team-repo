package com.bezkoder.springjwt.services;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bezkoder.springjwt.controllers.RoleController;
import com.bezkoder.springjwt.entity.UserRoles;
import com.bezkoder.springjwt.errors.GenericCustomException;

@Service
public class UserRoleService {
	
	private static final Logger logger = LogManager.getLogger(RoleController.class);
	
	@Autowired
	private MessageSource messages;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	UserRoles userRoles;
	
	
	
	public Integer userrole(int id) {
		Integer data=0;
	    String sql= "Select role_id from user_roles where role_id= \"" + id + "\" " ;
	    List<Integer> selectdata= jdbcTemplate.queryForList(sql, Integer.class);
	    System.out.println(selectdata);
	    
	    if(!selectdata.isEmpty())
	    {
	    	logger.error(messages.getMessage("role.message.cannotDeleteRole", null ,null));
	    	throw new GenericCustomException(messages.getMessage("role.message.cannotDeleteRole" ,null, null), new Date());
	    }
	    
	    else {
	    	
	    	String deleteQuery = "delete from role_actions where role_id  = ?";
	    	data=jdbcTemplate.update(deleteQuery, id);
	    	System.out.println(data);
	    }
		return data;
	}
	    

}
