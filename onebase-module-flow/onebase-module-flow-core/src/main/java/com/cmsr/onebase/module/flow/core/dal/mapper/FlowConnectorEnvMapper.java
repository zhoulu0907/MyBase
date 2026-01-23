package com.cmsr.onebase.module.flow.core.dal.mapper;

import com.mybatisflex.core.BaseMapper;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 连接器环境配置 Mapper
 * <p>
 * 负责 flow_connector_env 表的数据库操作
 *
 * @author zhoulu
 * @since 2026-01-23
 */
public interface FlowConnectorEnvMapper extends BaseMapper<FlowConnectorEnvDO> {

    /**
     * 根据环境UUID查询
     *
     * @param envUuid 环境配置UUID
     * @return 环境配置DO
     */
    @Select("SELECT * FROM flow_connector_env WHERE env_uuid = #{envUuid} AND deleted = 0")
    FlowConnectorEnvDO selectByEnvUuid(@Param("envUuid") String envUuid);

    /**
     * 根据连接器类型查询环境配置列表
     *
     * @param typeCode 连接器类型编号
     * @return 环境配置列表
     */
    @Select("SELECT * FROM flow_connector_env WHERE type_code = #{typeCode} AND deleted = 0 ORDER BY sort_order, create_time")
    List<FlowConnectorEnvDO> selectByTypeCode(@Param("typeCode") String typeCode);

    /**
     * 检查环境编码是否已存在（同类型、同应用下）
     *
     * @param typeCode      连接器类型编号
     * @param envCode       环境编码
     * @param applicationId 应用ID
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM flow_connector_env " +
            "WHERE type_code = #{typeCode} AND env_code = #{envCode} " +
            "AND application_id = #{applicationId} AND deleted = 0")
    int countByTypeAndEnvCode(@Param("typeCode") String typeCode,
                               @Param("envCode") String envCode,
                               @Param("applicationId") Long applicationId);
}
