package com.cmsr.v2.service;

import com.cmsr.v2.model.SysFile;
import com.baomidou.mybatisplus.extension.service.IService;

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
