package com.cmsr.onebase.module.etl.core.dal.dataobject.metainfo;

import lombok.Data;
import org.anyline.metadata.Column;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Data
public class MetaColumn {
    private String id;

    private String fullyQualifiedName;

    private String keyword;

    private String comment;

    /**
     * 展示字段
     */
    private String displayName;

    /**
     * 用户自定义描述
     */
    private String declaration;

    /**
     * 字段名
     */
    private String name;

    /**
     * 原始数据库中存储的类型（不一定准确，Anyline有替换）
     */
    private String originType;

    /**
`    * Flink类型
     */
    private String flinkType;

    /**
     * 列字段的排序
     */
    private Integer position;

    /**
     * 是否允许字段为空
     */
    private Boolean nullable;

    /**
     * 是否忽略字段的长度
     */
    private Integer ignoreLength;

    private Integer length;

    /**
     * 是否忽略字段的精度
     */
    private Integer ignorePrecision;

    private Integer precision;

    /**
     * 是否忽略字段的范围
     */
    private Integer ignoreScale;

    private Integer scale;

    /**
     * 是否为主键
     */
    private Boolean primary;

    /**
     * 是否为唯一键
     */
    private Boolean unique;

    /**
     * 默认值
     */
    private Object defaultValue;

    public static MetaColumn convert(Column column) {
        MetaColumn metaColumn = new MetaColumn();
        String columnName = column.getName();
        metaColumn.setName(columnName);
        metaColumn.setFullyQualifiedName(String.join(".",
                column.getCatalogName(),
                column.getSchemaName(),
                column.getTableName(),
                columnName));
        UUID nameBaseUUID = UUID.nameUUIDFromBytes(metaColumn.getFullyQualifiedName().getBytes(StandardCharsets.UTF_8));
        metaColumn.setId(nameBaseUUID.toString());
        metaColumn.setKeyword(column.keyword().toLowerCase());
        String comment = column.getComment();
        metaColumn.setComment(comment);
        if (StringUtils.isNotBlank(comment)) {
            metaColumn.setDisplayName(comment);
        } else {
            metaColumn.setDisplayName(columnName);
        }
        metaColumn.setOriginType(column.getOriginType().toLowerCase());
        metaColumn.setPosition(column.getPosition());
        metaColumn.setNullable(column.getNullable());
        metaColumn.setIgnoreLength(column.ignoreLength());
        metaColumn.setLength(column.getLength());
        metaColumn.setIgnorePrecision(column.ignorePrecision());
        metaColumn.setPrecision(column.getPrecision());
        metaColumn.setIgnoreScale(column.ignoreScale());
        metaColumn.setScale(column.getScale());
        metaColumn.setPrimary(column.isPrimaryKey());
        metaColumn.setUnique(column.isUnique());
        metaColumn.setDefaultValue(column.getDefaultValue());

        return metaColumn;
    }

    public static void applyChanges(MetaColumn oldMeta, MetaColumn newMeta) {
        String oldDisplayName = oldMeta.getDisplayName();
        String oldName = oldMeta.getName();
        String oldComment = oldMeta.getComment();
        String oldDeclaration = oldMeta.getDeclaration();
        if (!StringUtils.equals(oldDisplayName, oldName)) {
            newMeta.setDisplayName(oldDisplayName);
        }
        if (!StringUtils.equals(oldComment, oldDeclaration)) {
            newMeta.setDeclaration(oldDeclaration);
        }
        // 保持老ID
        newMeta.setId(oldMeta.getId());
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof MetaColumn oColumn) {
            // basic compare
            if (StringUtils.equals(this.fullyQualifiedName, oColumn.fullyQualifiedName) &&
                    StringUtils.equals(this.comment, oColumn.comment) &&
                    StringUtils.equals(this.name, oColumn.name) &&
                    StringUtils.equals(this.originType, oColumn.originType) &&
                    StringUtils.equals(this.flinkType, oColumn.flinkType) &&
                    this.position == oColumn.position &&
                    this.nullable == oColumn.nullable &&
                    this.primary == oColumn.primary &&
                    this.unique == oColumn.unique
            ) {
                return false;
            }
            if (this.ignoreLength != oColumn.ignoreLength) {
                return false;
            }
            if (this.ignoreLength == 0 && this.length != oColumn.length) {
                return false;
            }
            if (this.ignorePrecision != oColumn.ignorePrecision) {
                return false;
            }
            if (this.ignorePrecision == 0 && this.precision != oColumn.precision) {
                return false;
            }
            if (this.ignoreScale != oColumn.ignoreScale) {
                return false;
            }
            if (this.ignoreScale == 0 && this.scale != oColumn.scale) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }
}
