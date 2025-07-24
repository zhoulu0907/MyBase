package com.cmsr.onebase.module.app.enums.protocol.page;

import com.cmsr.onebase.module.app.enums.protocol.common.Common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page extends Common {

    private PageSpec spec;

}
