package com.bezkoder.springjwt.validators;

import com.bezkoder.springjwt.entity.UserEntity;
import com.bezkoder.springjwt.enums.ActionCommandEnum;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.UserDto;
import com.bezkoder.springjwt.services.MerchantService;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.utils.ValidatorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    private static final Logger logger = LogManager.getLogger(UserValidator.class);

    private static final int MAX_USER_NAME_LENGTH = 33;
    private static final int MAX_FIRST_OR_LAST_NAME_LENGTH = 20;
    private static final int MAX_EMAIL_LENGTH = 50;
    //private static final int MAX_PHONE_NUMBER_LENGTH = 12;

    @Autowired
    private MerchantService merchantService;

    public CommonReponse validateUser(final UserDto user, final ActionCommandEnum operation) {

        if (operation == ActionCommandEnum.Create) {
            final String username = user.getUserName();
            if (Utils.nullOrEmptyString(username)) {
                logger.error("Error: User name can not be null or empty");
                return new CommonReponse("Error: User name can not be null or empty");
            }
            if (!ValidatorUtil.isAlphaNumericID(username, MAX_USER_NAME_LENGTH)) {
                logger.error("Error: User name can only be alpha numeric with allowed special chars and no more than " + MAX_USER_NAME_LENGTH + " chars allowed");
                return new CommonReponse("Error: User name can only be alpha numeric with allowed special chars and no more than " + MAX_USER_NAME_LENGTH + " chars allowed");
            }
            final String merchantShortCode = merchantService.getMerchantShortCode(user.getMerchantId());
            if (Utils.nullOrEmptyString(merchantShortCode) || !username.endsWith("@" + merchantShortCode)) {
                logger.error("Error: Invalid merchant short code in username");
                return new CommonReponse("Error: Invalid merchant short code in username");
            }

            final String firstname = user.getUserFirstName();
            if (Utils.nullOrEmptyString(firstname)) {
                logger.error("Error: First name can not be null or empty");
                return new CommonReponse("Error: First name can not be null or empty");
            }
            if (!ValidatorUtil.isAlphabetsWithSpace(firstname, MAX_FIRST_OR_LAST_NAME_LENGTH)) {
                logger.error("Error: First name can only contains alphabets and no more than " + MAX_FIRST_OR_LAST_NAME_LENGTH + " chars allowed");
                return new CommonReponse("Error: First name can only contains alphabets and no more than " + MAX_FIRST_OR_LAST_NAME_LENGTH + " chars allowed");
            }

            final String lastname = user.getUserLastName();
            if (Utils.nullOrEmptyString(lastname)) {
                logger.error("Error: Last name can not be null or empty");
                return new CommonReponse("Error: Last name can not be null or empty");
            }
            if (!ValidatorUtil.isAlphabetsWithSpace(lastname, MAX_FIRST_OR_LAST_NAME_LENGTH)) {
                logger.error("Error: Last name can only contains alphabets and no more than " + MAX_FIRST_OR_LAST_NAME_LENGTH + " chars allowed");
                return new CommonReponse("Error: Last name can only contains alphabets and no more than " + MAX_FIRST_OR_LAST_NAME_LENGTH + " chars allowed");
            }
        }

        final String email = user.getUserEmail();
        if (Utils.nullOrEmptyString(email)) {
            logger.error("Error: Email can not be null or empty");
            return new CommonReponse("Error: Email can not be null or empty");
        }
        if (!ValidatorUtil.isValidEmail(email, MAX_EMAIL_LENGTH)) {
            logger.error("Error: Invalid email and no more than " + MAX_EMAIL_LENGTH + " chars allowed");
            return new CommonReponse("Error: Invalid email and no more than " + MAX_EMAIL_LENGTH + " chars allowed");
        }

//        final String phonenumber = user.getUserPhone();
//        if (Utils.nullOrEmptyString(phonenumber)) {
//            logger.error("Error: Phone number can not be null or empty");
//            return new CommonReponse("Error: Phone number can not be null or empty");
        //}
//        if (!ValidatorUtil.isNumeric(phonenumber, MAX_PHONE_NUMBER_LENGTH)) {
//            logger.error("Error: Phone number can contains only numeric values and no more than 10 digits allowed");
//            return new CommonReponse("Error: Phone number can contains only numeric values and no more than 10 digits allowed");
//        }

        return null;
    }

    public CommonReponse validateUser(final UserEntity user, final ActionCommandEnum operation) {
        final UserDto userDto = new UserDto();
        userDto.setUserEmail(user.getUserEmail());
        userDto.setUserFirstName(user.getUserFirstName());
        userDto.setUserLastName(user.getUserLastName());
        userDto.setUserName(user.getUserName());
        userDto.setUserPhone(user.getUserPhone());

        return validateUser(userDto, operation);
    }
}
