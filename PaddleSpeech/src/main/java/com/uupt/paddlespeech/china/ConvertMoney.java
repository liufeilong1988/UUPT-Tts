package com.uupt.paddlespeech.china;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertMoney {

    public static String expandMoney(String text) {
        String result = ConvertMoney.expandRmb(text);
        result = ConvertMoney.expandDollars(result);
        result = ConvertMoney.expandPounds(result);
        return result;
    }

    //region 匹配人民币
    private static final Pattern RMB_RE = Pattern.compile("￥([0-9.\\,]*[0-9]+)");

    /**
     * 获取人民币
     *
     * @param text
     * @return
     */
    private static String expandRmb(String text) {
        return ConvertMoney.expandString(ConvertMoney.RMB_RE, text, "元");
    }
    //endregion

    //region 匹配美元
    private static final Pattern DOLLARS_RE = Pattern.compile("\\$([0-9.\\,]*[0-9]+)");

    /**
     * 匹配美元
     *
     * @param text
     * @return
     */
    private static String expandDollars(String text) {
        return expandString(ConvertMoney.DOLLARS_RE, text, "美元");
    }
    //endregion

    //region 匹配欧元
    private static final Pattern POUNDS_RE = Pattern.compile("£([0-9\\,]*[0-9]+)");

    /**
     * 获取欧元
     *
     * @param text
     * @return
     */
    private static String expandPounds(String text) {
        return ConvertMoney.expandString(ConvertMoney.POUNDS_RE, text, "欧元");
    }
    //endregion

    //region 匹配字符串追加

    /**
     * 匹配字符串追加
     *
     * @param pattern
     * @param text
     * @param end
     * @return
     */
    private static String expandString(Pattern pattern, String text, String end) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group();
            text = text.replaceFirst(group, group + end);
        }
        return text;
    }
    //endregion

}
