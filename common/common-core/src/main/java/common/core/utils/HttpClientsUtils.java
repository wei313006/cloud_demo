package common.core.utils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author abing
 * @created 2025/3/10 14:00
 */

public class HttpClientsUtils {

    public static String sendGet(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
//        httpGet.setHeader("Content-Type", "application/json");
        return httpClient.execute(httpGet, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));
    }

    public static String sendPost(String url, String params) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(params);
        httpPost.setEntity(entity);
        return httpClient.execute(httpPost, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));
    }

}
