package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictDataDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemDictDataMapper;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 字典数据数据访问层
 *
 * 负责字典数据相关的数据操作，基于 MyBatis-Flex 实现。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class DictDataRepository extends BaseDataRepository<SystemDictDataMapper, DictDataDO> {

    /**
     * 根据状态和字典类型查询字典数据列表
     *
     * @param status   状态
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    public List<DictDataDO> findListByStatusAndDictType(Integer status, String dictType) {
        QueryWrapper queryWrapper = query()
                .eq(DictDataDO.STATUS, status, status != null)
                .eq(DictDataDO.DICT_TYPE, dictType, dictType != null && !dictType.trim().isEmpty())
                .orderBy(DictDataDO.SORT, true);
        return list(queryWrapper);
    }

    /**
     * 分页查询字典数据
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<DictDataDO> findPage(DictDataPageReqVO reqVO) {
        QueryWrapper queryWrapper = query()
                .like(DictDataDO.LABEL, reqVO.getLabel(), reqVO.getLabel() != null && !reqVO.getLabel().trim().isEmpty())
                .eq(DictDataDO.DICT_TYPE, reqVO.getDictType(), reqVO.getDictType() != null && !reqVO.getDictType().trim().isEmpty())
                .eq(DictDataDO.STATUS, reqVO.getStatus(), reqVO.getStatus() != null)
                .orderBy(DictDataDO.SORT, true);

        Page<DictDataDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 根据字典类型和值查询字典数据
     *
     * @param dictType 字典类型
     * @param value    字典值
     * @return 字典数据对象
     */
    public DictDataDO findOneByDictTypeAndValue(String dictType, String value) {
        return getOne(query()
                .eq(DictDataDO.DICT_TYPE, dictType)
                .eq(DictDataDO.VALUE, value));
    }

    /**
     * 根据字典类型和标签查询字典数据
     *
     * @param dictType 字典类型
     * @param label    字典标签
     * @return 字典数据对象
     */
    public DictDataDO findOneByDictTypeAndLabel(String dictType, String label) {
        return getOne(query()
                .eq(DictDataDO.DICT_TYPE, dictType)
                .eq(DictDataDO.LABEL, label));
    }

    /**
     * 根据字典类型查询字典数据数量
     *
     * @param dictType 字典类型
     * @return 数据数量
     */
    public long countByDictType(String dictType) {
        return count(query().eq(DictDataDO.DICT_TYPE, dictType));
    }

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    public List<DictDataDO> findListByDictType(String dictType) {
        return list(query()
                .eq(DictDataDO.DICT_TYPE, dictType)
                .orderBy(DictDataDO.SORT, true));
    }

    /**
     * 根据字典类型和值集合查询字典数据列表
     *
     * @param dictType 字典类型
     * @param values   值集合
     * @return 字典数据列表
     */
    public List<DictDataDO> findListByDictTypeAndValues(String dictType, Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query()
                .eq(DictDataDO.DICT_TYPE, dictType)
                .in(DictDataDO.VALUE, values));
    }
}
