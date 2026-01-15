package com.cmsr.onebase.module.flow.core.dal.mapper;

import com.mybatisflex.core.BaseMapper;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *  映射层。
 *
 * @author HuangJie
 * @since 2025-11-25
 */
public interface FlowConnectorMapper extends BaseMapper<FlowConnectorDO> {

    @Select("SELECT * FROM flow_connector WHERE application_id = #{applicationId} AND code = #{code} AND deleted = 0")
    FlowConnectorDO selectByApplicationAndCode(@Param("applicationId") Long applicationId, @Param("code") String code);

}
