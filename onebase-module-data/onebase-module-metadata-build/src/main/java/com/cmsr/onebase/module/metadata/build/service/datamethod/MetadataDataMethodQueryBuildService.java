package com.cmsr.onebase.module.metadata.build.service.datamethod;

import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.build.service.datamethod.vo.DataMethodQueryVO;

import java.util.List;

/**
 * 构建端 - 数据方法查询服务
 *
 * @author matianyu
 * @date 2025-09-10
 */
public interface MetadataDataMethodQueryBuildService {

    /**
     * 获取数据方法列表
     *
     * @param queryVO 查询条件
     * @return 数据方法列表
     */
    List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO);

    /**
     * 获取数据方法详情
     *
     * @param entityUuid 实体UUID
     * @param methodCode 方法代码
     * @return 数据方法详情
     */
    DataMethodDetailRespVO getDataMethodDetail(String entityUuid, String methodCode);
}
