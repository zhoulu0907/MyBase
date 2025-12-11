package com.cmsr.api.visualization.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.cmsr.api.visualization.dto.VisualizationLinkageDTO;
import com.cmsr.api.visualization.vo.VisualizationLinkageVO;
import com.cmsr.constant.CommonConstants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : WangJiaHao
 * @date : 2023/7/13
 */
@Data
public class VisualizationLinkageRequest extends VisualizationLinkageVO {

    /**
     * 仪表板 or 大屏ID
     * */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dvId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceViewId;

    private Boolean ActiveStatus;

    private List<String> targetViewIds;

    private String resourceTable = CommonConstants.RESOURCE_TABLE.CORE;

    private List<VisualizationLinkageDTO> linkageInfo = new ArrayList<>();

}
