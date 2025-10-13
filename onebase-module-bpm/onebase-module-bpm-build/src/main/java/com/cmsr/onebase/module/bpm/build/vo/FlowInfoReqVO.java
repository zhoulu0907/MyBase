package com.cmsr.onebase.module.bpm.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "流程信息请求VO")
public class FlowInfoReqVO extends FlowDefinitionVO {


    private List<FlowNodeVO> nodeList;




}