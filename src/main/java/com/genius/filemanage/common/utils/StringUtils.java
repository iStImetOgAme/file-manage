package com.genius.filemanage.common.utils;

public class StringUtils {

    public static String trimRight(String value) {
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return (len < value.length()) ? value.substring(st, len) : value;
    }
}
