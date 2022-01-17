package com.bezkoder.springjwt.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bezkoder.springjwt.entity.Payout;
import com.bezkoder.springjwt.payload.DtreansactionReportPayload;
import com.bezkoder.springjwt.payload.PayoutPayload;
import com.bezkoder.springjwt.payload.TransactionReportDto;
import com.bezkoder.springjwt.payload.TransactionReportPayload;
import com.bezkoder.springjwt.repository.PayoutRepository;
import com.bezkoder.springjwt.utils.Utils;

@Service
public class PayoutService {
	
	@Autowired
	PayoutRepository payoutRepository;
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public int payOutStatus(PayoutPayload payout) {
		 int payOutStatus = payoutRepository.updatepayoutstatus(payout);
	     return payOutStatus;
	}

	public int updateBymerchnatId(Payout payout, String action) {
		 int payoutcancle = payoutRepository.updatepayoutStatus(payout,action);
	        return payoutcancle;
	}

	public Map<String, Object> findAll(Payout payout, Pageable page ) {
		 Map<String, Object> results = new HashMap<>();
	        StringBuilder sql1 = new StringBuilder();
	        sql1.append("select count(*) from payout ");

			
			System.out.println("findAll count query: " + sql1);
			
	        results.put("data",payoutRepository.findAll(payout, page));
	        results.put("count", jdbcTemplate .queryForObject(sql1.toString(), Integer.class));
	         
			return results;
	}

//	public List<Object> findById(String merchantid) {
//        return payoutRepository.findMerchantId(merchantid);
//
//	}

	public List<Object> findAll(String merchantPayoutId, String accountId) {
        return payoutRepository.findAll(merchantPayoutId,accountId);

	}

//	public List<Object> findAllMarkForCancleList() {
//		List<Object> payoutcancle = payoutRepository.findallMarkForCancleAccounts();
//		return payoutcancle;
//	}

//	public List<Object> findAllReadyForProcessList(Payout payout, Pageable page) {
//		List<Object> payoutreadyforprocess = payoutRepository.findallReadyForProcess();
//		return payoutreadyforprocess;
//	}
	
	public Map<String, Object> findAllReadyForProcessList(Payout payout, Pageable page ) {
		 Map<String, Object> results = new HashMap<>();
	        StringBuilder sql1 = new StringBuilder();
	        sql1.append("select count(*) from payout where payoutStatus= 'READYFORPROCESSING' AND payoutType = 'FILEUPLOAD' AND accountId = \""+payout.getAccountId()+"\" ");

			
			System.out.println("findAll count query: " + sql1);
			
	        results.put("data",payoutRepository.findallReadyForProcess(payout,page));
	        results.put("count", jdbcTemplate .queryForObject(sql1.toString(), Integer.class));
	         
			return results;
	}

	public int getuploadfileapprovalCount(String merchantid) {
		int uploadfileapprovalCount = payoutRepository.findCountById(merchantid);
		return uploadfileapprovalCount;

	}

	public int getinstaPayoutsCount(String merchantid) {
		int instaPayoutsCount = payoutRepository.findinstaPayoutsCountById(merchantid);
		return instaPayoutsCount;
	}
	
	
	public int getselfPayoutsApiCount(String merchantid) {
		int instaselfPayoutsCount = payoutRepository.findselfPayoutsCountById(merchantid);
		return instaselfPayoutsCount;
	}
	
	public int getinstaPayoutsApiCount(String merchantid) {
		int instaPayoutsApiCount = payoutRepository.findinstaPayoutsApiCountById(merchantid);
		return instaPayoutsApiCount;
	}

	public int getaccountCloseCount(String merchantid) {
		int accountCloseCount = payoutRepository.findaccountCloseCountById(merchantid);
		return accountCloseCount;
	}

	public int getcancelPayoutCount(String merchantid) {
		int cancelPayoutCount = payoutRepository.findcancelpayoutCountById(merchantid);
		return cancelPayoutCount;
	}

