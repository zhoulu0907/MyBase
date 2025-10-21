package com.cmsr.onebase.module.bpm.convert;

import com.cmsr.onebase.module.bpm.build.vo.design.BpmSkipPointVo;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmSkipVO;
import org.dromara.warm.flow.core.dto.SkipJson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程跳转转换类
 * 专门处理节点跳转相关的转换逻辑
 *
 * @author liyang
 * @date 2025-10-21
 */
@Mapper(componentModel = "spring")
public interface BpmSkipConvert {

    /**
     * SkipJson转换为BpmSkipVO
     *
     * @param skipJson SkipJson
     * @return FlowSkipVO
     */
    @Mapping(target = "points", source = "coordinate", qualifiedByName = "coordinateToPoints")
    BpmSkipVO skipJsonToFlowSkipVO(SkipJson skipJson);

    /**
     * FlowSkipVO转换为SkipJson
     *
     * @param bpmSkipVO BpmSkipVO
     * @return SkipJson
     */
    @Mapping(target = "coordinate", source = "points", qualifiedByName = "pointsToCoordinate")
    SkipJson flowSkipVOToSkipJson(BpmSkipVO bpmSkipVO);

    /**
     * SkipJson列表转换为FlowSkipVO列表
     *
     * @param skipJsonList SkipJson列表
     * @return FlowSkipVO列表
     */
    @Named("skipJsonListToBpmSkipVOList")
    default List<BpmSkipVO> skipJsonListToBpmSkipVOList(List<SkipJson> skipJsonList) {
        if (skipJsonList == null) {
            return null;
        }
        return skipJsonList.stream()
                .map(this::skipJsonToFlowSkipVO)
                .collect(Collectors.toList());
    }

    /**
     * FlowSkipVO列表转换为SkipJson列表
     *
     * @param bpmSkipVOList FlowSkipVO列表
     * @return SkipJson列表
     */
    @Named("bpmSkipVOListToSkipJsonList")
    default List<SkipJson> bpmSkipVOListToSkipJsonList(List<BpmSkipVO> bpmSkipVOList) {
        if (bpmSkipVOList == null) {
            return null;
        }
        return bpmSkipVOList.stream()
                .map(this::flowSkipVOToSkipJson)
                .collect(Collectors.toList());
    }
     /**
     * 坐标字符串转换为点字符串数组
     *
     * @param coordinate 坐标字符串，格式为"x1,y1|x2,y2|..."
     * @return 点字符串数组，格式为"x1,y1|x2,y2|..."
     */
    @Named("coordinateToPoints")
    default List<BpmSkipPointVo> coordinateToPoints(String coordinate) {
        if (coordinate == null || coordinate.trim().isEmpty()) {
            return null;
        }

        String[] coordinateArray = coordinate.split(";");

        return List.of(coordinateArray)
                .stream()
                .map(point -> {
                    String[] pointArray = point.split(",");
                    BpmSkipPointVo bpmSkipPointVo = new BpmSkipPointVo();
                    bpmSkipPointVo.setX(Integer.parseInt(pointArray[0]));
                    bpmSkipPointVo.setY(Integer.parseInt(pointArray[1]));
                    return bpmSkipPointVo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 点字符串数组转换为坐标字符串
     *
     * @param points 点字符串数组，格式为"x1,y1|x2,y2|..."
     * @return 坐标字符串，格式为"x1,y1|x2,y2|..."
     */
    @Named("pointsToCoordinate")
    default String pointsToCoordinate(List<BpmSkipPointVo> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }

        return points.stream()
                .map(point -> point.getX() + "," + point.getY())
                .collect(Collectors.joining("|"));
    }
}
