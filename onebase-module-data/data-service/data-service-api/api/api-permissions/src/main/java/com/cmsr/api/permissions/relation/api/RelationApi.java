package com.cmsr.api.permissions.relation.api;

import com.cmsr.exception.DEException;

/**
 * @Author Junjun
 */
public interface RelationApi {
    Long getDsResource(Long id);

    Long getDatasetResource(Long id);

    void checkAuth() throws DEException;
}
