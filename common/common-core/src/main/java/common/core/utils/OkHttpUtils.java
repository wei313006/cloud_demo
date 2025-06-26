package common.core.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp工具类
 * 特性：
 * 1. 内置连接池复用机制
 * 2. 支持同步/异步请求（示例为同步）
 * 3. 自动JSON序列化
 * 4. 完善的超时配置
 * 5. 自动关闭响应资源
 */
public class OkHttpUtils {

    // 使用单例OkHttpClient
    private static final OkHttpClient httpClient;

    static {
        // 创建连接池（默认5个空闲连接，存活5分钟）
        ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);

        // 配置HTTP客户端
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)    // 连接超时
                .readTimeout(30, TimeUnit.SECONDS)       // 读取超时
                .writeTimeout(30, TimeUnit.SECONDS)      // 写入超时
                .connectionPool(connectionPool)
                .build();
    }

    /**
     * GET请求
     *
     * @param url     请求地址
     * @param headers 请求头（可选）
     */
    public static String doGet(String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeaders(builder, headers);

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("HTTP GET请求失败", e);
        }
    }

    /**
     * DELETE请求
     *
     * @param url     请求地址
     * @param headers 请求头（可选）
     */
    public static String doDelete(String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder().url(url).delete();
        addHeaders(builder, headers);

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("HTTP DELETE请求失败", e);
        }
    }

    /**
     * POST JSON请求
     *
     * @param url      请求地址
     * @param jsonBody JSON字符串
     * @param headers  请求头（可选）
     */
    public static String doPostJson(String url, String jsonBody, Map<String, String> headers) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        addHeaders(builder, headers);

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("HTTP POST请求失败", e);
        }
    }

    /**
     * Put JSON请求
     *
     * @param url      请求地址
     * @param jsonBody JSON字符串
     * @param headers  请求头（可选）
     */
    public static String doPutJson(String url, String jsonBody, Map<String, String> headers) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(body);
        addHeaders(builder, headers);

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("HTTP PUT请求失败", e);
        }
    }

    /**
     * POST表单请求
     *
     * @param url        请求地址
     * @param formParams 表单参数
     * @param headers    请求头（可选）
     */
    public static String doPostForm(String url, Map<String, String> formParams, Map<String, String> headers) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formParams.forEach(formBuilder::add);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBuilder.build());
        addHeaders(builder, headers);

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("HTTP表单请求失败", e);
        }
    }

    // 处理响应结果
    private static String handleResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new RuntimeException("HTTP请求失败，状态码: " + response.code());
        }

        try (ResponseBody body = response.body()) {
            return body != null ? body.string() : "";
        }
    }

    // 添加请求头
    private static void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
    }

    public static void main(String[] args) {
        // GET请求
        String result = OkHttpUtils.doGet("http://localhost:9090/user/common/config",
                Map.of());
        System.out.println(result);

//        String result = OkHttpUtils.doDelete("http://localhost:9090/user/delete_by_id/1115437401863639041",
//                Map.of());
//        System.out.println(result);

//        String json = """
//                {
//                  "username":"abing",
//                  "password":"123",
//                  "checkCodeId":"123",
//                  "checkCode":"abing"
//                }
//                """;
//        String jsonResponse = OkHttpUtils.doPostJson("http://localhost:9090/user/auth/login", json, Map.of());
//        System.out.println(jsonResponse);
//
//        Map<String, String> formParams = Map.of("username", "john", "password", "secret");
//        String formResponse = OkHttpUtils.doPostForm("https://api.example.com/login", formParams, null);
    }

}