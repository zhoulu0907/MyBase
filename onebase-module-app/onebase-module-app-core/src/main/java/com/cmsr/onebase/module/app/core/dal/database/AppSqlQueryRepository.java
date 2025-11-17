package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dto.auth.RoleMemberDTO;
import com.cmsr.onebase.module.app.core.vo.app.AppUserPhotoDTO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:58
 */
@Repository
public class AppSqlQueryRepository {

    @Autowired
    private AnylineService<?> anylineService;

    public PageResult<RoleMemberDTO> findRoleMembers(Long roleId, String memberName, String memberType, PageParam pageParam) {
        ConfigStore configs = new DefaultConfigStore();
        configs.param("roleId", roleId);
        configs.param("memberName", memberName);
        configs.order("update_time", Order.TYPE.DESC);
        configs.page(pageParam.getPageNo(), pageParam.getPageSize());
        String sql;
        if (StringUtils.equals(memberType, RoleMemberDTO.MEMBER_TYPE_USER)) {
            sql = """
                    select
                        aaru.id,
                        aaru.user_id AS member_id,
                        aaru.update_time,
                        -1 AS is_include_child,
                        d.name AS dept_name,
                        u.nickname AS member_name,
                        'user' AS member_type
                    from
                        app_auth_role_user aaru
                    left join system_users u on aaru.user_id = u.id and u.deleted = 0
                    left join system_dept d on u.dept_id = d.id
                    where
                        aaru.role_id = #{roleId}
                        ${and u.nickname like '%'|| :memberName ||'%'}
                        and aaru.deleted = 0
                    """;
        } else if (StringUtils.equals(memberType, RoleMemberDTO.MEMBER_TYPE_DEPT)) {
            sql = """
                    select
                        aard.id,
                        aard.dept_id as member_id,
                        aard.update_time,
                        aard.is_include_child,
                        d.name as dept_name,
                        d.name as member_name,
                        'dept' as member_type
                    from
                        app_auth_role_dept aard
                    left join system_dept d on aard.dept_id = d.id
                    where
                        aard.role_id = #{roleid}
                        ${and d.name like '%'|| :membername ||'%'}
                        and aard.deleted = 0
                        and (d.id is null or d.deleted = 0)
                    """;
        } else {
            sql = """
                    select * from (
                    select
                        aaru.id,
                        aaru.user_id AS member_id,
                        aaru.update_time,
                        -1 AS is_include_child,
                        d.name AS dept_name,
                        u.nickname AS member_name,
                        'user' AS member_type
                    from
                        app_auth_role_user aaru
                    left join system_users u on aaru.user_id = u.id and u.deleted = 0
                    left join system_dept d on u.dept_id = d.id
                    where
                        aaru.role_id = #{roleId}
                        ${and u.nickname like '%'|| :memberName ||'%'}
                        and aaru.deleted = 0
                    union all
                    select
                        aard.id,
                        aard.dept_id as member_id,
                        aard.update_time,
                        aard.is_include_child,
                        d.name as dept_name,
                        d.name as member_name,
                        'dept' as member_type
                    from
                        app_auth_role_dept aard
                    left join system_dept d on aard.dept_id = d.id
                    where
                        aard.role_id = #{roleid}
                        ${and d.name like '%'|| :membername ||'%'}
                        and aard.deleted = 0
                        and (d.id is null or d.deleted = 0)
                    ) as combined_result
                    """;
        }
        DataSet dataSet = anylineService.querys(sql, configs);
        List<RoleMemberDTO> roleMemberDTOS = dataSet.stream().map(row -> {
            RoleMemberDTO roleMemberDTO = new RoleMemberDTO();
            roleMemberDTO.setId(row.getLong("id"));
            roleMemberDTO.setMemberId(row.getLong("member_id"));
            roleMemberDTO.setMemberName(row.getString("member_name"));
            roleMemberDTO.setDeptName(row.getString("dept_name"));
            roleMemberDTO.setMemberType(row.getString("member_type"));
            roleMemberDTO.setIsIncludeChild(row.getInt("is_include_child"));
            return roleMemberDTO;
        }).toList();
        return new PageResult(roleMemberDTOS, dataSet.total());
    }

