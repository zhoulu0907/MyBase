package com.cmsr.onebase.module.flow.flow;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @Author：huangjie
 * @Date：2025/9/5 17:28
 */
@Data
public class ExecuteContext {

    private Optional<String> interruptNodeTag;

    public boolean equalsInterruptNodeTag(String tag) {
        return StringUtils.equals(tag, this.getInterruptNodeTag().get());
    }

    public void restInterruptNodeTag() {
        this.interruptNodeTag = Optional.empty();
    }

}
