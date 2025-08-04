package com.cmsr.onebase.module.infra.dal.dataobject.codegen;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.dataobject.db.DataSourceConfigDO;
import com.cmsr.onebase.module.infra.enums.codegen.CodegenFrontTypeEnum;
import com.cmsr.onebase.module.infra.enums.codegen.CodegenSceneEnum;
import com.cmsr.onebase.module.infra.enums.codegen.CodegenTemplateTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 代码生成 table 表定义
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "infra_codegen_table")
@TenantIgnore
public class CodegenTableDO extends BaseDO {

    // 新增各字段对应的常量
    public static final String DATA_SOURCE_CONFIG_ID = "data_source_config_id";
    public static final String SCENE = "scene";
    public static final String TABLE_NAME = "table_name";
    public static final String TABLE_COMMENT = "table_comment";
    public static final String REMARK = "remark";
    public static final String MODULE_NAME = "module_name";
    public static final String BUSINESS_NAME = "business_name";
    public static final String CLASS_NAME = "class_name";
    public static final String CLASS_COMMENT = "class_comment";
    public static final String AUTHOR = "author";
    public static final String TEMPLATE_TYPE = "template_type";
    public static final String FRONT_TYPE = "front_type";
    public static final String PARENT_MENU_ID = "parent_menu_id";
    public static final String MASTER_TABLE_ID = "master_table_id";
    public static final String SUB_JOIN_COLUMN_ID = "sub_join_column_id";
    public static final String SUB_JOIN_MANY = "sub_join_many";
    public static final String TREE_PARENT_COLUMN_ID = "tree_parent_column_id";
    public static final String TREE_NAME_COLUMN_ID = "tree_name_column_id";

    /**
     * 数据源编号
     *
     * 关联 {@link DataSourceConfigDO#getId()}
     */
    @Column(name = DATA_SOURCE_CONFIG_ID)
    private Long dataSourceConfigId;
    /**
     * 生成场景
     *
     * 枚举 {@link CodegenSceneEnum}
     */
    @Column(name = SCENE)
    private Integer scene;

    // ========== 表相关字段 ==========

    /**
     * 表名称
     *
     * 关联 {@link TableInfo#getName()}
     */
    @Column(name = TABLE_NAME)
    private String tableName;
    /**
     * 表描述
     *
     * 关联 {@link TableInfo#getComment()}
     */
    @Column(name = TABLE_COMMENT)
    private String tableComment;
    /**
     * 备注
     */
    @Column(name = REMARK)
    private String remark;

    // ========== 类相关字段 ==========

    /**
     * 模块名，即一级目录
     *
     * 例如说，system、infra、tool 等等
     */
    @Column(name = MODULE_NAME)
    private String moduleName;
    /**
     * 业务名，即二级目录
     *
     * 例如说，user、permission、dict 等等
     */
    @Column(name = BUSINESS_NAME)
    private String businessName;
    /**
     * 类名称（首字母大写）
     *
     * 例如说，SysUser、SysMenu、SysDictData 等等
     */
    @Column(name = CLASS_NAME)
    private String className;
    /**
     * 类描述
     */
    @Column(name = CLASS_COMMENT)
    private String classComment;
    /**
     * 作者
     */
    @Column(name = AUTHOR)
    private String author;

    // ========== 生成相关字段 ==========

    /**
     * 模板类型
     *
     * 枚举 {@link CodegenTemplateTypeEnum}
     */
    @Column(name = TEMPLATE_TYPE)
    private Integer templateType;
    /**
     * 代码生成的前端类型
     *
     * 枚举 {@link CodegenFrontTypeEnum}
     */
    @Column(name = FRONT_TYPE)
    private Integer frontType;

    // ========== 菜单相关字段 ==========

    /**
     * 父菜单编号
     *
     * 关联 MenuDO 的 id 属性
     */
    @Column(name = PARENT_MENU_ID)
    private Long parentMenuId;

    // ========== 主子表相关字段 ==========

    /**
     * 主表的编号
     *
     * 关联 {@link CodegenTableDO#getId()}
     */
    @Column(name = MASTER_TABLE_ID)
    private Long masterTableId;
    /**
     * 【自己】子表关联主表的字段编号
     *
     * 关联 {@link CodegenColumnDO#getId()}
     */
    @Column(name = SUB_JOIN_COLUMN_ID)
    private Long subJoinColumnId;
    /**
     * 主表与子表是否一对多
     *
     * true：一对多
     * false：一对一
     */
    @Column(name = SUB_JOIN_MANY)
    private Boolean subJoinMany;

    // ========== 树表相关字段 ==========

    /**
     * 树表的父字段编号
     *
     * 关联 {@link CodegenColumnDO#getId()}
     */
    @Column(name = TREE_PARENT_COLUMN_ID)
    private Long treeParentColumnId;
    /**
     * 树表的名字字段编号
     *
     * 名字的用途：新增或修改时，select 框展示的字段
     *
     * 关联 {@link CodegenColumnDO#getId()}
     */
    @Column(name = TREE_NAME_COLUMN_ID)
    private Long treeNameColumnId;

}