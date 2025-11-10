package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowDelegationRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowDelegationDO;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmDelegationService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmDelegationPageResVO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
import org.apache.commons.lang3.StringUtils;
import com.cmsr.onebase.module.bpm.core.enums.BpmDelegationStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
@Service
public class BpmDelegationServiceImpl implements BpmDelegationService {
    @Resource
    private BpmFlowDelegationRepository bpmFlowDelegationRepository;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public PageResult<BpmDelegationPageResVO> getDelegationPage(BpmDelegationPageReqVO pageReqVO) {
        // 获取当前登录用户ID
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 构建查询条件
        ConfigStore condition = buildDynamicCondition(pageReqVO, String.valueOf(loginUserId));

        PageResult<BpmFlowDelegationDO> pageResult =bpmFlowDelegationRepository.getDelegationPage( condition);

        // 获取代理人，被代理人，创建人信息
        Map<Long, AdminUserRespDTO>  userMap = queryDelegateUser(pageResult.getList());

        List<BpmDelegationPageResVO> list = pageResult.getList().stream().map(item -> {
            BpmDelegationPageResVO vo = new BpmDelegationPageResVO();
            // 代理人
            vo.setDelegate(createDelegateUser(userMap.get(item.getDelegateId())));
            // 被代理人
            vo.setPrincipal(createDelegateUser(userMap.get(item.getPrincipalId())));
            //创建人
            vo.setCreator(createDelegateUser(userMap.get(item.getCreator())));
            // 设置委托时间
            vo.setDelegateTime(formatDelegateTimePeriod(item.getStartTime(), item.getEndTime()));
            vo.setCreateTime(item.getCreateTime());
            // 代理状态
            vo.setDelegateStatus(calculateDelegateStatus(item.getStartTime(), item.getEndTime(),item.getRevokedTime()));
            return vo;
        }).toList();
       return new PageResult<>(list, pageResult.getTotal());
    }
    /**
     * 计算委托状态
     * @param startTime 代理生效时间
     * @param endTime 代理失效时间
     * @param revokedTime 撤销时间
     * @return 委托状态字符串
     */
    private String calculateDelegateStatus(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime revokedTime) {
        // 如果撤销时间不为空，直接返回已撤销
        if (revokedTime != null) {
            return BpmDelegationStatus.REVOKED.getName();
        }

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 判断待生效：当前时间 < 代理生效时间
        if (startTime != null && now.isBefore(startTime)) {
            return BpmDelegationStatus.INACTIVE.getName();
        }

        // 判断代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
        if (startTime != null && endTime != null &&
                now.isAfter(startTime) && now.isBefore(endTime)) {
            return BpmDelegationStatus.ACTIVE.getName();
        }

        // 判断已失效：当前时间 > 代理失效时间
        if (endTime != null && now.isAfter(endTime)) {
            return BpmDelegationStatus.EXPIRED.getName();
        }

        return "未知状态";
    }


    /**
     * 格式化委托时间期间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 格式化后的时间字符串，格式为 "yyyy-MM-dd至yyyy-MM-dd"
     */
    private String formatDelegateTimePeriod(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return "";
        }
        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return startTimeStr + "至" + endTimeStr;
    }

    private UserBasicInfoVO createDelegateUser(AdminUserRespDTO user) {
        if (user == null) {
            return null;
        }
        UserBasicInfoVO operationUser = new UserBasicInfoVO();
        operationUser.setUserId(user.getId());
        operationUser.setName(user.getNickname());
        operationUser.setAvatar(user.getAvatar());
        return operationUser;
    }

    private Map<Long, AdminUserRespDTO> queryDelegateUser(List<BpmFlowDelegationDO>  list) {
        // 获取创建人和代理人被代理人去重后一次性查出
        Set<Long> userIds = list.stream()
                .flatMap(item -> Stream.of(item.getCreator(), item.getPrincipalId(), item.getDelegateId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        CommonResult<List<AdminUserRespDTO>> userResult = adminUserApi.getUserList(userIds);
        if (userResult.isSuccess()) {
            Map<Long, AdminUserRespDTO> userMap = userResult.getData().stream()
                    .collect(Collectors.toMap(AdminUserRespDTO::getId, user -> user));
            return userMap;
        }else{
            throw exception(ErrorCodeConstants.USER_API_CALL_FAILED);
        }
    }

    private ConfigStore buildDynamicCondition(BpmDelegationPageReqVO reqVO, String userId) {
        DefaultConfigStore condition = new DefaultConfigStore();

        // 设置分页参数
        fillPageNavi(condition, reqVO.getPageNo(), reqVO.getPageSize());

        // 填充条件
        fillCondition(condition, reqVO,userId);

        return condition;
    }
    private void fillPageNavi(ConfigStore condition, Integer pageNo, Integer pageSize) {
        // 设置分页参数
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(pageNo);
        navi.setPageRows(pageSize);
        condition.setPageNavi(navi);
    }
    private void fillCondition(ConfigStore condition, BpmDelegationPageReqVO queryVO,String userId) {
        condition.and(Compare.EQUAL, "t.app_id", queryVO.getAppId());
        ConfigStore userIdConfig = new DefaultConfigStore();
        userIdConfig.or(Compare.EQUAL, "t.principal_id", userId);
        userIdConfig.or(Compare.EQUAL, "t.delegate_id", userId);
        condition.and(userIdConfig);
        // 动态添加其他查询条件
        if (StringUtils.isNotBlank(queryVO.getDelegatePersonName())) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, "t1.nickname", queryVO.getDelegatePersonName());
            orCondition.or(Compare.LIKE, "t2.nickname", queryVO.getDelegatePersonName());
            condition.and(orCondition);
        }
        if (StringUtils.isNotBlank(queryVO.getDelegateStatus())) {
            String status = queryVO.getDelegateStatus();
            LocalDateTime now = LocalDateTime.now();

            ConfigStore statusCondition = new DefaultConfigStore();
            if (BpmDelegationStatus.INACTIVE.getCode().equals(status)) {
                // 待生效：当前时间 < 代理生效时间
                statusCondition.and(Compare.GREAT_EQUAL, "t.start_time", now);
            } else if (BpmDelegationStatus.ACTIVE.getCode().equals(status)) {
                // 代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
                statusCondition.and(Compare.GREAT_EQUAL, "t.start_time", now);
                statusCondition.and(Compare.LESS_EQUAL, "t.end_time", now);
            } else if (BpmDelegationStatus.EXPIRED.getCode().equals(status)) {
                // 已失效：当前时间 > 代理失效时间
                statusCondition.and(Compare.GREAT, "t.end_time", now);
            } else if (BpmDelegationStatus.REVOKED.getCode().equals(status)) {
                // 已撤销：撤销时间不为空
                statusCondition.and(Compare.NOT_NULL, "t.revoked_time");
            }
            condition.and(statusCondition);
        }
        condition.order("t.create_time", "desc");
    }

}
