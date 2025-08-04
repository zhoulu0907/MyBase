package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.*;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;

import java.util.List;

/**
 * 数据方法 Service 接口
 *
 * @author bty418
 * @date 2025-01-25
 */
public interface MetadataDataMethodService {

    /**
     * 查询业务实体的数据方法列表
     *
     * @param queryVO 查询条件VO
     * @return 数据方法列表
     */
    List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO);

    /**
     * 获取指定数据方法的详细信息
     *
     * @param entityId 实体ID
     * @param methodCode 方法编码
     * @return 数据方法详情
     */
    DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode);

    // ========== 系统级别的动态数据操作方法 ==========

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