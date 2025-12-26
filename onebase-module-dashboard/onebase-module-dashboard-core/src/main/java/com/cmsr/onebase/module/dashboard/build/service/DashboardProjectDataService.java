package com.cmsr.onebase.module.dashboard.build.service;

import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.mybatisflex.core.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mty
 * @since 2023-04-30
 */
public interface DashboardProjectDataService extends IService<DashboardProjectData> {

	public DashboardProjectData getProjectid(Long projectId);

}
