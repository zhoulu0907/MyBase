package com.cmsr.onebase.module.bpm.api.app;

/**
 * BpmApplicationApi
 *
 * @author liyang
 * @date 2025-12-15
 */
public interface BpmApplicationApi {
    /**
     * 检测实体关系是否存在
     * @param entityUuid 实体uuid
     * @param entityName 实体名称
     * @return
     */
    boolean existsEntityRelation(String entityUuid, String entityName);

    /**
     * 检测实体字段关系是否存在
     * @param entityUuid 实体uuid
     * @param entityName 实体名称
     * @param fieldUuid 字段uuid
     * @param fieldName 字段名称
     * @return
     */
    boolean existsEntityFieldRelation(String entityUuid, String entityName, String fieldUuid, String fieldName);
}
