package com.cmsr.onebase.module.metadata.build.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodRequestContext;
import com.cmsr.onebase.module.metadata.core.enums.ClientTypeEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.AbstractMetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import lombok.extern.slf4j.Slf4j;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueTransformMode;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 数据方法核心服务 - 编辑态实现
 * 
 * 本类为编辑态（build）环境提供 MetadataDataMethodCoreService 实现
 * 使用 @ConditionalOnMissingBean 注解，仅在运行态实现类不存在时才创建此 Bean
 * 
 * 与运行态实现的主要区别：
 * 1. 使用 LoginUser 代替 RuntimeLoginUser
 * 2. 使用 SecurityFrameworkUtils 代替 RTSecurityContext
 * 3. 编辑态不进行权限校验（enableAuthCheck=false）
 * 4. 无 applicationId 概念
 * 
 * @author matianyu
 * @date 2025-01-13
 */
@Service
@Slf4j
@ConditionalOnMissingBean(name = "metadataDataMethodCoreServiceImpl")
public class MetadataDataMethodCoreServiceBuildImpl extends AbstractMetadataDataMethodCoreService
        implements MetadataDataMethodCoreService {

    /**
     * 新增数据
     * 
     * @param metadataDataMethodRequestContext 请求上下文
     * @return 新增后的数据
     */
    @Override
    public Map<String, Object> createData(MetadataDataMethodRequestContext metadataDataMethodRequestContext) {
        // 填充编辑态登录用户信息
        fillBuildLoginUser(metadataDataMethodRequestContext);
        
        // 使用父类统一流程处理新增操作
        return executeProcess(metadataDataMethodRequestContext);
    }

    /**
     * 更新数据
     * 
     * @param metadataDataMethodRequestContext 请求上下文
     * @return 更新后的数据
     */
    @Override
    public Map<String, Object> updateData(MetadataDataMethodRequestContext metadataDataMethodRequestContext) {
        // 填充编辑态登录用户信息
        fillBuildLoginUser(metadataDataMethodRequestContext);
        
        // 使用父类统一流程处理更新操作
        return executeProcess(metadataDataMethodRequestContext);
    }

    /**
     * 删除数据
     * 
     * @param methodCoreContext 请求上下文
     * @return 删除是否成功
     */
    @Override
    public Boolean deleteData(MetadataDataMethodRequestContext methodCoreContext) {
        // 填充编辑态登录用户信息
        fillBuildLoginUser(methodCoreContext);
        
        // 使用父类统一流程处理删除操作
        executeProcess(methodCoreContext);
        return true;
    }

    /**
     * 根据ID获取单条数据
     * 
     * @param entityId 实体ID
     * @param id 数据ID
     * @param methodCode 方法代码
     * @return 数据
     */
    @Override
    public Map<String, Object> getData(Long entityId, Object id, String methodCode, Long menuId) {
        MetadataDataMethodRequestContext requestContext = new MetadataDataMethodRequestContext();
        requestContext.setEntityId(entityId);
        requestContext.setId(id);
        requestContext.setMethodCode(methodCode);
        
        // 填充编辑态登录用户信息
        fillBuildLoginUser(requestContext);
        
        return executeProcess(requestContext);
    }

    /**
     * 分页查询数据
     * 
     * @param entityId 实体ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param filters 过滤条件
     * @param methodCode 方法代码
     * @return 分页结果
     */
    @Override
    public PageResult<Map<String, Object>> getDataPage(Long entityId, Integer pageNo, Integer pageSize,
                                                        String sortField, String sortDirection,
                                                        Map<String, Object> filters, String methodCode, Long menuId) {
        log.info("编辑态分页查询 - entityId: {}, pageNo: {}, pageSize: {}", entityId, pageNo, pageSize);
        
        // 编辑态暂不支持分页查询，返回空结果
        // 如需支持，需要实现完整的分页查询逻辑
        log.warn("编辑态暂不支持分页查询，返回空结果");
        return PageResult.empty();
    }

    /**
     * 分页查询数据（OR条件组）
     * 
     * @param entityId 实体ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param orConditionGroups OR条件组列表
     * @param methodCode 方法代码
     * @return 分页结果
     */
    @Override
    public PageResult<Map<String, Object>> getDataPageOr(Long entityId, Integer pageNo, Integer pageSize,
                                                          String sortField, String sortDirection,
                                                          java.util.List<Map<String, Object>> orConditionGroups,
                                                          String methodCode) {
        log.info("编辑态OR条件分页查询 - entityId: {}, pageNo: {}, pageSize: {}, orGroups: {}", 
                 entityId, pageNo, pageSize, orConditionGroups != null ? orConditionGroups.size() : 0);
        
        // 编辑态暂不支持OR条件分页查询，返回空结果
        // 如需支持，需要实现完整的分页查询逻辑
        log.warn("编辑态暂不支持OR条件分页查询，返回空结果");
        return PageResult.empty();
    }

    /**
     * 填充编辑态登录用户信息
     * 
     * 编辑态特点：
     * 1. 不进行权限校验
     * 2. 使用 SecurityFrameworkUtils 获取 LoginUser
     * 3. 无 applicationId
     * 
     * @param context 请求上下文
     */
    private void fillBuildLoginUser(MetadataDataMethodRequestContext context) {
        // 编辑态不进行权限校验
        context.setEnableAuthCheck(false);
        
        // 设置客户端类型为编辑态
        if (context.getClientTypeEnum() == null) {
            context.setClientTypeEnum(ClientTypeEnum.BUILD);
        }
        
        // 获取当前登录用户
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null) {
            LoginUserCtx loginUserCtx = new LoginUserCtx();
            loginUserCtx.setUserId(loginUser.getId());
            // 编辑态无 applicationId，设置为 null
            loginUserCtx.setApplicationId(null);
            context.setLoginUserCtx(loginUserCtx);
            
            log.debug("编辑态登录用户 - userId: {}", loginUser.getId());
        } else {
            log.warn("编辑态未获取到登录用户信息");
        }
    }

    @Override
    protected java.util.Map<String, Object> processDataAndSetDefaults(java.util.Map<String, Object> data,
                                                                      java.util.List<MetadataEntityFieldDO> fields) {
        java.util.Map<String, Object> processedData = convertFieldIdToFieldName(data, fields);

        String realPrimaryKey = getPrimaryKeyFieldName(fields);
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = dateTime.format(dateTimeFormatter);

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            if (fieldName == null) {
                continue;
            }

            if (fieldName.equalsIgnoreCase(realPrimaryKey)) {
                if (!processedData.containsKey(fieldName)) {
                    processedData.put(fieldName, uidGenerator.getUID());
                }
                continue;
            }

            if (BooleanStatusEnum.isYes(field.getIsSystemField())) {
                switch (fieldName.toLowerCase()) {
                    case "created_time":
                    case "createtime":
                        if (!processedData.containsKey(fieldName)) {
                            processedData.put(fieldName, now);
                        }
                        break;
                    case "updated_time":
                    case "updatetime":
                        processedData.put(fieldName, now);
                        break;
                    case "deleted":
                        if (!processedData.containsKey(fieldName)) {
                            processedData.put(fieldName, 0);
                        }
                        break;
                    case "lock_version":
                    case "lockversion":
                        if (!processedData.containsKey(fieldName)) {
                            processedData.put(fieldName, 0);
                        }
                        break;
                    default:
                        if (!processedData.containsKey(fieldName) && StringUtils.hasText(field.getDefaultValue())) {
                            processedData.put(fieldName, field.getDefaultValue());
                        }
                        break;
                }
            } else {
                if (!processedData.containsKey(fieldName) && StringUtils.hasText(field.getDefaultValue())) {
                    processedData.put(fieldName, field.getDefaultValue());
                }
            }
        }

        applyFieldStorageStrategies(processedData, fields, FieldValueTransformMode.STORE);
        return processedData;
    }
}
