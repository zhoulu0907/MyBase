package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dto.auth.UserMemberDTO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.Order;
import org.anyline.entity.PageNavi;
import org.anyline.service.AnylineService;
import org.apache.commons.lang3.StringUtils;
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
        PageNavi page = new DefaultPageNavi(pageParam.getPageNo(), pageParam.getPageSize());
        configs.setPageNavi(page);
        configs.param("roleId", roleId);
        //configs.param("memberName", memberName);
        configs.order("update_time", Order.TYPE.DESC);
        String sql = """
                select id, member_id, member_type, update_time from (
                select
                	aaru.id,
                	aaru.role_id as member_id,
                	u.nickname as member_name,
                	'user' as member_type,
                	aaru.update_time
                from
                	app_auth_role_user aaru,
                	system_users u
                where
                	aaru.user_id = u.id
                	and aaru.role_id = #{roleId}
                	and aaru.deleted = 0 and u.deleted = 0
                union all
                select
                	aard.id,
                	aard.dept_id as member_id,
                	d.name as member_name,
                	'dept' as member_type,
                	aard.update_time
                from
                	app_auth_role_dept aard,
                	system_dept d
                where
                	aard.dept_id = d.id
                	and
                	aard.role_id = #{roleId}
                	and aard.deleted = 0 and d.deleted = 0
                ) as combined_result
                """;
        DataSet dataSet;
        if (StringUtils.isEmpty(memberName)) {
            dataSet = anylineService.querys(sql, configs);
        } else {
            //dataSet = anylineService.querys(sql, configs,"member_name like '%' || #{memberName} || '%')");
            dataSet = anylineService.querys(sql, configs);
        }
        List<UserMemberDTO> userMemberDTOS = dataSet.stream().map(row -> {
            UserMemberDTO userMemberDTO = new UserMemberDTO();
            userMemberDTO.setId(row.getLong("id"));
            userMemberDTO.setMemberId(row.getLong("member_id"));
            userMemberDTO.setMemberName(row.getString("member_name"));
            userMemberDTO.setMemberType(row.getString("member_type"));
            return userMemberDTO;
        }).toList();
        return new PageResult(userMemberDTOS, dataSet.total());
    }
}
