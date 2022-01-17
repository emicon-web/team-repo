package com.bezkoder.springjwt.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bezkoder.springjwt.errors.GenericCustomException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

public class PhiEncryptor {

    private static final Logger logger = LogManager.getLogger(PhiEncryptor.class);
    private static final Object lockObj = new Object();

    private SecretKeySpec secret;
    private IvParameterSpec ivSpec;

    private static final String AES_ALGO = "AES";
    private static final String AES_CBC_ALGO = "AES/CBC/PKCS5PADDING";

    private static volatile PhiEncryptor phiEncryptor = null;

    private PhiEncryptor() {
    }

    private void init(JdbcTemplate jdbcTemplate) throws Exception {
        // sample row in sysparams
        // columns = sysType, sysKey, sysValue, etc
        // row     = ONL, EncKey, <32 char hexString> (e.g., ABCDEF0123456789ABCDEF0123456789)
        String userDataEncKey = null; //<get from db> ;// select sysValue from sysparams where sysType = 'ONL' and sysKey = 'EncKey';
        String ivParameterSpecString = null; //<get from db> ;//select sysValue from sysparams where sysType = 'ONL' and sysKey = 'ivParamSpecString';
        if (jdbcTemplate != null) {
            userDataEncKey = jdbcTemplate.queryForObject("SELECT sysValue FROM sysparams WHERE sysType = 'ONL' AND sysKey = 'EncKey'",
                    String.class);
            ivParameterSpecString = jdbcTemplate.queryForObject("SELECT sysValue FROM sysparams WHERE sysType = 'ONL' AND sysKey = 'ivParamSpecString'",
                    String.class);
        }
        byte[] key = null;
        key = userDataEncKey.getBytes();
        secret = new SecretKeySpec(key, "AES");
        java.util.Arrays.fill(key, (byte) 0x00); // make sure to fill the key

        byte[] bytesIV = ivParameterSpecString.getBytes();
        ivSpec = new IvParameterSpec(bytesIV);
    }

    public String encrypt(String strToEncrypt) {
        try {
            Cipher encrypt = Cipher.getInstance(AES_CBC_ALGO);
            encrypt.init(Cipher.ENCRYPT_MODE, secret, ivSpec);

            byte[] encryptedMessage = encrypt.doFinal(strToEncrypt.getBytes());
            return Base64.getEncoder().encodeToString(encryptedMessage);
        } catch (Exception e) {
            logger.error("Error in encrypt :", e);
            new GenericCustomException("Error in encrypt :" + e, new Date());
            return null;
        }
    }

    public String decrypt(String encryptedVal) {
        try {
            Cipher decrypt = Cipher.getInstance(AES_CBC_ALGO);
            decrypt.init(Cipher.DECRYPT_MODE, secret, ivSpec);
            byte[] byteArray = Base64.getDecoder().decode(encryptedVal);
            String decryptedMessage = new String(decrypt.doFinal(byteArray));
            return decryptedMessage;
        } catch (Exception e) {
            logger.error("Error in decrypt :", e);
            new GenericCustomException("Error in decrypt :" + e, new Date());
            return null;
        }
    }

    public static PhiEncryptor getInstance(JdbcTemplate jdbcTemplate) throws Exception {
        if (phiEncryptor == null) {
            synchronized(lockObj) {
                if (phiEncryptor == null) {
                    phiEncryptor = new PhiEncryptor();
                    phiEncryptor.init(jdbcTemplate);
                }
            }
        }
        return phiEncryptor;
    }

    public static String bytesToHex(byte[] message) {
        if (message == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (int i = 0; i < message.length; i++) {
                stringBuilder.append(Integer.toString((message[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return stringBuilder.toString().toUpperCase();
    }

    // utility method
    public static String generateHMAC512(String message, String hashKey) {
        Mac shaHMAC;
        byte[] hashedBytes = null;
        try {
            shaHMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(hashKey.getBytes(), "HmacSHA512");
            shaHMAC.init(secret_key);
            hashedBytes = shaHMAC.doFinal(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytesToHex(hashedBytes);
    }
}