package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDefinitionVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDefVersionMgtVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmUpdateReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmVersionMgmtPageReqVo;

import java.util.List;


/**
 * 流程版本管理服务接口
 *
 * @author liyang
 * @date 2025-10-25
 */
public interface BpmVersionMgmtService {
    /**
     * 删除流程
     *
     * @param reqVo
     */
    void delete(BpmDeleteReqVo reqVo);

    /**
     * 获取流程版本管理分页列表
     *
     * @param reqVo
     * @return
     */
    PageResult<BpmDefVersionMgtVO> getVersionMgmtPage(BpmVersionMgmtPageReqVo reqVo);
    /**
     * 更新流程版本别名
     *
     * @param reqVo
     */
    void updateVersionAliasById(BpmUpdateReqVo reqVo);

}
