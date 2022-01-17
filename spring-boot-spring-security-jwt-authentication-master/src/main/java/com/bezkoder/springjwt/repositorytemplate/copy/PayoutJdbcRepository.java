package com.bezkoder.springjwt.repositorytemplate.copy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import javax.validation.Valid;
import javax.validation.constraints.Null;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.bezkoder.springjwt.controllers.PayoutDataController;
import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.entity.Payout;
import com.bezkoder.springjwt.entity.Sysparams;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.DtreansactionReportPayload;
import com.bezkoder.springjwt.payload.PayoutPayload;
import com.bezkoder.springjwt.payload.TransactionReportDto;
import com.bezkoder.springjwt.payload.TransactionReportPayload;
import com.bezkoder.springjwt.payload.VirtualAccountDto;
import com.bezkoder.springjwt.repository.PayoutRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.utils.Utils;

import net.bytebuddy.implementation.bytecode.Throw;

@Repository
public class PayoutJdbcRepository implements PayoutRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	UserRepository userRepository;
	
    private static final Logger logger = LogManager.getLogger(PayoutDataController.class);


	@Override
	public int updatepayoutstatus(PayoutPayload payout) {
		String merchnatPayoutIdList = "";

		for(String str:payout.getMerchantPayoutId())
		{
			merchnatPayoutIdList+="merchantPayoutId= \""+str+"\" OR ";
			
		}
		
		merchnatPayoutIdList=merchnatPayoutIdList.substring(0, (merchnatPayoutIdList.length()-4));
		String payStatus = "select * from payout where "+merchnatPayoutIdList+"  AND  merchantid = \"" +payout.getMerchantid()+"\"";
		List<Payout> selectdata = jdbcTemplate.query(payStatus, new Object[]{}, new BeanPropertyRowMapper<Payout>(Payout.class));
        
		  String mrerchat = "Select enableCancelPayChecker from merchant_configuration where merchantid = \"" +payout.getMerchantid()+"\"";
	        boolean selectdata1 = jdbcTemplate.queryForObject(mrerchat, Boolean.class);
	        System.out.println(selectdata1);

		
		String payOutIds = "";
		
		for(Payout payout3 : selectdata)
		{
			if(payout3.getPayoutStatus().equals("READYFORPROCESSING"))
            {
              payOutIds+=payout3.getPayoutid() +",";
            }
			
			if(payout3.getPayoutStatus().equals("MARKFORCANCEL"))
            {
            	throw new GenericCustomException("The account is already processed for MARKFORCANCEL", new Date());
	        }
		}
		
		payOutIds=payOutIds.substring(0, (payOutIds.length()-1));
		if(selectdata1==false)
		{
		 return	jdbcTemplate.update(
	        		"update payout set payoutStatus = 'CANCELLED', canceledBy=?, cancelDate=?  WHERE payoutid  IN ( "+payOutIds+")",
					 payout.getCanceledBy(),
					 payout.getCancelDate());
			
		}
		
		else {
	
		return jdbcTemplate.update(
        		"update payout set payoutStatus = 'MARKFORCANCEL', canceledBy=?, cancelDate=?  WHERE payoutid  IN ( "+payOutIds+")",
				 payout.getCanceledBy(),
				 payout.getCancelDate());
		}
		 

 }

	@Override
	public int updatepayoutStatus(Payout payout,String action) {
		String payStatus = "select payoutStatus from payout where merchantPayoutId = \"" + payout.getMerchantPayoutId()+"\" ";
        List<Payout> selectdata = jdbcTemplate.query(payStatus, new Object[]{}, new BeanPropertyRowMapper<Payout>(Payout.class));
        
//        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
//                .get();
//        String sql1="SELECT createdBy FROM payout where payoutStatus= 'MARKFORCANCEL' AND merchantPayoutId = \"" + payout.getMerchantPayoutId()+"\" ";
//    	String name = jdbcTemplate.queryForObject(sql1, String.class);
//    	System.out.println(name);
//    	if(action.equals("APPROVE") && name.equals(user.getUserName()))
//    	{
//            throw new GenericCustomException("you are not authorized to approve the record as you are the creator of the same", new Date());
//
//    	}
//    	if(action.equals("REJECT") && name.equals(user.getUserName()))
//    	{
//            throw new GenericCustomException("you are not authorized to reject the record as you are the creator of the same", new Date());
//    	}
        
       for (Payout payout2 : selectdata) {
        	if(payout2.getPayoutStatus().equals("MARKFORCANCEL"))
        	{
        		if(action.equals("APPROVE"))  
        	    {
        		return jdbcTemplate.update(
        				"update payout set payoutStatus='CANCELLED' , approvedBy = ? , approvedDate =?  WHERE merchantPayoutId = ?",

        				payout.getApprovedBy(),
        				payout.getApproveDate(),
        				payout.getMerchantPayoutId());    
        	    }	if(action.equals("REJECT"))
        			
        		{
        			return jdbcTemplate.update(
        					"update payout set payoutStatus='READYFORPROCESSING' , approvedBy = ? , approvedDate =?  WHERE merchantPayoutId = ?",

        					payout.getApprovedBy(),
        					payout.getApproveDate(),
        					payout.getMerchantPayoutId()); 
        			
        		}
        		
        		
        	}

        	else if(payout2.getPayoutStatus().equals("CANCELLED"))
        	{
        		throw new GenericCustomException("The Account is already CANCELLED", new Date());

        	}

        	else if(!payout2.getPayoutStatus().equals("MARKFORCANCEL")) {
        		throw new GenericCustomException("The Account is ACTIVE please process it before sending for CANCELLATION ", new Date());
        	}
        
	}
	
	
       
        return 0;
      
	}

	@Override
	public Object findAll(Payout payout, Pageable page) {
		 StringBuilder sql = new StringBuilder();
	        sql.append("select * from payout where accountId = \""+payout.getAccountId()+"\" ");

	        sql.append(" LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
	        System.out.println("findAll query: " + sql);

	        Map<String, Object> results = new HashMap<>();
	        results.put("data", jdbcTemplate.query(sql.toString(),
	                (rs, rowNum) ->
	                        new Payout(
	                        		rs.getInt("payoutid"),
	                        		rs.getString("merchantid"), 
	                        		rs.getString("payoutFileUploadId"),
	                        		rs.getString("merchantPayoutId"),
	                        		rs.getString("accountId"),
	                        		rs.getString("beneficiaryName"),
	                        		rs.getString("payoutPaymentInstrument"),
	                        		rs.getString("payoutPaymentMode"), 
	                        		rs.getString("bankAccountName"),
	                        		rs.getString("beneficiaryAccountNumber"),
	                        		rs.getString("beneficiaryIFSCCode"), 
	                        		rs.getString("beneficiaryCardNumber"),
	                        		rs.getString("cardHolderName"),
	                        		rs.getString("cardHolderBankName"), 
	                        		rs.getString("cardHolderBankIFSCCode"),
	                        		rs.getString("beneficiaryVPA"),
	                        		rs.getString("payoutPurpose"),
	                        		rs.getString("payoutStatus"), 
	                        		rs.getDate("payoutDateTime"), 
	                        		rs.getDouble("amount"),
	                        		rs.getString("beneficiaryMobileNumber"),
	                        		rs.getString("beneficiaryEmailId"),
	                        		rs.getString("payoutType"),
	                        		rs.getString("batchId"),
	                        		rs.getString("responseDescription"), 
	                        		rs.getString("acquirerResponseCode"),
	                        		rs.getString("batchReferenceId"),
	                        		rs.getString("approvedBy"),
	                        		rs.getString("canceledBy"),
	                        		rs.getDate("cancelDate"),
	                        		rs.getDate("approvedDate"),
	                        		rs.getDate("createdDate"))

	        ));

	        return results;
	}

	@Override
	public List<Object> findAll(String merchantPayoutId, String accountId) {
		
		return jdbcTemplate.query(
				"select * from payout where payoutStatus= 'READYFORPROCESSING' AND  merchantPayoutId = ? AND accountId = ?",
				new Object[]{merchantPayoutId,accountId},
				(rs, rowNum) ->
				 new Payout(
                 		rs.getInt("payoutid"),
                 		rs.getString("merchantid"), 
                 		rs.getString("payoutFileUploadId"),
                 		rs.getString("merchantPayoutId"),
                 		rs.getString("accountId"),
                 		rs.getString("beneficiaryName"),
                 		rs.getString("payoutPaymentInstrument"),
                 		rs.getString("payoutPaymentMode"), 
                 		rs.getString("bankAccountName"),
                 		rs.getString("beneficiaryAccountNumber"),
                 		rs.getString("beneficiaryIFSCCode"), 
                 		rs.getString("beneficiaryCardNumber"),
                 		rs.getString("cardHolderName"),
                 		rs.getString("cardHolderBankName"), 
                 		rs.getString("cardHolderBankIFSCCode"),
                 		rs.getString("beneficiaryVPA"),
                 		rs.getString("payoutPurpose"),
                 		rs.getString("payoutStatus"), 
                 		rs.getDate("payoutDateTime"), 
                 		rs.getDouble("amount"),
                 		rs.getString("beneficiaryMobileNumber"),
                 		rs.getString("beneficiaryEmailId"),
                 		rs.getString("payoutType"),
                 		rs.getString("batchId"),
                 		rs.getString("responseDescription"), 
                 		rs.getString("acquirerResponseCode"),
                 		rs.getString("batchReferenceId"),
                 		rs.getString("approvedBy"),
                 		rs.getString("canceledBy"),
                		rs.getDate("cancelDate"),
                		rs.getDate("approvedDate"),
                		rs.getDate("createdDate"))
		);
	}

	@Override
	public Object findallMarkForCancleAccounts(String merchantid,Pageable page) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM payout where payoutStatus= 'MARKFORCANCEL' AND merchantid= ? ");

		sql.append(" LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
		System.out.println("findAll query: " + sql);
		
		Map<String, Object> results = new HashMap<>();
		results.put("data", jdbcTemplate.query(sql.toString(),
				new Object[]{merchantid},
				(rs, rowNum) ->
		new Payout(
				rs.getInt("payoutid"),
				rs.getString("merchantid"), 
				rs.getString("payoutFileUploadId"),
				rs.getString("merchantPayoutId"),
				rs.getString("accountId"),
				rs.getString("beneficiaryName"),
				rs.getString("payoutPaymentInstrument"),
				rs.getString("payoutPaymentMode"), 
				rs.getString("bankAccountName"),
				rs.getString("beneficiaryAccountNumber"),
				rs.getString("beneficiaryIFSCCode"), 
				rs.getString("beneficiaryCardNumber"),
				rs.getString("cardHolderName"),
				rs.getString("cardHolderBankName"), 
				rs.getString("cardHolderBankIFSCCode"),
				rs.getString("beneficiaryVPA"),
				rs.getString("payoutPurpose"),
				rs.getString("payoutStatus"), 
				rs.getDate("payoutDateTime"), 
				rs.getDouble("amount"),
				rs.getString("beneficiaryMobileNumber"),
				rs.getString("beneficiaryEmailId"),
				rs.getString("payoutType"),
				rs.getString("batchId"),
				rs.getString("responseDescription"), 
				rs.getString("acquirerResponseCode"),
				rs.getString("batchReferenceId"),
				rs.getString("approvedBy"),
				rs.getString("canceledBy"),
				rs.getDate("cancelDate"),
				rs.getDate("approvedDate"),
				rs.getDate("createdDate"))

				));
		return results;
		
		
		
	}

	@Override
	public Object findallReadyForProcess(Payout payout, Pageable page) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * from payout where payoutStatus= 'READYFORPROCESSING' AND payoutType = 'FILEUPLOAD' AND accountId = \""+payout.getAccountId()+"\" ");

		sql.append(" LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
		System.out.println("findAll query: " + sql);
		
		Map<String, Object> results = new HashMap<>();
		results.put("data", jdbcTemplate.query(sql.toString(),
				(rs, rowNum) ->
		new Payout(
				rs.getInt("payoutid"),
				rs.getString("merchantid"), 
				rs.getString("payoutFileUploadId"),
				rs.getString("merchantPayoutId"),
				rs.getString("accountId"),
				rs.getString("beneficiaryName"),
				rs.getString("payoutPaymentInstrument"),
				rs.getString("payoutPaymentMode"), 
				rs.getString("bankAccountName"),
				rs.getString("beneficiaryAccountNumber"),
				rs.getString("beneficiaryIFSCCode"), 
				rs.getString("beneficiaryCardNumber"),
				rs.getString("cardHolderName"),
				rs.getString("cardHolderBankName"), 
				rs.getString("cardHolderBankIFSCCode"),
				rs.getString("beneficiaryVPA"),
				rs.getString("payoutPurpose"),
				rs.getString("payoutStatus"), 
				rs.getDate("payoutDateTime"), 
				rs.getDouble("amount"),
				rs.getString("beneficiaryMobileNumber"),
				rs.getString("beneficiaryEmailId"),
				rs.getString("payoutType"),
				rs.getString("batchId"),
				rs.getString("responseDescription"), 
				rs.getString("acquirerResponseCode"),
				rs.getString("batchReferenceId"),
				rs.getString("approvedBy"),
				rs.getString("canceledBy"),
				rs.getDate("cancelDate"),
				rs.getDate("approvedDate"),
				rs.getDate("createdDate"))

				));
		return results;
	}

	@Override
	public int findCountById(String merchantid) {
		int count=0;

		String sql = "select count(*) from payout_file_upload where successRecordCount!=0  AND  status ='PENDING' AND merchantid= ?";
		count = jdbcTemplate.queryForObject(sql,new Object[]{merchantid}, Integer.class);
		return count;
	}

	@Override
	public int findinstaPayoutsCountById(String merchantid) {
		int count=0;

		String sql = "select count(*) from payout_staging where payoutType='INSTAPAY' AND payoutStatus='SUCCESS' and merchantid= ? ";
		count = jdbcTemplate.queryForObject(sql,new Object[]{merchantid}, Integer.class);
		return count;
	}
	
	@Override
	public int findselfPayoutsCountById(String merchantid) {
		int count=0;

		String sql = "select count(*) from payout_staging where payoutType='SELFPAY' AND payoutStatus='SUCCESS' and merchantid= ? ";
		count = jdbcTemplate.queryForObject(sql,new Object[]{merchantid}, Integer.class);
		return count;
	}
	
	@Override
	public int findinstaPayoutsApiCountById(String merchantid) {
		int count=0;

		String sql = "select count(*) from payout_staging where payoutType='API' AND payoutStatus='SUCCESS' and merchantid= ? ";
		count = jdbcTemplate.queryForObject(sql,new Object[]{merchantid}, Integer.class);
		return count;
	} 


	@Override
	public int findaccountCloseCountById(String merchantid) {
		int count=0;

		String sql = "select count(*) from virtual_account where  status ='PENDINGCLOSURE' AND  merchantid= ? ";
		count = jdbcTemplate.queryForObject(sql,new Object[]{merchantid}, Integer.class);
		return count;
	}

	@Override
	public int findcancelpayoutCountById(String merchantid) {
		int count=0;

		String sql = "select count(*) from payout where payoutStatus ='MARKFORCANCEL' AND  merchantid= ?";
		count = jdbcTemplate.queryForObject(sql,new Object[]{merchantid}, Integer.class);
		return count;
	}



	@Override
    public TransactionReportDto findTransactionReport(TransactionReportPayload transactionReportPayload, Pageable page) {
           TransactionReportDto response = new TransactionReportDto();

           StringBuilder transactionReportQuery = new StringBuilder();

           try {
        	  
        	  
        		   transactionReportQuery.append("SELECT * FROM  payout WHERE");

        		   if (!Utils.nullOrEmptyString(transactionReportPayload.getAccountId())) {
        			   transactionReportQuery.append(" accountId = '" + transactionReportPayload.getAccountId() + "'AND ");
        		   }
        		   
        		   if (transactionReportPayload.getFromDate()!=null && transactionReportPayload.getToDate()!=null) {
        	            final Timestamp fromTimeStamp = new Timestamp(Utils.getStartOfDay(transactionReportPayload.getFromDate()).getTime());
        	            final Timestamp toTimeStamp = new Timestamp(Utils.getEndOfDay(transactionReportPayload.getToDate()).getTime());

        			    transactionReportQuery.append(" createdDate BETWEEN \"" + fromTimeStamp + "\" AND \"" + toTimeStamp + "\" ");
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
        			   transactionReportQuery.append(" AND payoutPaymentInstrument = '" + transactionReportPayload.getPayoutPaymentInstrument() + "' ");
        		   }

        		   if (!Utils.nullOrEmptyString(transactionReportPayload.getPayoutPaymentMode())) {
        			   transactionReportQuery.append(" AND payoutPaymentMode = '" + transactionReportPayload.getPayoutPaymentMode() + "' ");
        		   }

        		   if (!Utils.nullOrEmptyString(transactionReportPayload.getBatchId())) {
        			   transactionReportQuery.append(" AND batchId = '" + transactionReportPayload.getBatchId() + "' ");
        		   }

        		   

        	   transactionReportQuery.append(" LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
        	   System.out.println("findAll query: " + transactionReportQuery);
        	   logger.info("getTransactionReport() - transactionReportQuery : " + transactionReportQuery);

        	   List<Payout> transactionData = jdbcTemplate.query(transactionReportQuery.toString(), new Object[]{}, new BeanPropertyRowMapper<Payout>(Payout.class));

        	   System.out.println(transactionData);


        	   if(null!=transactionData && transactionData.size()>0)
        	   {

        		   response.setTransactionData(transactionData);

        	   }

        	   else {
        		   throw new GenericCustomException("No data found", new Date());
        	   }
           }

           catch (Exception e) {
        	   throw new GenericCustomException(e.getMessage(), new Date());
           }

           return response;

	}
	
	@Override
	public TransactionReportDto findDefaultTransactionReport(DtreansactionReportPayload transactionReportPayload,Pageable page) {
	     TransactionReportDto response = new TransactionReportDto();

         StringBuilder transactionReportQuery = new StringBuilder();

         try {
      	  
        	 transactionReportQuery.append("SELECT * FROM  payout WHERE");

        	 if (!Utils.nullOrEmptyString(transactionReportPayload.getPayoutStatus())) {
        		 transactionReportQuery.append(" payoutStatus = '" + transactionReportPayload.getPayoutStatus() + "' AND ");
        	 }

        	 if (transactionReportPayload.getFromDate()!=null && transactionReportPayload.getToDate()!=null) {
        		 final Timestamp fromTimeStamp = new Timestamp(Utils.getStartOfDay(transactionReportPayload.getFromDate()).getTime());
        		 final Timestamp toTimeStamp = new Timestamp(Utils.getEndOfDay(transactionReportPayload.getToDate()).getTime());

        		 transactionReportQuery.append(" createdDate BETWEEN \"" + fromTimeStamp + "\" AND \"" + toTimeStamp + "\" ");
        	 }

        	 transactionReportQuery.append(" LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
        	 System.out.println("findAll query: " + transactionReportQuery);
        	 logger.info("getTransactionReport() - transactionReportQuery : " + transactionReportQuery);

        	 List<Payout> transactionData = jdbcTemplate.query(transactionReportQuery.toString(), new Object[]{}, new BeanPropertyRowMapper<Payout>(Payout.class));

        	 System.out.println(transactionData);


        	 if(null!=transactionData && transactionData.size()>0)
        	 {

        		 response.setTransactionData(transactionData);

        	 }

        	 else {
        		 throw new GenericCustomException("No data found", new Date());
        	 }
         }

         catch (Exception e) {
      	   throw new GenericCustomException(e.getMessage(), new Date());
         }

         return response;
	}

	@Override
	public String getPayoutStatusList() {
        String sql = "SELECT  COLUMN_TYPE FROM information_schema.`COLUMNS` WHERE TABLE_NAME = 'payout' AND COLUMN_NAME = 'payoutStatus' AND table_schema=DATABASE();";
        String payoutStatus= jdbcTemplate.queryForObject(sql,new Object[]{}, String.class);
		return payoutStatus;
	}

	
}
