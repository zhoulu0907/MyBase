package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo.DataMethodRespVO;
import com.cmsr.onebase.module.metadata.service.datamethod.vo.DataMethodQueryVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetadataDataMethodQueryServiceImpl implements MetadataDataMethodQueryService {

    @Resource
    private MetadataDataMethodService delegate;

    @Override
    public List<DataMethodRespVO> getDataMethodList(DataMethodQueryVO queryVO) {
        return delegate.getDataMethodList(queryVO);
    }

    @Override
    public DataMethodDetailRespVO getDataMethodDetail(Long entityId, String methodCode) {
        return delegate.getDataMethodDetail(entityId, methodCode);
    }
}
