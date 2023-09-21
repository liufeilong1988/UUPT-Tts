package com.uupt.paddlespeech.china;

import java.util.HashMap;
import java.util.Map;

public class ConvertNumber {

    private static final Map<String, String> NUMBER_2_HAN_ZI = new HashMap<>();

    static {
        NUMBER_2_HAN_ZI.put("0", "零");
        NUMBER_2_HAN_ZI.put("1", "一");
        NUMBER_2_HAN_ZI.put("2", "二");
        NUMBER_2_HAN_ZI.put("3", "三");
        NUMBER_2_HAN_ZI.put("4", "四");
        NUMBER_2_HAN_ZI.put("5", "五");
        NUMBER_2_HAN_ZI.put("6", "六");
        NUMBER_2_HAN_ZI.put("7", "七");
        NUMBER_2_HAN_ZI.put("8", "八");
        NUMBER_2_HAN_ZI.put("9", "九");
        NUMBER_2_HAN_ZI.put(".", "点");
    }

    public static String number2Hanzi(String number) {
        StringBuilder builder = new StringBuilder();
        char[] chars = number.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            builder.append(NUMBER_2_HAN_ZI.get(String.valueOf(chars[i])));
        }
        return builder.toString();
    }

}
