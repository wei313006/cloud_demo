package common.core.utils;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HttpClient工具类（适配Java 17）
 * 特性：
 * 1. 使用连接池提升性能
 * 2. 支持自定义超时时间
 * 3. 自动处理响应结果
 * 4. 支持JSON/Form格式参数
 */
public class HttpClientUtils {

    private static final CloseableHttpClient httpClient;

    static {
        // 创建连接池管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200); // 最大连接数
        connectionManager.setDefaultMaxPerRoute(50); // 每个路由最大连接数

        // 配置请求参数
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.of(5, TimeUnit.SECONDS)) // 连接超时
                .setResponseTimeout(Timeout.of(10, TimeUnit.SECONDS)) // 响应超时
                .build();

        // 创建HttpClient
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    /**
     * 发送GET请求
     * @param url 请求地址
     * @param headers 请求头（可选）
     */
    public static String doGet(String url, Map<String, String> headers) {
        HttpGet httpGet = new HttpGet(url);
        setHeaders(httpGet, headers);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return handleResponse(response);
        } catch (IOException | ParseException e) {
            throw new RuntimeException("HTTP GET请求失败", e);
        }
    }

    /**
     * 发送POST请求（支持JSON）
     * @param url 请求地址
     * @param jsonBody JSON请求体
     * @param headers 请求头（可选）
     */
    public static String doPostJson(String url, String jsonBody, Map<String, String> headers) {
        HttpPost httpPost = new HttpPost(url);
        setHeaders(httpPost, headers);
        httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return handleResponse(response);
        } catch (IOException | ParseException e) {
            throw new RuntimeException("HTTP POST请求失败", e);
        }
    }

    /**
     * 发送POST表单请求
     * @param url 请求地址
     * @param formParams 表单参数
     * @param headers 请求头（可选）
     */
    public static String doPostForm(String url, Map<String, String> formParams, Map<String, String> headers) {
        HttpPost httpPost = new HttpPost(url);
        setHeaders(httpPost, headers);

        // 构建表单参数
        StringBuilder formBody = new StringBuilder();
        formParams.forEach((k, v) -> formBody.append(k).append("=").append(v).append("&"));
        StringEntity entity = new StringEntity(
                formBody.toString(),
                ContentType.APPLICATION_FORM_URLENCODED
        );
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return handleResponse(response);
        } catch (IOException | ParseException e) {
            throw new RuntimeException("HTTP POST表单请求失败", e);
        }
    }

    // 处理响应结果
    private static String handleResponse(CloseableHttpResponse response) throws IOException, ParseException {
        int statusCode = response.getCode();
        if (statusCode >= 200 && statusCode < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";
        } else {
            throw new RuntimeException("HTTP请求失败，状态码: " + statusCode);
        }
    }

    // 设置请求头
    private static void setHeaders(HttpUriRequest request, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(request::setHeader);
        }
    }
}