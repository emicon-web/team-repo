package com.bezkoder.springjwt.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bezkoder.springjwt.payload.VirtualAccountStatementPayload;
import com.bezkoder.springjwt.repository.VirtualAccountRepository;
import com.bezkoder.springjwt.repository.VirtualAccountStatementRepository;

@Service
public class VirtualAccountStatementService {
	
	@Autowired
	VirtualAccountStatementRepository virtualAccountStatementRepository;


	public List<Object> findByAccountId(String accountid, int days) {
		return virtualAccountStatementRepository.findByaccountid(accountid,days);

	}

	

}
