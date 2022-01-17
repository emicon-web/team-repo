package com.bezkoder.springjwt.repositorytemplate.copy;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.payload.MerchantInstrumentPayload;
import com.bezkoder.springjwt.payload.MerchantPaymentModePayload;
import com.bezkoder.springjwt.repository.MerchantConfigurationRepository;

@Repository
public class MerchantConfigurationJdbcRepository implements MerchantConfigurationRepository {

    private static final Logger logger = LogManager.getLogger(MerchantConfigurationJdbcRepository.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public Object findMerchantId(String merchantid) {
		try {
		return jdbcTemplate.queryForObject(
				 "select * from merchant_configuration where merchantid = ?",
				 new Object[]{merchantid},
				 (rs, rowNum) ->
				 new MerchantConfiguration(rs.getString("merchantid"), 
						                   rs.getString("paymentMode"), 
						                   rs.getString("instruments"), 
						                   rs.getString("acquirerBankName"),
						                   rs.getString("acquirerBankIFSC"), 
						                   rs.getDouble("minPayoutAmount"),
						                   rs.getDouble("maxPayoutAmount"), 
						                   rs.getDouble("lowBalanceThreshold"),
						                   rs.getDate("updateDate"), 
						                   rs.getString("updatedBy"),
										   rs.getBoolean("enableInstaPayChecker"),
										   rs.getBoolean("enableSelfPayChecker"),
										   rs.getBoolean("enableCancelPayChecker"),
										   rs.getBoolean("enableCloseAcctChecker"),
										   rs.getBoolean("enableAPIPayChecker"))
				 );
		}
		catch (Exception e) {
            logger.error(" merchantid is not present in database ", e.getMessage());
            throw new GenericCustomException("merchantid is not present in Database", new Date());
		}
	}

	@Override
	public int updateByMerchnatID(MerchantConfiguration merchantConfiguration) {
		return jdbcTemplate.update(
				"update merchant_configuration set paymentMode = ?, instruments = ?, acquirerBankName = ?, acquirerBankIFSC = ?, minPayoutAmount = ?, maxPayoutAmount = ? , lowBalanceThreshold = ?, updateDate = ? , updatedBy = ? , enableInstaPayChecker = ?, enableSelfPayChecker = ?, enableCancelPayChecker = ?, enableCloseAcctChecker = ? , enableAPIPayChecker= ?   where  merchantid = ?",
				merchantConfiguration.getPaymentMode(),
				merchantConfiguration.getInstruments(),
				merchantConfiguration.getAcquirerBankName(),
				merchantConfiguration.getAcquirerBankIFSC(),
				merchantConfiguration.getMinPayoutAmount(),
				merchantConfiguration.getMaxPayoutAmount(),
				merchantConfiguration.getLowBalanceThreshold(),
				merchantConfiguration.getUpdateDate(), 
				merchantConfiguration.getUpdatedBy(),
				merchantConfiguration.isEnableInstaPayChecker(),
				merchantConfiguration.isEnableSelfPayChecker(),
				merchantConfiguration.isEnableCancelPayChecker(),
				merchantConfiguration.isEnableCloseAcctChecker(),
				merchantConfiguration.isEnableAPIPayChecker(),
				merchantConfiguration.getMerchantid());
	}

	@Override
	public Object findMerchantMode(String merchantid) {
		try {
			return jdbcTemplate.queryForObject(
					 "select paymentMode from merchant_configuration where merchantid = ?",
					 new Object[]{merchantid},
					 (rs, rowNum) ->
					 new MerchantPaymentModePayload( 
							                   rs.getString("paymentMode"))
					 );
			}
			catch (Exception e) {
	            logger.error(" merchantid is not present in database ", e.getMessage());
	            throw new GenericCustomException("merchantid is not present in Database", new Date());
			}
	}

	@Override
	public Object findByInstrument(String merchantid) {
		try {
			return jdbcTemplate.queryForObject(
					 "select instruments from merchant_configuration where merchantid = ?",
					 new Object[]{merchantid},
					 (rs, rowNum) ->
					 new MerchantInstrumentPayload( 
							                   rs.getString("instruments"))
					 );
			}
			catch (Exception e) {
	            logger.error(" merchantid is not present in database ", e.getMessage());
	            throw new GenericCustomException("merchantid is not present in Database", new Date());
			}
	}

}
