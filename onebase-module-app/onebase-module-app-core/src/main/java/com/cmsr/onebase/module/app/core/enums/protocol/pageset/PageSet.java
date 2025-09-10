package com.cmsr.onebase.module.app.core.enums.protocol.pageset;

import com.cmsr.onebase.module.app.core.enums.protocol.common.Common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSet extends Common {
    private PageSetSpec spec;
}
