package com.cmsr.system.dao.ext.mapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmsr.system.dao.auto.entity.CoreSysSetting;
import com.cmsr.system.dao.auto.mapper.CoreSysSettingMapper;
import org.springframework.stereotype.Component;

@Component("extCoreSysSettingMapper")
public class ExtCoreSysSettingMapper extends ServiceImpl<CoreSysSettingMapper, CoreSysSetting> {
}
