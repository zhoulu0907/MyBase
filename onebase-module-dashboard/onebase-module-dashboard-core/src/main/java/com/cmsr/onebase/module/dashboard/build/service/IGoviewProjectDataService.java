package com.cmsr.onebase.module.dashboard.build.service;

import com.cmsr.onebase.module.dashboard.build.model.GoviewProjectData;
import com.mybatisflex.core.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fc
 * @since 2023-04-30
 */
public interface IGoviewProjectDataService extends IService<GoviewProjectData> {

	public GoviewProjectData getProjectid(Long projectId);

}
