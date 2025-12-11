package com.cmsr.datasource.type;

import com.cmsr.extensions.datasource.vo.DatasourceConfiguration;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component("h2")
public class H2 extends DatasourceConfiguration {
    private String driver = "org.h2.Driver";
}
