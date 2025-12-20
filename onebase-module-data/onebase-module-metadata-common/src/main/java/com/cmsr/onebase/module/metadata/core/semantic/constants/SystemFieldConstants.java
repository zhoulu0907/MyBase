package com.cmsr.onebase.module.metadata.core.semantic.constants;

public final class SystemFieldConstants {
    private SystemFieldConstants() {}

    public static final class REQUIRE {
        public static final String OWNER_ID = "owner_id";
        public static final String CREATOR = "creator";
        public static final String UPDATER = "updater";
        public static final String OWNER_DEPT = "owner_dept";

        private REQUIRE() {}
    }

    public static final class OPTIONAL {
        public static final String CREATED_TIME = "created_time";
        public static final String UPDATED_TIME = "updated_time";
        public static final String DELETED = "deleted";
        public static final String LOCK_VERSION = "lock_version";
        public static final String DRAFT_STATUS = "draft_status";

        private OPTIONAL() {}
    }
}
