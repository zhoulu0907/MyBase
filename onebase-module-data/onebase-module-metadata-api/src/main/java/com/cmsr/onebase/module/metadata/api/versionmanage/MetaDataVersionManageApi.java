package com.cmsr.onebase.module.metadata.api.versionmanage;

public interface MetaDataVersionManageApi {
    /**
     * moveMetaDataRuntimeToHistory
     * @param applicationId
     * @param versionTag
     */
    public void moveMetaDataRuntimeToHistory(Long applicationId, Long versionTag);

    /**
     * copyMetaDataEditToRuntime
     * @param applicationId
     */
    public void copyMetaDataEditToRuntime(Long applicationId);

}
