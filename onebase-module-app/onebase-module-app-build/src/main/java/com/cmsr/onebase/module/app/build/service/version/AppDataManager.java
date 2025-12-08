package com.cmsr.onebase.module.app.build.service.version;

import com.cmsr.onebase.module.app.core.dal.database.auth.*;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppDataManager {

    @Autowired
    private AppWorkbenchComponentRepository workbenchComponentRepository;

    @Autowired
    private AppComponentRepository resourceComponentRepository;

    @Autowired
    private AppWorkbenchPageRepository workbenchPageRepository;

    @Autowired
    private AppPageRepository resourcePageRepository;

    @Autowired
    private AppPageSetRepository resourcePageSetRepository;

    @Autowired
    private AppMenuRepository menuRepository;

    @Autowired
    private AppAuthViewRepository authViewRepository;

    @Autowired
    private AppAuthFieldRepository authFieldRepository;

    @Autowired
    private AppAuthDataGroupRepository authDataGroupRepository;

    @Autowired
    private AppAuthPermissionRepository authPermissionRepository;

    @Autowired
    private AppAuthRoleRepository authRoleRepository;


    public void moveRuntimeToHistory(Long applicationId, Long versionTag) {
        workbenchComponentRepository.moveRuntimeToHistory(applicationId, versionTag);
        resourceComponentRepository.moveRuntimeToHistory(applicationId, versionTag);
        workbenchPageRepository.moveRuntimeToHistory(applicationId, versionTag);
        resourcePageRepository.moveRuntimeToHistory(applicationId, versionTag);
        resourcePageSetRepository.moveRuntimeToHistory(applicationId, versionTag);
        menuRepository.moveRuntimeToHistory(applicationId, versionTag);
        authViewRepository.moveRuntimeToHistory(applicationId, versionTag);
        authFieldRepository.moveRuntimeToHistory(applicationId, versionTag);
        authDataGroupRepository.moveRuntimeToHistory(applicationId, versionTag);
        authPermissionRepository.moveRuntimeToHistory(applicationId, versionTag);
        authRoleRepository.moveRuntimeToHistory(applicationId, versionTag);
    }


    public void copyEditToRuntime(Long applicationId) {
        workbenchComponentRepository.copyEditToRuntime(applicationId);
        resourceComponentRepository.copyEditToRuntime(applicationId);
        workbenchPageRepository.copyEditToRuntime(applicationId);
        resourcePageRepository.copyEditToRuntime(applicationId);
        resourcePageSetRepository.copyEditToRuntime(applicationId);
        menuRepository.copyEditToRuntime(applicationId);
        authViewRepository.copyEditToRuntime(applicationId);
        authFieldRepository.copyEditToRuntime(applicationId);
        authDataGroupRepository.copyEditToRuntime(applicationId);
        authPermissionRepository.copyEditToRuntime(applicationId);
        authRoleRepository.copyEditToRuntime(applicationId);
    }

    // 3、历史版本数据回滚为运行态数据
    public void copyHistoryToRuntime(Long applicationId, Long versionTag) {
        // 实现回滚逻辑
        // 执行select、insert 动作。
        // 1、select：查询versionTag为参数`versionTag`值的数据
        // 2、insert：插入第一步查询出来的数据，versionTag为1
    }

    // 4、历史版本数据回滚为编辑态数据
    public void historyToEdit(Long applicationId, Long versionTag) {
        // 实现回滚到编辑态逻辑，没想好！
    }
}
