package com.cmsr.onebase.module.bpm.convert;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bmp.api.dto.BpmNodeExtDto;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmNodeCoordinateVo;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmNodeVO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.enums.NodeType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程节点转换类
 * 专门处理节点相关的转换逻辑
 *
 * @author liyang
 * @date 2025-10-21
 */
@Mapper(componentModel = "spring", uses = {BpmSkipConvert.class})
public interface BpmNodeConvert {

    /**
     * NodeJson转换为BpmNodeVO
     *
     * @param nodeJson NodeJson
     * @return BpmNodeVO
     */
    @Mapping(target = "nodeType", source = "ext", qualifiedByName = "extToBpmNodeType")
    @Mapping(target = "coordinate", source = "coordinate", qualifiedByName = "nodeJsonToCoordinate")
    @Mapping(target = "skipList", source = "skipList", qualifiedByName = "skipJsonListToBpmSkipVOList")
    BpmNodeVO nodeJsonToBpmNodeVO(NodeJson nodeJson);

    /**
     * BpmNodeVO转换为NodeJson
     *
     * @param bpmNodeVO BpmNodeVO
     * @return NodeJson
     */
    @Mapping(target = "nodeType", source = "nodeType", qualifiedByName = "bpmNodeTypeToWarmFlowNodeType")
    @Mapping(target = "ext", source = ".", qualifiedByName = "bpmNodeVOToExt")
    @Mapping(target = "coordinate", source = "coordinate", qualifiedByName = "bpmNodeVOToCoordinate")
    @Mapping(target = "skipList", source = "skipList", qualifiedByName = "bpmSkipVOListToSkipJsonList")
    NodeJson bpmNodeVOToNodeJson(BpmNodeVO bpmNodeVO);

    /**
     * NodeJson的ext字段转换为BpmNodeVO的nodeType
     *
     * @param ext NodeJson的ext字段
     * @return BpmNodeVO的nodeType
     */
    @Named("extToBpmNodeType")
    default String extToBpmNodeType(String ext) {
        if (ext == null || ext.trim().isEmpty()) {
            return null;
        }

        try {
            BpmNodeExtDto extDto = JsonUtils.parseObject(ext, BpmNodeExtDto.class);
            return extDto != null ? extDto.getNodeType() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * BpmNodeTypeEnum的String code转换为WarmFlow的Integer nodeType
     *
     * @param bpmNodeTypeCode BpmNodeTypeEnum的String code
     * @return WarmFlow的Integer nodeType
     */
    @Named("bpmNodeTypeToWarmFlowNodeType")
    default Integer bpmNodeTypeToWarmFlowNodeType(String bpmNodeTypeCode) {
        if (bpmNodeTypeCode == null || bpmNodeTypeCode.trim().isEmpty()) {
            return null;
        }

        BpmNodeTypeEnum bpmNodeType = BpmNodeTypeEnum.getByCode(bpmNodeTypeCode);
        if (bpmNodeType == null) {
            return null;
        }

        NodeType nodeType = bpmNodeType.toWarmFlowNodeType();

        if (nodeType == null) {
            return null;
        }

         return nodeType.getKey();
    }

    /**
     * BpmNodeVO转换为ext字段JSON字符串
     *
     * @param bpmNodeVO BpmNodeVO对象
     * @return ext字段的JSON字符串
     */
    @Named("bpmNodeVOToExt")
    default String bpmNodeVOToExt(BpmNodeVO bpmNodeVO) {
        if (bpmNodeVO == null) {
            return null;
        }

        BpmNodeExtDto extDto = new BpmNodeExtDto();
        extDto.setNodeType(bpmNodeVO.getNodeType());

        return JsonUtils.toJsonString(extDto);
    }

    /**
     * NodeJson列表转换为BpmNodeVO列表
     *
     * @param nodeJsonList NodeJson列表
     * @return BpmNodeVO列表
     */
    @Named("nodeJsonListToBpmNodeVOList")
    default List<BpmNodeVO> nodeJsonListToBpmNodeVOList(List<NodeJson> nodeJsonList) {
        if (nodeJsonList == null) {
            return null;
        }
        return nodeJsonList.stream()
                .map(this::nodeJsonToBpmNodeVO)
                .collect(Collectors.toList());
    }

    /**
     * BpmNodeVO列表转换为NodeJson列表
     *
     * @param bpmNodeVOList BpmNodeVO列表
     * @return NodeJson列表
     */
    @Named("bpmNodeVOListToNodeJsonList")
    default List<NodeJson> bpmNodeVOListToNodeJsonList(List<BpmNodeVO> bpmNodeVOList) {
        if (bpmNodeVOList == null) {
            return null;
        }
        return bpmNodeVOList.stream()
                .map(this::bpmNodeVOToNodeJson)
                .collect(Collectors.toList());
    }

    /**
     * NodeJson的coordinate字段转换为BpmNodeVO的coordinate
     *
     * @param coordinate NodeJson的coordinate字段
     * @return BpmNodeVO的coordinate
     */
    @Named("nodeJsonToCoordinate")
    default BpmNodeCoordinateVo nodeJsonToCoordinate(String coordinate) {
        BpmNodeCoordinateVo coordinateVo = new BpmNodeCoordinateVo();

        if (coordinate == null || coordinate.trim().isEmpty()) {
            return coordinateVo;
        }

        String[] coordinateArray = coordinate.split("\\|");
        int index = 0;

        for (String item : coordinateArray) {
            String[] point = item.split(",");

            if (point.length != 2) {
                index ++;
                continue;
            }

            if (index == 0) {
                coordinateVo.setX(Integer.parseInt(point[0]));
                coordinateVo.setY(Integer.parseInt(point[1]));
            } else if (index == 1) {
                coordinateVo.setTextX(Integer.parseInt(point[0]));
                coordinateVo.setTextY(Integer.parseInt(point[1]));
            } else if (index == 2) {
                coordinateVo.setWidth(Integer.parseInt(point[0]));
                coordinateVo.setHeight(Integer.parseInt(point[1]));
            }

            index ++;
        }

        return coordinateVo;
    }

    /**
     * BpmNodeVO的coordinate转换为NodeJson的coordinate
     *
     * @param coordinate BpmNodeVO的coordinate
     * @return NodeJson的coordinate
     */
    @Named("bpmNodeVOToCoordinate")
    default String bpmNodeVOToCoordinate(BpmNodeCoordinateVo coordinate) {
        if (coordinate == null) {
            return null;
        }

        String coordinateStr = String.format("%d,%d|%d,%d|%d,%d",
                coordinate.getX(), coordinate.getY(),
                coordinate.getTextX(), coordinate.getTextY(),
                coordinate.getWidth(), coordinate.getHeight());

        return coordinateStr;
    }
}
