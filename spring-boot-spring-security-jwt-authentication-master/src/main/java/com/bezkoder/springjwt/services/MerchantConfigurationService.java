package com.bezkoder.springjwt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.repository.MerchantConfigurationRepository;

@Service
public class MerchantConfigurationService {

	@Autowired
	MerchantConfigurationRepository merchantConfigurationRepository;
	
	
	public Object findBymerchantId(String merchantid) {
        return merchantConfigurationRepository.findMerchantId(merchantid);

	}


	public int updateByMerchantId(MerchantConfiguration merchantConfiguration) {
		 int merchnatConfig = merchantConfigurationRepository.updateByMerchnatID(merchantConfiguration);
	        return merchnatConfig;
	}


	public Object findByMode(String merchantid) {
        return merchantConfigurationRepository.findMerchantMode(merchantid);

	}

	public Object findByInstrument(String merchantid) {
        return merchantConfigurationRepository.findByInstrument(merchantid);

	}

}
