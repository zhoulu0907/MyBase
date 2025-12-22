package com.cmsr.onebase.module.screen.build.service.impl;

import com.cmsr.onebase.module.screen.build.mapper.GoviewProjectDataMapper;
import com.cmsr.onebase.module.screen.build.model.GoviewProjectData;
import com.cmsr.onebase.module.screen.build.service.IGoviewProjectDataService;
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
public class GoviewProjectDataServiceImpl extends ServiceImpl<GoviewProjectDataMapper, GoviewProjectData> implements IGoviewProjectDataService {
	@Autowired
	GoviewProjectDataMapper dataMapper;
	@Override
	public GoviewProjectData getProjectid(String projectId) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq(GoviewProjectData::getProjectId,projectId);
		return dataMapper.selectOneByQuery(queryWrapper);

	}

}
