package com.uupt.paddlespeech.china;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertPinyin {

    private static final Pattern ENGLISH_PATTERN = Pattern.compile("[a-zA-Z]");

    /**
     * 是否是拼音
     *
     * @param text
     * @return
     */
    public static boolean isPinYin(String text) {
        Pattern pattern = ConvertPinyin.ENGLISH_PATTERN;
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static String convertPinyinToLetter(String text) {
        text = text.toLowerCase();
        if (TextUtils.equals(text, "a")) {
            return "ei4";
        } else if (TextUtils.equals(text, "b")) {
            return "bi4";
        } else if (TextUtils.equals(text, "c")) {
            return "xi1";
        } else if (TextUtils.equals(text.toLowerCase(), "d")) {
            return "di4";
        } else if (TextUtils.equals(text.toLowerCase(), "e")) {
            return "yi4";
        } else if (TextUtils.equals(text.toLowerCase(), "f")) {
            return "哎夫";
        } else if (TextUtils.equals(text.toLowerCase(), "g")) {
            return "急";
        } else if (TextUtils.equals(text.toLowerCase(), "h")) {
            return "诶取";
        } else if (TextUtils.equals(text.toLowerCase(), "i")) {
            return "诶";
        } else if (TextUtils.equals(text.toLowerCase(), "j")) {
            return "jie4";
        } else if (TextUtils.equals(text.toLowerCase(), "k")) {
            return "kei1";
        } else if (TextUtils.equals(text.toLowerCase(), "l")) {
            return "爱刘";
        } else if (TextUtils.equals(text.toLowerCase(), "m")) {
            return "爱木";
        } else if (TextUtils.equals(text.toLowerCase(), "n")) {
            return "en4";
        } else if (TextUtils.equals(text.toLowerCase(), "o")) {
            return "ou1";
        } else if (TextUtils.equals(text.toLowerCase(), "p")) {
            return "皮";
        } else if (TextUtils.equals(text.toLowerCase(), "q")) {
            return "剋有";
        } else if (TextUtils.equals(text.toLowerCase(), "r")) {
            return "a4er4";
        } else if (TextUtils.equals(text.toLowerCase(), "s")) {
            return "ai1si4";
        } else if (TextUtils.equals(text.toLowerCase(), "t")) {
            return "提";
        } else if (TextUtils.equals(text.toLowerCase(), "u")) {
            return "优";
        } else if (TextUtils.equals(text.toLowerCase(), "v")) {
            return "未";
        } else if (TextUtils.equals(text.toLowerCase(), "w")) {
            return "大不刘";
        } else if (TextUtils.equals(text.toLowerCase(), "x")) {
            return "爱可四";
        } else if (TextUtils.equals(text.toLowerCase(), "y")) {
            return "wai4";
        } else if (TextUtils.equals(text.toLowerCase(), "z")) {
            return "zei4";
        }
        return "";
    }


}
