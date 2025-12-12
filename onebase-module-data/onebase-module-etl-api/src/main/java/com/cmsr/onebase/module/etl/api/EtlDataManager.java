package com.cmsr.onebase.module.etl.api;

public interface EtlDataManager {

    void offlineAllByApplication(Long applicationId);

    void removeAllByApplication(Long applicationId);

}
