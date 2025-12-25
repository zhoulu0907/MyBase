package com.cmsr.onebase.module.system.service.dict;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypeListReqVO;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypePageReqVO;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypeSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;

import java.util.Collection;
import java.util.List;

/**
 * 字典类型 Service 接口
 *
 */
public interface DictTypeService {

    /**
     * 创建字典类型
     *
     * @param createReqVO 字典类型信息
     * @return 字典类型编号
     */
    Long createDictType(DictTypeSaveReqVO createReqVO);

    /**
     * 更新字典类型
     *
     * @param updateReqVO 字典类型信息
     */
    void updateDictType(DictTypeSaveReqVO updateReqVO);

    /**
     * 删除字典类型
     *
     * @param id 字典类型编号
     */
    void deleteDictType(Long id);

    /**
     * 获得字典类型分页列表
     *
     * @param pageReqVO 分页请求
     * @return 字典类型分页列表
     */
    PageResult<DictTypeDO> getDictTypePage(DictTypePageReqVO pageReqVO);

    /**
     * 获得字典类型详情
     *
     * @param id 字典类型编号
     * @return 字典类型
     */
    DictTypeDO getDictType(Long id);

    /**
     * 获得字典类型详情
     *
     * @param type 字典类型
     * @return 字典类型详情
     */
    DictTypeDO getDictType(String type);

    /**
     * 获得全部字典类型列表
     *
     * @return 字典类型列表
     */
    List<DictTypeDO> getDictTypeList();

    /**
     * 根据条件获得字典类型列表（不分页）
     *
     * @param reqVO 查询条件
     * @return 字典类型列表
     */
    List<DictTypeDO> getDictTypeList(DictTypeListReqVO reqVO);

    /**
     * 根据 ID 集合批量获取字典类型
     *
     * @param ids ID集合
     * @return 字典类型列表
     */
    List<DictTypeDO> getDictTypesByIds(Collection<Long> ids);

}
