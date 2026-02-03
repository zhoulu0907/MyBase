package com.cmsr.onebase.module.app.build.service.version;

import java.util.List;

import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.util.CollectionUtils;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;

/**
 * 应用版本数据导入工具类
 *
 * @author zhoumingji
 * @date 2026/01/13
 */
public class VersionDataImporter {

    private VersionDataImporter() {
    }

    /**
     * 保存BaseBizEntity列表数据
     *
     * @param list          数据列表
     * @param applicationId 应用ID
     * @param tenantId      租户ID
     * @param versionTag    版本标签
     * @param repository    数据仓库
     * @param <T>           实体类型
     */
    public static <T extends BaseBizEntity> void saveList(List<T> list, Long applicationId, Long tenantId,
            Long versionTag, BaseBizRepository<?, T> repository) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(item -> prepareEntity(item, applicationId, tenantId, versionTag));
        repository.saveBatch(list);
    }

    /**
     * 准备BaseBizEntity实体数据
     *
     * @param entity        实体对象
     * @param applicationId 应用ID
     * @param tenantId      租户ID
     * @param versionTag    版本标签
     */
    public static void prepareEntity(BaseBizEntity entity, Long applicationId, Long tenantId, Long versionTag) {
        entity.setId(null);
        entity.setApplicationId(applicationId);
        entity.setTenantId(tenantId);
        entity.setVersionTag(versionTag);
    }

    /**
     * 保存BaseAppEntity列表数据
     *
     * @param list          数据列表
     * @param applicationId 应用ID
     * @param tenantId      租户ID
     * @param repository    数据仓库
     * @param <T>           实体类型
     */
    public static <T extends BaseAppEntity> void saveAppEntityList(List<T> list, Long applicationId, Long tenantId,
            BaseAppRepository<?, T> repository) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(item -> prepareAppEntity(item, applicationId, tenantId));
        repository.saveBatch(list);
    }

    /**
     * 准备BaseAppEntity实体数据
     *
     * @param entity        实体对象
     * @param applicationId 应用ID
     * @param tenantId      租户ID
     */
    public static void prepareAppEntity(BaseAppEntity entity, Long applicationId, Long tenantId) {
        entity.setId(null);
        entity.setApplicationId(applicationId);
        entity.setTenantId(tenantId);
    }
}
