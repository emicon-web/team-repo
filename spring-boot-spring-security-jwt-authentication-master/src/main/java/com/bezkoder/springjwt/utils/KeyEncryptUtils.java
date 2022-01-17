package com.bezkoder.springjwt.utils;

import org.springframework.util.StringUtils;

public class KeyEncryptUtils {

	public static String encryptPublic(PhiEncryptor encryptor, String clearValue) {
	   if (StringUtils.isEmpty(clearValue)) {
		   return null;
	   }
	   return extractPublicAndEncrypt(encryptor, clearValue);	   
	}
	
	public static String extractPublicAndEncrypt(PhiEncryptor encryptor, String pubKei) {
		if (pubKei.startsWith("-----BEGIN PUBLIC KEY")) {
			// raw key. need to encrypt
			
			pubKei = pubKei.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
			pubKei = pubKei.replaceAll("(\\r|\\n)", "");			

			return encryptor.encrypt(pubKei);			
		}
		return pubKei;
	}
	
/*	public static void main(String[] args) {
		String key = "-----BEGIN PUBLIC KEY-----\n" +
					"e\n" +
					"y\n" +
					"B\n" +
					"-----END PUBLIC KEY-----";
		System.out.println(extractPublic(key));
	}*/
}
