package com.bezkoder.springjwt.repository;

import java.util.List;

public interface BankRepository {

	List<Object> finBankName(String ifscprefix);

}
