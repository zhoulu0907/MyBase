package com.cmsr.onebase.module.metadata.build.service.datamethod;

import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.build.service.datamethod.vo.DataMethodQueryVO;

import java.util.List;

/**
 * 构建端 - 数据方法查询服务
 */
public interface MetadataDataMethodQueryBuildService {
    List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO);
    DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode);
}
