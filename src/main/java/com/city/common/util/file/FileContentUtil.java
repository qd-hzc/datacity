package com.city.common.util.file;

import com.city.common.pojo.Constant;
import com.google.gson.Gson;

import java.io.*;
import java.net.URLDecoder;

/**
 * Created by wxl on 2016/3/10.
 * 加载文件内容 工具类
 */
public class FileContentUtil {
    /**
     * 读取文件内容,返回字符串
     *
     * @param filePath 文件路径
     */
    public static String getConfigJsonStr(String filePath) {
        BufferedReader bf = null;
        try {
            String path = URLDecoder.decode(filePath, "UTF-8");
            File file = new File(path);
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String tmpStr = null;
            while ((tmpStr = bf.readLine()) != null) {
                sb.append(tmpStr);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 读取文件内容,返回tClass类型的对象
     */
    public static <T> T getConfigJsonStr(String filePath, Class<T> tClass) {
        String str = getConfigJsonStr(filePath);
        if (str.length() > 0) {
            Gson gson = new Gson();
            return gson.fromJson(str, tClass);
        }
        return null;
    }

    /**
     * 写入文件
     *
     * @param filePath 文件路径
     * @param content  要写入的内容
     * @param isAppend 是否在原来内容基础上继续写入
     */
    public static void writeConfigStr(String filePath, String content, boolean isAppend) {
        PrintWriter out = null;
        FileOutputStream os = null;
        try {
            String path = URLDecoder.decode(filePath, "UTF-8");
            os = new FileOutputStream(path, isAppend);
            out = new PrintWriter(os);
            out.println(content);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
