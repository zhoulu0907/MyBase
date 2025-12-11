package com.cmsr.template.service;

import com.cmsr.api.template.TemplateMarketApi;
import com.cmsr.api.template.response.MarketBaseResponse;
import com.cmsr.api.template.response.MarketPreviewBaseResponse;
import com.cmsr.api.template.vo.MarketMetaDataVO;
import com.cmsr.template.manage.TemplateCenterManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : WangJiaHao
 * @date : 2023/11/17 13:20
 */
@RestController
@RequestMapping("/templateMarket")
public class TemplateMarketService implements TemplateMarketApi {

    @Resource
    private TemplateCenterManage templateCenterManage;
    @Override
    public MarketBaseResponse searchTemplate() {
        return templateCenterManage.searchTemplate();
    }
    @Override
    public MarketBaseResponse searchTemplateRecommend() {
        return templateCenterManage.searchTemplateRecommend();
    }

    @Override
    public MarketPreviewBaseResponse searchTemplatePreview() {
        return templateCenterManage.searchTemplatePreview();
    }

    @Override
    public List<String> categories() {
        return templateCenterManage.getCategories();
    }

    @Override
    public List<MarketMetaDataVO> categoriesObject() {
        return templateCenterManage.getCategoriesObject();
    }
}
