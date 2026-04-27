package com.cmsr.onebase.module.metadata.core.domain.query;

import com.cmsr.onebase.module.metadata.core.enums.ClientTypeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MetadataDataMethodRequestContext {

    /**
     * 操作追踪ID
     */
    private String traceId;

    /**
     * 操作类型
     */
    private MetadataDataMethodOpEnum metadataDataMethodOpEnum;

    /**
     * 被调用的模块类型，默认runtime
     */
    private ClientTypeEnum clientTypeEnum = ClientTypeEnum.RUNTIME;
    /**
     * 实体ID（兼容字段，建议使用entityUuid）
     */
    private Long entityId;
    
    /**
     * 实体UUID
     */
    private String entityUuid;
    /**
     * 数据ID
     */
    private Object id;
    /**
     * 数据内容：字段列表，key-字段id，value-字段值
     */
    private Map<String, Object> data;
    /**
     * 方法编码（可选）
     */
    private String methodCode;

    /**
     * 菜单ID（如果是runtime调用，必填）
     */
    private Long menuId;

    /**
     * 子表上下文数据
     */
    private List<MetadataDataMethodSubEntityContext> subEntities;

    /**
     * 当前登录用户
     */
    private LoginUserCtx loginUserCtx;

    /**
     * 权限上下文
     */
    private MetadataPermissionContext permissionContext;

    /**
     * 权限校验开关， 默认false
     */
    private boolean enableAuthCheck;





}
