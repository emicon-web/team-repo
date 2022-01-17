package com.bezkoder.springjwt.utils;
public class IssureKeyGenerator {
	
	static final String keyText = "TPSEISS";
	 int keyNumber = 1;
	
	String key;
	
	public IssureKeyGenerator() {
		// TODO Auto-generated constructor stub
		key = keyText+keyNumber;
	}
	
	  public String generateKey(String currentKey) throws NumberFormatException
	{
		String temp = currentKey;
		temp = temp.replaceAll("TPSEISS", "");
		temp = temp.trim();
		
		keyNumber = Integer.parseInt(temp);
		keyNumber++;
		key = keyText+keyNumber;
		
		return key;
	}
	
	public String generateKey() 
	{
		return key;
	}

}