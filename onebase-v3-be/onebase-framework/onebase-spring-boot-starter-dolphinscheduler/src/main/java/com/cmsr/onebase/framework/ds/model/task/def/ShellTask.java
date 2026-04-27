package com.cmsr.onebase.framework.ds.model.task.def;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShellTask extends AbstractTask {

    private String rawScript;

    @Override
    public String grantTaskType() {
        return "SHELL";
    }
}
