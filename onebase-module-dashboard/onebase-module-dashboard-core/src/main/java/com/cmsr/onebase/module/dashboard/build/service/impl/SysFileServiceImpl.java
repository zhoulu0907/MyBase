package com.cmsr.onebase.module.dashboard.build.service.impl;

import com.cmsr.onebase.module.dashboard.build.mapper.SysFileMapper;
import com.cmsr.onebase.module.dashboard.build.model.DashboardFile;
import com.cmsr.onebase.module.dashboard.build.service.ISysFileService;
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
 * @since 2022-12-22
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, DashboardFile> implements ISysFileService {

	@Autowired
	private SysFileMapper sysFileMapper;

	@Override
	public DashboardFile selectByExamplefileName(String filename) {
		DashboardFile dashboardFile =sysFileMapper.selectOneByQuery(new QueryWrapper().eq(DashboardFile::getFileName, filename));
        return dashboardFile;
	}

}
