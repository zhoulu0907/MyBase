package com.cmsr.onebase.module.dashboard.build.service.impl;

import com.cmsr.onebase.module.dashboard.build.mapper.DashboardProjectDataMapper;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.cmsr.onebase.module.dashboard.build.service.DashboardProjectDataService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mty
 * @since 2023-04-30
 */
@Service
public class DashboardProjectDataServiceImpl extends ServiceImpl<DashboardProjectDataMapper, DashboardProjectData> implements DashboardProjectDataService {

	@Autowired
	DashboardProjectDataMapper dataMapper;

	@Override
	public DashboardProjectData getProjectid(Long projectId) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq(DashboardProjectData::getProjectId,projectId);
		return dataMapper.selectOneByQuery(queryWrapper);

	}

}
