package com.dreamfish.fishblog.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 判断字符串是否为空
     * @param string
     * @return
     */
    public static boolean isEmpty(String string){
        return (string == null || string.length() == 0);
    }

    /**
     * 判断字符串是否是空白
     * @param cs
     * @return
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen = 0;
        if ((cs == null) || ((strLen = cs.length()) == 0))
            return true;
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    /**
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 检测字符串是否是数字
     * @param str 要检测的字符串
     * @return 检测结果
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 检测字符串是否以某个字符串开头
     * @param str 要检测的字符串
     * @param c 某个字符串
     * @return 检测结果
     */
    public static boolean startWith(String str, String c){
        return str.indexOf(c) == 0;
    }

    /**
     * 检测是否是合法Email
     * @param string 要检测的字符串
     * @return 检测结果
     */
    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        return m.matches();
    }

}
