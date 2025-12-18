package com.cmsr.onebase.module.screen.build.v2.service.impl;

import com.cmsr.onebase.module.screen.build.v2.mapper.SysFileMapper;
import com.cmsr.onebase.module.screen.build.v2.model.SysFile;
import com.cmsr.onebase.module.screen.build.v2.service.ISysFileService;
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
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {
	@Autowired
	private SysFileMapper sysFileMapper;

	@Override
	public SysFile selectByExamplefileName(String filename) {
		SysFile sysFile=sysFileMapper.selectOneByQuery(new QueryWrapper().eq(SysFile::getFileName, filename));
        return sysFile;
	}

}
