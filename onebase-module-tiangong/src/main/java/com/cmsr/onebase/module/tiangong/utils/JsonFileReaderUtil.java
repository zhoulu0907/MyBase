package com.cmsr.onebase.module.tiangong.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * JSON文件读取工具类
 *
 * @author lingma
 * @date 2026-02-11
 */
@Slf4j
@Component
public class JsonFileReaderUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 读取classpath下的JSON文件并转换为Map对象
     *
     * @param filePath classpath下的文件路径
     * @return Map格式的JSON数据
     */
    public Map<String, Object> readJsonFileToMap(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream inputStream = resource.getInputStream();
            return objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            log.error("读取JSON文件失败: {}", filePath, e);
            throw new RuntimeException("读取JSON文件失败: " + filePath, e);
        }
    }
}