package com.bezkoder.springjwt.controllers;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.springjwt.entity.Merchant;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.services.MerchantService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    private static final Logger logger = LogManager.getLogger(VirtualAccountController.class);
    
    @Autowired
    MerchantService merchantService;

    @PostMapping(path = "/getMerchantListForAgg")
    public ResponseEntity getMerchantListForAgg(@RequestBody Merchant merchant)
    {
    	try {
    		
           List<Object> merchantList = merchantService.findMerchantAggList(merchant.getMerchantid());
           ResponseEntity<Object> response= new ResponseEntity<Object>(merchantList, HttpStatus.OK);
       	   return response;

        	}
        	catch (Exception e) {
         	   logger.error(e.getMessage());

        		throw new GenericCustomException(e.getMessage(), new Date());
    		}
    }

	
}
