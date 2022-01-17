package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.entity.Syserrorcode;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.services.SyserrorcodeService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/syserrorcode")
public class SyserrorcodeController {

	private static final Logger logger = LogManager.getLogger(UserController.class);

	
    @Autowired
    SyserrorcodeService syserrorcodeService;

    @GetMapping("/getAllSyserrorcodes")
    public List<Object> getSysErroeCodeData() {
    	try {
        return syserrorcodeService.findAll();
    	}
    	catch (Exception e) {
     	   logger.error(e.getMessage());

    		throw new GenericCustomException(e.getMessage(), new Date());
		}
//		
//		 Pageable page = PageRequest.of(offset, limit);
//	     Map<String, Object> transactionsMap = syserrorcodeService.findAll(page);
//		 return transactionsMap;

    }


    @PostMapping("/getSyserrorcodeBySysId")
    public Object getSysparamsBySyskey(@RequestBody Syserrorcode request) {
    	try 
    	{
    		return syserrorcodeService.findBySysId(request.getSysID());
    	}
    	catch (Exception e) {
      	   logger.error(e.getMessage());

    		throw new GenericCustomException(e.getMessage(), new Date());
		}

    }

    @PostMapping("/getSyserrorcode")
    public Object getSyserrorcode(@RequestBody Syserrorcode request) {
        try {
        return syserrorcodeService.findBySysIDAndErrorCode(request.getSysID(), request.getErrorCode());
        }
        catch (Exception e) {
      	   logger.error(e.getMessage());
      	   throw new GenericCustomException(e.getMessage(), new Date());
		}

    }

    @PutMapping("/EditSysErrorCode")
    public Object editSysparam(@RequestBody Syserrorcode syserrorcode) {
        int sysErrorcode = syserrorcodeService.updateBySysId(syserrorcode);

        try {
        	if (sysErrorcode > 0) {
        		return ResponseEntity.ok(new CommonReponse("SysErrorcode Updated Successfully"));
        	} else if (sysErrorcode <= 0) {
        		return ResponseEntity.badRequest()
        				.body(new CommonReponse("SysErrorcode cannot be update"));
        	}
        }

        catch (Exception e) {
      	   logger.error(e.getMessage());

        }

        return null;
    }


}
