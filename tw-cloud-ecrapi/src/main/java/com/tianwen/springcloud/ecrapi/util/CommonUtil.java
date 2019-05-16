package com.tianwen.springcloud.ecrapi.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class CommonUtil {
    static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    public static String increment(String s) {
        Matcher m = NUMBER_PATTERN.matcher(s);
        if (!m.find())
            throw new NumberFormatException();
        String num = m.group();
        int inc = Integer.parseInt(num) + 1;
        String incStr = String.format("%0" + num.length() + "d", inc);
        return  m.replaceFirst(incStr);
    }

    /*---------------- convert string to Md5 -----------------------------*/
    public static String convertToHexMd5(final byte[] md5) {
        StringBuffer sb = null;
        if (md5 == null)
            return null;

        try {
            final java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            final byte[] array = md.digest(md5);
            sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (final java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String convertToOctectMd5(final byte[] md5) {
        StringBuffer sb = null;
        if (md5 == null)
            return null;

        try {
            final java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            final byte[] array = md.digest(md5);
            sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toOctalString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (final java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String convertToMd5(String str, boolean hex) throws UnsupportedEncodingException {
        if (hex)
            return convertToHexMd5(str.getBytes("UTF-8"));
        else
            return convertToOctectMd5(str.getBytes("UTF-8"));
    }

    public static String trimString(String string) {
        if (StringUtils.isEmpty(string)) return string;
        string = string.replace("\n", "");
        string = string.trim();
        return string;
    }
}
