package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;

import java.util.List;

/**
 * 构建端 - 数据方法查询服务
 */
public interface MetadataDataMethodQueryService {
    List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO);
    DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode);
}
