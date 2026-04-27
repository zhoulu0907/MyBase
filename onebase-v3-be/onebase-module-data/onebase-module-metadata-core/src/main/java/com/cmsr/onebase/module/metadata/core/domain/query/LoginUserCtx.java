package com.cmsr.onebase.module.metadata.core.domain.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserCtx {
    private Long userId;
    private Long applicationId;
}
