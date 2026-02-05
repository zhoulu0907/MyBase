package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourceComponentMapper;
import com.cmsr.onebase.module.app.core.enums.resource.PageEnum;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourceComponentTableDef.APP_RESOURCE_COMPONENT;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePageTableDef.APP_RESOURCE_PAGE;

@Repository
public class AppComponentRepository extends BaseBizRepository<AppResourceComponentMapper, AppResourceComponentDO> {

    public void deleteComponentByPageUuid(Long applicationId, String pageUuid) {
        QueryWrapper queryWrapper = query()
                .where(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_COMPONENT.PAGE_UUID.eq(pageUuid));
        this.remove(queryWrapper);
    }

    public List<AppResourceComponentDO> findByAppIdAndPageUuid(Long applicationId, String pageUuid) {
        QueryWrapper queryWrapper = query()
                .where(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_COMPONENT.PAGE_UUID.eq(pageUuid))
                .orderBy(APP_RESOURCE_COMPONENT.COMPONENT_INDEX, true);
        return this.list(queryWrapper);
    }

    public List<AppResourceComponentDO> findByAppIdAndPageUuids(Long applicationId, List<String> pageUuids) {
        QueryWrapper queryWrapper = query()
                .where(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_COMPONENT.PAGE_UUID.in(pageUuids));
        return this.list(queryWrapper);
    }

    /**
     * 仅查询属于列表页（page_type = list）的组件：通过 JOIN 限定组件所属页面为列表页
     *
     * @param applicationId 应用ID
     * @return 仅列表页下的组件列表，按 component_index 排序
     */
    public List<AppResourceComponentDO> findByAppIdOnlyListPageComponents(Long applicationId) {
        QueryWrapper queryWrapper = query()
                .select(APP_RESOURCE_COMPONENT.ALL_COLUMNS)
                .from(APP_RESOURCE_COMPONENT)
                .innerJoin(APP_RESOURCE_PAGE)
                .on(APP_RESOURCE_COMPONENT.PAGE_UUID.eq(APP_RESOURCE_PAGE.PAGE_UUID)
                        .and(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(APP_RESOURCE_PAGE.APPLICATION_ID))
                        .and(APP_RESOURCE_COMPONENT.VERSION_TAG.eq(APP_RESOURCE_PAGE.VERSION_TAG))
                        .and(APP_RESOURCE_COMPONENT.VERSION_TAG.eq(ApplicationManager.getVersionTag()))
                )
                        .and(APP_RESOURCE_COMPONENT.COMPONENT_TYPE.eq("XTable"))
                .where(APP_RESOURCE_COMPONENT.APPLICATION_ID.eq(applicationId))
                .where(APP_RESOURCE_PAGE.PAGE_TYPE.eq(PageEnum.LIST.getValue()))
                .orderBy(APP_RESOURCE_COMPONENT.COMPONENT_INDEX, true);
        return this.list(queryWrapper);
    }

    public boolean existsEntityRefferedByTable(String entityName) {
        if (StringUtils.isBlank(entityName)) {
            throw new IllegalArgumentException("实体名称不能为空");
        }
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_COMPONENT.COMPONENT_TYPE.eq("XTable"))
                .where(APP_RESOURCE_COMPONENT.CONFIG.like("\"tableName\":\"" + entityName + "\""));
        return this.exists(queryWrapper);
    }

    public boolean existsEntityRefferedBySubTable(String entityUuid) {
        if (StringUtils.isBlank(entityUuid)) {
            throw new IllegalArgumentException("实体UUID不能为空");
        }
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_COMPONENT.COMPONENT_TYPE.eq("XSubTable"))
                .where(APP_RESOURCE_COMPONENT.CONFIG.like("\"subTable\":\"" + entityUuid + "\""));
        return this.exists(queryWrapper);
    }

    public boolean existsFieldRefferedByTable(String entityName, String fieldName) {
        if (StringUtils.isBlank(entityName)) {
            throw new IllegalArgumentException("实体名称不能为空");
        }
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_COMPONENT.COMPONENT_TYPE.eq("XTable"))
                .where(APP_RESOURCE_COMPONENT.CONFIG.like("\"tableName\":\"" + entityName + "\""))
                .where(APP_RESOURCE_COMPONENT.CONFIG.like("\"dataIndex\":\"" + fieldName + "\""));
        return this.exists(queryWrapper);
    }

    public boolean existsFieldRefferedByComponent(String entityName, String fieldName) {
        if (StringUtils.isBlank(entityName)) {
            throw new IllegalArgumentException("实体名称不能为空");
        }
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("字段名称不能为空");
        }
        QueryWrapper queryWrapper = this.query()
                .where(APP_RESOURCE_COMPONENT.COMPONENT_TYPE.ne("XTable"))
                .where(APP_RESOURCE_COMPONENT.COMPONENT_TYPE.ne("XSubTable"))
                .where(APP_RESOURCE_COMPONENT.CONFIG.like("\"dataField\":[\"" + entityName + "\",\"" + fieldName + "\"]"));
        return this.exists(queryWrapper);
    }
}
