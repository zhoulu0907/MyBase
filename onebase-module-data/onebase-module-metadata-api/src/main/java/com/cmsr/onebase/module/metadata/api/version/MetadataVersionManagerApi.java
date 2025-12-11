package com.cmsr.onebase.module.metadata.api.version;

public interface MetadataVersionManagerApi {
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
