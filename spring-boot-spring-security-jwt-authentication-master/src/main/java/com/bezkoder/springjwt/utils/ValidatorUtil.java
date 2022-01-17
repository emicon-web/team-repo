package com.bezkoder.springjwt.utils;

import java.util.regex.Pattern;

public final class ValidatorUtil {

    public static final int MAX_PAGE_LIMIT = 25;
    public static final int MAX_OFFSET_LIMIT = 100;

    public static final int MAX_TOKEN_REF_ID_LIMIT = 64;

    private static final String EMAIL_ADDRESS_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(EMAIL_ADDRESS_REGEX, Pattern.CASE_INSENSITIVE);

    private static final String NUMERIC_REGEX = "[0-9]+";
    private static final Pattern NUMERIC_PATTERN = Pattern.compile(NUMERIC_REGEX);

    private static final String ALPHA_REGEX = "^[a-zA-Z]*$";
    private static final Pattern ALPHA_PATTERN = Pattern.compile(ALPHA_REGEX);

    private static final String ALPHA_WITH_SPACE_REGEX = "^[a-zA-Z ]*$";
    private static final Pattern ALPHA_WITH_SPACE_PATTERN = Pattern.compile(ALPHA_WITH_SPACE_REGEX);

    private static final String ALPHA_NUMERIC_REGEX = "^[a-zA-Z0-9]*$";
    private static final Pattern ALPHA_NUMERIC_PATTERN = Pattern.compile(ALPHA_NUMERIC_REGEX);

    private static final String ALPHA_NUMERIC_SPACE_REGEX = "^[a-zA-Z0-9 ]*$";
    private static final Pattern ALPHA_NUMERIC_SPACE_PATTERN = Pattern.compile(ALPHA_NUMERIC_SPACE_REGEX);

    private static final String PACKAGENAME_REGEX = "^[a-zA-Z0-9.]*$";
    private static final Pattern PACKAGENAME_PATTERN = Pattern.compile(PACKAGENAME_REGEX);

    private static final String ALPHA_NUM_SPECIAL_REGEX = "^[a-zA-Z0-9!@#$&()\\-`.+,_/\"]*$";
    private static final Pattern ALPHA_NUM_SPECIAL_PATTERN = Pattern.compile(ALPHA_NUM_SPECIAL_REGEX);

    private static final String ALPHA_NUMERIC_HYPHEN_REGEX = "^[a-zA-Z0-9-]*$";
    private static final Pattern ALPHA_NUMERIC_HYPHEN_PATTERN = Pattern.compile(ALPHA_NUMERIC_HYPHEN_REGEX);

    private static final String ALPHA_NUMERIC_UNDERSCORE_REGEX = "^[a-zA-Z0-9_]*$";
    private static final Pattern ALPHA_NUMERIC_UNDERSCORE_PATTERN = Pattern.compile(ALPHA_NUMERIC_UNDERSCORE_REGEX);

    private static final String NAME_REGEX = "^[a-zA-Z0-9 &_.-]*$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    private static final String ALPHA_NUMERIC_ID_REGEX = "^[a-zA-Z0-9@_.-]*$";
    private static final Pattern ALPHA_NUMERIC_ID_PATTERN = Pattern.compile(ALPHA_NUMERIC_ID_REGEX);

    private static final String HEX_REGEX = "^[0-9A-Fa-f]+$";
    private static final Pattern HEX_PATTERN = Pattern.compile(HEX_REGEX);

    private static final String URL_REGEX = "^(?:(?!asset).)*$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);


    /**
     * Check if email string has valid email patter
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(final String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public static boolean isValidEmail(final String email, final int length) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches() && email.length() <= length;
    }

    /**
     * Check if string contains only Numeric characters
     *
     * @param value
     * @return
     */
    public static boolean isNumeric(final String value) {
        return NUMERIC_PATTERN.matcher(value).matches();
    }