    public List<Long> findDeptHierarchyByUserId(Long userId) {
        List<Long> result = new ArrayList<>();
        Long currentDeptId = null;
        {
            ConfigStore configs = new DefaultConfigStore();
            configs.param("userId", userId);
            String sql = """
                    select
                    	dept_id
                    from
                    	system_users
                    where
                    	deleted = 0 and id = #{userId}
                    """;
            DataSet dataSet = anylineService.querys(sql, configs);
            for (DataRow dataRow : dataSet) {
                currentDeptId = dataRow.getLong("dept_id");
                if (currentDeptId != null) {
                    result.add(currentDeptId);
                }
            }
        }
        int loopCount = 0;
        while (currentDeptId != null && currentDeptId > 0) {
            ConfigStore configs = new DefaultConfigStore();
            configs.param("deptId", currentDeptId);
            String sql = """
                    select
                    	id,
                    	parent_id
                    from
                    	system_dept
                    where
                    	deleted = 0 and id = #{deptId}
                    """;
            DataSet dataSet = anylineService.querys(sql, configs);
            for (DataRow dataRow : dataSet) {
                currentDeptId = dataRow.getLong("parent_id");
                if (currentDeptId != null && currentDeptId > 0) {
                    result.add(currentDeptId);
                }
            }
            loopCount++;
            if (loopCount > 100) {
                throw new RuntimeException("findDeptHierarchyByUserId 部门层级过深");
            }
        }
        return result;
    }

    public List<Long> findAllUserIdsByDeptIds(Long deptId, Integer isIncludeChild) {
        if (deptId == null || deptId <= 0) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();
        {
            List<Long> ids = findAllUserIdsByDeptIds(List.of(deptId));
            result.addAll(ids);
        }
        if (isIncludeChild != null && isIncludeChild == 1) {
            List<Long> deptIds = List.of(deptId);
            int loopCount = 0;
            while (CollectionUtils.isNotEmpty(deptIds)) {
                String sql = """
                        select id from system_dept where parent_id in (#{deptIds})
                        """;
                ConfigStore configs = new DefaultConfigStore();
                configs.param("deptIds", deptIds);
                DataSet dataSet = anylineService.querys(sql, configs);
                deptIds = new ArrayList<>();
                for (DataRow dataRow : dataSet) {
                    Long id = dataRow.getLong("id");
                    if (id != null && id > 0) {
                        deptIds.add(id);
                    }
                }
                List<Long> ids = findAllUserIdsByDeptIds(deptIds);
                result.addAll(ids);
                //
                loopCount++;
                if (loopCount > 100) {
                    throw new RuntimeException("findAllUserIdsByDeptIds部门层级过深");
                }
            }
        }
        return result;
    }

    private List<Long> findAllUserIdsByDeptIds(List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();
        ConfigStore configs = new DefaultConfigStore();
        configs.param("deptIds", deptIds);
        String sql = """
                select id from system_users where deleted = 0 and dept_id in (#{deptIds})
                """;
        DataSet dataSet = anylineService.querys(sql, configs);
        for (DataRow dataRow : dataSet) {
            result.add(dataRow.getLong("id"));
        }
        return result;
    }

    public Map<Long, List<AppUserPhotoDTO>> findUserPhotoList(List<Long> appIds) {
        if (appIds == null || appIds.isEmpty()) {
            return new HashMap<>();
        }
        ConfigStore configs = new DefaultConfigStore();
        configs.param("appIds", appIds);
        String sql = """
                 select
                    su.id,
                    su.avatar,
                    su.nickName,
                    r.application_id
                from
                    app_auth_role_user ru
                left join app_auth_role r on
                    r.id = ru.role_id
                    and role_type = 1
                    and ru.deleted = 0
                    and r.deleted = 0
                left join system_users su on
                    su.id = ru.user_id
                    and su.deleted = 0
                where
                    r.application_id in (#{appIds})
                    and su.id is not null
                """;

        DataSet dataSet = anylineService.querys(sql, configs);

        // 处理结果集，按application_id分组
        return dataSet.stream().map(row -> {
            AppUserPhotoDTO appPhotoDTO = new AppUserPhotoDTO();
            appPhotoDTO.setApplicationId(row.getLong("application_id"));
            appPhotoDTO.setId(String.valueOf(row.get("id")));
            appPhotoDTO.setAvatar((String) row.get("avatar"));
            appPhotoDTO.setNickName((String) row.get("nickName"));
            return appPhotoDTO;
        }).collect(Collectors.groupingBy(AppUserPhotoDTO::getApplicationId, Collectors.toList()));
    }
}
