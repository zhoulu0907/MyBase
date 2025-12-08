package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessMapper;
import com.cmsr.onebase.module.flow.core.vo.PageFlowProcessReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowProcessTableDef.FLOW_PROCESS;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:35
 */
@Repository
public class FlowProcessRepository extends BaseBizRepository<FlowProcessMapper, FlowProcessDO> {

    public String selectProcessName(Long processId) {
        QueryWrapper query = this.query()
                .select(FLOW_PROCESS.PROCESS_NAME)
                .where(FLOW_PROCESS.ID.eq(processId));
        return getObjAs(query, String.class);
    }

    public Map<Long, String> selectProcessNames(List<Long> processIds) {
        QueryWrapper query = this.query()
                .select(FLOW_PROCESS.ID, FLOW_PROCESS.PROCESS_NAME)
                .where(FLOW_PROCESS.ID.in(processIds));
        List<Row> rows = listAs(query, Row.class);
        return rows.stream()
                .collect(Collectors.toMap(
                        row -> row.getLong(FLOW_PROCESS.ID.getName()),
                        row -> row.getString(FLOW_PROCESS.PROCESS_NAME.getName())
                ));
    }

    public PageResult<FlowProcessDO> findPageByQuery(PageFlowProcessReqVO reqVO) {
        QueryWrapper query = this.query()
                .where(FLOW_PROCESS.APPLICATION_ID.eq(reqVO.getApplicationId()).when(reqVO.getApplicationId() != null))
                .where(FLOW_PROCESS.PROCESS_NAME.like(reqVO.getProcessName()).when(StringUtils.isNotEmpty(reqVO.getProcessName())))
                .where(FLOW_PROCESS.ENABLE_STATUS.eq(reqVO.getEnableStatus()).when(reqVO.getEnableStatus() != null))
                .where(FLOW_PROCESS.TRIGGER_TYPE.eq(reqVO.getTriggerType()).when(StringUtils.isNotEmpty(reqVO.getTriggerType())))
                .orderBy(FLOW_PROCESS.UPDATE_TIME, false);
        Page page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        Page<FlowProcessDO> pageData = this.page(page, query);
        return new PageResult(pageData.getRecords(), pageData.getTotalRow());
    }

    public List<FlowProcessDO> findAllByEnableStatusAndVersionTag(Integer status, Long versionTag) {
        QueryWrapper query = this.query()
                .where(FLOW_PROCESS.ENABLE_STATUS.eq(status))
                .where(FLOW_PROCESS.VERSION_TAG.eq(versionTag));
        return getMapper().selectListByQuery(query);
    }

    public List<FlowProcessDO> findAllByEnableStatusAndVersionTagAndTriggerType(Integer status, Long versionTag, List<String> triggerTypes) {
        QueryWrapper query = this.query()
                .where(FLOW_PROCESS.ENABLE_STATUS.eq(status))
                .where(FLOW_PROCESS.VERSION_TAG.eq(versionTag))
                .where(FLOW_PROCESS.TRIGGER_TYPE.in(triggerTypes));
        return getMapper().selectListByQuery(query);
    }


    public List<FlowProcessDO> findByApplicationIdAndEnableStatus(Long applicationId, Integer status, Long versionTag) {
        QueryWrapper query = this.query()
                .where(FLOW_PROCESS.APPLICATION_ID.eq(applicationId))
                .where(FLOW_PROCESS.ENABLE_STATUS.eq(status))
                .where(FLOW_PROCESS.VERSION_TAG.eq(versionTag));
        return getMapper().selectListByQuery(query);
    }


    public void moveRuntimeToHistory(Long applicationId, Long versionTag) {
        updateChain()
                .set(FLOW_PROCESS.VERSION_TAG, versionTag)
                .where(FLOW_PROCESS.APPLICATION_ID.eq(applicationId))
                .where(FLOW_PROCESS.VERSION_TAG.eq(VersionTagEnum.RUNTIME.getValue()))
                .update();
    }

    public void copyEditToRuntime(Long applicationId) {
        QueryWrapper query = this.query()
                .where(FLOW_PROCESS.APPLICATION_ID.eq(applicationId))
                .where(FLOW_PROCESS.VERSION_TAG.eq(VersionTagEnum.BUILD.getValue()));
        List<FlowProcessDO> flowProcessDOS = getMapper().selectListByQuery(query);
        flowProcessDOS.forEach(flowProcessDO -> {
            flowProcessDO.setId(null);
            flowProcessDO.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        });
        getMapper().insertBatch(flowProcessDOS);
    }

}
