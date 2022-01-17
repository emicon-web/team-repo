package com.bezkoder.springjwt.repository;

import java.util.List;

public interface VirtualAccountStatementRepository {


	List<Object> findByaccountid(String accountid, int days);

}
