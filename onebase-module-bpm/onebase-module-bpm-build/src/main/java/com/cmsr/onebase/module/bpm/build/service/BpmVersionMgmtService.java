package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.module.bpm.build.vo.design.BpmDefinitionVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDefVersionMgtVO;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmDeleteReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmGetReqVo;
import com.cmsr.onebase.module.bpm.build.vo.vermgmt.BpmUpdateReqVo;

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
     * 根据业务id获取流程版本列表
     *
     * @param reqVo
     */
    List<BpmDefVersionMgtVO> getByBusinessId(BpmGetReqVo reqVo);
    /**
     * 更新流程版本别名
     *
     * @param reqVo
     */
    void updateVersionAliasById(BpmUpdateReqVo reqVo);

}
