package com.cmsr.onebase.module.flow.component.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PropertyDefine {

    private String name;

    private String type;

}
