package com.bezkoder.springjwt.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.entity.Payout;
import com.bezkoder.springjwt.entity.Sysparams;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.DtreansactionReportPayload;
import com.bezkoder.springjwt.payload.PayoutPayload;
import com.bezkoder.springjwt.payload.ProcessCountPayload;
import com.bezkoder.springjwt.payload.TransactionReportDto;
import com.bezkoder.springjwt.payload.TransactionReportPayload;
import com.bezkoder.springjwt.payload.VirtualAccountDto;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.MerchantConfigurationService;
import com.bezkoder.springjwt.services.PayoutService;
import com.bezkoder.springjwt.utils.Utils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payout")
public class PayoutController {
	
    private static final Logger logger = LogManager.getLogger(PayoutDataController.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PayoutService payoutService;
    
    @Autowired
    MerchantConfigurationService merchantConfigurationService;
    
    
    @PostMapping("/getAllPayouts")
	public  Map<String, Object> getIssuerData(@RequestParam(value = "limit", defaultValue = "25") Integer limit,
                                              @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                              @RequestBody Payout payout)
	{
		
           try
           {
		    Pageable page = PageRequest.of(offset, limit);
	        Map<String, Object> transactionsMap = payoutService.findAll(payout,page);
		    return transactionsMap;
           }
           
           catch (Exception e) {
        	   logger.error(e.getMessage());
        	   throw new GenericCustomException(e.getMessage(), new Date());
		}
	}
    
    @PostMapping("/getAllPayoutsById")
    @PreAuthorize("hasAuthority('VIEW_CANCEL_PAYOUTS')")
    public List<Object> getPayOutsById(@RequestBody Payout request) {
        try
        {
         return payoutService.findAll(request.getMerchantPayoutId(),
        		                      request.getAccountId());
        }
        catch (Exception e) {
 		    logger.error(e.getMessage());
 		   throw new GenericCustomException(e.getMessage(), new Date());
        }
 	}
    
    @PostMapping("/readyForProcessList")
    @PreAuthorize("hasAuthority('VIEW_CANCEL_PAYOUTS')")
   	public  Map<String, Object> getAllReadyForProcessList(@RequestParam(value = "limit", defaultValue = "25") Integer limit,
                                                 @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                 @RequestBody Payout payout)
   	{
   		
              try
              {
   		    Pageable page = PageRequest.of(offset, limit);
   	        Map<String, Object> transactionsMap = payoutService.findAllReadyForProcessList(payout,page);
   		    return transactionsMap;
              }
              
              catch (Exception e) {
           	   logger.error(e.getMessage());
           	   throw new GenericCustomException(e.getMessage(), new Date());
   		}
   	}
    

	@PostMapping(path = "/markForCancleList") 
    @PreAuthorize("hasAuthority('CANCEL_PAYOUT_BUTTON')")
	public Map<String, Object> getAllMarkForCancleAccounts(@RequestParam(value = "limit", defaultValue = "25") Integer limit,
            											   @RequestParam(value = "offset", defaultValue = "0") Integer offset,
			                                               @RequestBody Payout payout) 
	{
    	
    	try
        {
   		    Pageable page = PageRequest.of(offset, limit);
   	        Map<String, Object> transactionsMap = payoutService.findAllMarkForCancleList(payout.getMerchantid(),page);

         return  transactionsMap;
        }
        catch (Exception e) {
 		    logger.error(e.getMessage());
 		   throw new GenericCustomException(e.getMessage(), new Date());
        }
        
    }
	

    @PostMapping(path ="/canclePayout")
    ResponseEntity setPayoutStatus(@RequestBody PayoutPayload payout)
    {
    
    	int payoutstatus =0;

    	try {

    		payoutstatus = payoutService.payOutStatus(payout);
    	}
    	catch (Exception e) {
    		logger.error( e.getMessage());
    		return ResponseEntity.badRequest().body(new CommonReponse(e.getMessage()));
    	}
    
    User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                 .get();
    MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationService.findBymerchantId(user.getMerchantId());
   System.out.println(merchantConfiguration.isEnableCancelPayChecker());

    	if (payoutstatus > 0 && merchantConfiguration.isEnableCancelPayChecker()==true) {
    		return ResponseEntity.ok(new CommonReponse("Selected payouts are successfully submitted for approval"));
    	}
    	
    	if(payoutstatus > 0 && merchantConfiguration.isEnableCancelPayChecker()==false)
    	{
    		return ResponseEntity.ok(new CommonReponse("Selected payouts are successfully CANCELLED"));
    	}

    	else if (payoutstatus <= 0) {
    		return ResponseEntity.badRequest()
    				.body(new CommonReponse("Payout Status cannot be update"));
    	}

    	return null;
    }
    
    
    @PostMapping(
            path = "/approvedPayoutCancellation")
    public ResponseEntity setapprovedStatus(@RequestBody Payout payout,@RequestParam String action) {

    	int canclestatus = 0;

        try {
        		canclestatus = payoutService.updateBymerchnatId(payout,action);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(new CommonReponse(e.getMessage()));
        }

        if (canclestatus > 0 && action.equals("APPROVE")) {
            return ResponseEntity.ok(new CommonReponse("Payout approved Status Updated Successfully"));
        }
        if(action.equals("REJECT"))
        {
            return ResponseEntity.ok(new CommonReponse("Payout approved Status Rejected Successfully"));

        }
        
        else if (canclestatus <= 0) {
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Payout approved cannot be update"));
        }
        
        return null;

    }
    
    
    @PostMapping("/getPendingCount")
	public Object getinstrumrntcount(@RequestBody ProcessCountPayload request)
	{
		int  uploadfileapproval=0;
		int  instaPayouts=0;
		int instaPayoutApi=0;
		int selfpay=0;
		int  accountClose=0;
		int cancelPayout=0;
		HashMap<String, Integer> map = new HashMap<>();
		try {
			uploadfileapproval = payoutService.getuploadfileapprovalCount(request.getMerchantid());
			instaPayouts = payoutService.getinstaPayoutsCount(request.getMerchantid());
			instaPayoutApi = payoutService.getinstaPayoutsApiCount(request.getMerchantid());
			selfpay = payoutService.getselfPayoutsApiCount(request.getMerchantid());
			accountClose = payoutService.getaccountCloseCount(request.getMerchantid());
			cancelPayout = payoutService.getcancelPayoutCount(request.getMerchantid());
			map.put("uploadfileapprovalCount", uploadfileapproval);
			map.put("instaPayoutsCount", instaPayouts);
			map.put("instaPayoutsApiCount", instaPayoutApi);
			map.put("selfpayCount", selfpay);
			map.put("accountCloseCount", accountClose);
			map.put("cancelPayoutCount", cancelPayout);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}
		return map;

	}
    
    @PostMapping(path = "/transactionReport")
	public ResponseEntity getSummaryReport(HttpServletRequest request,
			@RequestParam(value = "limit", defaultValue = "25") Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
			@Valid @RequestBody TransactionReportPayload transactionReportPayload) {
    	
    	Map<String, Object> payout = null;
    	try {
   		     Pageable page = PageRequest.of(offset, limit);
   		     payout = payoutService.getTransactionReport(transactionReportPayload,page);
    	}
    	catch (Exception e) {
    		return ResponseEntity.ok().body(new CommonReponse(e.getMessage()));
		}
    	
		return ResponseEntity.ok(payout);

	}
    
    @PostMapping(path = "/defaultTransactionReport")
   	public ResponseEntity getTransactionReportReport(HttpServletRequest request,
   			@RequestParam(value = "limit", defaultValue = "25") Integer limit,
               @RequestParam(value = "offset", defaultValue = "0") Integer offset,
   			@Valid @RequestBody DtreansactionReportPayload transactionReportPayload) {
       	
       	Map<String, Object> payout = null;
       	try {
      		     Pageable page = PageRequest.of(offset, limit);
      		     payout = payoutService.getDefaultTransactionReport(transactionReportPayload,page);
       	}
       	catch (Exception e) {
       		return ResponseEntity.ok().body(new CommonReponse(e.getMessage()));
   		}
       	
   		return ResponseEntity.ok(payout);

   	}
    
    @GetMapping(path = "/getPayoutStatusList")
    public ResponseEntity getPayoutStatusList()
    {
    	try {
    		
           List<String> payoutstatus=  payoutService.findPayoutStatus();
           ResponseEntity<Object> response= new ResponseEntity<Object>(payoutstatus, HttpStatus.OK);
       	   return response;

        	}
        	catch (Exception e) {
         	   logger.error(e.getMessage());

        		throw new GenericCustomException(e.getMessage(), new Date());
    		}
    }


}
