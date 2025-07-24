package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationVersionListRespVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationVersionDO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/24 11:04
 */
@Service
@Validated
public class ApplicationVersionServiceImpl implements ApplicationVersionService {

    @Resource
    private DataRepository dataRepository;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public List<ApplicationVersionListRespVO> listApplicationVersion(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.order("create_time", Order.TYPE.DESC);
        List<ApplicationVersionDO> dos = dataRepository.findAll(ApplicationVersionDO.class, configs);
        return dos.stream().map(v -> {
            ApplicationVersionListRespVO vo = BeanUtils.toBean(v, ApplicationVersionListRespVO.class);
            vo.setCreatorName(getUserName(v.getCreator()));
            return vo;
        }).toList();
    }

    @Override
    public Long createApplicationVersion(ApplicationVersionCreateReqVO applicationVersionCreateReqVO) {
        return 0L;
    }

    @Override
    public void turnOnApplicationVersion(Long applicationId, String versionNumber) {

    }

    @Override
    public void deleteApplicationVersion(Long applicationId, String versionNumber) {

    }

    private String getUserName(Long userId) {
        AdminUserRespDTO dto = adminUserApi.getUser(userId).getData();
        if (dto == null) {
            return "";
        }
        return dto.getNickname();
    }

}
