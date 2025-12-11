package com.cmsr.onebase.module.app.api.security.bo;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/18 14:54
 */
@Data
public class DataPermissionGroup {

    /**
     * 数据权限范围标签，界面配置可多选，当选择  "自定义条件" 的时候，需要使用  scopeFieldId 和 scopeLevel 和 scopeValue 做条件
     */
    private List<DataPermissionTag> scopTags;

    /**
     * 数据权限范围字段，是 拥有着 创建者 更新者 等字段
     */
    private String scopeFieldUuid;

    /**
     * 数据权限范围级别，与 scopeFieldId 配合，比如指定 拥有着 是 当前员工所在主部门
     * 当 scopeLevel 等于  指定部门 或者  指定人员 时，需要使用 scopeValue 做条件
     */
    private DataPermissionLevel scopeLevel;

    /**
     * 权限范围值，当时scopeLevel等于 指定部门 或者 指定人员时 有代码的值
     * TODO department 是什么意思？
     * [
     *     {
     *         "key": "545473992555892736",
     *         "name": "科创中心",
     *         "department": "未分配部门"
     *     },
     *     {
     *         "key": "114",
     *         "name": "测试一级",
     *         "department": "未分配部门"
     *     },
     *     {
     *         "key": "100",
     *         "name": "一级部门",
     *         "department": "未分配部门"
     *     }
     * ]
     * [
     *     {
     *         "key": "66272565183643648",
     *         "name": "黎树豪",
     *         "department": "未分配部门"
     *     },
     *     {
     *         "key": "92907220776845312",
     *         "name": "新柳",
     *         "department": "未分配部门"
     *     },
     *     {
     *         "key": "545442621363982336",
     *         "name": "卞天宇",
     *         "department": "未分配部门"
     *     }
     */
    private String scopeValue;

    /**
     * 数据过滤条件，界面上数据过滤配置的条件
     * 内部List是and关系，List中的List是or关系
     */
    private List<List<DataPermissionFilter>> filters;


    /**
     *
     */
    private boolean canEdit;

    private boolean canDelete;

    //TODO 为了兼容暂时的
    @Deprecated
    public Long getScopeFieldId() {
        return scopeFieldUuid == null ? null : Long.parseLong(scopeFieldUuid);
    }

}
