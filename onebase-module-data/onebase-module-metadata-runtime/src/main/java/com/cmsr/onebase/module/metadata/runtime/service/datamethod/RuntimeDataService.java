package com.cmsr.onebase.module.metadata.runtime.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.*;

/**
 * 运行时动态数据操作服务接口
 *
 * @author bty418
 * @date 2025-09-10
 */
public interface RuntimeDataService {

    /**
     * 新增单条数据
     *
     * @param reqVO 新增请求VO
     * @return 新增后的数据
     */
    DynamicDataRespVO createData(DynamicDataCreateReqVO reqVO);

    /**
     * 更新单条数据
     *
     * @param reqVO 更新请求VO
     * @return 更新后的数据
     */
    DynamicDataRespVO updateData(DynamicDataUpdateReqVO reqVO);

    /**
     * 删除单条数据
     *
     * @param reqVO 删除请求VO
     * @return 删除是否成功
     */
    Boolean deleteData(DynamicDataDeleteReqVO reqVO);

    /**
     * 根据ID查询数据详情
     *
     * @param reqVO 查询请求VO
     * @return 数据详情
     */
    DynamicDataRespVO getData(DynamicDataGetReqVO reqVO);

    /**
     * 分页查询数据列表
     *
     * @param reqVO 分页查询请求VO
     * @return 分页数据列表
     */
    PageResult<DynamicDataRespVO> getDataPage(DynamicDataPageReqVO reqVO);
}
