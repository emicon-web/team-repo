package com.bezkoder.springjwt.repository;

import java.util.List;

public interface MerchantRepository {

	 List<Object> findMerchantAggList(String merchantid); 

}
