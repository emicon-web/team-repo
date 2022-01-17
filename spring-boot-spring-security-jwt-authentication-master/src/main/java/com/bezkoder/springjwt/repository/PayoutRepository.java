package com.bezkoder.springjwt.repository;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.bezkoder.springjwt.entity.Payout;
import com.bezkoder.springjwt.payload.DtreansactionReportPayload;
import com.bezkoder.springjwt.payload.PayoutPayload;
import com.bezkoder.springjwt.payload.TransactionReportDto;
import com.bezkoder.springjwt.payload.TransactionReportPayload;

public interface PayoutRepository {

	int updatepayoutstatus(PayoutPayload payout);

	int updatepayoutStatus(Payout payout, String action);

	Object findAll(Payout payout, Pageable page);
	
	Object findallMarkForCancleAccounts(String merchantid, Pageable page);

	List<Object> findAll(String merchantPayoutId, String accountId);

	Object findallReadyForProcess(Payout payout, Pageable page);

	int findCountById(String merchantid);

	int findinstaPayoutsCountById(String merchantid);
	
	int findselfPayoutsCountById(String merchantid);

	int findinstaPayoutsApiCountById(String merchantid);

	int findaccountCloseCountById(String merchantid);

	int findcancelpayoutCountById(String merchantid);

	TransactionReportDto findTransactionReport(TransactionReportPayload transactionReportPayload, Pageable page);
	
	TransactionReportDto findDefaultTransactionReport(DtreansactionReportPayload transactionReportPayload, Pageable page);

    String getPayoutStatusList();






}
