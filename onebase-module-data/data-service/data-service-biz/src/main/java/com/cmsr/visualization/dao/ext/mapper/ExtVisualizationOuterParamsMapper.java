package com.cmsr.visualization.dao.ext.mapper;


import com.cmsr.api.dataset.vo.CoreDatasetGroupVO;
import com.cmsr.api.visualization.dto.VisualizationOuterParamsDTO;
import com.cmsr.api.visualization.dto.VisualizationOuterParamsInfoDTO;
import com.cmsr.visualization.dao.auto.entity.SnapshotVisualizationOuterParamsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtVisualizationOuterParamsMapper {

    VisualizationOuterParamsDTO queryWithVisualizationIdSnapshot(@Param("visualizationId") String visualizationId);

    void deleteOuterParamsTargetWithVisualizationId(@Param("visualizationId") String visualizationId);

    void deleteOuterParamsInfoWithVisualizationId(@Param("visualizationId") String visualizationId);

    void deleteOuterParamsWithVisualizationId(@Param("visualizationId") String visualizationId);

    void deleteOuterParamsTargetWithVisualizationIdSnapshot(@Param("visualizationId") String visualizationId);

    void deleteOuterParamsInfoWithVisualizationIdSnapshot(@Param("visualizationId") String visualizationId);

    void deleteOuterParamsWithVisualizationIdSnapshot(@Param("visualizationId") String visualizationId);

    List<VisualizationOuterParamsInfoDTO> getVisualizationOuterParamsInfo(@Param("visualizationId") String visualizationId);

    List<SnapshotVisualizationOuterParamsInfo> getVisualizationOuterParamsInfoBase(@Param("visualizationId") String visualizationId);

    List<CoreDatasetGroupVO> queryDsWithVisualizationId(@Param("visualizationId") String visualizationId);
}
