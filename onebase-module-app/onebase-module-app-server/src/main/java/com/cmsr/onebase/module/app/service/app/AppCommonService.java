package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.app.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/7/24 12:42
 */
@Service
public class AppCommonService {

    @Resource
    private AppApplicationRepository applicationRepository;

    @Resource
    private AdminUserApi adminUserApi;

    public ApplicationDO validateApplicationExist(Long id) {
        ApplicationDO applicationDO = applicationRepository.findById(id);
        if (applicationDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_NOT_EXIST);
        }
        return applicationDO;
    }

    public UserHelper getUserHelper(List<? extends BaseDO> baseDOS) {
        Set<Long> ids1 = baseDOS.stream().map(BaseDO::getCreator).collect(Collectors.toSet());
        Set<Long> ids2 = baseDOS.stream().map(BaseDO::getUpdater).collect(Collectors.toSet());
        Set<Long> ids = SetUtils.union(ids1, ids2);
        CommonResult<List<AdminUserRespDTO>> dtos = adminUserApi.getUserList(ids);
        Map<Long, AdminUserRespDTO> dtoMap = dtos.getData().stream().collect(Collectors.toMap(AdminUserRespDTO::getId, v -> v));
        return new UserHelper(dtoMap);
    }


    public static class UserHelper {

        private Map<Long, AdminUserRespDTO> userMap;

        public UserHelper(Map<Long, AdminUserRespDTO> userMap) {
            this.userMap = userMap;
        }

        public String getUserName(Long userId) {
            AdminUserRespDTO adminUserRespDTO = userMap.get(userId);
            if (adminUserRespDTO == null) {
                return "";
            }
            return adminUserRespDTO.getNickname();
        }
    }
}
