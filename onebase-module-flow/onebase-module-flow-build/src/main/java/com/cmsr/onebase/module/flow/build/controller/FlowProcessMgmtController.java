package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowProcessMgmtService;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.utils.CronUtils;
import com.cmsr.onebase.module.flow.core.vo.PageFlowProcessReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程管理 - 管理员控制器
 *
 * <p>提供流程的增删改查、启用/关闭等管理功能接口，供管理员使用。</p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li>流程列表查询（分页）</li>
 *   <li>流程详情查询</li>
 *   <li>创建流程</li>
 *   <li>更新流程信息</li>
 *   <li>重命名流程</li>
 *   <li>更新流程定义</li>
 *   <li>启用/关闭流程</li>
 *   <li>删除流程（单个/批量）</li>
 *   <li>Cron 表达式解析验证</li>
 * </ul>
 *
 * @author onebase
 * @since 2025
 */
@Slf4j
@Setter
@RestController
@RequestMapping("/flow/mgmt")
@Tag(name = "流程管理", description = "流程管理相关接口")
@Validated
public class FlowProcessMgmtController
{

    @Autowired
    private FlowProcessMgmtService flowProcessMgmtService;

    /**
     * 分页查询流程列表
     *
     * <p>根据查询条件分页返回流程列表，支持按应用ID、流程名称、触发类型等条件筛选。</p>
     *
     * @param reqVO 分页查询请求参数
     * @return 分页结果，包含流程列表和分页信息
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询流程列表")
    public CommonResult<PageResult<FlowProcessVO>> pageList(PageFlowProcessReqVO reqVO) {
        log.info("分页查询流程列表, reqVO={}", reqVO);
        PageResult<FlowProcessVO> pageResult = flowProcessMgmtService.pageList(reqVO);
        log.info("分页查询流程列表成功, total={}, size={}", pageResult.getTotal(),
                pageResult.getList() != null ? pageResult.getList().size() : 0);
        return CommonResult.success(pageResult);
    }

    /**
     * 获取流程详情
     *
     * <p>根据流程ID查询流程的完整信息，包括流程定义、触发配置等。</p>
     *
     * @param id 流程ID
     * @return 流程详情，如果流程不存在则返回错误
     */
    @GetMapping("/get")
    @Operation(summary = "获取流程详情")
    public CommonResult<FlowProcessVO> getDetail(@RequestParam("id") Long id) {
        log.info("查询流程详情, id={}", id);
        FlowProcessVO flowProcessVO = flowProcessMgmtService.getDetail(id);
        if (flowProcessVO == null) {
            log.warn("流程不存在, id={}", id);
            return CommonResult.error(FlowErrorCodeConstants.FLOW_NOT_EXIST);
        }
        log.info("查询流程详情成功, id={}, processName={}", id, flowProcessVO.getProcessName());
        return CommonResult.success(flowProcessVO);
    }

    /**
     * 创建流程
     *
     * <p>创建一个新的流程，需要提供流程名称、触发类型、流程定义等信息。</p>
     *
     * <p><b>创建后会自动：</b></p>
     * <ul>
     *   <li>生成流程UUID</li>
     *   <li>解析流程定义JSON</li>
     *   <li>将流程加入内存缓存</li>
     *   <li>如果是定时触发类型，创建调度任务</li>
     * </ul>
     *
     * @param reqVO 创建流程请求参数
     * @return 新创建的流程ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建流程")
    public CommonResult<Long> create(@RequestBody @Valid CreateFlowProcessReqVO reqVO) {
        log.info("创建流程, reqVO={}", reqVO);
        Long id = flowProcessMgmtService.create(reqVO);
        log.info("创建流程成功, id={}, processName={}", id, reqVO.getProcessName());
        return CommonResult.success(id);
    }

    /**
     * 更新流程
     *
     * <p>更新流程的基本信息（如流程名称、描述等），不包含流程定义。</p>
     *
     * <p><b>注意：</b>更新流程定义请使用 {@link #updateProcessDefinition(UpdateProcessDefinitionReqVO)} 接口。</p>
     *
     * @param reqVO 更新流程请求参数
     * @return 操作结果
     */
    @PostMapping("/update")
    @Operation(summary = "更新流程")
    public CommonResult<Boolean> update(@RequestBody @Valid UpdateFlowProcessReqVO reqVO) {
        log.info("更新流程, reqVO={}", reqVO);
        flowProcessMgmtService.update(reqVO);
        log.info("更新流程成功, id={}", reqVO.getId());
        return CommonResult.success(true);
    }

    /**
     * 重命名流程
     *
     * <p>仅修改流程的名称，不影响其他配置和流程定义。</p>
     *
     * @param reqVO 重命名请求参数，包含流程ID和新名称
     * @return 操作结果
     */
    @PostMapping("/rename")
    @Operation(summary = "重命名流程")
    public CommonResult<Boolean> renameFlowProcess(@RequestBody @Valid RenameFlowProcessReqVO reqVO) {
        log.info("重命名流程, reqVO={}", reqVO);
        flowProcessMgmtService.renameFlowProcess(reqVO);
        log.info("重命名流程成功, id={}, newName={}", reqVO.getId(), reqVO.getProcessName());
        return CommonResult.success(true);
    }

