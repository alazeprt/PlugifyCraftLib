package top.alazeprt.pclib.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static final RequestConfig rc = RequestConfig.custom().setConnectionRequestTimeout(3000L, TimeUnit.MILLISECONDS).setResponseTimeout(10000L, TimeUnit.MILLISECONDS).build();

    public static String getImageEncoded(String url) throws IOException {
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(rc).build()) {
            HttpGet httpGet = new HttpGet(url);
            return client.execute(httpGet, response -> {
                int statusCode = response.getCode();
                HttpEntity entity = response.getEntity();

                if (statusCode >= 200 && statusCode < 300) {
                    if (entity != null) {
                        byte[] imageBytes = EntityUtils.toByteArray(entity);
                        return Base64.getEncoder().encodeToString(imageBytes);
                    }
                    throw new IOException("Empty response body");
                } else {
                    EntityUtils.consume(entity);
                    throw new IOException("HTTP Drror: " + statusCode);
                }
            });
        }
    }

    public static String get(String url, Map<String, String> headers, Map<String, String> queryParams) throws IOException {
        String fullUrl = buildUrlWithQueryParams(url, queryParams);
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(rc).build()) {
            HttpGet httpGet = new HttpGet(fullUrl);

            // 添加请求头
            if (headers != null) {
                headers.forEach(httpGet::addHeader);
            }

            return client.execute(httpGet, response -> {
                int statusCode = response.getCode();
                HttpEntity entity = response.getEntity();

                return handleResponse(statusCode, entity);
            });
        }
    }

    private static String buildUrlWithQueryParams(String url, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return url;
        }

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append('&');
            }
            queryString.append(encode(entry.getKey()))
                    .append('=')
                    .append(encode(entry.getValue()));
        }

        return url + (url.contains("?") ? "&" : "?") + queryString;
    }

    private static String handleResponse(int statusCode, HttpEntity entity) throws IOException, ParseException {
        if (statusCode >= 200 && statusCode < 300) {
            return entity != null ? EntityUtils.toString(entity) : "";
        } else {
            EntityUtils.consume(entity);
            throw new IOException("HTTP Error: " + statusCode);
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
