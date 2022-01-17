package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.repository.MerchantRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class MerchantService {
    private static final Logger logger = LoggerFactory.getLogger(MerchantService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    public ListCommonReponse getAllMerchants(String callerAppId, MerchantListRequest merchantListRequest) {
//        String listQuerySql = getAllMerchantsQuery(merchantListRequest);
//        logger.info("getAllMerchants() - listQuerySql : " + listQuerySql);
//
//        ListCommonReponse<MerchantDto> response = new ListCommonReponse();
//        response.setRequestedAt(merchantListRequest.getRequestedAt());
//        response.setRequestId(merchantListRequest.getRequestId());
//        try {
//            List<MerchantDto> merchantDtoList = this.jdbcTemplate.query(listQuerySql, (rs, rowNum) -> mapAllMerchantResult(rs));
//            response.setData(merchantDtoList);
//            response.setStatus(ResponseStatusEnum.SUCCESS.getDescription());
//            response.setResponseCode(ResponseStatusCodeEnum.RESPONSE_200.getResponseCode());
//            return response;
//        } catch (Exception e) {
//            logger.error("Exception while fetching all merchants {}", e.getMessage());
//            response.setStatus(ResponseStatusEnum.FAILURE.getDescription());
//            response.setResponseCode(ResponseStatusCodeEnum.RESPONSE_500.getResponseCode());
//            response.setErrorMsg(ResponseStatusCodeEnum.RESPONSE_500.getDescription());
//            return response;
//        }
//    }
//
//    private MerchantDto mapAllMerchantResult(ResultSet rs) throws SQLException {
//        MerchantDto merchantDto = new MerchantDto();
//        merchantDto.setPartnerID(rs.getString("partnerID"));
//        merchantDto.setPartnerName(rs.getString("partnerName"));
//        merchantDto.setPartnerType(PartnerTypeEnum.getById(rs.getString("partnerType")));
//        merchantDto.setPartnerEstablishment(rs.getString("partnerEstablishment"));
//        merchantDto.setCvmRequired(CvmRequiredCodeEnum.getByDescription(rs.getString("cvmRequired")));
//        merchantDto.setPreferredCvmMethod(CvmMethodCodeEnum.getByDescription(rs.getString("preferredCvmMethod")));
//        merchantDto.setPresentmentPreferences(PresentmentPreferenceCodeEnum.getById(rs.getString("presentmentPreferences")));
//        merchantDto.setAlias(rs.getString("alias"));
//        merchantDto.setAggregatorID(rs.getString("aggregatorID"));
//        merchantDto.setStatus(rs.getString("status"));
//        merchantDto.setNoOfMerchantsEnrolled(rs.getInt("noOfMerchantsEnrolled"));
//        merchantDto.setNumberOfPaymentInst(rs.getInt("NumberOfPaymentInst"));
//        merchantDto.setTrgRSAPrivateKey(rs.getString("trgRSAPrivateKey"));
//        merchantDto.setTrgRSAPublicKey(rs.getString("trgRSAPublicKey"));
//        merchantDto.setPartnerRSAPublicKey(rs.getString("partnerRSAPublicKey"));
//        merchantDto.setCreatedAt(rs.getDate("createdAt"));
//        merchantDto.setUpdatedAt(rs.getDate("updatedAt"));
//        merchantDto.setCreatedBy(rs.getString("createdBy"));
//        merchantDto.setUpdatedBy(rs.getString("updatedBy"));
//        return merchantDto;
//    }
//
//    private String getAllMerchantsQuery(MerchantListRequest merchantListRequest) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT * FROM  " + TABLE_PARTNER + " WHERE 1 = 1 and status != 'Inactive'");
//        if (merchantListRequest.getPartnerId() != null) {
//            sql.append(" AND aggregatorID = \"" + merchantListRequest.getPartnerId() + "\" ");
//        }
//        return sql.toString();
//    }


    public boolean existsByMerchantId(String merchantId) {
        if (Utils.nullOrEmptyString(merchantId)) {
            return false;
        }
        try {
            final String query = "SELECT EXISTS ( SELECT * from merchant WHERE merchantid = \"" + merchantId + "\" )";
            logger.info("existsByMerchantId() - query : " + query);
            return jdbcTemplate.queryForObject(query, Boolean.class);
        } catch (Exception e) {
            logger.error("Error while fetching/checking merchantId {}", e.getMessage());
            throw new GenericCustomException("Error while fetching/checking merchantId ", new Date());
        }
    }

    public String getMerchantName(String merchantId) {
        return jdbcTemplate.queryForObject("SELECT name FROM merchant WHERE merchantid = '" + merchantId + "' ",
                String.class);
    }

    public String getMerchantShortCode(String merchantId) {
        return jdbcTemplate.queryForObject("SELECT shortCode FROM merchant WHERE merchantid = '" + merchantId + "' ",
                String.class);
    }
    
    public String getMerchantType(String merchantId) {
        return jdbcTemplate.queryForObject("SELECT merchantType FROM merchant WHERE merchantid = '" + merchantId + "' ",
                String.class);
    }

	public List<Object> findMerchantAggList(String merchantid) {
		
		return merchantRepository.findMerchantAggList(merchantid);
	}

}
