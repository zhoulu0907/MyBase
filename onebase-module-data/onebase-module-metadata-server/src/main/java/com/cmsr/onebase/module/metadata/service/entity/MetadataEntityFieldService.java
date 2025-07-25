package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 实体字段 Service 接口
 */
public interface MetadataEntityFieldService {

    /**
     * 创建实体字段
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEntityField(@Valid EntityFieldSaveReqVO createReqVO);

    /**
     * 更新实体字段
     *
     * @param updateReqVO 更新信息
     */
    void updateEntityField(@Valid EntityFieldSaveReqVO updateReqVO);

    /**
     * 删除实体字段
     *
     * @param id 编号
     */
    void deleteEntityField(Long id);

    /**
     * 获得实体字段
     *
     * @param id 编号
     * @return 实体字段
     */
    MetadataEntityFieldDO getEntityField(Long id);

    /**
     * 获得实体字段分页
     *
     * @param pageReqVO 分页查询
     * @return 实体字段分页
     */
    PageResult<MetadataEntityFieldDO> getEntityFieldPage(EntityFieldPageReqVO pageReqVO);

    /**
     * 获得实体字段列表
     *
     * @return 实体字段列表
     */
    List<MetadataEntityFieldDO> getEntityFieldList();

    /**
     * 根据实体ID获得字段列表
     *
     * @param entityId 实体ID
     * @return 字段列表
     */
    List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId);

    /**
     * 批量删除实体字段
     *
     * @param entityId 实体ID
     */
    void deleteEntityFieldsByEntityId(Long entityId);

}
