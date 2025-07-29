package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailOutputParameterVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailParameterVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodOutputParameterVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodParameterVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodPropertyVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

/**
 * 数据方法 Service 实现类
 *
 * @author bty418
 * @date 2025-01-25
 */
@Service
@Slf4j
public class MetadataDataMethodServiceImpl implements MetadataDataMethodService {

    @Resource
    private DataRepository dataRepository;

    @Override
    public List<DataMethodRespVO> getDataMethodList(Long entityId, String methodType, String keyword) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = dataRepository.findById(MetadataBusinessEntityDO.class, entityId);
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 获取实体字段，用于生成方法参数
        DefaultConfigStore fieldConfigStore = new DefaultConfigStore();
        fieldConfigStore.and("entity_id", entityId);
        List<MetadataEntityFieldDO> fields = dataRepository.findAllByConfig(MetadataEntityFieldDO.class, fieldConfigStore);

        // 生成系统内置的数据方法
        List<DataMethodRespVO> methods = generateBuiltInMethods(entity, fields);

        // 根据条件过滤
        if (StringUtils.hasText(methodType)) {
            methods = methods.stream()
                    .filter(method -> methodType.equals(method.getMethodType()))
                    .toList();
        }
        
        if (StringUtils.hasText(keyword)) {
            methods = methods.stream()
                    .filter(method -> method.getMethodName().contains(keyword) 
                                   || method.getDescription().contains(keyword))
                    .toList();
        }

