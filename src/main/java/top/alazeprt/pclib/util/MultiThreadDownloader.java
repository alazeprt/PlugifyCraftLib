package top.alazeprt.pclib.util;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class MultiThreadDownloader {
    // 最大重定向次数
    private static final int MAX_REDIRECTS = 5;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public static void download(String url, int threadCount, File path) throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setRedirectsEnabled(false)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setUserAgent(USER_AGENT)
                .build()) {

            RedirectResult redirectResult = resolveRedirects(httpClient, url);
            String effectiveUrl = redirectResult.effectiveUrl;

            try (CloseableHttpResponse headResponse = redirectResult.response) {
                verifyResponseStatus(headResponse);

                String fileName = extractFileName(effectiveUrl);
                if (fileName.isBlank() || !fileName.contains(".") || !Objects.equals(fileName.split(".")[fileName.split(".").length - 1], "jar")) {
                    fileName = "plugin (downloaded by plugify craft).jar";
                }
                File outputFile = new File(path, fileName);

                boolean supportsMultiThread = supportMultiThread(headResponse);
                Long fileSize = tryGetFileSize(headResponse);

                // 决策逻辑：同时需要支持分块下载和已知文件大小
                if (supportsMultiThread && fileSize != null) {
                    downloadMultiThread(httpClient, effectiveUrl, outputFile, fileSize, threadCount);
                } else {
                    downloadSingleThread(httpClient, effectiveUrl, outputFile);
                }
            }
        }
    }

    // 修改后的文件大小获取方法（返回可空Long）
    private static Long tryGetFileSize(CloseableHttpResponse response) {
        try {
            Header contentLength = response.getFirstHeader("Content-Length");
            if (contentLength != null) {
                return Long.parseLong(contentLength.getValue());
            }

            // 尝试从Content-Disposition获取文件大小（备用方案）
            Header contentDisposition = response.getFirstHeader("Content-Disposition");
            if (contentDisposition != null) {
                String value = contentDisposition.getValue();
                // 尝试解析类似 "attachment; filename=file.zip; size=123456"
                String[] parts = value.split(";");
                for (String part : parts) {
                    if (part.trim().startsWith("size=")) {
                        return Long.parseLong(part.split("=")[1].trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("获取文件大小失败: " + e.getMessage());
        }
        return null;
    }

    // 修改后的重定向处理方法
    private static RedirectResult resolveRedirects(CloseableHttpClient client, String originalUrl) throws IOException {
        String currentUrl = originalUrl;
        int redirectCount = 0;
        CloseableHttpResponse response = null;

        while (redirectCount < MAX_REDIRECTS) {
            HttpGet request = new HttpGet(currentUrl);
            request.setHeader("User-Agent", USER_AGENT);
            request.setHeader("Accept-Encoding", "identity"); // 禁用压缩以获取真实文件大小

            response = client.execute(request);
            int statusCode = response.getCode();

            if (isRedirect(statusCode)) {
                Header location = response.getFirstHeader("Location");
                if (location == null) {
                    throw new IOException("重定向响应缺少Location头");
                }
                currentUrl = location.getValue();
                redirectCount++;
                response.close();
            } else if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
                return new RedirectResult(response, currentUrl);
            } else {
                String body = readResponseBody(response);
                response.close();
                throw new IOException("无效响应状态码: " + statusCode + "\n响应内容: " + body);
            }
        }
        throw new IOException("超过最大重定向次数: " + MAX_REDIRECTS);
    }

    // 修改后的多线程下载方法（增加安全校验）
    private static void downloadMultiThread(CloseableHttpClient httpClient, String url,
                                            File outputFile, long fileSize, int threadCount)
            throws IOException {
        // 验证文件大小有效性
        if (fileSize <= 0) {
            throw new IllegalArgumentException("无效文件大小: " + fileSize);
        }

        try (RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {
            raf.setLength(fileSize);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>(threadCount);

        long chunkSize = fileSize / threadCount;
        for (int i = 0; i < threadCount; i++) {
            long start = i * chunkSize;
            long end = (i == threadCount - 1) ? fileSize - 1 : start + chunkSize - 1;
            futures.add(executor.submit(
                    new DownloadTask(httpClient, url, outputFile, start, end)
            ));
        }

        waitForCompletion(executor, futures);
    }

    // 新增方法：增强型文件名解析
    private static String extractFileName(String url) throws IOException {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            // 优先从Content-Disposition获取文件名
            HttpGet probeRequest = new HttpGet(url);
            probeRequest.setHeader("Range", "bytes=0-0"); // 最小范围请求

            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(probeRequest)) {

                Header contentDisposition = response.getFirstHeader("Content-Disposition");
                if (contentDisposition != null) {
                    String value = contentDisposition.getValue();
                    if (value.contains("filename=")) {
                        String fileName = value.split("filename=")[1];
                        fileName = fileName.replace("\"", "");
                        if (!fileName.isEmpty()) {
                            return fileName;
                        }
                    }
                }
            }

            // 回退到URL解析
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            if (fileName.isEmpty()) {
                throw new IOException("无法从URL解析文件名: " + url);
            }
            return fileName;
        } catch (URISyntaxException e) {
            throw new IOException("无效的URL格式: " + url, e);
        }
    }
    // 新增方法：读取响应内容用于错误诊断
    private static String readResponseBody(CloseableHttpResponse response) {
        try (InputStream content = response.getEntity().getContent();
             BufferedReader reader = new BufferedReader(new InputStreamReader(content))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            return "[无法读取响应内容]";
        }
    }

    // 单线程下载实现
    private static void downloadSingleThread(CloseableHttpClient httpClient,
                                             String url, File outputFile)
            throws IOException {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request);
             InputStream input = response.getEntity().getContent();
             FileOutputStream output = new FileOutputStream(outputFile)) {

            verifyResponseStatus(response);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
    }

    private static class DownloadTask implements Runnable {
        private final CloseableHttpClient client;
        private final String url;
        private final File outputFile;
        private final long startByte;
        private final long endByte;

        DownloadTask(CloseableHttpClient client, String url, File outputFile,
                     long start, long end) {
            this.client = client;
            this.url = url;
            this.outputFile = outputFile;
            this.startByte = start;
            this.endByte = end;
        }

        @Override
        public void run() {
            HttpGet request = new HttpGet(url);
            request.setHeader("Range", "bytes=" + startByte + "-" + endByte);
            request.setHeader("User-Agent", USER_AGENT);

            try (CloseableHttpResponse response = client.execute(request);
                 InputStream content = response.getEntity().getContent();
                 RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {

                verifyPartialContent(response);
                raf.seek(startByte);

                byte[] buffer = new byte[8192]; // 增大缓冲区
                int bytesRead;
                while ((bytesRead = content.read(buffer)) != -1) {
                    raf.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new UncheckedIOException("分块下载失败", e);
            }
        }
    }

    // 其他辅助方法保持不变，但添加更多错误信息
    private static boolean isRedirect(int statusCode) {
        return statusCode == HttpStatus.SC_MOVED_TEMPORARILY
                || statusCode == HttpStatus.SC_MOVED_PERMANENTLY
                || statusCode == HttpStatus.SC_SEE_OTHER
                || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT;
    }

    private static void verifyResponseStatus(CloseableHttpResponse response) throws IOException {
        int statusCode = response.getCode();
        if (statusCode < 200 || statusCode >= 300) {
            String body = readResponseBody(response);
            throw new IOException("无效响应状态码: " + statusCode + "\n响应内容: " + body);
        }
    }

    private static void verifyPartialContent(CloseableHttpResponse response) throws IOException {
        if (response.getCode() != HttpStatus.SC_PARTIAL_CONTENT) {
            throw new IOException("无效的分段响应: " + response.getCode());
        }
    }

    private static boolean supportMultiThread(CloseableHttpResponse response) {
        Header acceptRanges = response.getFirstHeader("Accept-Ranges");
        return acceptRanges != null && "bytes".equalsIgnoreCase(acceptRanges.getValue());
    }

    private static long getFileSize(CloseableHttpResponse response) throws IOException {
        Header contentLength = response.getFirstHeader("Content-Length");
        for (Header header : response.getHeaders()) {
            System.out.println(header.getName() + ": " + header.getValue());
        }
        if (contentLength == null) {
            throw new IOException("无法获取文件大小");
        }
        return Long.parseLong(contentLength.getValue());
    }

    private static void waitForCompletion(ExecutorService executor, List<Future<?>> futures)
            throws IOException {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                executor.shutdownNow();
                throw new IOException("下载失败: " + e.getCause().getMessage(), e);
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                throw new IOException("下载被中断", e);
            }
        }
        executor.shutdown();
    }

    // 重定向结果包装类
    private static class RedirectResult {
        final CloseableHttpResponse response;
        final String effectiveUrl;

        RedirectResult(CloseableHttpResponse response, String effectiveUrl) {
            this.response = response;
            this.effectiveUrl = effectiveUrl;
        }
    }
}