package com.bezkoder.springjwt.validators;

import com.bezkoder.springjwt.enums.ActionCommandEnum;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.RoleDto;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.utils.ValidatorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RoleValidator {
    private static final Logger logger = LogManager.getLogger(RoleValidator.class);

    private static final int MAX_ROLE_NAME_LENGTH = 20;
    private static final int MAX_ROLE_DESCRIPTION_LENGTH = 255;

    public CommonReponse validateRole(final RoleDto role, final ActionCommandEnum operation) {

        if (operation == ActionCommandEnum.Create) {
            final String rolename = role.getName();
            if (Utils.nullOrEmptyString(rolename)) {
                logger.error("Error: Role name can not be null or empty");
                return new CommonReponse("Error: Role name can not be null or empty");
            }
            if (!ValidatorUtil.isAlphaNumericWithUnderscore(rolename, MAX_ROLE_NAME_LENGTH)) {
                logger.error("Error: Role name can only be alpha numeric with underscore chars and no more than " + MAX_ROLE_NAME_LENGTH + " chars allowed");
                return new CommonReponse("Error: Role name can only be alpha numeric with underscore chars and no more than " + MAX_ROLE_NAME_LENGTH + " chars allowed");
            }
        }

        final String description = role.getDescription();
        if (Utils.nullOrEmptyString(description)) {
            logger.error("Error: Role description can not be null or empty");
            return new CommonReponse("Error: Role description can not be null or empty");
        }
        if (!ValidatorUtil.isAlphaNumericWithSpace(description, MAX_ROLE_DESCRIPTION_LENGTH)) {
            logger.error("Error: Role description can only contains alpha numeric values and no more than " + MAX_ROLE_DESCRIPTION_LENGTH + " chars allowed");
            return new CommonReponse("Error: Role description can only contains alpha numeric values and no more than " + MAX_ROLE_DESCRIPTION_LENGTH + " chars allowed");
        }

        return null;
    }
}
