package com.bezkoder.springjwt.validators;

import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.enums.ActionCommandEnum;
import com.bezkoder.springjwt.enums.ModeOfPaymentEnum;
import com.bezkoder.springjwt.enums.PaymentInstrumentEnum;
import com.bezkoder.springjwt.enums.PayoutTypeEnum;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.PayoutStagingDto;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.utils.ValidatorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class PayoutFileDataValidator {
    private static final Logger logger = LogManager.getLogger(PayoutFileDataValidator.class);

    public static final String VPA_REGEX = "[a-zA-Z0-9\\.\\-]{2,256}\\@[a-zA-Z][a-zA-Z]{2,64}";
    private static final List<String> impsSupportedInstruments = Arrays.asList(PaymentInstrumentEnum.ACCOUNT.name(), PaymentInstrumentEnum.CARD.name());
    private static final List<String> neftSupportedInstruments = Arrays.asList(PaymentInstrumentEnum.ACCOUNT.name(), PaymentInstrumentEnum.CARD.name());
    private static final List<String> upiSupportedInstruments = Arrays.asList(PaymentInstrumentEnum.ACCOUNT.name(), PaymentInstrumentEnum.VPA.name());
    private static final List<String> rtgsSupportedInstruments = Arrays.asList(PaymentInstrumentEnum.ACCOUNT.name(), PaymentInstrumentEnum.CARD.name());

    public List<CommonReponse> validatePayoutFileData(PayoutStagingDto payoutStagingDto,
                                                      MerchantConfiguration merchantConfiguration,
                                                      List<String> virtualAccountIds,
                                                      User user, ActionCommandEnum operation,
                                                      PayoutTypeEnum payoutType) {
        List<CommonReponse> errorList = new ArrayList<>();

        if (!merchantConfiguration.getMerchantid().equals(payoutStagingDto.getMerchantid())) {
            logger.error("Error: Invalid Merchant ID");
            errorList.add(new CommonReponse("Error: Invalid Merchant ID"));
            payoutStagingDto.setMerchantid("NULL");
        }

        final String virtualAccountId = payoutStagingDto.getAccountId();
        if (!virtualAccountIds.contains(virtualAccountId)) {
            logger.error("Error: Invalid Virtual Account ID, not exist or not belong to logged in User's merchant");
            errorList.add(new CommonReponse("Error: Invalid Virtual Account ID, not exist or not belong to logged in User's merchant"));
            payoutStagingDto.setAccountId(null);
        }

        if (payoutType == PayoutTypeEnum.FILEUPLOAD) {
            if (Utils.nullOrEmptyString(payoutStagingDto.getMerchantPayoutId())) {
                logger.error("Error: Payout trans id can not be null or empty");
                errorList.add(new CommonReponse("Error: Payout trans id can not be null or empty"));
            } else if (!ValidatorUtil.isAlphaNumeric(payoutStagingDto.getMerchantPayoutId())) {
                logger.error("Error: Payout trans id can only contains alphanumeric values");
                errorList.add(new CommonReponse("Error: Payout trans id can only contains alphanumeric values"));
            }

            if (Utils.nullOrEmptyString(payoutStagingDto.getPayoutDateTimeStr()) && payoutStagingDto.getPayoutDateTime() == null) {
                logger.error("Error: Payout date time can not be null or empty");
                errorList.add(new CommonReponse("Error: Payout date time can not be null or empty"));
            } else {
                try {
                    Date payoutDateTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(payoutStagingDto.getPayoutDateTimeStr());
                    Date currentDate = Utils.getStartOfDay(new Date());

                    if (payoutDateTime.before(currentDate)) {
                        logger.error("Error: Payout date time can not be in past");
                        errorList.add(new CommonReponse("Error: Payout date time can not be in past"));
                    }
                    payoutStagingDto.setPayoutDateTime(payoutDateTime);
                } catch (ParseException e) {
                    logger.error("Error: Invalid payout date time format ", e.getMessage());
                    errorList.add(new CommonReponse("Error: Invalid payout date time format"));
                    payoutStagingDto.setPayoutDateTime(null);
                }
            }
        }

        if (Utils.nullOrEmptyString(payoutStagingDto.getAmountStr()) && payoutStagingDto.getAmount() <= 0) {
            logger.error("Error: Amount can not be null, empty or zero");
            errorList.add(new CommonReponse("Error: Amount can not be null, empty or zero"));
        } else {
            if (payoutType == PayoutTypeEnum.FILEUPLOAD) {
                try {
                    Double amount = Double.parseDouble(payoutStagingDto.getAmountStr());
                    if (amount <= 0) {
                        logger.error("Error: Amount can not be null, empty or zero");
                        errorList.add(new CommonReponse("Error: Amount can not be null, empty or zero"));
                    } else {
                        payoutStagingDto.setAmount(amount);
                    }
                } catch (NumberFormatException e) {
                    logger.error("Error: Invalid amount ", e.getMessage());
                    errorList.add(new CommonReponse("Error: Invalid amount"));
                    payoutStagingDto.setAmount(0);
                }
            }
        }


        if (merchantConfiguration != null) {
            final double minPayoutAmount = merchantConfiguration.getMinPayoutAmount();
            final double maxPayoutAmount = merchantConfiguration.getMaxPayoutAmount();
            if (payoutStagingDto.getAmount() < minPayoutAmount || payoutStagingDto.getAmount() > maxPayoutAmount) {
                logger.error("Error: Amount should be within merchant configured min-max range");
                errorList.add(new CommonReponse("Error:Amount should be within merchant configured min-max range"));
            }
        } else {
            logger.error("Error: Merchant configuration not found");
            errorList.add(new CommonReponse("Error: Merchant configuration not found"));
        }


        if (payoutType == PayoutTypeEnum.FILEUPLOAD || payoutType == PayoutTypeEnum.INSTAPAY) {
            if (Utils.nullOrEmptyString(payoutStagingDto.getPayoutPaymentMode())) {
                logger.error("Error: Mode of payment can not be null or empty");
                errorList.add(new CommonReponse("Error: Mode of payment can not be null or empty"));
            } else if (ModeOfPaymentEnum.getModeOfPayment(payoutStagingDto.getPayoutPaymentMode()) == null) {
                logger.error("Error: Invalid Mode of payment ");
                errorList.add(new CommonReponse("Error: Invalid Mode of payment "));
                payoutStagingDto.setPayoutPaymentMode(null);
            } else {
                if (merchantConfiguration != null) {
                    String merchantConfigPaymentModes = merchantConfiguration.getPaymentMode();
                    if (Utils.nullOrEmptyString(merchantConfigPaymentModes)) {
                        logger.error("Error: No merchant configured Mode of payment found ");
                        errorList.add(new CommonReponse("Error: No merchant configured Mode of payment found "));
                    } else {
                        List<String> paymentModes = Arrays.asList(merchantConfigPaymentModes.split("\\s*,\\s*"));
                        if (!paymentModes.contains(payoutStagingDto.getPayoutPaymentMode())) {
                            logger.error("Error: Mode of payment does not belong to merchant configured Payment Modes ");
                            errorList.add(new CommonReponse("Error: Mode of payment does not belong to merchant configured Payment Modes "));
                        }
                    }
                }
            }

            final String payoutPaymentInstrument = payoutStagingDto.getPayoutPaymentInstrument();
            if (PaymentInstrumentEnum.getPaymentInstrument(payoutPaymentInstrument) == null) {
                logger.error("Error: Invalid Payment Instrument ");
                errorList.add(new CommonReponse("Error: Invalid Payment Instrument "));
                payoutStagingDto.setPayoutPaymentInstrument(null);
            } else {
                if (merchantConfiguration != null) {
                    if (Utils.nullOrEmptyString(payoutStagingDto.getPayoutPaymentMode())) {
                        logger.error("Error: No merchant configured payment instrument found ");
                        errorList.add(new CommonReponse("Error: No merchant configured payment instrument found "));
                    } else {
                        String paymentMode = payoutStagingDto.getPayoutPaymentMode();
                        if ((paymentMode.equals(ModeOfPaymentEnum.IMPS.name()) && !impsSupportedInstruments.contains(payoutPaymentInstrument))
                                || (paymentMode.equals(ModeOfPaymentEnum.NEFT.name()) && !neftSupportedInstruments.contains(payoutPaymentInstrument))
                                || (paymentMode.equals(ModeOfPaymentEnum.UPI.name()) && !upiSupportedInstruments.contains(payoutPaymentInstrument))
                                || (paymentMode.equals(ModeOfPaymentEnum.RTGS.name()) && !rtgsSupportedInstruments.contains(payoutPaymentInstrument))) {
                            logger.error("Error: Payment instrument does not belong to merchant configured Payment Instruments ");
                            errorList.add(new CommonReponse("Error: Payment instrument does not belong to merchant configured Payment Instruments "));
                        } else {
                            if (payoutPaymentInstrument.equalsIgnoreCase(PaymentInstrumentEnum.ACCOUNT.name())) {

                                if (Utils.nullOrEmptyString(payoutStagingDto.getBankAccountName())) {
                                    logger.error("Error: Bank account name can not be null or empty");
                                    errorList.add(new CommonReponse("Error: Bank account name can not be null or empty"));
                                } else if (!ValidatorUtil.isAlphabetsWithSpace(payoutStagingDto.getBankAccountName(), 45)) {
                                    logger.error("Error: Bank account name can only contains alphabets value and no more than 45 chars allowed");
                                    errorList.add(new CommonReponse("Error: Bank account name can only contains alphabets value and no more than 45 chars allowed"));
                                    payoutStagingDto.setBankAccountName(null);
                                }

                                if (Utils.nullOrEmptyString(payoutStagingDto.getBeneficiaryAccountNumber())) {
                                    logger.error("Error: Bank account number can not be null or empty");
                                    errorList.add(new CommonReponse("Error: Bank account number can not be null or empty"));
                                } else if (!ValidatorUtil.isAlphaNumeric(payoutStagingDto.getBeneficiaryAccountNumber(), 45)) {
                                    logger.error("Error: Bank account number can only contains alphanumeric value and no more than 45 chars allowed");
                                    errorList.add(new CommonReponse("Error: Bank account number can only contains alphanumeric value and no more than 45 chars allowed"));
                                    payoutStagingDto.setBeneficiaryAccountNumber(null);
                                }

                                if (Utils.nullOrEmptyString(payoutStagingDto.getBeneficiaryIFSCCode())) {
                                    logger.error("Error: Bank IFSC code can not be null or empty");
                                    errorList.add(new CommonReponse("Error: Bank IFSC code can not be null or empty"));
                                } else if (!ValidatorUtil.isAlphaNumeric(payoutStagingDto.getBeneficiaryIFSCCode(), 11)) {
                                    logger.error("Error: Bank IFSC code can only contains alphanumeric value and no more than 11 chars allowed");
                                    errorList.add(new CommonReponse("Error: Bank IFSC code can only contains alphanumeric value and no more than 11 chars allowed"));
                                    payoutStagingDto.setBeneficiaryIFSCCode(null);
                                }

                            } else if (payoutPaymentInstrument.equalsIgnoreCase(PaymentInstrumentEnum.CARD.name())) {

                                if (Utils.nullOrEmptyString(payoutStagingDto.getBeneficiaryCardNumber())) {
                                    logger.error("Error: Credit Card number can not be null or empty");
                                    errorList.add(new CommonReponse("Error: Credit Card number can not be null or empty"));
                                } else if (!ValidatorUtil.isNumeric(payoutStagingDto.getBeneficiaryCardNumber())) {
                                    logger.error("Error: Credit card number can only contains numeric value");
                                    errorList.add(new CommonReponse("Error: Credit card number can only contains numeric value"));
                                    payoutStagingDto.setBeneficiaryCardNumber(null);
                                }

                                if (Utils.nullOrEmptyString(payoutStagingDto.getCardHolderName())) {
                                    logger.error("Error: Card holder name can not be null or empty");
                                    errorList.add(new CommonReponse("Error: Card holder name can not be null or empty"));
                                } else if (!ValidatorUtil.isAlphabetsWithSpace(payoutStagingDto.getCardHolderName())) {
                                    logger.error("Error: Card holder name can only contains alphabets value");
                                    errorList.add(new CommonReponse("Error: Card holder name can only contains alphabets value"));
                                    payoutStagingDto.setCardHolderName(null);
                                }

                                if (Utils.nullOrEmptyString(payoutStagingDto.getCardHolderBankName())) {
                                    logger.error("Error: Card holder bank name can not be null or empty");
                                    errorList.add(new CommonReponse("Error: Card holder bank name can not be null or empty"));
                                } else if (!ValidatorUtil.isAlphabetsWithSpace(payoutStagingDto.getCardHolderBankName())) {
                                    logger.error("Error: Card holder bank name can only contains alphabets value");
                                    errorList.add(new CommonReponse("Error: Card holder bank name can only contains alphabets value"));
                                    payoutStagingDto.setCardHolderBankName(null);
                                }

                                if (Utils.nullOrEmptyString(payoutStagingDto.getCardHolderBankIFSCCode())) {
                                    logger.error("Error: Card holder bank IFSC code can not be null or empty");
                                    errorList.add(new CommonReponse("Error: Card holder bank IFSC code can not be null or empty"));
                                } else if (!ValidatorUtil.isAlphaNumeric(payoutStagingDto.getCardHolderBankIFSCCode(), 11)) {
                                    logger.error("Error: Card holder bank IFSC code can only contains alphanumeric value and no more than 11 chars allowed");
                                    errorList.add(new CommonReponse("Error: Card holder bank IFSC code can only contains alphanumeric value and no more than 11 chars allowed"));
                                    payoutStagingDto.setCardHolderBankIFSCCode(null);
                                }

                            } else if (payoutPaymentInstrument.equalsIgnoreCase(PaymentInstrumentEnum.VPA.name())) {

                                if (!ValidatorUtil.isRegexMatches(VPA_REGEX, payoutStagingDto.getBeneficiaryVPA())) {
                                    logger.error("Error: Invalid Beneficiary VPA");
                                    errorList.add(new CommonReponse("Error: Invalid Beneficiary VPA"));
                                    payoutStagingDto.setBeneficiaryVPA(null);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (Utils.nullOrEmptyString(payoutStagingDto.getPayoutPurpose())) {
            logger.error("Error: Payout purpose can not be null or empty");
            errorList.add(new CommonReponse("Error: Payout purpose can not be null or empty"));
        } else if (!ValidatorUtil.isAlphabetsWithSpace(payoutStagingDto.getPayoutPurpose(), 256)) {
            logger.error("Error: Payout purpose can only contains alphanumeric value and no more than 256 chars allowed");
            errorList.add(new CommonReponse("Error: Payout purpose can only contains alphanumeric value and no more than 256 chars allowed"));
            payoutStagingDto.setPayoutPurpose(null);
        }

        if (Utils.nullOrEmptyString(payoutStagingDto.getBeneficiaryName())) {
            logger.error("Error: Beneficiary name can not be null or empty");
            errorList.add(new CommonReponse("Error: Beneficiary name can not be null or empty"));
        } else if (!ValidatorUtil.isAlphabetsWithSpace(payoutStagingDto.getBeneficiaryName(), 45)) {
            logger.error("Error: Beneficiary name can only contains alphanumeric value and no more than 45 chars allowed");
            errorList.add(new CommonReponse("Error: Beneficiary name can only contains alphanumeric value and no more than 45 chars allowed"));
            payoutStagingDto.setBeneficiaryName(null);
        }

        if (Utils.nullOrEmptyString(payoutStagingDto.getBeneficiaryEmailId())) {
            logger.error("Error: Beneficiary Email Id can not be null or empty");
            errorList.add(new CommonReponse("Error: Beneficiary Email Id can not be null or empty"));
        } else if (!ValidatorUtil.isValidEmail(payoutStagingDto.getBeneficiaryEmailId(), 45)) {
            logger.error("Error: Beneficiary Email Id should be valid email and no more than 45 chars allowed");
            errorList.add(new CommonReponse("Error: Beneficiary Email Id should be valid email and no more than 45 chars allowed"));
            payoutStagingDto.setBeneficiaryEmailId(null);
        }

        if (Utils.nullOrEmptyString(payoutStagingDto.getBeneficiaryMobileNumber())) {
            logger.error("Error: Beneficiary Mobile Number can not be null or empty");
            errorList.add(new CommonReponse("Error: Beneficiary Mobile Number can not be null or empty"));
        } else if (!ValidatorUtil.isNumeric(payoutStagingDto.getBeneficiaryMobileNumber(), 10)) {
            logger.error("Error: Beneficiary Mobile Number can only contains numeric and no more than 10 digits allowed");
            errorList.add(new CommonReponse("Error: Beneficiary Mobile Number can only contains numeric and no more than 10 digits allowed"));
            payoutStagingDto.setBeneficiaryMobileNumber(null);
        }

        return errorList;
    }

}
