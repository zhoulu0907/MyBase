package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:07
 */
@Data
@Table(value = "app_auth_data_group")
public class AppAuthDataGroupDO extends BaseBizEntity {

    @Column(value = "role_uuid", comment = "角色id")
    private String roleUuid;

    @Column(value = "menu_id", comment = "菜单id")
    private String menuUuid;

    @Column(value = "group_name", comment = "组名称")
    private String groupName;

    @Column(value = "group_order", comment = "组排序")
    private Integer groupOrder;

    @Column(value = "description", comment = "描述")
    private String description;

    @Column(value = "scope_tags", comment = "权限标签")
    private String scopeTags;

    @Column(value = "scope_field_uuid", comment = "字段id")
    private Long scopeFieldUuid;

    @Column(value = "scope_level", comment = "字段对应的权限范围")
    private String scopeLevel;

    @Column(value = "scope_value", comment = "字段对应的权限范围值")
    private String scopeValue;

    @Column(value = "data_filter", comment = "数据过滤")
    private String dataFilter;

    @Column(value = "operation_tags", comment = "操作标签")
    private String operationTags;

}
