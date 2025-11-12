package com.cmsr.onebase.module.metadata.core.domain.query;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import lombok.Data;
import org.anyline.service.AnylineService;

import java.util.List;
import java.util.Map;
@Data
public class ProcessContext {

    /**
     * 请求上下文
     */
    private String traceId;
    private MetadataDataMethodRequestContext requestContext;
    private MetadataDataMethodOpEnum operationType;
    private Long entityId;
    private Object id; // 数据ID，用于update/delete/get操作
    private Map<String, Object> data;
    private String methodCode;
    // 核心上下文字段
    private MetadataBusinessEntityDO entity;
    private List<MetadataEntityFieldDO> fields; // 实体字段列表
    private Map<String, Object> processedData; // 处理后的数据
    private AnylineService<?> temporaryService;
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
     * 当前登录用户的权限上下文
     */
    private MetadataPermissionContext metadataPermissionContext;
}
