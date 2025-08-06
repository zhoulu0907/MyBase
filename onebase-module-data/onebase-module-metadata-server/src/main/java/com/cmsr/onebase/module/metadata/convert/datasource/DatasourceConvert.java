package com.cmsr.onebase.module.metadata.convert.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datasource.vo.DatasourceSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface DatasourceConvert {

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
     * 将Map转换为JSON字符串
     */
    default String mapToString(Map<String, Object> configMap) {
        if (configMap == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(configMap);
        } catch (Exception e) {
            // 如果序列化失败，返回null或者抛出运行时异常
            throw new RuntimeException("配置信息JSON序列化失败", e);
        }
    }
}
