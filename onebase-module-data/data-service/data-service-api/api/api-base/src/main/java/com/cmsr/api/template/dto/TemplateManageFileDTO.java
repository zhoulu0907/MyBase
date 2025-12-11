package com.cmsr.api.template.dto;


import com.cmsr.api.template.vo.VisualizationTemplateVO;
import lombok.Data;


@Data
public class TemplateManageFileDTO extends VisualizationTemplateVO {

    /**
     * 样式数据
     */
    private String canvasStyleData;

    /**
     * 组件数据
     */
    private String componentData;


    private String staticResource;

}
