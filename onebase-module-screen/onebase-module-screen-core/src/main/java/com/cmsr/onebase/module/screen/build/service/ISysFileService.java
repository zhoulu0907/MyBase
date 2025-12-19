package com.cmsr.onebase.module.screen.build.service;

import com.cmsr.onebase.module.screen.build.model.SysFile;
import com.mybatisflex.core.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fc
 * @since 2022-12-22
 */
public interface ISysFileService extends IService<SysFile> {


	public SysFile selectByExamplefileName(String filename);
}
