package com.cmsr.onebase.module.infra.dal.dataobject.codegen;

import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.dataobject.file.FileConfigDO;
import com.cmsr.onebase.module.infra.enums.codegen.CodegenColumnHtmlTypeEnum;
import com.cmsr.onebase.module.infra.enums.codegen.CodegenColumnListConditionEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 代码生成 column 字段定义
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "infra_codegen_column")
@TenantIgnore
public class CodegenColumnDO extends BaseDO {

    // builder模式可正常运作
    public CodegenColumnDO setId(Long id){
        super.setId(id);
        return this;
    }
    
    // 新增各字段对应的常量
    public static final String TABLE_ID = "table_id";
    public static final String COLUMN_NAME = "column_name";
    public static final String DATA_TYPE = "data_type";
    public static final String COLUMN_COMMENT = "column_comment";
    public static final String NULLABLE = "nullable";
    public static final String PRIMARY_KEY = "primary_key";
    public static final String ORDINAL_POSITION = "ordinal_position";
    public static final String JAVA_TYPE = "java_type";
    public static final String JAVA_FIELD = "java_field";
    public static final String DICT_TYPE = "dict_type";
    public static final String EXAMPLE = "example";
    public static final String CREATE_OPERATION = "create_operation";
    public static final String UPDATE_OPERATION = "update_operation";
    public static final String LIST_OPERATION = "list_operation";
    public static final String LIST_OPERATION_CONDITION = "list_operation_condition";
    public static final String LIST_OPERATION_RESULT = "list_operation_result";
    public static final String HTML_TYPE = "html_type";

    /**
     * 表编号
     * <p>
     * 关联 {@link CodegenTableDO#getId()}
     */
    @Column(name = TABLE_ID)
    private Long tableId;

    // ========== 表相关字段 ==========

    /**
     * 字段名
     */
    @Column(name = COLUMN_NAME)
    private String columnName;
    /**
     * 数据库字段类型
     */
    @Column(name = DATA_TYPE)
    private String dataType;
    /**
     * 字段描述
     */
    @Column(name = COLUMN_COMMENT)
    private String columnComment;
    /**
     * 是否允许为空
     */
    @Column(name = NULLABLE)
    private Boolean nullable;
    /**
     * 是否主键
     */
    @Column(name = PRIMARY_KEY)
    private Boolean primaryKey;
    /**
     * 排序
     */
    @Column(name = ORDINAL_POSITION)
    private Integer ordinalPosition;

    // ========== Java 相关字段 ==========

    /**
     * Java 属性类型
     *
     * 例如说 String、Boolean 等等
     */
    @Column(name = JAVA_TYPE)
    private String javaType;
    /**
     * Java 属性名
     */
    @Column(name = JAVA_FIELD)
    private String javaField;
    /**
     * 字典类型
     * <p>
     * 关联 DictTypeDO 的 type 属性
     */
    @Column(name = DICT_TYPE)
    private String dictType;
    /**
     * 数据示例，主要用于生成 Swagger 注解的 example 字段
     */
    @Column(name = EXAMPLE)
    private String example;

    // ========== CRUD 相关字段 ==========

    /**
     * 是否为 Create 创建操作的字段
     */
    @Column(name = CREATE_OPERATION)
    private Boolean createOperation;
    /**
     * 是否为 Update 更新操作的字段
     */
    @Column(name = UPDATE_OPERATION)
    private Boolean updateOperation;
    /**
     * 是否为 List 查询操作的字段
     */
    @Column(name = LIST_OPERATION)
    private Boolean listOperation;
    /**
     * List 查询操作的条件类型
     * <p>
     * 枚举 {@link CodegenColumnListConditionEnum}
     */
    @Column(name = LIST_OPERATION_CONDITION)
    private String listOperationCondition;
    /**
     * 是否为 List 查询操作的返回字段
     */
    @Column(name = LIST_OPERATION_RESULT)
    private Boolean listOperationResult;

    // ========== UI 相关字段 ==========

    /**
     * 显示类型
     * <p>
     * 枚举 {@link CodegenColumnHtmlTypeEnum}
     */
    @Column(name = HTML_TYPE)
    private String htmlType;

}