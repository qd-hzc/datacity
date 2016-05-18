package com.city.support.sys.update;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/12.
 */
public class EsiHttpClient {
    final static DefaultHttpClient httpClient = new DefaultHttpClient();
    private static Boolean isConnect = false;
    final private static String LOGINURL = "http://10.0.1.163:8088/dm_taian/frame";
    final private static String USER = "admin";
    final private static String PWD = "mdss";
    final private static String KEYUSER = "loginName";
    final private static String KEYPWD = "loginPWD";

    public static synchronized DefaultHttpClient getHttpClient(/*String loginName, String loginPwd, String url*/) {
        DefaultHttpClient result = null;
        HttpPost httpost = null;
        if (!isConnect) {
            //如果未连接
            try {
                CloseableHttpResponse response = null;
                HttpEntity entity = null;
                httpost = new HttpPost(LOGINURL);

                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair(KEYUSER, USER));
                nvps.add(new BasicNameValuePair(KEYPWD, PWD));
                httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));


                response = httpClient.execute(httpost);
                System.out.println(httpClient.getCookieStore());
                entity = response.getEntity();
                EntityUtils.consume(entity);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpost.releaseConnection();
            }
            //假定连接上了
            isConnect = true;

        }

        //已连接上
        result = new DefaultHttpClient();
        result.setCookieStore(httpClient.getCookieStore());
        return result;
    }

    public String sendRequest(String url, Map<String, String> params) {
        @SuppressWarnings({"resource"})
        CloseableHttpResponse response = null;
        HttpPost httpost = null;

        try {
            httpost = new HttpPost(url);

            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            UrlEncodedFormEntity entity1 = null;
            if (params != null) {

                for (String key : params.keySet()) {
                    formparams.add(new BasicNameValuePair(key, params.get(key)));

                }

                entity1 = new UrlEncodedFormEntity(formparams, "UTF-8");
            }

            if (entity1 != null) {
                httpost.setEntity(entity1);
            }


            response = getHttpClient().execute(httpost);
            HttpEntity entity = response.getEntity();
            System.out.println(getHttpClient().getCookieStore().toString());
            System.out.println(response.getStatusLine());

            String responseContent = EntityUtils.toString(entity, "UTF-8");
            return responseContent;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpost.releaseConnection();
        }
        return "";
    }

}
