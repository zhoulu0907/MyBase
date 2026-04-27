package com.cmsr.onebase.framework.ds.model.workflow.sub;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComplementTime {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime complementStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime complementEndDate;

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }
}
