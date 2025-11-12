package com.cmsr.onebase.module.metadata.core.domain.query;

import lombok.Data;

@Data
public class LoginUserCtx {
    private String token;

    private Long userId;

    private Long applicationId;
}
