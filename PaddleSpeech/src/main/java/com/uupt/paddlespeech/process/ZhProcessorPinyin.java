package com.uupt.paddlespeech.process;

import android.content.Context;
import android.util.Log;

import com.uupt.paddle.BuildConfig;
import com.uupt.paddlespeech.utils.TtsFileUtils;
import com.uupt.paddlespeech.utils.TtsResource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ZhProcessorPinyin {

    private static final String TAG = "ZhProcessor";

    private static final HashMap<String, String[]> PINYIN_DICT = new HashMap<>();
    private static final HashMap<String, Integer> SYMBOL_TO_ID = new HashMap<>();

    private Context context;

    public ZhProcessorPinyin(Context context) {
        this.context = context;
    }

    public void init() {
        try {
            String json = TtsFileUtils.getAssetFile(context, TtsResource.PREFIX,TtsResource.PINYIN_MAP);
            JSONObject root = new JSONObject(json);
            JSONObject pinyinDictObject = root.getJSONObject("pinyin_dict");
            Iterator<String> pinyinKeys = pinyinDictObject.keys();
            while (pinyinKeys.hasNext()) {
                String key = pinyinKeys.next();
                JSONArray jsonArray = pinyinDictObject.getJSONArray(key);
                String[] array = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    array[i] = jsonArray.getString(i);
                }
                PINYIN_DICT.put(key, array);
            }

            List<String> pinyinList = TtsFileUtils.getAssetFileData(context, TtsResource.PREFIX, TtsResource.PHONE_ID_MAP);
            for (int i = 0; i < pinyinList.size(); i++) {
                String[] pinyin = pinyinList.get(i).split(" ");
                SYMBOL_TO_ID.put(pinyin[0], Integer.parseInt(pinyin[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float[] getResult(String[] pinyins) {
        String symbols = this.pinyin2Symbol(pinyins);
        if (BuildConfig.DEBUG) {
            Log.d(ZhProcessorPinyin.TAG, symbols);
        }
        return this.symbol2ids(symbols.replaceAll("\\^","").split("\\s+"));
    }

    private String pinyin2Symbol(String[] pinyins) {
        StringBuilder result = new StringBuilder();
        for (String pinyin : pinyins) {
            if (pinyin.isEmpty()) {
                continue;
            }
            String[] split = ZhProcessorPinyin.PINYIN_DICT.get(pinyin.substring(0, pinyin.length() - 1));
            if (split != null && split.length >= 2) {
                if (!split[0].equals("^")) {
                    result
                            .append(split[0])
                            .append(" ");
                }
                result
                        .append(split[1])
                        .append(pinyin.substring(pinyin.length() - 1))
                        .append(" ");
            }
        }
        return result.toString();
    }

    private float[] symbol2ids(String[] symbols) {
        float[] ids = new float[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            if (SYMBOL_TO_ID.get(symbols[i]) != null) {
                ids[i] = SYMBOL_TO_ID.get(symbols[i]);
            } else {
                ids[i] = 2;
            }
        }
        return ids;
    }

}
