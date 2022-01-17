package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.entity.Sysparams;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.services.SysparamsService;

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
@RequestMapping("/api/sysparam")
public class SysparamsController {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);


    @Autowired
    SysparamsService sysparamsService;

    @GetMapping("/getAllSysparams")
    public List<Object> getIssuerData() {
      try {
	     //Pageable page = PageRequest.of(offset, limit);
	    //Map<String, Object> transactionsMap = sysparamsService.findAll(page);
	    return sysparamsService.findAll();
      }
      
      catch (Exception e) {
    	   logger.error(e.getMessage());

    	  throw new GenericCustomException(e.getMessage(), new Date());
	}

    }

    @PostMapping("/getSysparamsBySysKey")
    public Object getSysparamsBySyskey(@RequestBody Sysparams reqsys) {
       try
       {
        return sysparamsService.findBySyskey(reqsys.getSysKey());
       }
       catch (Exception e) {
		    logger.error(e.getMessage());
		   throw new GenericCustomException(e.getMessage(), new Date());
	}

    }

    @PostMapping("/getSysparams")
    public List<Object> getSysparams(@RequestBody Sysparams reqsys) {
    	try 
    	{
    		List<Object>sysparams= sysparamsService.findBySysTypeAndSyskey(reqsys.getSysType(), 
    																	   reqsys.getSysKey());
    		if(sysparams!=null && !sysparams.isEmpty())
			{
				return  sysparams;
			}
			else {
				logger.error("IssuerBin is Empty");
				throw new GenericCustomException("sysparms is Empty", new Date());
			}
    	}
    	
        catch (Exception e) {
      	   logger.error(e.getMessage());
      	   throw new GenericCustomException(e.getMessage(), new Date());
        	
		}
    }

    @PutMapping("/EditSysparam")
    public Object editSysparam(@RequestBody Sysparams sysparams) {
        int sysparam = sysparamsService.updateBySysKey(sysparams);
 
        try {
        if (sysparam > 0) {
            return ResponseEntity.ok(new CommonReponse("Sysparam Updated Successfully"));
        } else if (sysparam <= 0) {
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Sysparam cannot be update"));
        }
        }
        catch (Exception e) {
      	   logger.error(e.getMessage());
		}

        return null;
    }

}
