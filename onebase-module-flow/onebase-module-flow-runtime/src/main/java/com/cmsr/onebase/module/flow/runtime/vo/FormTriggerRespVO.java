package com.cmsr.onebase.module.flow.runtime.vo;

import lombok.Data;

import java.util.Map;

@Data
public class FormTriggerRespVO {

    private String executionUuid;

    private Integer triggered;

    private Map<String, Object> result;

}
