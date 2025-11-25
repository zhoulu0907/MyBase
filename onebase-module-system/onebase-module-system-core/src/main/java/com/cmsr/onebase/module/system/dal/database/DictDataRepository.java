package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictDataDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 字典数据数据访问层
 *
 * 负责字典数据相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class DictDataRepository extends DataRepository<DictDataDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public DictDataRepository() {
        super(DictDataDO.class);
    }

    /**
     * 根据状态和字典类型查询字典数据列表
     *
     * @param status 状态
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    public List<DictDataDO> findListByStatusAndDictType(Integer status, String dictType) {
        DefaultConfigStore configs = new DefaultConfigStore();
        if (status != null) {
            configs.and(Compare.EQUAL, DictDataDO.STATUS, status);
        }
        if (dictType != null && !dictType.trim().isEmpty()) {
            configs.and(Compare.EQUAL, DictDataDO.DICT_TYPE, dictType);
        }
        return findAllByConfig(configs);
    }

    /**
     * 分页查询字典数据
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<DictDataDO> findPage(DictDataPageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqVO.getLabel() != null && !reqVO.getLabel().trim().isEmpty()) {
            configs.and(Compare.LIKE, DictDataDO.LABEL, reqVO.getLabel());
        }
        if (reqVO.getDictType() != null && !reqVO.getDictType().trim().isEmpty()) {
            // 使用EQUAL精确匹配，确保与其他字典数据查询接口行为一致
            configs.and(Compare.EQUAL, DictDataDO.DICT_TYPE, reqVO.getDictType());
        }
        if (reqVO.getStatus() != null) {
            configs.and(Compare.EQUAL, DictDataDO.STATUS, reqVO.getStatus());
        }

        // 添加排序条件，按sort升序排列
        configs.order(DictDataDO.SORT, org.anyline.entity.Order.TYPE.ASC);

        return findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }

    /**
     * 根据字典类型和值查询字典数据
     *
     * @param dictType 字典类型
     * @param value 字典值
     * @return 字典数据对象
     */
    public DictDataDO findOneByDictTypeAndValue(String dictType, String value) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, DictDataDO.DICT_TYPE, dictType);
        configs.and(Compare.EQUAL, DictDataDO.VALUE, value);
        return findOne(configs);
    }

    /**
     * 根据字典类型和标签查询字典数据
     *
     * @param dictType 字典类型
     * @param label 字典标签
     * @return 字典数据对象
     */
    public DictDataDO findOneByDictTypeAndLabel(String dictType, String label) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, DictDataDO.DICT_TYPE, dictType);
        configs.and(Compare.EQUAL, DictDataDO.LABEL, label);
        return findOne(configs);
    }

    /**
     * 根据字典类型查询字典数据数量
     *
     * @param dictType 字典类型
     * @return 数据数量
     */
    public long countByDictType(String dictType) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, DictDataDO.DICT_TYPE, dictType);
        return countByConfig(configs);
    }

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    public List<DictDataDO> findListByDictType(String dictType) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, DictDataDO.DICT_TYPE, dictType);
        configs.order(DictDataDO.SORT, org.anyline.entity.Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

    /**
     * 根据字典类型和值集合查询字典数据列表
     *
     * @param dictType 字典类型
     * @param values 值集合
     * @return 字典数据列表
     */
    public List<DictDataDO> findListByDictTypeAndValues(String dictType, Collection<String> values) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, DictDataDO.DICT_TYPE, dictType);
        configs.and(Compare.IN, DictDataDO.VALUE, values);
        return findAllByConfig(configs);
    }
}
