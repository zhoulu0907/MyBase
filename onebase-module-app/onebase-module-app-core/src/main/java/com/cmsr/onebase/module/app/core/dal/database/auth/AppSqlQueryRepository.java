package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dto.auth.UserMemberDTO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/4 14:58
 */
@Repository
public class AppSqlQueryRepository {

    @Autowired
    private AnylineService<?> anylineService;

    public PageResult<UserMemberDTO> findUserMemberDTOByRoleId(Long roleId, String memberName, PageParam pageParam) {
        ConfigStore configs = new DefaultConfigStore();
        configs.param("roleId", roleId);
        configs.param("memberName", memberName);
        configs.order("update_time", Order.TYPE.DESC);
        configs.page(pageParam.getPageNo(), pageParam.getPageSize());
        String sql = """
                select * from (
                select
                	aaru.id,
                	aaru.role_id as member_id,
                	aaru.update_time,
                	-1 as is_include_child,
                	d.name as dept_name,
                	u.nickname as member_name,
                	'user' as member_type
                from
                	app_auth_role_user aaru,
                	system_users u,
                	system_dept d
                where
                	aaru.user_id = u.id
                	and u.dept_id = d.id
                	and aaru.role_id = #{roleId}
                	${and u.nickname like '%'|| :memberName ||'%'}
                	and aaru.deleted = 0 and u.deleted = 0
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
                	app_auth_role_dept aard,
                	system_dept d
                where
                	aard.dept_id = d.id
                	and aard.role_id = #{roleId}
                	${and d.name like '%'|| :memberName ||'%'}
                	and aard.deleted = 0 and d.deleted = 0
                ) as combined_result
                """;
        DataSet dataSet = anylineService.querys(sql, configs);
        List<UserMemberDTO> userMemberDTOS = dataSet.stream().map(row -> {
            UserMemberDTO userMemberDTO = new UserMemberDTO();
            userMemberDTO.setId(row.getLong("id"));
            userMemberDTO.setMemberId(row.getLong("member_id"));
            userMemberDTO.setMemberName(row.getString("member_name"));
            userMemberDTO.setDeptName(row.getString("dept_name"));
            userMemberDTO.setMemberType(row.getString("member_type"));
            userMemberDTO.setIsIncludeChild(row.getInt("is_include_child"));
            return userMemberDTO;
        }).toList();
        return new PageResult(userMemberDTOS, dataSet.total());
    }
}
