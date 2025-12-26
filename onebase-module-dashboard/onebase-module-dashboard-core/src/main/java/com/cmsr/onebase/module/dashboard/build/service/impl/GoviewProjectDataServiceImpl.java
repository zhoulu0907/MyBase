package com.cmsr.onebase.module.dashboard.build.service.impl;

import com.cmsr.onebase.module.dashboard.build.mapper.GoviewProjectDataMapper;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.cmsr.onebase.module.dashboard.build.service.IGoviewProjectDataService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fc
 * @since 2023-04-30
 */
@Service
public class GoviewProjectDataServiceImpl extends ServiceImpl<GoviewProjectDataMapper, DashboardProjectData> implements IGoviewProjectDataService {
	@Autowired
	GoviewProjectDataMapper dataMapper;
	@Override
	public DashboardProjectData getProjectid(Long projectId) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq(DashboardProjectData::getProjectId,projectId);
		return dataMapper.selectOneByQuery(queryWrapper);

	}

}
