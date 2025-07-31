package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.module.app.controller.admin.app.vo.ApplicationTagListRespVO;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @ClassName ApplicationTagServiceImpl
 * @Description TODO
 * @Author mickey
 * @Date 2025/7/31 08:31
 */
@Setter
@Service
@Validated
public class ApplicationTagServiceImpl implements ApplicationTagService{
    @Override
    public List<ApplicationTagListRespVO> listApplicationTags(String tagName) {
        return null;
    }

    @Override
    public void createApplicationTag(String tagName) {

    }
}
