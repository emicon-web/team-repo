package com.bezkoder.springjwt.utils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.bezkoder.springjwt.models.Token;
import com.bezkoder.springjwt.models.User;

public class Utils {

	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
	
	public static int generatePasscode() {
		Random random = new Random();
		return 100000 + random.nextInt(900000);
	}
	
	public static String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
	
	public static boolean isTokenFound(Token passToken) {
        return passToken != null;
    }
	
	public static boolean passcodeMatch(Token passToken, Integer passcode) {
        return passToken.getTokenCred().intValue() == passcode.intValue();
    }

    public static boolean isTokenExpired(Token passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
    
    public static boolean isUserLocked(User user) {
    	if (user.getUserIsLocked() != null && user.getUserIsLocked()) {
    		final Calendar cal = Calendar.getInstance();
    		return user.getUserBlockReleaseTime().after(cal.getTime());
    	}
        return false;
    }
    
	public static boolean isCause(Class<? extends Throwable> expected, Throwable exc) {
		return expected.isInstance(exc)
				|| (exc != null && isCause(expected, exc.getCause()));
	}
	
	public static Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

	public static Date getLastNDaysDate(final int lastNDays) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(new Date().getTime());
		cal.add(Calendar.DAY_OF_MONTH, -(lastNDays));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTime().getTime());
	}

	public static  Date getStartOfDay(Date date) {
		 Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
			return new Date(cal.getTime().getTime());

	}
	
	public static  Date getEndOfDay(Date date) {
		Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 23);
        cal2.set(Calendar.SECOND, 59);
        cal2.set(Calendar.MILLISECOND, 0);
		return new Date(cal2.getTime().getTime());


	}

	public static boolean nullOrEmptyCollection(Collection<?> src) {
		if (src != null && !src.isEmpty()) {
			return false;
		}
		return true;
	}
	

	public static boolean nullOrEmptyList(List<?> src) {
		if (src != null && !src.isEmpty()) {
			return false;
		}
		return true;
	}

	public static boolean nullOrEmptyArray(Object[] src) {
		if (src != null && src.length > 0) {
			return false;
		}
		return true;
	}

	public static boolean nullOrEmptyMap(Map<?,?> src) {
		return (src == null || src.isEmpty());
	}
	
	public static boolean nullOrEmptyList(Set<?> src) {
		if (src != null && !src.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public static final boolean nullOrEmptyString(String test) {
		if (test == null || test.trim().length() == 0) {
			return true;
		}
		return false;
	}
	
	public static final boolean nullOrEmptyString(StringBuilder test) {
		if (test == null) {
			return true;
		}
		return false;
	}

	public static final String largestNDigitNumber(int digit) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < digit; i++) {
			s.append("9");
		}
		return s.toString();
	}

	public static final String smallestNDigitNumber(int digit) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < digit; i++) {
			s.append("0");
		}
		return s.toString();
	}

	public static boolean nullOrEmptyString(List<String> test) {
		if (test == null) {
			return true;
		}
		return false;
	}
}
