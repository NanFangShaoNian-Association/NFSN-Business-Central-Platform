package cn.nfsn.common.core.utils;

import cn.nfsn.common.core.exception.UnexpectedHttpStatusException;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: ImprovedHttpClientUtil
 * @Description: 改进的HttpClient工具类，提供了更高效和安全的网络请求操作
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-03 22:50:14
 **/
public class ImprovedHttpClientUtil {
    /**
     * 日志处理对象
     */
    private static final Logger logger = LoggerFactory.getLogger(ImprovedHttpClientUtil.class);

    /**
     * http客户端对象，用于执行http请求
     */
    private final CloseableHttpClient httpClient;

    /**
     * 默认的请求头
     */
    private final Header[] defaultHeaders;

    /**
     * 连接池管理器
     */
    private final HttpClientConnectionManager connectionManager;

    /**
     * 限流器
     */
    private final RateLimiter rateLimiter;


    /**
     * 构造函数
     *
     * @param connectTimeout    连接超时时间
     * @param socketTimeout     Socket超时时间
     * @param rate              限流器每秒允许的请求数
     * @param maxTotal          最大连接数
     * @param defaultMaxPerRoute 最大路由数
     */
    public ImprovedHttpClientUtil(int connectTimeout, int socketTimeout, double rate, int maxTotal, int defaultMaxPerRoute) {
        // 设置默认的请求头
        this.defaultHeaders = new Header[]{new BasicHeader("Accept", "application/json"),
                new BasicHeader("Content-Type", "application/json")};

        // 创建request配置，设置连接和Socket超时时间
        RequestConfig config = RequestConfig.custom()
                // 设置连接超时时间
                .setConnectTimeout(connectTimeout)
                // 设置Socket超时时间
                .setSocketTimeout(socketTimeout)
                .build();

        // 创建连接池管理器
        this.connectionManager = new PoolingHttpClientConnectionManager();

        // 创建限流器
        rateLimiter = RateLimiter.create(rate);

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        cm.setMaxTotal(maxTotal);
        // 设置最大路由
        cm.setDefaultMaxPerRoute(defaultMaxPerRoute);

        // 使用默认的请求配置创建httpClient对象
        this.httpClient = HttpClients.custom().setDefaultRequestConfig(config).setConnectionManager(cm).build();

        // 在JVM退出时关闭httpClient
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connectionManager.shutdown();
            } catch (Exception e) {
                logger.error("Error closing HttpClientConnectionManager", e);
            }
        }));
    }

    /**
     * 发送POST请求
     *
     * @param url    请求的URL
     * @param entity 请求的实体内容
     * @return 返回结果字符串
     * @throws IOException        网络异常
     * @throws URISyntaxException URI语法异常
     */
    public String sendPost(String url, HttpEntity entity, Header... headers) throws IOException, URISyntaxException {
        // 在发送请求前获取令牌，如果没有获取到令牌则阻塞等待
        rateLimiter.acquire();

        // 创建HttpPost对象
        HttpPost httpPost = new HttpPost();

        // 初始化返回结果为空字符串
        String result = "";

        // 设置请求的URI
        httpPost.setURI(new URI(url));

        // 设置请求的实体
        httpPost.setEntity(entity);

        // 设置请求头，如果headers参数没有传入，则使用默认的请求头
        if (headers != null && headers.length > 0) {
            httpPost.setHeaders(headers);
        } else {
            httpPost.setHeaders(defaultHeaders);
        }

        // 使用try-with-resources确保response的关闭
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 获取请求的字符集，如果没有指定，则默认为 UTF-8
            Charset charset = ContentType.getOrDefault(response.getEntity()).getCharset();
            if (charset == null) {
                charset = StandardCharsets.UTF_8;
            }
            // 获取响应状态码
            int statusCode = response.getStatusLine().getStatusCode();

            // 如果状态码在200-300之间，表示请求成功，获取返回结果
            if (statusCode >= 200 && statusCode < 300) {
                result = EntityUtils.toString(response.getEntity(), charset);
            } else {
                // 如果状态码不在200-300之间，记录错误日志，并抛出异常
                logger.error("Unexpected response status: {}", statusCode);
                throw new UnexpectedHttpStatusException("Unexpected response status: " + statusCode, response);
            }
        }
        // 返回调用结果
        return result;
    }

    /**
     * 关闭httpClient
     * 如果httpClient不为null，就调用其close方法来关闭连接。
     *
     * @throws IOException 网络异常
     */
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}