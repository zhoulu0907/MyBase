package com.cmsr.onebase.module.app.core.enums.protocol.page;

import com.cmsr.onebase.module.app.core.enums.protocol.common.Common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page extends Common {

    private PageSpec spec;

}
