package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.logger.LoginLogDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

/**
 * 登录日志数据访问层
 *
 * 负责登录日志相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class LoginLogRepository extends DataRepositoryNew<LoginLogDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public LoginLogRepository() {
        super(LoginLogDO.class);
    }

    /**
     * 分页查询登录日志
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<LoginLogDO> findPage(LoginLogPageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqVO.getUserIp() != null && !reqVO.getUserIp().trim().isEmpty()) {
            configs.and(Compare.LIKE, LoginLogDO.USER_IP, reqVO.getUserIp());
        }
        if (reqVO.getUsername() != null && !reqVO.getUsername().trim().isEmpty()) {
            configs.and(Compare.LIKE, LoginLogDO.USERNAME, reqVO.getUsername());
        }
        if (reqVO.getStatus() != null) {
            configs.and(Compare.EQUAL, LoginLogDO.RESULT, reqVO.getStatus());
        }
        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            if (reqVO.getCreateTime()[0] != null) {
                configs.and(Compare.GREAT_EQUAL, LoginLogDO.CREATE_TIME, reqVO.getCreateTime()[0]);
            }
            if (reqVO.getCreateTime()[1] != null) {
                configs.and(Compare.LESS_EQUAL, LoginLogDO.CREATE_TIME, reqVO.getCreateTime()[1]);
            }
        }

        // 添加排序条件，按ID降序排列
        configs.order(LoginLogDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }
}
