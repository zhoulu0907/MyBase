package com.cmsr.onebase.module.system.dal.dataobject.permission;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.enums.permission.MenuTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 菜单 DO
 *
 * @author ma
 */
@Data
@TenantIgnore
@Table(name = "system_menu")
public class MenuDO extends BaseDO {

    // 字段常量
    public static final String NAME           = "name";
    public static final String PERMISSION     = "permission";
    public static final String TYPE           = "type";
    public static final String SORT           = "sort";
    public static final String PARENT_ID      = "parent_id";
    public static final String PATH           = "path";
    public static final String ICON           = "icon";
    public static final String COMPONENT      = "component";
    public static final String COMPONENT_NAME = "component_name";
    public static final String STATUS         = "status";
    public static final String VISIBLE        = "visible";
    public static final String KEEP_ALIVE     = "keep_alive";
    public static final String ALWAYS_SHOW    = "always_show";

    // builder模式可正常运作
    public MenuDO setId(Long id){
        super.setId(id);
        return this;
    }
    
    /**
     * 菜单编号 - 根节点
     */
    public static final Long ID_ROOT = 0L;


    /**
     * 菜单名称
     */
    @Column(name = NAME)
    private String name;
    /**
     * 权限标识
     *
     * 一般格式为：${系统}:${模块}:${操作}
     * 例如说：system:admin:add，即 system 服务的添加管理员。
     *
     * 当我们把该 MenuDO 赋予给角色后，意味着该角色有该资源：
     * - 对于后端，配合 @PreAuthorize 注解，配置 API 接口需要该权限，从而对 API 接口进行权限控制。
     * - 对于前端，配合前端标签，配置按钮是否展示，避免用户没有该权限时，结果可以看到该操作。
     */
    @Column(name = PERMISSION)
    private String permission;
    /**
     * 菜单类型
     *
     * 枚举 {@link MenuTypeEnum}
     */
    @Column(name = TYPE)
    private Integer type;
    /**
     * 显示顺序
     */
    @Column(name = SORT)
    private Integer sort;
    /**
     * 父菜单ID
     */
    @Column(name = PARENT_ID)
    private Long parentId;
    /**
     * 路由地址
     *
     * 如果 path 为 http(s) 时，则它是外链
     */
    @Column(name = PATH)
    private String path;
    /**
     * 菜单图标
     */
    @Column(name = ICON)
    private String icon;
    /**
     * 组件路径
     */
    @Column(name = COMPONENT)
    private String component;
    /**
     * 组件名
     */
    @Column(name = COMPONENT_NAME)
    private String componentName;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;
    /**
     * 是否可见
     *
     * 只有菜单、目录使用
     * 当设置为 true 时，该菜单不会展示在侧边栏，但是路由还是存在。例如说，一些独立的编辑页面 /edit/1024 等等
     */
    @Column(name = VISIBLE)
    private Integer visible;
    /**
     * 是否缓存
     *
     * 只有菜单、目录使用，否使用 Vue 路由的 keep-alive 特性
     * 注意：如果开启缓存，则必须填写 {@link #componentName} 属性，否则无法缓存
     */
    @Column(name = KEEP_ALIVE)
    private Integer keepAlive;
    /**
     * 是否总是显示
     *
     * 如果为 false 时，当该菜单只有一个子菜单时，不展示自己，直接展示子菜单
     */
    @Column(name = ALWAYS_SHOW)
    private Integer alwaysShow;

}
