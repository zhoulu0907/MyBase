package com.cmsr.onebase.module.etl.build.service.etl.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ETLInstanceLogVO {

    private Long id;

    private Long etlId;

    private LocalDateTime businessDate;

    private String triggerType;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long duration;

    private Long operator;

    private String operatorName;
}
