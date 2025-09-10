package com.cmsr.onebase.module.app.core.enums.protocol.application;


import com.cmsr.onebase.module.app.core.enums.protocol.common.Common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application extends Common {
    private ApplicationSpec spec;
}
