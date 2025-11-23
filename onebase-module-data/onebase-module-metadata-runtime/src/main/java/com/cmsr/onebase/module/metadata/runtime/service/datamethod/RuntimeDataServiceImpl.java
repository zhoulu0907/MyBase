package com.cmsr.onebase.module.metadata.runtime.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodRequestContext;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

/**
 * 运行时动态数据操作服务实现类
 * 作为适配器，调用core模块的基础服务并转换为VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Service
@Slf4j
public class RuntimeDataServiceImpl implements RuntimeDataService {

    @Resource
    private MetadataDataMethodCoreService coreDataMethodService;

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityService;

    @Resource
    protected TemporaryDatasourceService temporaryDatasourceService;

    @Resource
    protected MetadataDatasourceCoreService metadataDatasourceCoreService;

    @Override
    public DynamicDataRespVO createData(DynamicDataCreateReqVO reqVO) {
        log.info("接收到创建数据请求，entityId: {}, 原始数据: {}, 子实体数据: {}", reqVO.getEntityId(), reqVO.getData(), reqVO.getSubEntities());

        // 将 field_id -> value 转换为 field_name -> value
        Map<String, Object> dataByName = convertIdKeyMapToNameKeyMap(reqVO.getEntityId(), reqVO.getData());

        log.info("字段ID映射为名称后的数据: {}", dataByName);

        // 打印每个字段值的类型
        dataByName.forEach((key, value) -> {
            if (value != null) {
                log.info("字段 {} 的值类型: {}, 值: {}", key, value.getClass().getName(), value);
            } else {
                log.info("字段 {} 的值为null", key);
            }
        });

        MetadataDataMethodRequestContext methodCoreContext = new MetadataDataMethodRequestContext();
        methodCoreContext.setEntityId(reqVO.getEntityId());
        methodCoreContext.setData(dataByName);
        methodCoreContext.setMethodCode(reqVO.getMethodCode());
    // 运行时请求需要传递 menuId 给 core 层用于权限校验
    methodCoreContext.setMenuId(reqVO.getMenuId());
        methodCoreContext.setTraceId(reqVO.getTraceId());
        methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.CREATE);

        if (CollectionUtils.isNotEmpty(reqVO.getSubEntities())) {
            List<MetadataDataMethodSubEntityContext> entityContexts = reqVO.getSubEntities().stream().map(item -> {
                MetadataDataMethodSubEntityContext metadataDataMethodSubEntityContext = new MetadataDataMethodSubEntityContext();
                metadataDataMethodSubEntityContext.setEntityId(item.getSubEntityId());
                metadataDataMethodSubEntityContext.setSubData(item.getSubData());
                return metadataDataMethodSubEntityContext;
            }).collect(Collectors.toList());
            methodCoreContext.setSubEntities(entityContexts);
        }


        // 调用core模块的基础服务
        Map<String, Object> resultData = coreDataMethodService.createData(
                methodCoreContext
        );

        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public DynamicDataRespVO updateData(DynamicDataUpdateReqVO reqVO) {
        // 将 field_id -> value 转换为 field_name -> value
        Map<String, Object> dataByName = convertIdKeyMapToNameKeyMap(reqVO.getEntityId(), reqVO.getData());

        MetadataDataMethodRequestContext methodCoreContext = new MetadataDataMethodRequestContext();
        methodCoreContext.setEntityId(reqVO.getEntityId());
        methodCoreContext.setId(reqVO.getId());
        methodCoreContext.setData(dataByName);
        methodCoreContext.setMethodCode(reqVO.getMethodCode());
        methodCoreContext.setMenuId(reqVO.getMenuId());
        methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.UPDATE);

        if (CollectionUtils.isNotEmpty(reqVO.getSubEntities())) {
            List<MetadataDataMethodSubEntityContext> entityContexts = reqVO.getSubEntities().stream().map(item -> {
                MetadataDataMethodSubEntityContext metadataDataMethodSubEntityContext = new MetadataDataMethodSubEntityContext();
                metadataDataMethodSubEntityContext.setEntityId(item.getSubEntityId());
                metadataDataMethodSubEntityContext.setSubData(item.getSubData());
                return metadataDataMethodSubEntityContext;
            }).collect(Collectors.toList());
            methodCoreContext.setSubEntities(entityContexts);
        }

        // 调用core模块的基础服务 更新主表信息
        Map<String, Object> resultData = coreDataMethodService.updateData(methodCoreContext);

        // 转换为VO
        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public Boolean deleteData(DynamicDataDeleteReqVO reqVO) {
        MetadataDataMethodRequestContext metadataDataMethodRequestContext = new MetadataDataMethodRequestContext();
        metadataDataMethodRequestContext.setEntityId(reqVO.getEntityId());
        metadataDataMethodRequestContext.setId(reqVO.getId());
        metadataDataMethodRequestContext.setMethodCode(reqVO.getMethodCode());
        metadataDataMethodRequestContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.DELETE);
        metadataDataMethodRequestContext.setMenuId(reqVO.getMenuId());


        // 调用core模块的基础服务
        return coreDataMethodService.deleteData(
                metadataDataMethodRequestContext
        );
    }

    @Override
    public DynamicDataRespVO getData(DynamicDataGetReqVO reqVO) {
        // 调用core模块的基础服务查询主表数据
        Map<String, Object> resultData = coreDataMethodService.getData(
                reqVO.getEntityId(),
                reqVO.getId(),
                reqVO.getMethodCode(),
                reqVO.getMenuId()
        );

        return convertToDynamicDataRespVO(resultData);
    }

    @Override
    public PageResult<DynamicDataRespVO> getDataPage(DynamicDataPageReqVO reqVO) {
        // 添加调试日志
        //--
        log.info("分页查询参数 - entityId: {}, pageNo: {}, pageSize: {}, pageSize类型: {}",
                reqVO.getEntityId(), reqVO.getPageNo(), reqVO.getPageSize(),
                reqVO.getPageSize() != null ? reqVO.getPageSize().getClass().getSimpleName() : "null");

        // 将 filters 的 field_id -> value 转换为 field_name -> value
        Map<String, Object> filtersByName = convertIdKeyMapToNameKeyMap(reqVO.getEntityId(), reqVO.getFilters());

        // 允许 sortField 传字段ID：如果是数字则转换为字段名
        String sortField = convertSortFieldToName(reqVO.getEntityId(), reqVO.getSortField());

        // 调用core模块的基础服务
        PageResult<Map<String, Object>> pageResult = coreDataMethodService.getDataPage(
                reqVO.getEntityId(),
                reqVO.getPageNo(),
                reqVO.getPageSize(),
                sortField,
                reqVO.getSortDirection(),
                filtersByName,
                reqVO.getMethodCode(),
                reqVO.getMenuId()
        );

        // 转换为VO
        List<DynamicDataRespVO> list = pageResult.getList().stream()
                .map(this::convertToDynamicDataRespVO)
                .collect(Collectors.toList());

        return new PageResult<>(list, pageResult.getTotal());
    }

    /**
     * 将Map数据转换为DynamicDataRespVO
     */
    @SuppressWarnings("unchecked")
    private DynamicDataRespVO convertToDynamicDataRespVO(Map<String, Object> data) {
        DynamicDataRespVO respVO = new DynamicDataRespVO();
        Object eid = data.get("entityId");
        if(eid instanceof String){
            try { respVO.setEntityId(Long.valueOf((String)eid)); } catch (NumberFormatException ex){ respVO.setEntityId(null); }
        } else if (eid instanceof Number){
            respVO.setEntityId(((Number) eid).longValue());
        }
        respVO.setEntityName((String) data.get("entityName"));
        respVO.setData((Map<String, Object>) data.get("data"));
        respVO.setFieldType((Map<String, String>) data.get("fieldType"));
        respVO.setSubEntities((List<SubEntityVo>)data.get("subEntities"));
        return respVO;

    }

    /**
     * 将以字段ID为键的Map转换为以字段名称为键的Map。
     * 对于无法匹配到字段名称的条目将被忽略，避免将无效字段传递到核心服务。
     *
     * @param entityId 实体ID
     * @param idKeyMap 以字段ID为key的Map
     * @return 以字段名称为key的Map
     */
    private Map<String, Object> convertIdKeyMapToNameKeyMap(Long entityId, Map<Long, Object> idKeyMap) {
        if (idKeyMap == null || idKeyMap.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
        Map<Long, String> idToName = fields.stream()
                .filter(f -> f.getId() != null && f.getFieldName() != null)
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldName, (a,b) -> a));

        return idKeyMap.entrySet().stream()
                .filter(e -> e.getKey() != null && idToName.containsKey(e.getKey()))
                .collect(Collectors.toMap(e -> idToName.get(e.getKey()), Map.Entry::getValue, (a,b) -> b));
    }

    /**
     * 将排序字段支持ID或名称。若传入可解析为Long的ID，则转换为字段名称；否则原样返回。
     */
    private String convertSortFieldToName(Long entityId, String sortField) {
        if (sortField == null || sortField.isBlank()) {
            return sortField;
        }
        try {
            Long id = Long.valueOf(sortField.trim());
            List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
            for (MetadataEntityFieldDO f : fields) {
                if (f.getId() != null && f.getId().equals(id)) {
                    return f.getFieldName();
                }
            }
        } catch (NumberFormatException ignore) {
            // 非数字，按名称处理
        }
        return sortField;
    }

}
