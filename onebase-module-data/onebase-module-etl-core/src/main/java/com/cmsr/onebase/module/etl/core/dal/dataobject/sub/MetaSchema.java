package com.cmsr.onebase.module.etl.core.dal.dataobject.sub;

import lombok.Data;
import org.anyline.metadata.Schema;

@Data
public class MetaSchema {

    private String fullyQualifiedName;

    public static MetaSchema convert(Schema schema) {
        MetaSchema metaSchema = new MetaSchema();
        metaSchema.setFullyQualifiedName(schema.getName());

        return metaSchema;
    }
}
