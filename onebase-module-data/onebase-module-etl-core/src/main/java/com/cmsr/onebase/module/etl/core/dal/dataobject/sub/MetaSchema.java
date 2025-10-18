package com.cmsr.onebase.module.etl.core.dal.dataobject.sub;

import lombok.Data;
import org.anyline.metadata.Schema;

@Data
public class MetaSchema {

    private String fullyQualifiedName;

    private String keyword;

    private String comment;

    public static MetaSchema convert(Schema schema) {
        MetaSchema metaSchema = new MetaSchema();
        metaSchema.setFullyQualifiedName(String.join(",",
                schema.getCatalogName(),
                schema.getName()));
        metaSchema.setKeyword(schema.keyword());
        metaSchema.setComment(schema.getComment());

        return metaSchema;
    }
}
