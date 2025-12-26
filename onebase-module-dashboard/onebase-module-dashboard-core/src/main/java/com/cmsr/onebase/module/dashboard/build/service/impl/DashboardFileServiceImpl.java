package com.cmsr.onebase.module.dashboard.build.service.impl;

import com.cmsr.onebase.module.dashboard.build.mapper.DashboardFileMapper;
import com.cmsr.onebase.module.dashboard.build.model.DashboardFile;
import com.cmsr.onebase.module.dashboard.build.service.DashboardFileService;
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
 * @since 2022-12-22
 */
@Service
public class DashboardFileServiceImpl extends ServiceImpl<DashboardFileMapper, DashboardFile> implements DashboardFileService {

	@Autowired
	private DashboardFileMapper dashboardFileMapper;

	@Override
	public DashboardFile selectByExamplefileName(String filename) {
		DashboardFile dashboardFile = dashboardFileMapper.selectOneByQuery(new QueryWrapper().eq(DashboardFile::getFileName, filename));
        return dashboardFile;
	}

}
