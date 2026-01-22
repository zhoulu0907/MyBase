package com.cmsr.onebase.module.flow.core.dal.mapper;

import com.mybatisflex.core.BaseMapper;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 *  映射层。
 *
 * @author HuangJie
 * @since 2025-11-25
 */
public interface FlowConnectorMapper extends BaseMapper<FlowConnectorDO> {

    @Select("SELECT * FROM flow_connector WHERE application_id = #{applicationId} AND type_code = #{typeCode} AND deleted = 0")
    FlowConnectorDO selectByApplicationAndTypeCode(@Param("applicationId") Long applicationId, @Param("typeCode") String typeCode);

    /**
     * Count connector instances by type codes (only non-deleted records)
     *
     * @param typeCodes the connector type code list
     * @return list of Map with type_code and count
     */
    @Select("<script>" +
            "SELECT type_code, COUNT(*) as count FROM flow_connector " +
            "WHERE deleted = 0 " +
            "AND type_code IN " +
            "<foreach collection='typeCodes' item='typeCode' open='(' separator=',' close=')'>" +
            "#{typeCode}" +
            "</foreach>" +
            "GROUP BY type_code" +
            "</script>")
    List<Map<String, Object>> countByTypeCodes(@Param("typeCodes") List<String> typeCodes);

}
