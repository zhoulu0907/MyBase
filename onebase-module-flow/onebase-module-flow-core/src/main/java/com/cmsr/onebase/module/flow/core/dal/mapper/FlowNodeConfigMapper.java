package com.cmsr.onebase.module.flow.core.dal.mapper;

import com.mybatisflex.core.BaseMapper;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *  映射层。
 *
 * @author HuangJie
 * @since 2025-12-22
 */
public interface FlowNodeConfigMapper extends BaseMapper<FlowNodeConfigDO> {

    @Select("SELECT * FROM flow_node_config WHERE application_id = #{applicationId} AND code = #{code} AND deleted = 0")
    FlowNodeConfigDO selectByApplicationAndCode(@Param("applicationId") Long applicationId, @Param("code") String code);

}
