package cmsr.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ChartDSLConfig {

    //柱状图DSL
    @Value("${charBarDSL}")
    private String charBarDSL;
    ////折线图DSL
    @Value("${charLineDSL}")
    private String charLineDSL;
    ////饼图DSL
    @Value("${charPieDSL}")
    private String charPieDSL;
    ////漏斗图DSL
    @Value("${charFunnelDSL}")
    private String charFunnelDSL;

}
