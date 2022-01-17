package com.bezkoder.springjwt.repository;

import org.springframework.http.ResponseEntity;

import com.bezkoder.springjwt.entity.MerchantConfiguration;

public interface MerchantConfigurationRepository {

	Object findMerchantId(String merchantid);

	int updateByMerchnatID(MerchantConfiguration merchantConfiguration);

	Object findMerchantMode(String merchantid);

	Object findByInstrument(String merchantid);

}
