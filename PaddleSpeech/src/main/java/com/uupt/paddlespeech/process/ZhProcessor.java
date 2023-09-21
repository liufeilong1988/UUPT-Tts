package com.uupt.paddlespeech.process;

import android.content.Context;
import android.util.Log;

import com.uupt.paddlespeech.china.ConvertDate;
import com.uupt.paddlespeech.china.ConvertMoney;
import com.uupt.paddlespeech.china.ConvertNumber;
import com.uupt.paddlespeech.china.ConvertPinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by zhp on 2021/2/5
 * Description:
 */
public class ZhProcessor {
    private static final String TAG = "ZhProcessor";

    private static final Pattern ZH_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[\n，。？?！!,;；、:：]");
    private static final int TYPE_UNKONWN = -1;
    private static final int TYPE_ZH = 0;
    private static final int TYPE_NUMBER = 1;

    private static final Pattern COMMA_NUMBER_RE = Pattern.compile("([0-9][0-9\\,]+[0-9])");
    private static final Pattern DECIMAL_RE = Pattern.compile("([0-9]+\\.[0-9]+)");

    private static final Pattern NUMBER_RE = Pattern.compile("[0-9]+");


    private ZhProcessorPinyin processorPinyin;

    public ZhProcessor(Context context) {
        this.processorPinyin = new ZhProcessorPinyin(context);
    }

    public void init() {
        this.processorPinyin.init();
    }

    public float[] text2ids(String text) {
        String parseText = parseText(text);
        String[] pinyin;
        try {
            pinyin = convert2Pinyin(parseText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return this.processorPinyin.getResult(pinyin);
    }


    private String parseText(String text) {
        text = removeCommasFromNumbers(text);
        text = ConvertMoney.expandMoney(text);
        text = expandDecimals(text);
        text = ConvertDate.expandDate(text);
        text = expandCardinals(text);
        StringBuilder pinyinBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        String hanzi = "";
        String number = "";
        int lastType = TYPE_UNKONWN;
        int type = TYPE_UNKONWN;
        for (int i = 0; i < chars.length; i++) {
            String s = String.valueOf(chars[i]);
            if (ConvertPinyin.isPinYin(s)) {
                hanzi += ConvertPinyin.convertPinyinToLetter(s);
                type = TYPE_ZH;
            }
            if (isZhWord(s)) {
                hanzi += s;
                type = TYPE_ZH;
            } else if (isNumber(s)) {
                number += s;
                type = TYPE_NUMBER;
            } else if (".".equals(s) && lastType == TYPE_NUMBER) {
                number += s;
            } else if (isSymbol(s) && lastType != TYPE_UNKONWN) {
                pinyinBuilder.append(ConvertNumber.number2Hanzi(number))
                        .append(hanzi)
                        .append("#3 ");
                hanzi = "";
                number = "";
            }

            if (lastType != TYPE_UNKONWN && lastType != type) {
                switch (lastType) {
                    case TYPE_NUMBER:
                        pinyinBuilder.append(ConvertNumber.number2Hanzi(number));
                        number = "";
                        break;
                    case TYPE_ZH:
                        pinyinBuilder.append(hanzi);
                        hanzi = "";
                        break;
                }
            }

            lastType = type;
        }

        pinyinBuilder.append(hanzi)
                .append(number);

        return pinyinBuilder.toString();
    }

    private String removeCommasFromNumbers(String text) {
        Matcher m = COMMA_NUMBER_RE.matcher(text);
        while (m.find()) {
            String s = m.group().replaceAll(",", "");
            text = text.replaceFirst(m.group(), s);
        }
        return text;
    }


    private String expandDecimals(String text) {
        Matcher m = DECIMAL_RE.matcher(text);
        while (m.find()) {
            String[] ss = m.group().split("\\.");
            String s = ss[0] + "点" + ConvertNumber.number2Hanzi(ss[1]);
            text = text.replaceFirst(m.group(), s);
        }
        return text;
    }



    private String expandCardinals(String text) {
        Matcher m = NUMBER_RE.matcher(text);
        while (m.find()) {
            int l = 0;
            try {
                l = Integer.valueOf(m.group());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String spelling = ConvertDate.numberToCH(l);
            text = text.replaceFirst(m.group(), spelling);
        }
        return text;
    }

    private static boolean isZhWord(String s) {
        return ZH_PATTERN.matcher(s).matches();
    }

    private static boolean isNumber(String s) {
        return NUMBER_PATTERN.matcher(s).matches();
    }

    private static boolean isSymbol(String s) {
        return SYMBOL_PATTERN.matcher(s).matches();
    }

    private static String[] convert2Pinyin(String text) throws Exception {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        String pinyin = PinyinHelper.toHanYuPinyinString(text, format, "", true);
        StringBuilder builder = new StringBuilder();
        char[] chars = pinyin.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String s = String.valueOf(chars[i]);
            builder.append(s);
            if (isNumber(s)) {
                builder.append(" ");
            }
        }
        Log.e(TAG, builder.toString());
        return builder.toString().split(" ");
    }


}
