package com.cmsr.onebase.module.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.dal.database.OAuth2AccessTokenDataRepository;
import com.cmsr.onebase.module.system.dal.database.OAuth2RefreshTokenDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2AccessTokenPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserSimpleRespVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * OAuth2.0 Token Service 实现类
 *
 */
@Service
public class OAuth2UserServiceImpl implements OAuth2UserService {

    @Resource
    private OAuth2TokenService oauth2TokenService ;

    @Resource
    private UserService userService ;

    @Override
    public UserSimpleRespVO getUserInfoByToken(String accessToken) {
        OAuth2AccessTokenDO token = oauth2TokenService.getAccessToken(accessToken);
        if (token == null) {
            throw exception(GlobalErrorCodeConstants.UNAUTHORIZED);
        }
        AdminUserDO user = userService.getUser(token.getUserId());
        return BeanUtils.toBean(user, UserSimpleRespVO.class);
    }
}
