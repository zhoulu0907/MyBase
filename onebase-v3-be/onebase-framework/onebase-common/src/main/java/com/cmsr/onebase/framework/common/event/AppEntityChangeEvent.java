package com.cmsr.onebase.framework.common.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppEntityChangeEvent {

    private Long applicationId;

}
