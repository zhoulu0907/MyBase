package com.cmsr.onebase.module.flow.component.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PropertyDefine {

    private String type;

    private Map<String, PropertyDefine> properties;

    private Set<String> required;

}
