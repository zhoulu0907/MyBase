package com.cmsr.onebase.module.system.service.dict;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataBatchReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataBatchRespVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataInsertReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataPageReqVO;
import com.cmsr.onebase.module.system.vo.dictdata.DictDataUpdateReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictDataDO;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 字典数据 Service 接口
 *
 * @author ma
 */
public interface DictDataService {

    /**
     * 创建字典数据
     *
     * @param createReqVO 字典数据信息
     * @return 字典数据编号
     */
    Long createDictData(DictDataInsertReqVO createReqVO);

    /**
     * 更新字典数据
     *
     * @param updateReqVO 字典数据信息
     */
    void updateDictData(DictDataUpdateReqVO updateReqVO);

    /**
     * 更新字典数据状态
     *
     * @param id 字典数据编号
     * @param status 状态
     */
    void updateDictDataStatus(Long id, Integer status);

    /**
     * 删除字典数据
     *
     * @param id 字典数据编号
     */
    void deleteDictData(Long id);

    /**
     * 获得字典数据列表
     *
     * @param status   状态
     * @param dictType 字典类型
     * @return 字典数据全列表
     */
    List<DictDataDO> getDictDataList(@Nullable Integer status, @Nullable String dictType);

    /**
     * 获得字典数据分页列表
     *
     * @param pageReqVO 分页请求
     * @return 字典数据分页列表
     */
    PageResult<DictDataDO> getDictDataPage(DictDataPageReqVO pageReqVO);

    /**
     * 获得字典数据详情
     *
     * @param id 字典数据编号
     * @return 字典数据
     */
    DictDataDO getDictData(Long id);

    /**
     * 获得指定字典类型的数据数量
     *
     * @param dictType 字典类型
     * @return 数据数量
     */
    long getDictDataCountByDictType(String dictType);

    /**
     * 校验字典数据们是否有效。如下情况，视为无效：
     * 1. 字典数据不存在
     * 2. 字典数据被禁用
     *
     * @param dictType 字典类型
     * @param values   字典数据值的数组
     */
    void validateDictDataList(String dictType, Collection<String> values);

    /**
     * 获得指定的字典数据
     *
     * @param dictType 字典类型
     * @param value    字典数据值
     * @return 字典数据
     */
    DictDataDO getDictData(String dictType, String value);

    /**
     * 解析获得指定的字典数据，从缓存中
     *
     * @param dictType 字典类型
     * @param label    字典数据标签
     * @return 字典数据
     */
    DictDataDO parseDictData(String dictType, String label);

    /**
     * 获得指定数据类型的字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DictDataDO> getDictDataListByDictType(String dictType);

    /**
     * 批量操作字典数据（批量新增、更新、删除）
     *
     * @param batchReqVO 批量操作请求参数
     * @return 批量操作结果
     */
    DictDataBatchRespVO batchOperateDictData(DictDataBatchReqVO batchReqVO);

    /**
     * 根据多个字典类型批量获取字典数据列表（按dictType分组）
     *
     * @param dictTypes 字典类型集合
     * @return 按dictType分组的字典数据 Map，key为dictType，value为该类型对应的字典数据列表
     */
    Map<String, List<DictDataDO>> getDictDataMapByTypes(Collection<String> dictTypes);

    /**
     * 根据字典类型和字典类型ID批量获取字典数据列表（按dictType分组）
     * <p>
     * 封装了所有业务逻辑：将dictTypeIds转换为dictTypes，然后批量查询并分组返回
     *
     * @param dictTypes   字典类型集合
     * @param dictTypeIds 字典类型ID集合
     * @return 按dictType分组的字典数据 Map
     */
    Map<String, List<DictDataDO>> getDictDataMapByTypesAndTypeIds(Collection<String> dictTypes, Collection<Long> dictTypeIds);

}
