package com.cmsr.onebase.module.etl.core.dal.dataobject.sub;

import lombok.Data;
import org.anyline.metadata.Catalog;

@Data
public class MetaCatalog {

    private String fullyQualifiedName;

    private String keyword;

    private String comment;

    public static MetaCatalog convert(Catalog catalog) {
        MetaCatalog metaCatalog = new MetaCatalog();
        metaCatalog.setFullyQualifiedName(catalog.getName());
        metaCatalog.setKeyword(catalog.keyword());
        metaCatalog.setComment(catalog.getComment());

        return metaCatalog;
    }
}
