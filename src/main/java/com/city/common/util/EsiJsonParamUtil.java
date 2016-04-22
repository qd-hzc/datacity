package com.city.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wys on 2016/1/7.
 */
public class EsiJsonParamUtil<T> {
    public static Logger log = EsiLogUtil.getLogInstance(EsiJsonParamUtil.class);

    public String getParam(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName);
    }

    public T parseObj(HttpServletRequest request, Class clazz) throws Exception {
        T result = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = br.readLine();
            String jsonStr = "";
            while (line != null) {
                jsonStr += line;
                line = br.readLine();
            }
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jsonStr);
            Gson g = new Gson();
            if (!je.isJsonNull()) {
                if (je.isJsonArray()) {
                    throw new Exception("传入的为数组对象");
                } else {
                    if (je.getAsJsonObject().has("id")) {
                        String idStr = null;
                        Integer idInt = null;
                        try {
                            idStr = je.getAsJsonObject().get("id").getAsString();
                            idInt = Integer.parseInt(idStr);
                            je.getAsJsonObject().remove("id");
                            je.getAsJsonObject().addProperty("id", idInt);
                        } catch (Exception e) {
                            if ("".equals(idStr)) {
                                idStr = null;
                                je.getAsJsonObject().remove("id");
                                je.getAsJsonObject().addProperty("id", idStr);
                            }
                        }
                    }
                    result = (T) g.fromJson(je, clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(log, e.getMessage());
            throw e;
        }
        return result;
    }

    public List<T> parseObjToList(HttpServletRequest request, Class clazz) throws Exception {
        List<T> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = br.readLine();
            String jsonStr = "";
            while (line != null) {
                jsonStr += line;
                line = br.readLine();
            }
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jsonStr);
            Gson g = new Gson();
            JsonArray ja = null;
            if (!je.isJsonNull()) {
                if (je.isJsonArray()) {
                    JsonElement tmpJe = null;
                    ja = je.getAsJsonArray();
                    Iterator<JsonElement> jIt = ja.iterator();
                    while (jIt.hasNext()) {
                        tmpJe = jIt.next();
                        if (tmpJe.getAsJsonObject().has("id")) {
                            String idStr = null;
                            Integer idInt = null;
                            idStr = tmpJe.getAsJsonObject().get("id").getAsString();
                            try {
                                idInt = Integer.parseInt(idStr);
                                tmpJe.getAsJsonObject().remove("id");
                                tmpJe.getAsJsonObject().addProperty("id", idInt);
                            } catch (Exception e) {
                                if ("".equals(idStr)) {
                                    idStr = null;
                                    tmpJe.getAsJsonObject().remove("id");
                                    tmpJe.getAsJsonObject().addProperty("id", idStr);
                                }
                            }
                        }
                        result.add((T) g.fromJson(tmpJe, clazz));
                    }
                } else {
                    if (je.getAsJsonObject().has("id")) {
                        String idStr = null;
                        Integer idInt = null;

                        try {
                            idStr = je.getAsJsonObject().get("id").getAsString();
                            idInt = Integer.parseInt(idStr);
                            je.getAsJsonObject().remove("id");
                            je.getAsJsonObject().addProperty("id", idInt);
                        } catch (Exception e) {
                            if ("".equals(idStr)) {
                                idStr = null;
                                je.getAsJsonObject().remove("id");
                                je.getAsJsonObject().addProperty("id", idStr);
                            }
                        }
                    }
                    result.add((T) g.fromJson(je, clazz));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            EsiLogUtil.error(log, e.getMessage());
            throw e;
        }
        return result;
    }

}
