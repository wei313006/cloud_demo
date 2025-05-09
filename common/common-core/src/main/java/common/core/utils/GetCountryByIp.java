package common.core.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 获取国家
 */
public class GetCountryByIp {
    public static JSONObject getCountry(String ip) throws IOException {
        String apiURL = "http://ip-api.com/json/" + ip; // 替换为实际的IP定位API地址
        URL url = new URL(apiURL);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(5000); // 设置连接超时时间
        conn.setReadTimeout(5000); // 设置读取超时时间

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        // 返回的JSON结果，获取国家信息
        return new JSONObject(sb.toString());
    }

    public static JSONObject getCountryAndCityInfo(String ip) throws IOException {
        String apiURL = "http://ip-api.com/json/" + ip + "?fields=country,countryCode,regionName,city"; // 替换为实际的IP定位API地址
        URL url = new URL(apiURL);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(5000); // 设置连接超时时间
        conn.setReadTimeout(5000); // 设置读取超时时间

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return new JSONObject(sb.toString());
    }

//    public static void main(String[] args) throws IOException {
//        JSONObject jsonObject = getCountryAndCityInfo("114.132.222.11");
//        System.out.println(jsonObject);
//    }

}
