/**
 * 诊断测试 - 用于查看DolphinScheduler服务器实际返回内容
 *
 * @author matianyu
 * @date 2025-10-16
 */
package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.config.DolphinSchedulerClientProperties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class DiagnosticTest {

    @Autowired
    private DolphinSchedulerClientProperties properties;

    @Test
    void diagnoseServerResponse() throws IOException {
        if (!properties.isEnabled() || !properties.isEnableLiveConnectivityTest()) {
            System.out.println("跳过诊断测试");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String url = properties.getBaseUrl() + "/projects/1/schedules?pageNo=1&pageSize=10";
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", properties.getToken())
                .get()
                .build();

        System.out.println("=== 诊断信息 ===");
        System.out.println("请求URL: " + url);
        System.out.println("Token: " + properties.getToken());
        System.out.println();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("响应状态码: " + response.code());
            System.out.println("响应头信息:");
            response.headers().forEach(header -> 
                System.out.println("  " + header.getFirst() + ": " + header.getSecond())
            );
            
            ResponseBody body = response.body();
            if (body != null) {
                String content = body.string();
                System.out.println("\n响应内容 (前500字符):");
                System.out.println(content.substring(0, Math.min(500, content.length())));
                
                if (content.startsWith("<")) {
                    System.out.println("\n⚠️ 警告: 服务器返回的是HTML内容,不是JSON!");
                    System.out.println("可能原因:");
                    System.out.println("1. Token无效或已过期");
                    System.out.println("2. 需要先登录");
                    System.out.println("3. API路径错误");
                    System.out.println("4. DolphinScheduler版本不匹配");
                }
            }
        }
    }
}
