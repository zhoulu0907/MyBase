package com.cmsr.onebase.module.etl.core.dal.dataobject.metainfo;

import lombok.Data;
import org.anyline.metadata.Catalog;

@Data
public class MetaCatalog {

    private String fullyQualifiedName;

    public static MetaCatalog convert(Catalog catalog) {
        MetaCatalog metaCatalog = new MetaCatalog();
        metaCatalog.setFullyQualifiedName(catalog.getName());

        return metaCatalog;
    }
}
