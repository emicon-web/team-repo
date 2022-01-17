package com.bezkoder.springjwt.controllers;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.springjwt.entity.Ifscbankmaster;
import com.bezkoder.springjwt.entity.Payout;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.services.BankService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bank")
public class BankController {

    private static final Logger logger = LogManager.getLogger(PayoutDataController.class);
    
    @Autowired
    BankService bankService;
    
    @PostMapping("/getBankName")
    public List<Object> getBankNameById(@RequestBody Ifscbankmaster request) {
        try
        {
         return bankService.findBankName(request.getIfscprefix());
        }
        catch (Exception e) {
 		    logger.error(e.getMessage());
 		   throw new GenericCustomException(e.getMessage(), new Date());
        }
 	}

}
