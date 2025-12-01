package com.cmsr.onebase.module.metadata.runtime.service.field;

import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionBatchSortReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 字段选项 Runtime Service 接口
 *
 * @author bty418
 * @date 2025-10-30
 */
public interface MetadataEntityFieldOptionRuntimeService {

    /**
     * 根据字段ID获取选项列表
     *
     * @param fieldId 字段ID
     * @return 选项DO列表
     */
    List<MetadataEntityFieldOptionDO> listByFieldId(Long fieldId);

    Map<Long, List<MetadataEntityFieldOptionDO>> listByFieldIds(Collection<Long> fieldIds);

    /**
     * 创建字段选项
     *
     * @param option 选项DO对象
     * @return 选项ID
     */
    Long create(MetadataEntityFieldOptionDO option);

    /**
     * 更新字段选项
     *
     * @param option 选项DO对象
     */
    void update(MetadataEntityFieldOptionDO option);

    /**
     * 根据ID删除选项
     *
     * @param id 选项ID
     */
    void deleteById(Long id);

    /**
     * 根据字段ID删除所有选项
     *
     * @param fieldId 字段ID
     */
    void deleteByFieldId(Long fieldId);

    /**
     * 批量排序字段选项
     *
     * @param fieldId 字段ID
     * @param optionsInOrder 按顺序排列的选项列表
     */
    void batchSort(Long fieldId, List<MetadataEntityFieldOptionDO> optionsInOrder);

    /**
     * 获取字段选项列表
     *
     * @param fieldId 字段ID
     * @return 选项列表响应VO
     */
    List<FieldOptionRespVO> getFieldOptionList(Long fieldId);

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
