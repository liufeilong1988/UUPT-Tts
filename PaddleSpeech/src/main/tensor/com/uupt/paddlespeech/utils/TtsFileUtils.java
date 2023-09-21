package com.uupt.paddlespeech.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TtsFileUtils {

    public static File getTtsFile(Context context, String name) {
        File cacheFile = TtsFileUtils.getTTSCacheFile(context);
        if (cacheFile == null) {
            return null;
        } else {
            return new File(cacheFile, name);
        }
    }

    private static File getTTSCacheFile(Context context) {
        File rootPath = null;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                rootPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                if (rootPath != null && !rootPath.exists()) {
                    rootPath.mkdir();
                }
                if (!rootPath.exists()) {
                    rootPath = null;
                }
                if (!rootPath.canWrite()) {
                    rootPath = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                rootPath = null;
            }
        }
        if (rootPath == null) {
            try {
                rootPath = context.getFilesDir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootPath;
    }

    /**
     * 获取assets目录下的文件并转换为字符串
     *
     * @param context
     * @param resourcesName
     * @return
     */
    public static String getAssetFile(Context context, String prefix, String resourcesName) {
        AssetManager assets = context.getAssets();
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        String result = null;
        try {
            outputStream = new ByteArrayOutputStream();
            inputStream = assets.open(prefix + resourcesName);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            inputStream = null;

            result = outputStream.toString("UTF-8");
            outputStream.close();
            outputStream = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 逐行读取
     *
     * @param context
     * @param resourcesName
     * @return
     */
    public static List<String> getAssetFileData(Context context, String prefix, String resourcesName) {
        List<String> result = new ArrayList<>();
        AssetManager assets = context.getAssets();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = assets.open(prefix + resourcesName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
            inputStream.close();
            inputStream = null;

            bufferedReader.close();
            bufferedReader = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //region 文件复制
    Context context;

    String source;

    File dest;

    public TtsFileUtils(Context context, String source, File dest) {
        this.context = context;
        this.source = source;
        this.dest = dest;
    }

    public boolean copyFile() {
        boolean success = false;
        InputStream is = null;
        FileOutputStream fos = null;
        File file = new File(dest + ".tmp");
        try {
            is = context.getResources().getAssets().open(TtsResource.PREFIX + source);
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[40960];
            int size = 0;
            while ((size = is.read(buffer)) >= 0 && !this.release) {
                fos.write(buffer, 0, size);
            }
            fos.close();
            is.close();
            fos = null;
            is = null;
            if (!this.release) {
                success = file.renameTo(dest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    boolean release;

    public void release() {
        this.release = true;
    }
    //endregion

}
