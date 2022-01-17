package com.bezkoder.springjwt.utils;

import com.bezkoder.springjwt.errors.GenericCustomException;
import com.google.gson.JsonObject;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JweEncrypt {
    private static String PUB_KEY_PATH;
    static Key pubKey = null;

//    static {
//        try {
////            File file = new File(JweEncrypt.class.getResource("/testpub.key").getFile());
//            File file = PUB_KEY_PATH != null ? new File(PUB_KEY_PATH)
//                    : new File(JweEncrypt.class.getResource("/testpub.key").getFile());
//            String pubKeyContents = new String(Files.readAllBytes(file.toPath()));
//            pubKey = getRSAPublicKey(pubKeyContents);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Invalid key file / file not found");
//        }
//    }

    @Value("${external.pubkey.path}")
    public void setPath(String path) {
        PUB_KEY_PATH = path;
        setPubKey();
    }

    public static void setPubKey() {
        try {
            File file = PUB_KEY_PATH != null ? new File(PUB_KEY_PATH)
                    : new File(JweEncrypt.class.getResource("/testpub.key").getFile());
            String pubKeyContents = new String(Files.readAllBytes(file.toPath()));
            pubKey = getRSAPublicKey(pubKeyContents);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Invalid key file / file not found");
            new GenericCustomException("Invalid key file / file not found", new Date());
        }
    }

    public static PublicKey getRSAPublicKey(String acquirerIssuerpublicKey) throws Exception {
        PublicKey publicKey = null;
        // generate public key
        byte[] decodedKey = Base64.getDecoder().decode(acquirerIssuerpublicKey.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(spec);
        return publicKey;
    }

    public static String createJWEString(String plainText, Key key, String keyId) throws Exception {
        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setPlaintext(plainText);
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.RSA1_5);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(key);
        if (key != null) {
            jwe.setKeyIdHeaderValue(keyId);
        }
        String serializedJwe = jwe.getCompactSerialization();
        return serializedJwe;
    }

    public static String getEncryptedInstMappingData(String realPanNo, String issuerID) throws Exception {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("realPan", realPanNo);
        jsonObj.addProperty("issuerId", issuerID);
        String instMappingData = jsonObj.toString();

        String encInstMappingData = createJWEString(instMappingData, pubKey, null);

        return encInstMappingData;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getEncryptedInstMappingData("5305620408052302", "TPSEISS01"));
    }
}