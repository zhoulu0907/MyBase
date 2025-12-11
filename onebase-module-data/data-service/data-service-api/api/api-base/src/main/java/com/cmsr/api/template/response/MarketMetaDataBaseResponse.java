package com.cmsr.api.template.response;

import com.cmsr.api.template.vo.MarketMetaDataVO;
import lombok.Data;

import java.util.List;

/**
 * Author: wangjiahao
 * Date: 2022/7/15
 * Description:
 */
@Data
public class MarketMetaDataBaseResponse {

    private List<MarketMetaDataVO> deVersion;

    private List<MarketMetaDataVO> templateTypes;

    private List<MarketMetaDataVO> labels;
}
