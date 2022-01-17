package com.bezkoder.springjwt.validators;

import com.bezkoder.springjwt.enums.ActionCommandEnum;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.VirtualAccountDto;
import com.bezkoder.springjwt.services.VirtualAccountService;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.utils.ValidatorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VirtualAccountValidator {
    private static final Logger logger = LogManager.getLogger(VirtualAccountValidator.class);

    private static final String VI_ID_PREFIX = "PHIP";
    private static final int VI_ID_LENGTH = 18;
    private static final int VI_ID_USER_INPUT_LENGHT = 9;
    private static final int VI_DESC_MAX_LENGTH = 45;
    private static final int MERCHANT_ID_LAST_N_DIGIT = 5;

    @Autowired
    private VirtualAccountService virtualAccountService;

    public CommonReponse validateVirtualAccount(final VirtualAccountDto viAccount,
                                                final User user,
                                                final ActionCommandEnum operation) {

        if (operation == ActionCommandEnum.Create) {
            final String virtualAccountId = viAccount.getVirtualAccountID();
            if (Utils.nullOrEmptyString(virtualAccountId)) {
                logger.error("Error: Virtual Account ID can not be null or empty");
                return new CommonReponse("Error: Virtual Account ID can not be null or empty");
            }
            if (!ValidatorUtil.isAlphaNumeric(virtualAccountId, VI_ID_LENGTH)) {
                logger.error("Error: Invalid Virtual Account ID, it should only contains Alphanumeric chars and max 18 digits allowed");
                return new CommonReponse("Error: Invalid Virtual Account ID, it should only contains Alphanumeric chars and max 18 digits allowed");
            }
            if (!virtualAccountId.startsWith(VI_ID_PREFIX)) {
                logger.error("Error: Invalid Virtual Account ID, it should be start with " + VI_ID_PREFIX);
                return new CommonReponse("Error: Invalid Virtual Account ID, it should be start with " + VI_ID_PREFIX);
            }

            final String merchantId = viAccount.getMerchantid();
            if (Utils.nullOrEmptyString(merchantId) || !user.getMerchantId().equals(merchantId)) {
                logger.error("Error: Invalid Merchant ID");
                return new CommonReponse("Error: Invalid Merchant ID");
            }

            final String last5CharsMerchantId = merchantId.substring(merchantId.length() - MERCHANT_ID_LAST_N_DIGIT);
            final String viAccountIdMerchantIdChars = virtualAccountId.substring(4, 9);
            if (!viAccountIdMerchantIdChars.equals(last5CharsMerchantId)) {
                logger.error("Error: Invalid Merchant ID chars in Virtual Account ID");
                return new CommonReponse("Error: Invalid Merchant ID chars in Virtual Account ID");
            }

            final String last9CharsVI = virtualAccountId.substring(virtualAccountId.length() - VI_ID_USER_INPUT_LENGHT);
            if (!ValidatorUtil.isAlphaNumeric(last9CharsVI)) {
                logger.error("Error: Invalid Virtual Account ID, Last 9 digits should be alphanumeric only");
                return new CommonReponse("Error: Invalid Virtual Account ID, Last 9 digits should be alphanumeric only");
            }

            final boolean existingVIAccount = virtualAccountService.existsByVirtualAccountId(virtualAccountId);
            if (existingVIAccount) {
                logger.error("Error: Virtual Account ID should be unique");
                return new CommonReponse("Error: Virtual Account ID should be unique");
            }

            final String description = viAccount.getDescription();
            if (!ValidatorUtil.isAlphaNumericWithSpace(description, VI_DESC_MAX_LENGTH)) {
                logger.error("Error: Description can only be alpha numeric with allowed special chars and no more than " + VI_DESC_MAX_LENGTH + " chars allowed");
                return new CommonReponse("Error: Description can only be alpha numeric with allowed special chars and no more than " + VI_DESC_MAX_LENGTH + " chars allowed");
            }
        }

        return null;
    }

}
