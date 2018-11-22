package com.vince.toolkit.http;

import com.vince.toolkit.base.exception.BusinessException;
import com.vince.toolkit.http.model.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HttpClientService {

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private RequestConfig requestConfig;

    /**
     * 执行GET请求
     *
     * @param url
     * @return
     * @throws BusinessException
     */
    public String doGet(String url) throws BusinessException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(this.requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    throw new BusinessException(e);
                }
            }
        }
        return null;
    }

    /**
     * 带有参数的GET请求
     *
     * @param url
     * @param params
     * @return
     * @throws BusinessException
     */
    public String doGet(String url, Map<String, String> params) throws BusinessException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String newUrl = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            URI build = uriBuilder.build();
            if (build != null) {
                newUrl = build.toString();
            }
        } catch (URISyntaxException e) {
            throw new BusinessException(e);
        }
        return this.doGet(newUrl);
    }

    /**
     * 执行POST请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url, Map<String, String> params) throws BusinessException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.requestConfig);
        if (params != null) {
            // 设置2个post参数，一个是scope、一个是q
            List<NameValuePair> parameters = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, Charset.forName("UTF-8"));
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8")));
        } catch (Exception e) {
            throw new BusinessException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new BusinessException(e);
                }
            }
        }
    }

    /**
     * 执行POST请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url) throws BusinessException {
        return this.doPost(url, null);
    }

    /**
     * 提交json数据
     *
     * @param url
     * @param json
     * @return
     * @throws BusinessException
     */
    public HttpResult doPostJson(String url, String json) throws BusinessException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.requestConfig);
        if (json != null) {
            // 构造一个form表单式的实体
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = this.httpClient.execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8")));
        } catch (Exception e) {
            throw new BusinessException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    throw new BusinessException(e);
                }
            }
        }
    }

}