	public Map<String, Object> findAllMarkForCancleList(String merchantid, Pageable page ) {
		 Map<String, Object> results = new HashMap<>();
	        StringBuilder sql1 = new StringBuilder();
	        sql1.append("select count(*) from payout where payoutStatus= 'MARKFORCANCEL'  AND merchantid = \""+merchantid+"\" ");

			
			System.out.println("findAll count query: " + sql1);
			
	        results.put("data",payoutRepository.findallMarkForCancleAccounts(merchantid,page));
	        results.put("count", jdbcTemplate .queryForObject(sql1.toString(), Integer.class));
	         
			return results;
	}

	
	public Map<String, Object> getTransactionReport(TransactionReportPayload transactionReportPayload, Pageable page) {
		
		 Map<String, Object> results = new HashMap<>();
	        StringBuilder transactionReportQuery = new StringBuilder();

	          transactionReportQuery.append("SELECT count(*) FROM  payout WHERE");

     		   if (!Utils.nullOrEmptyString(transactionReportPayload.getAccountId())) {
     			   transactionReportQuery.append(" accountId = '" + transactionReportPayload.getAccountId() + "' ");
     		   }

     		   if (transactionReportPayload.getPayoutid()!=null) {
     			   transactionReportQuery.append(" AND payoutid = '" + transactionReportPayload.getPayoutid() + "' ");
     		   }

     		   if (!Utils.nullOrEmptyString(transactionReportPayload.getMerchantPayoutId())) {
     			   transactionReportQuery.append(" AND merchantPayoutId = '" + transactionReportPayload.getMerchantPayoutId() + "' ");
     		   }

     		   if (!Utils.nullOrEmptyString(transactionReportPayload.getPayoutStatus())) {
     			   transactionReportQuery.append(" AND payoutStatus = '" + transactionReportPayload.getPayoutStatus() + "' ");
     		   }

     		   if (!Utils.nullOrEmptyString(transactionReportPayload.getPayoutPaymentInstrument())) {
     			   transactionReportQuery.append(" AND payoutPaymentInstrument IN ('" + transactionReportPayload.getPayoutPaymentInstrument() + "') ");
     		   }

     		   if (!Utils.nullOrEmptyString(transactionReportPayload.getPayoutPaymentMode())) {
     			   transactionReportQuery.append(" AND payoutPaymentMode IN( '" + transactionReportPayload.getPayoutPaymentMode() + "') ");
     		   }

     		   if (!Utils.nullOrEmptyString(transactionReportPayload.getBatchId())) {
     			   transactionReportQuery.append(" AND batchId = '" + transactionReportPayload.getBatchId() + "' ");
     		   }

     		   if (transactionReportPayload.getFromDate()!=null && transactionReportPayload.getToDate()!=null) {
     			  final Timestamp fromTimeStamp = new Timestamp(Utils.getStartOfDay(transactionReportPayload.getFromDate()).getTime());
     			  final Timestamp toTimeStamp = new Timestamp(Utils.getEndOfDay(transactionReportPayload.getToDate()).getTime());
     			  transactionReportQuery.append(" AND createdDate BETWEEN \"" + fromTimeStamp + "\" AND \"" + toTimeStamp + "\" ");
     		   }
			
	      
     	  results.put("data",  payoutRepository.findTransactionReport(transactionReportPayload,page));
	      results.put("count", jdbcTemplate .queryForObject(transactionReportQuery.toString(), Integer.class));
   		  return results;

	}
	
	public Map<String, Object> getDefaultTransactionReport(DtreansactionReportPayload transactionReportPayload,
			Pageable page) {
		Map<String, Object> results = new HashMap<>();
        StringBuilder transactionReportQuery = new StringBuilder();

          transactionReportQuery.append("SELECT count(*) FROM  payout WHERE");

 		  

 		   if (!Utils.nullOrEmptyString(transactionReportPayload.getPayoutStatus())) {
 			   transactionReportQuery.append(" payoutStatus = '" + transactionReportPayload.getPayoutStatus() + "'");
 		   }

 		   if (transactionReportPayload.getFromDate()!=null && transactionReportPayload.getToDate()!=null) {
 			  
 			  final Timestamp fromTimeStamp = new Timestamp(Utils.getStartOfDay(transactionReportPayload.getFromDate()).getTime());
 			  final Timestamp toTimeStamp = new Timestamp(Utils.getEndOfDay(transactionReportPayload.getToDate()).getTime());
 			  transactionReportQuery.append(" AND createdDate BETWEEN \"" + fromTimeStamp + "\" AND \"" + toTimeStamp + "\" ");
 		   }
		
      
 	  results.put("data",  payoutRepository.findDefaultTransactionReport(transactionReportPayload,page));
      results.put("count", jdbcTemplate .queryForObject(transactionReportQuery.toString(), Integer.class));
		  return results;

	}


	public List<String> findPayoutStatus() {
		
		String temp = payoutRepository.getPayoutStatusList();

		temp=temp.substring(5, temp.length()-1);
 		temp=temp.replaceAll("'","");
 		

		List<String> list = Arrays.asList(temp.split(","));
		return list;

		 
	}

}
