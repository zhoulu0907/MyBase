package com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext;

import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowTask;
import lombok.Data;

@Data
public class FlowTaskExt extends FlowTask {
    private String ext;
}
