package com.cmsr.onebase.framework.ds.model.common;

import lombok.Data;

@Data
public class Parameter {

    private String prop;

    private String value;

    private String direct;

    private String type = "VARCHAR";

    public static Parameter getIn(String propName) {
        Parameter parameter = new Parameter();
        parameter.setProp(propName);
        parameter.setDirect("IN");
        return parameter;
    }

    public static Parameter getOut(String propName) {
        Parameter parameter = new Parameter();
        parameter.setProp(propName);
        parameter.setDirect("OUT");
        return parameter;
    }
}
