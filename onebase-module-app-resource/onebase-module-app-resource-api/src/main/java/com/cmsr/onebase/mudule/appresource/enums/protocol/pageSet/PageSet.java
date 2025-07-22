package com.cmsr.onebase.mudule.appresource.enums.protocol.pageSet;

import com.onebase.protocols.common.Common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSet extends Common {
    private PageSetSpec spec;
}
