package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author：huangjie
 * @Date：2025/7/24 12:42
 */
@Service
public class AppCommonService {

    @Resource
    private DataRepository dataRepository;

    @Resource
    private AdminUserApi adminUserApi;

    public ApplicationDO validateApplicationExist(Long id) {
        ApplicationDO applicationDO = dataRepository.findById(ApplicationDO.class, id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        return applicationDO;
    }

    public String getUserName(Long userId) {
        AdminUserRespDTO dto = adminUserApi.getUser(userId).getData();
        if (dto == null) {
            return "";
        }
        return dto.getNickname();
    }

}
