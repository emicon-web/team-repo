package com.bezkoder.springjwt.controllers;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.springjwt.entity.Sysparams;
import com.bezkoder.springjwt.entity.VirtualAccountStatement;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.payload.VirtualAccountStatementPayload;
import com.bezkoder.springjwt.services.VirtualAccountStatementService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/virtualaccounts_statements")
public class VirtualAccountStatementController {
	
    private static final Logger logger = LogManager.getLogger(VirtualAccountController.class);

    
    @Autowired
    VirtualAccountStatementService virtualAccountStatementService;
    
    
    @PostMapping("/getVirtualAccountStatement/{accountid}/{days}")
    public List <Object> getSysparamsBySyskey(@PathVariable String accountid , @PathVariable int days) {
       try
       {
    	   return virtualAccountStatementService.findByAccountId(accountid,days);
       }
       catch (Exception e) {
    	   logger.error(e.getMessage());
    	   throw new GenericCustomException(e.getMessage(), new Date());
       }

    }

}
