package com.cmsr.api.template.response;

import com.cmsr.api.template.vo.MarketApplicationVO;
import com.cmsr.api.template.vo.MarketLatestReleaseVO;
import lombok.Data;

/**
 * @author : WangJiaHao
 * @date : 2023/11/17 13:41
 */
@Data
public class MarketTemplateV2ItemResult {

    private MarketApplicationVO application;

    private MarketLatestReleaseVO latestRelease;

}
