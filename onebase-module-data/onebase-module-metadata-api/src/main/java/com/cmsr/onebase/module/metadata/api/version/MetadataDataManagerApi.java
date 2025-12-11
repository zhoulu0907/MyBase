package com.cmsr.onebase.module.metadata.api.version;

public interface MetadataDataManagerApi {
    /**
     * moveMetaDataRuntimeToHistory
     *
     * @param applicationId
     * @param versionTag
     */
    void moveMetaDataRuntimeToHistory(Long applicationId, Long versionTag);

    /**
     * copyMetaDataEditToRuntime
     *
     * @param applicationId
     */
    void copyMetaDataEditToRuntime(Long applicationId);

}