    /**
     * 更新流程定义
     *
     * <p>更新流程的 JSON 定义，包括节点、边、连接关系等。</p>
     *
     * <p><b>更新后会自动：</b></p>
     * <ul>
     *   <li>重新解析流程定义JSON</li>
     *   <li>更新内存缓存中的流程图</li>
     *   <li>如果是定时触发类型，更新调度任务</li>
     * </ul>
     *
     * @param reqVO 更新流程定义请求参数
     * @return 操作结果
     */
    @PostMapping("/update-definition")
    @Operation(summary = "更新流程定义")
    public CommonResult<Boolean> updateProcessDefinition(@RequestBody @Valid UpdateProcessDefinitionReqVO reqVO) {
        log.info("更新流程定义, reqVO={}", reqVO);
        flowProcessMgmtService.updateProcessDefinition(reqVO);
        log.info("更新流程定义成功, id={}", reqVO.getId());
        return CommonResult.success(true);
    }

    /**
     * 启用流程
     *
     * <p>将流程状态设置为启用，流程可以被触发执行。</p>
     *
     * <p><b>启用后会自动：</b></p>
     * <ul>
     *   <li>将流程加载到内存缓存</li>
     *   <li>如果是定时触发类型，启动调度任务</li>
     * </ul>
     *
     * @param id 流程ID
     * @return 操作结果
     */
    @PostMapping("/enable")
    @Operation(summary = "启用流程")
    public CommonResult<Boolean> enableFlowProcess(@RequestParam Long id) {
        log.info("启用流程, id={}", id);
        flowProcessMgmtService.enableFlowProcess(id);
        log.info("启用流程成功, id={}", id);
        return CommonResult.success(true);
    }

    /**
     * 关闭流程
     *
     * <p>将流程状态设置为关闭，流程不再被触发执行。</p>
     *
     * <p><b>关闭后会自动：</b></p>
     * <ul>
     *   <li>从内存缓存中移除</li>
     *   <li>如果是定时触发类型，停止调度任务</li>
     * </ul>
     *
     * @param id 流程ID
     * @return 操作结果
     */
    @PostMapping("/disable")
    @Operation(summary = "关闭流程")
    public CommonResult<Boolean> disableFlowProcess(@RequestParam Long id) {
        log.info("关闭流程, id={}", id);
        flowProcessMgmtService.disableFlowProcess(id);
        log.info("关闭流程成功, id={}", id);
        return CommonResult.success(true);
    }

    /**
     * 删除流程
     *
     * <p>删除指定ID的流程，包括流程定义和所有相关配置。</p>
     *
     * <p><b>删除后会自动：</b></p>
     * <ul>
     *   <li>从数据库中删除流程记录</li>
     *   <li>从内存缓存中移除</li>
     *   <li>如果是定时触发类型，停止调度任务</li>
     * </ul>
     *
     * @param id 流程ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    @Operation(summary = "删除流程")
    public CommonResult<Boolean> delete(@RequestParam Long id) {
        log.info("删除流程, id={}", id);
        flowProcessMgmtService.delete(id);
        log.info("删除流程成功, id={}", id);
        return CommonResult.success(true);
    }

    /**
     * 批量删除流程
     *
     * <p>批量删除多个流程，提高删除效率。</p>
     *
     * @param ids 流程ID列表
     * @return 操作结果
     */
    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除流程")
    public CommonResult<Boolean> batchDelete(@RequestBody List<Long> ids) {
        log.info("批量删除流程, ids={}", ids);
        if (ids == null || ids.isEmpty()) {
            log.info("批量删除流程, ids为空，跳过处理");
            return CommonResult.success(true);
        }
        flowProcessMgmtService.batchDelete(ids);
        log.info("批量删除流程成功, count={}", ids.size());
        return CommonResult.success(true);
    }

    /**
     * Cron 表达式解析
     *
     * <p>验证 Cron 表达式的有效性，并计算接下来 5 次执行时间。</p>
     *
     * <p>用于前端在用户输入 Cron 表达式时提供实时反馈和预览。</p>
     *
     * @param cron Cron 表达式字符串
     * @return 解析结果，包含是否有效和下一次执行时间
     */
    @GetMapping("/cron-parse")
    @Operation(summary = "cron表达式解析")
    public CommonResult<CronParseRespVO> cronParse(@RequestParam String cron) {
        log.info("解析Cron表达式, cron={}", cron);
        CronParseRespVO cronParseRespVO = new CronParseRespVO();
        if (CronUtils.isValid(cron)) {
            cronParseRespVO.setValid(true);
            try {
                cronParseRespVO.setNext(CronUtils.nextExecuteTime(cron, 5));
                log.info("Cron表达式解析成功, cron={}, nextTimes={}", cron, cronParseRespVO.getNext());
            } catch (Exception e) {
                log.warn("Cron表达式计算下次执行时间失败, cron={}", cron, e);
                cronParseRespVO.setValid(false);
            }
        } else {
            log.info("Cron表达式无效, cron={}", cron);
            cronParseRespVO.setValid(false);
        }
        return CommonResult.success(cronParseRespVO);
    }
}
