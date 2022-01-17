package com.bezkoder.springjwt.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bezkoder.springjwt.repository.BankRepository;

@Service
public class BankService {
	
	@Autowired
	BankRepository bankRepository;

	public List<Object> findBankName(String ifscprefix) {
        return bankRepository.finBankName(ifscprefix);

	}

}
