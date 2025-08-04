package com.cmsr.onebase.module.metadata.convert.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface DatasourceConvert {

    DatasourceConvert INSTANCE = Mappers.getMapper(DatasourceConvert.class);

    @Mapping(target = "config", expression = "java(stringToMap(bean.getConfig()))")
    DatasourceRespVO convert(MetadataDatasourceDO bean);

    @Mapping(target = "config", expression = "java(mapToString(reqVO.getConfig()))")
    MetadataDatasourceDO convert(DatasourceSaveReqVO reqVO);

    List<DatasourceRespVO> convertList(List<MetadataDatasourceDO> list);

    default PageResult<DatasourceRespVO> convertPage(PageResult<MetadataDatasourceDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

    /**
     * 将JSON字符串转换为Map
     */
    default Map<String, Object> stringToMap(String configStr) {
        if (configStr == null || configStr.trim().isEmpty()) {
            return null;
        }
        
        // 处理16进制编码格式（PostgreSQL的字节数组格式）
        if (configStr.startsWith("\\x")) {
            try {
                // 移除 \x 前缀并解码16进制
                String hexString = configStr.substring(2);
                byte[] bytes = hexStringToByteArray(hexString);
                String jsonStr = new String(bytes, "UTF-8");
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                // 如果16进制解码失败，返回空Map
                return new java.util.HashMap<>();
            }
        }
        
        // 检查是否是数字串（可能是旧的字节数组格式）
        if (configStr.matches("^[0-9,]+$")) {
            // 如果是数字串，尝试转换为字节数组再解析
            try {
                String[] numbers = configStr.split(",");
                byte[] bytes = new byte[numbers.length];
                for (int i = 0; i < numbers.length; i++) {
                    bytes[i] = (byte) Integer.parseInt(numbers[i]);
                }
                String jsonStr = new String(bytes, "UTF-8");
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                // 如果字节数组转换也失败，返回空Map
                return new java.util.HashMap<>();
            }
        }
        
        // 正常的JSON字符串解析
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(configStr, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            // 如果解析失败，返回空Map而不是抛异常
            return new java.util.HashMap<>();
        }
    }

    /**
     * 将Map转换为JSON字符串（明文格式）
     */
    default String mapToString(Map<String, Object> configMap) {
        if (configMap == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 确保返回的是明文JSON字符串，不进行16进制编码
            return objectMapper.writeValueAsString(configMap);
        } catch (Exception e) {
            // 如果序列化失败，返回null或者抛出运行时异常
            throw new RuntimeException("配置信息JSON序列化失败", e);
        }
    }

    /**
     * 将16进制字符串转换为字节数组
     */
    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