        return methods;
    }

    @Override
    public DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode) {
        // 校验实体存在
        MetadataBusinessEntityDO entity = dataRepository.findById(MetadataBusinessEntityDO.class, entityId);
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 获取实体字段
        DefaultConfigStore fieldConfigStore = new DefaultConfigStore();
        fieldConfigStore.and("entity_id", entityId);
        List<MetadataEntityFieldDO> fields = dataRepository.findAllByConfig(MetadataEntityFieldDO.class, fieldConfigStore);

        // 生成对应的方法详情
        return generateMethodDetail(entity, fields, methodCode);
    }

    /**
     * 生成系统内置的数据方法
     */
    private List<DataMethodRespVO> generateBuiltInMethods(MetadataBusinessEntityDO entity, List<MetadataEntityFieldDO> fields) {
        List<DataMethodRespVO> methods = new ArrayList<>();
        String entityCode = entity.getCode();

        // 1. 新增单条数据
        DataMethodRespVO createMethod = new DataMethodRespVO();
        createMethod.setMethodName("新增单条数据");
        createMethod.setMethodCode("create_single");
        createMethod.setMethodType("CREATE");
        createMethod.setUrl("/api/data/" + entityCode + "/create");
        createMethod.setHttpMethod("POST");
        createMethod.setDescription("数据模型的新增服务，该服务会在业务对象被判断为新增的时候被调用");
        createMethod.setInputParameters(generateInputParameters(fields, false));
        createMethod.setOutputParameters(generateOutputParameter("OBJECT", "创建成功的数据对象"));
        methods.add(createMethod);

        // 2. 更新单条数据
        DataMethodRespVO updateMethod = new DataMethodRespVO();
        updateMethod.setMethodName("更新单条数据");
        updateMethod.setMethodCode("update_single");
        updateMethod.setMethodType("UPDATE");
        updateMethod.setUrl("/api/data/" + entityCode + "/update");
        updateMethod.setHttpMethod("PUT");
        updateMethod.setDescription("数据模型的更新服务，通过主键更新模型已存在的数据");
        updateMethod.setInputParameters(generateInputParameters(fields, true));
        updateMethod.setOutputParameters(generateOutputParameter("OBJECT", "更新成功的数据对象"));
        methods.add(updateMethod);

        // 3. 删除单条数据
        DataMethodRespVO deleteMethod = new DataMethodRespVO();
        deleteMethod.setMethodName("删除单条数据");
        deleteMethod.setMethodCode("delete_single");
        deleteMethod.setMethodType("DELETE");
        deleteMethod.setUrl("/api/data/" + entityCode + "/{id}");
        deleteMethod.setHttpMethod("DELETE");
        deleteMethod.setDescription("数据模型的删除单条记录的服务，通过主键删除目标模型中符合条件的数据");
        deleteMethod.setInputParameters(generateIdParameter());
        deleteMethod.setOutputParameters(generateOutputParameter("BOOLEAN", "删除是否成功"));
        methods.add(deleteMethod);

        // 4. 根据id查询数据详情
        DataMethodRespVO getByIdMethod = new DataMethodRespVO();
        getByIdMethod.setMethodName("根据id查询数据详情");
        getByIdMethod.setMethodCode("get_by_id");
        getByIdMethod.setMethodType("READ");
        getByIdMethod.setUrl("/api/data/" + entityCode + "/{id}");
        getByIdMethod.setHttpMethod("GET");
        getByIdMethod.setDescription("通过主键获取单条数据使用的服务，用来接收一条结果集");
        getByIdMethod.setInputParameters(generateIdParameter());
        getByIdMethod.setOutputParameters(generateOutputParameter("OBJECT", "数据详情对象"));
        methods.add(getByIdMethod);

        // 5. 分页查询数据列表
        DataMethodRespVO getPageMethod = new DataMethodRespVO();
        getPageMethod.setMethodName("分页查询数据列表");
        getPageMethod.setMethodCode("get_page_list");
        getPageMethod.setMethodType("READ");
        getPageMethod.setUrl("/api/data/" + entityCode + "/list");
        getPageMethod.setHttpMethod("GET");
        getPageMethod.setDescription("通过查询条件获取多条数据使用的服务，用来接收多条结果集");
        getPageMethod.setInputParameters(generatePageParameters());
        getPageMethod.setOutputParameters(generateOutputParameter("PAGE_OBJECT", "分页的数据列表"));
        methods.add(getPageMethod);

        return methods;
    }

    /**
     * 生成方法详情
     */
    private DataMethodDetailRespVO generateMethodDetail(MetadataBusinessEntityDO entity, List<MetadataEntityFieldDO> fields, String methodCode) {
        DataMethodDetailRespVO detail = new DataMethodDetailRespVO();
        String entityCode = entity.getCode();

        switch (methodCode) {
            case "create_single":
                detail.setMethodName("新增单条数据");
                detail.setMethodCode("create_single");
                detail.setMethodType("CREATE");
                detail.setUrl("/api/data/" + entityCode + "/create");
                detail.setHttpMethod("POST");
                detail.setDescription("新增单条" + entity.getDisplayName() + "记录");
                detail.setInputParameters(generateDetailInputParameters(fields, false));
                detail.setOutputParameters(generateDetailOutputParameter("OBJECT", "创建成功的" + entity.getDisplayName() + "对象", fields));
                detail.setRequestExample(generateRequestExample(fields, false));
                detail.setResponseExample(generateResponseExample(entity, fields));
                break;
            case "get_by_id":
                detail.setMethodName("根据id查询数据详情");
                detail.setMethodCode("get_by_id");
                detail.setMethodType("READ");
                detail.setUrl("/api/data/" + entityCode + "/{id}");
                detail.setHttpMethod("GET");
                detail.setDescription("根据ID查询" + entity.getDisplayName() + "详情");
                detail.setInputParameters(generateDetailIdParameter());
                detail.setOutputParameters(generateDetailOutputParameter("OBJECT", entity.getDisplayName() + "详情对象", fields));
                detail.setRequestExample(null);
                detail.setResponseExample(generateResponseExample(entity, fields));
                break;
            // 可以继续添加其他方法的详细实现
            default:
                detail.setMethodName("未知方法");
                detail.setDescription("方法不存在");
        }

        return detail;
    }

    /**
     * 生成输入参数
     */
    private List<DataMethodParameterVO> generateInputParameters(List<MetadataEntityFieldDO> fields, boolean includeId) {
        List<DataMethodParameterVO> parameters = new ArrayList<>();
        
        for (MetadataEntityFieldDO field : fields) {
            if (!includeId && field.getIsPrimaryKey()) {
                continue;
            }
            if (field.getIsSystemField()) {
                continue;
            }
            
            DataMethodParameterVO param = new DataMethodParameterVO();
            param.setParamName(field.getFieldName());
            param.setParamType(field.getFieldType());
            param.setRequired(field.getIsRequired());
            param.setDescription(field.getDisplayName());
            parameters.add(param);
        }
        
        return parameters;
    }

    /**
     * 生成详情输入参数
     */
    private List<DataMethodDetailParameterVO> generateDetailInputParameters(List<MetadataEntityFieldDO> fields, boolean includeId) {
        List<DataMethodDetailParameterVO> parameters = new ArrayList<>();
        
        for (MetadataEntityFieldDO field : fields) {
            if (!includeId && field.getIsPrimaryKey()) {
                continue;
            }
            if (field.getIsSystemField()) {
                continue;
            }
            
            DataMethodDetailParameterVO param = new DataMethodDetailParameterVO();
            param.setParamName(field.getFieldName());
            param.setParamType(field.getFieldType());
            param.setParamLength(field.getDataLength());
            param.setRequired(field.getIsRequired());
            param.setDescription(field.getDisplayName());
            param.setValidationRules(new ArrayList<>()); // 这里可以根据实际校验规则填充
            parameters.add(param);
        }
        
        return parameters;
    }

    /**
     * 生成ID参数
     */
    private List<DataMethodParameterVO> generateIdParameter() {
        List<DataMethodParameterVO> parameters = new ArrayList<>();
        DataMethodParameterVO param = new DataMethodParameterVO();
        param.setParamName("id");
        param.setParamType("BIGINT");
        param.setRequired(true);
        param.setDescription("主键ID");
        parameters.add(param);
        return parameters;
    }

    /**
     * 生成详情ID参数
     */
    private List<DataMethodDetailParameterVO> generateDetailIdParameter() {
        List<DataMethodDetailParameterVO> parameters = new ArrayList<>();
        DataMethodDetailParameterVO param = new DataMethodDetailParameterVO();
        param.setParamName("id");
        param.setParamType("BIGINT");
        param.setParamLength(20);
        param.setRequired(true);
        param.setDescription("主键ID");
        param.setValidationRules(new ArrayList<>());
        parameters.add(param);
        return parameters;
    }

    /**
     * 生成分页参数
     */
    private List<DataMethodParameterVO> generatePageParameters() {
        List<DataMethodParameterVO> parameters = new ArrayList<>();
        
        DataMethodParameterVO pageNumParam = new DataMethodParameterVO();
        pageNumParam.setParamName("pageNum");
        pageNumParam.setParamType("INTEGER");
        pageNumParam.setRequired(false);
        pageNumParam.setDescription("页码");
        parameters.add(pageNumParam);
        
        DataMethodParameterVO pageSizeParam = new DataMethodParameterVO();
        pageSizeParam.setParamName("pageSize");
        pageSizeParam.setParamType("INTEGER");
        pageSizeParam.setRequired(false);
        pageSizeParam.setDescription("每页大小");
        parameters.add(pageSizeParam);
        
        return parameters;
    }

    /**
     * 生成输出参数
     */
    private DataMethodOutputParameterVO generateOutputParameter(String type, String description) {
        DataMethodOutputParameterVO output = new DataMethodOutputParameterVO();
        output.setType(type);
        output.setDescription(description);
        return output;
    }

    /**
     * 生成详情输出参数
     */
    private DataMethodDetailOutputParameterVO generateDetailOutputParameter(String type, String description, List<MetadataEntityFieldDO> fields) {
        DataMethodDetailOutputParameterVO output = new DataMethodDetailOutputParameterVO();
        output.setType(type);
        output.setDescription(description);
        
        List<DataMethodPropertyVO> properties = new ArrayList<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getIsSystemField()) {
                continue;
            }
            
            DataMethodPropertyVO property = new DataMethodPropertyVO();
            property.setFieldName(field.getFieldName());
            property.setFieldType(field.getFieldType());
            property.setDescription(field.getDisplayName());
            properties.add(property);
        }
        output.setProperties(properties);
        
        return output;
    }

    /**
     * 生成请求示例
     */
    private Object generateRequestExample(List<MetadataEntityFieldDO> fields, boolean includeId) {
        Map<String, Object> example = new HashMap<>();
        
        for (MetadataEntityFieldDO field : fields) {
            if (!includeId && field.getIsPrimaryKey()) {
                continue;
            }
            if (field.getIsSystemField()) {
                continue;
            }
            
            // 根据字段类型生成示例值
            Object exampleValue = generateExampleValue(field);
            example.put(field.getFieldName(), exampleValue);
        }
        
        return example;
    }

    /**
     * 生成响应示例
     */
    private Object generateResponseExample(MetadataBusinessEntityDO entity, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "操作成功");
        
        Map<String, Object> data = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getIsSystemField()) {
                continue;
            }
            
            Object exampleValue = generateExampleValue(field);
            if (field.getIsPrimaryKey()) {
                exampleValue = 1001L;
            }
            data.put(field.getFieldName(), exampleValue);
        }
        data.put("createTime", LocalDateTime.now().toString());
        
        response.put("data", data);
        return response;
    }

    /**
     * 根据字段类型生成示例值
     */
    private Object generateExampleValue(MetadataEntityFieldDO field) {
        String fieldType = field.getFieldType();
        String fieldName = field.getFieldName().toLowerCase();
        
        return switch (fieldType) {
            case "VARCHAR", "TEXT" -> {
                if (fieldName.contains("name")) yield "示例名称";
                if (fieldName.contains("email")) yield "example@test.com";
                if (fieldName.contains("phone")) yield "13800138000";
                yield "示例文本";
            }
            case "INTEGER" -> 100;
            case "BIGINT" -> 1001L;
            case "DECIMAL" -> 99.99;
            case "BOOLEAN" -> true;
            case "DATE" -> "2025-01-25";
            case "DATETIME" -> "2025-01-25T10:30:00";
            default -> "示例值";
        };
    }

} 