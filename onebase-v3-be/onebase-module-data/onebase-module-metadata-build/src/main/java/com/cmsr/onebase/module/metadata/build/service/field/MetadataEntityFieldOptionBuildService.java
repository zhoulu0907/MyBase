package com.cmsr.onebase.module.metadata.build.service.field;

import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionBatchSortReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;

import java.util.List;

/**
 * 字段选项 Service
 *
 * @author bty418
 * @date 2025-08-18
 */
public interface MetadataEntityFieldOptionBuildService {

    List<MetadataEntityFieldOptionDO> listByFieldId(String fieldUuid);

    Long create(MetadataEntityFieldOptionDO option);

    void update(MetadataEntityFieldOptionDO option);

    void deleteById(Long id);

    void deleteByFieldId(String fieldUuid);

    void batchSort(String fieldUuid, List<MetadataEntityFieldOptionDO> optionsInOrder);

    /**
     * 获取字段选项列表
     *
     * @param fieldUuid 字段UUID
     * @return 选项列表响应VO
     */
    List<FieldOptionRespVO> getFieldOptionList(String fieldUuid);

    /**
     * 创建字段选项
     *
     * @param req 选项保存请求VO
     * @return 选项ID
     */
    Long createFieldOption(FieldOptionSaveReqVO req);

    /**
     * 更新字段选项
     *
     * @param req 选项保存请求VO
     */
    void updateFieldOption(FieldOptionSaveReqVO req);

    /**
     * 批量排序字段选项
     *
     * @param req 批量排序请求VO
     */
    void batchSortFieldOptions(FieldOptionBatchSortReqVO req);
}


