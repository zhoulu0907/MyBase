package com.cmsr.onebase.framework.security.runtime;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/18 14:54
 */
@Data
public class DataPermission {

    private List<DataPermissionScopeTagEnum> scopTags;

    private Long scopeFieldId;

    private DataPermissionScopeEnum scopeLevel;

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
     * 数据过滤条件
     */
    private List<List<DataPermissionFilter>> filters;


    /**
     * 操作标签 只有 编辑和删除两类值
     */
    private List<OperationEnum> operationTags;


}
