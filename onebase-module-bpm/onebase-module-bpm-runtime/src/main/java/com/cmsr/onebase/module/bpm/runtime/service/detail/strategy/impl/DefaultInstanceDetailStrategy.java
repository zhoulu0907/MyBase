package com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.InitiationNodeExtDTO;
import org.springframework.stereotype.Component;

@Component
public class DefaultInstanceDetailStrategy  extends AbstractInstanceDetailStrategy<InitiationNodeExtDTO> {
    @Override
    public boolean supports(String bizNodeType) {
        return false;
    }
}
