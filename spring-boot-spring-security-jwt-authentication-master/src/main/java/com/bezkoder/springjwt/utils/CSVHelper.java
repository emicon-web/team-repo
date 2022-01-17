package com.bezkoder.springjwt.utils;

import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.payload.PayoutStagingDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CSVHelper {
    private static final Logger logger = LogManager.getLogger(CSVHelper.class);
    public static final String TYPE = "text/csv";
    static final String[] HEADERs = {"MerchantID", "VA Number", "payoutTransID", "PayoutDatetime",
            "Amount", "ModeOfPayment", "PayoutDesc", "BeneName", "BeneEmailID", "BeneMobile", "PaymentInstrument", "BeneBankAcName",
            "BeneBankAcNum", "BeneBankIFSC", "VPA", "CreditCardNum", "CardHolderName", "CardBankName", "CardIFSC",
            "PayoutStatus", "PayoutStatusFailureReason"};

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<PayoutStagingDto> csvToPayoutStagingDto(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT)) {

            List<PayoutStagingDto> payoutStagingDtos = new ArrayList<>();

            List<CSVRecord> csvRecords = csvParser.getRecords();
            if (!Utils.nullOrEmptyList(csvRecords)) {
                for (int i = 1; i < csvRecords.size(); i++) {
                    CSVRecord csvRecord = csvRecords.get(i);
                    PayoutStagingDto payoutStagingDto = new PayoutStagingDto();

                    payoutStagingDto.setMerchantid(csvRecord.get(0));
                    payoutStagingDto.setAccountId(csvRecord.get(1));
                    payoutStagingDto.setMerchantPayoutId(csvRecord.get(2));
                    payoutStagingDto.setPayoutDateTimeStr(csvRecord.get(3));
                    payoutStagingDto.setAmountStr(csvRecord.get(4));
                    payoutStagingDto.setPayoutPaymentMode(csvRecord.get(5));
                    payoutStagingDto.setPayoutPurpose(csvRecord.get(6));
                    payoutStagingDto.setBeneficiaryName(csvRecord.get(7));
                    payoutStagingDto.setBeneficiaryEmailId(csvRecord.get(8));
                    payoutStagingDto.setBeneficiaryMobileNumber(csvRecord.get(9));
                    payoutStagingDto.setPayoutPaymentInstrument(csvRecord.get(10));
                    payoutStagingDto.setBankAccountName(csvRecord.get(11));
                    payoutStagingDto.setBeneficiaryAccountNumber(csvRecord.get(12));
                    payoutStagingDto.setBeneficiaryIFSCCode(csvRecord.get(13));
                    payoutStagingDto.setBeneficiaryVPA(csvRecord.get(14));
                    payoutStagingDto.setBeneficiaryCardNumber(csvRecord.get(15));
                    payoutStagingDto.setCardHolderName(csvRecord.get(16));
                    payoutStagingDto.setCardHolderBankName(csvRecord.get(17));
                    payoutStagingDto.setCardHolderBankIFSCCode(csvRecord.get(18));

                    payoutStagingDtos.add(payoutStagingDto);
                }
            }
            return payoutStagingDtos;
        } catch (IOException e) {
            logger.error("Filed to parse CSV payout file ", e);
            throw new GenericCustomException("Filed to parse CSV payout file ", new Date());
        }
    }

    public static ByteArrayInputStream payoutStagingDtoToCSV(List<PayoutStagingDto> payoutStagingDtoList) {
        final CSVFormat format = CSVFormat.DEFAULT;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            csvPrinter.printRecord(HEADERs);
            if (Utils.nullOrEmptyList(payoutStagingDtoList)) {
                csvPrinter.printRecord(Collections.emptyList());
            } else {
                for (PayoutStagingDto payoutStagingDto : payoutStagingDtoList) {
                    List<String> data = Arrays.asList(
                            payoutStagingDto.getMerchantid(),
                            payoutStagingDto.getAccountId(),
                            payoutStagingDto.getMerchantPayoutId(),
                            payoutStagingDto.getPayoutDateTime() == null ? null
                                    : new SimpleDateFormat("yyyyMMddHHmmss").format(payoutStagingDto.getPayoutDateTime()),
                            String.valueOf(payoutStagingDto.getAmount()),
                            payoutStagingDto.getPayoutPaymentMode(),
                            payoutStagingDto.getPayoutPurpose(),
                            payoutStagingDto.getBeneficiaryName(),
                            payoutStagingDto.getBeneficiaryEmailId(),
                            payoutStagingDto.getBeneficiaryMobileNumber(),
                            payoutStagingDto.getPayoutPaymentInstrument(),
                            payoutStagingDto.getBankAccountName(),
                            payoutStagingDto.getBeneficiaryAccountNumber(),
                            payoutStagingDto.getBeneficiaryIFSCCode(),
                            payoutStagingDto.getBeneficiaryVPA(),
                            payoutStagingDto.getBeneficiaryCardNumber(),
                            payoutStagingDto.getCardHolderName(),
                            payoutStagingDto.getCardHolderBankName(),
                            payoutStagingDto.getCardHolderBankIFSCCode(),
                            payoutStagingDto.getPayoutStatus(),
                            payoutStagingDto.getPayoutStatusFailureReason()
                    );
                    csvPrinter.printRecord(data);
                }
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            logger.error("Failed to import data to CSV file ", e);
            throw new GenericCustomException("Failed to import data to CSV file ", new Date());
        }
    }
}