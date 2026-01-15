package com.cmsr.onebase.plugin.ocr.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OCR 插件 API 测试 - Staging 模式
 * <p>
 * 测试 OCR 插件的三个识别接口在 Staging 模式下的连通性
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-14
 */
@SpringBootTest(classes = com.cmsr.onebase.plugin.simulator.PluginHostSimulatorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
@TestPropertySource(properties = {
        "onebase.plugin.enabled=true",
        "onebase.plugin.mode=staging",
        "onebase.plugin.auto-load=true",
        "onebase.plugin.auto-start=true",
        "onebase.plugin.plugins-dir=D:/cmsr/10_cmsr/CodingSpace/plugins-root"
})
public class OcrApiStagingModeTest {

    private static final Logger log = LoggerFactory.getLogger(OcrApiStagingModeTest.class);

    private static final String TEST_IMAGES_PATH = "/test-images/";

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port;
    }

    private File getResourceFile(String resourcePath) {
        try {
            return Paths.get(getClass().getResource(resourcePath).toURI()).toFile();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource file: " + resourcePath, e);
        }
    }

    /**
     * 测试身份证识别接口
     */
    @Test
    @DisplayName("OCR插件 - 身份证识别接口 (Staging模式)")
    void testIdCardRecognition() {
        // 准备测试文件
        File idCardFile = getResourceFile(TEST_IMAGES_PATH + "ID正.png");
        assertThat(idCardFile).exists();

        // 构建 multipart 请求
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("frontFile", new FileSystemResource(idCardFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        String url = baseUrl + "/runtime/plugin/onebase-plugin-ocr/id-card";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        // 验证响应
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo(0);

        log.info("✓ OCR插件 - 身份证识别接口测试通过 (Staging模式)");
    }

    /**
     * 测试港澳台通行证识别接口
     */
    @Test
    @DisplayName("OCR插件 - 港澳台通行证识别接口 (Staging模式)")
    void testExitentrypermitRecognition() {
        // 准备测试文件
        File permitFile = getResourceFile(TEST_IMAGES_PATH + "台湾护照.jpg");
        assertThat(permitFile).exists();

        // 构建 multipart 请求
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("frontFile", new FileSystemResource(permitFile));
        body.add("exitentrypermitType", "tw_passport");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        String url = baseUrl + "/runtime/plugin/onebase-plugin-ocr/exitentrypermit";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        // 验证响应
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo(0);

        log.info("✓ OCR插件 - 港澳台通行证识别接口测试通过 (Staging模式)");
    }

    /**
     * 测试护照识别接口
     */
    @Test
    @DisplayName("OCR插件 - 护照识别接口 (Staging模式)")
    void testPassportRecognition() {
        // 准备测试文件
        File passportFile = getResourceFile(TEST_IMAGES_PATH + "中国护照.jpg");
        assertThat(passportFile).exists();

        // 构建 multipart 请求
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(passportFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        String url = baseUrl + "/runtime/plugin/onebase-plugin-ocr/passport";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        // 验证响应
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo(0);

        log.info("✓ OCR插件 - 护照识别接口测试通过 (Staging模式)");
    }
}
