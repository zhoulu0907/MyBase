package com.cmsr.onebase.module.engine.orm.mybatisflex.repository;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowUser;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowUserMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 流程用户 仓储
 *
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowUserRepository extends ServiceImpl<FlowUserMapper, FlowUser> {

}


