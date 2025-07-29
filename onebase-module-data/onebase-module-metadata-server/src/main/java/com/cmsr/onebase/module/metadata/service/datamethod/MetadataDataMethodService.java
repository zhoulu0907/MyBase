package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodRespVO;

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
     * @param entityId 实体ID
     * @param methodType 方法类型
     * @param keyword 搜索关键词
     * @return 数据方法列表
     */
    List<DataMethodRespVO> getDataMethodList(Long entityId, String methodType, String keyword);

    /**
     * 获取指定数据方法的详细信息
     *
     * @param entityId 实体ID
     * @param methodCode 方法编码
     * @return 数据方法详情
     */
    DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode);

} 