    public static boolean isNumeric(final String value, final int length) {
        return NUMERIC_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    /**
     * Check if string contains only Alphanumeric characters
     *
     * @param value
     * @return
     */
    public static boolean isAlphaNumeric(final String value) {
        return ALPHA_NUMERIC_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphaNumeric(final String value, final int length) {
        return ALPHA_NUMERIC_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    /**
     * Check if string contains only Alphabets
     *
     * @param value
     * @return
     */
    public static boolean isAlphabets(final String value) {
        return ALPHA_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphabets(final String value, final int length) {
        return ALPHA_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    /**
     * Check if string contains only Alphabets with space
     *
     * @param value
     * @return
     */
    public static boolean isAlphabetsWithSpace(final String value) {
        return ALPHA_WITH_SPACE_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphabetsWithSpace(final String value, final int length) {
        return ALPHA_WITH_SPACE_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    /**
     * Check if string contains only Alphanumeric with spaces characters
     *
     * @param value
     * @return
     */
    public static boolean isAlphaNumericWithSpace(final String value) {
        return ALPHA_NUMERIC_SPACE_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphaNumericWithSpace(final String value, final int length) {
        return ALPHA_NUMERIC_SPACE_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    public static boolean isValidName(final String value, final int length) {
        return NAME_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    public static boolean isValidPackageName(final String value, final int length) {
        return PACKAGENAME_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    /**
     * Check if string contains only Alphanumeric and special characters
     *
     * @param value
     * @return
     */
    public static boolean isAlphaNumericWithSpecial(final String value) {
        return ALPHA_NUM_SPECIAL_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphaNumericWithSpecial(final String value, final int length) {
        return ALPHA_NUM_SPECIAL_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    /**
     * Check if string contains only Alphanumeric and hyphen characters
     *
     * @param value
     * @return
     */
    public static boolean isAlphaNumericWithHyphen(final String value) {
        return ALPHA_NUMERIC_HYPHEN_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphaNumericWithHyphen(final String value, final int length) {
        return ALPHA_NUMERIC_HYPHEN_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    /**
     * Check if string contains only Alphanumeric and underscore characters
     *
     * @param value
     * @return
     */
    public static boolean isAlphaNumericWithUnderscore(final String value) {
        return ALPHA_NUMERIC_UNDERSCORE_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphaNumericWithUnderscore(final String value, final int length) {
        return ALPHA_NUMERIC_UNDERSCORE_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    /**
     * Check if string contains only Alphanumeric for username or other ID
     *
     * @param value
     * @return
     */
    public static boolean isAlphaNumericID(final String value) {
        return ALPHA_NUMERIC_ID_PATTERN.matcher(value).matches();
    }

    public static boolean isAlphaNumericID(final String value, final int length) {
        return ALPHA_NUMERIC_ID_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    public static boolean validateMaxLimit(final int limit) {
        return limit <= MAX_PAGE_LIMIT;
    }

    public static boolean validateOffsetLimit(final int offset) {
        return offset <= MAX_OFFSET_LIMIT;
    }

//    public static boolean validateUrl(final String url) {
//        try {
//            new URL(url);
//        } catch (MalformedURLException e) {
//            return false;
//        }
//        return URL_PATTERN.matcher(url).matches();
//    }

    public static boolean validateUrl(final String url, final int length) {


        return URL_PATTERN.matcher(url).matches() && url.length() <= length;
    }

    public static boolean isHex(final String value) {
        return HEX_PATTERN.matcher(value).matches();
    }

    public static boolean isHex(final String value, final int length) {
        return HEX_PATTERN.matcher(value).matches() && value.length() <= length;
    }

    public static boolean isRegexMatches(final String regex, final String value) {
        return Pattern.compile(regex).matcher(value).matches();
    }

    public static boolean isRegexMatches(final String regex, final String value, final int length) {
        return Pattern.compile(regex).matcher(value).matches() && value.length() <= length;
    }
}
