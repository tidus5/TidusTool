package com.tidus.me.httpclient; /**
 * $Id: HttpClientUtil.java 254275 2012-10-24 07:11:46Z well.cheng $
 * Copyright(C) 2010-2016 happyelements.com. All rights reserved.
 */


import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * https://hc.apache.org/httpcomponents-client-5.2.x/migration-guide/migration-to-classic.html
 */
public class HttpClientUtil_5 {

    public static final int cache = 10 * 1024;
    private static final Log logger = LogFactory.getLog(HttpClientUtil_5.class);
    private static CloseableHttpClient httpClient;

    static {
        SSLContext ctx;
        try {
            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(SSLContexts.createSystemDefault())
                            .setTlsVersions(TLS.V_1_2)
                            .build())
                    .setDefaultSocketConfig(SocketConfig.custom()
                            .setSoTimeout(Timeout.ofMinutes(1))
                            .build())
                    .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                    .setConnPoolPolicy(PoolReusePolicy.LIFO)
                    .setDefaultConnectionConfig(ConnectionConfig.custom()
                            .setSocketTimeout(Timeout.ofMinutes(1))
                            .setConnectTimeout(Timeout.ofMinutes(1))
                            .setTimeToLive(TimeValue.ofMinutes(10))
                            .build())
                    .build();

            httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(StandardCookieSpec.STRICT)
                            .build())
                    .build();

        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        String res = doGet("http://www.bing.com");
        System.out.println(res);
    }

    /**
     * @param url   请求地址
     * @param param 请求参数
     */
    public static String doGet(String url, Map<String, String> param) {
        String resultString = null;
        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);


            response = httpClient.execute(httpGet);
            if (response.getCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException | URISyntaxException | ParseException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, String> param) {
        return doPost(url, param, null);
    }

    public static String doPost(String url, Map<String, String> param, Map<String, String> headerParam) {
        // 创建Httpclient对象
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            }
            if (headerParam != null) {
                for (Map.Entry<String, String> entry : headerParam.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    public static String doPostJson(String url, String json) {
        return doPostByContentType(url, json, ContentType.APPLICATION_JSON.withCharset("UTF-8"));
    }

    public static String doPostFormData(String url, String formData) {
        return doPostByContentType(url, formData, ContentType.APPLICATION_FORM_URLENCODED.withCharset("UTF-8"));
    }


    public static String doPostByContentType(String url, String data, ContentType contentType) {
        // 创建Httpclient对象
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(data, contentType);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultString;
    }

    /**
     * v1
     */
    public static String doPostJsonWithHeader(String url, Map<String, String> headerParam, String json) {
        // 创建Httpclient对象
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            if (headerParam != null) {
                for (Map.Entry<String, String> entry : headerParam.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultString;
    }

    /**
     * v2
     */
    public static String doPostJsonWithHeader(String url, Map<String, String> headerParam, String json, ContentType contentType) {
        // 创建Httpclient对象
        CloseableHttpResponse response = null;
        String resultString = null;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, contentType);
            httpPost.setEntity(entity);

            if (headerParam != null) {
                for (Map.Entry<String, String> entry : headerParam.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            // 执行http请求
            response = httpClient.execute(httpPost);
            int httpCode = response.getCode();
            if (httpCode != 200) {
                JSONObject jb = new JSONObject();
                jb.put("httpCode", response.getCode());
                if (response.getEntity() != null) {
                    jb.put("msg", EntityUtils.toString(response.getEntity(), "utf-8"));
                }
                return jb.toString();
            }

            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultString;
    }

    public static JSONObject getJson(String uri) throws JSONException {
        String result = doGet(uri);
        try {
            return JSONObject.fromObject(result);
        } catch (JSONException e) {
            throw new JSONException("result formate error " + result, e);
        }
    }

}
