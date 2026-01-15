package com.cmsr.onebase.module.dashboard.build.service;

import com.cmsr.onebase.module.dashboard.build.model.DashboardFile;
import com.mybatisflex.core.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mty
 * @since 2022-12-22
 */
public interface DashboardFileService extends IService<DashboardFile> {


	public DashboardFile selectByExamplefileName(String filename);
}
