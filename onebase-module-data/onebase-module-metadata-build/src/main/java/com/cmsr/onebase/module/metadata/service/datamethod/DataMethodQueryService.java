package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;

import java.util.List;

/**
 * 数据方法查询 Service 接口 - build模块专用
 *
 * @author bty418  
 * @date 2025-09-10
 */
public interface DataMethodQueryService {

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
}