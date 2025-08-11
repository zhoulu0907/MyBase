package com.cmsr.onebase.module.system.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.social.dto.SocialUserBindReqDTO;
import com.cmsr.onebase.module.system.api.social.dto.SocialUserRespDTO;
import com.cmsr.onebase.module.system.controller.admin.socail.vo.user.SocialUserPageReqVO;
import com.cmsr.onebase.module.system.dal.database.SocialUserBindDataRepository;
import com.cmsr.onebase.module.system.dal.database.SocialUserDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.social.SocialUserBindDO;
import com.cmsr.onebase.module.system.dal.dataobject.social.SocialUserDO;
import com.cmsr.onebase.module.system.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.system.enums.social.SocialTypeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;
import static com.cmsr.onebase.framework.common.util.json.JsonUtils.toJsonString;

@Service
@Validated
@Slf4j
public class SocialUserServiceImpl implements SocialUserService {

    @Resource
    private SocialUserDataRepository socialUserDataRepository;

    @Resource
    private SocialUserBindDataRepository socialUserBindDataRepository;

    @Resource
    private SocialClientService socialClientService;

    @Override
    public List<SocialUserDO> getSocialUserList(Long userId, Integer userType) {
        // 获得绑定
        List<SocialUserBindDO> socialUserBinds = socialUserBindDataRepository.findListByUserIdAndUserType(userId, userType);
        if (CollUtil.isEmpty(socialUserBinds)) {
            return Collections.emptyList();
        }
        return socialUserDataRepository.findAllByIds(convertSet(socialUserBinds, SocialUserBindDO::getSocialUserId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String bindSocialUser(SocialUserBindReqDTO reqDTO) {
        // 获得社交用户
        SocialUserDO socialUser = authSocialUser(reqDTO.getSocialType(), reqDTO.getUserType(),
                reqDTO.getCode(), reqDTO.getState());
        Assert.notNull(socialUser, "社交用户不能为空");

        // 解绑旧关系
        socialUserBindDataRepository.deleteByUserTypeAndSocialUserId(reqDTO.getUserType(), socialUser.getId());
        socialUserBindDataRepository.deleteByUserTypeAndUserIdAndSocialType(reqDTO.getUserType(), reqDTO.getUserId(), socialUser.getType());

        // 插入新绑定
        SocialUserBindDO bind = SocialUserBindDO.builder()
            .userId(reqDTO.getUserId()).userType(reqDTO.getUserType())
            .socialUserId(socialUser.getId()).socialType(socialUser.getType())
            .build();
        socialUserBindDataRepository.insert(bind);
        return socialUser.getOpenid();
    }

    @Override
    public void unbindSocialUser(Long userId, Integer userType, Integer socialType, String openid) {
        // 获得 openid 对应的 SocialUserDO 社交用户
        SocialUserDO socialUser = socialUserDataRepository.findByTypeAndOpenid(socialType, openid);
        if (socialUser == null) {
            throw exception(ErrorCodeConstants.SOCIAL_USER_NOT_FOUND);
        }
        socialUserBindDataRepository.deleteByUserTypeAndUserIdAndSocialType(userType, userId, socialUser.getType());
    }

    @Override
    public SocialUserRespDTO getSocialUserByUserId(Integer userType, Long userId, Integer socialType) {
        // 获得绑定用户
        SocialUserBindDO bind = socialUserBindDataRepository.findByUserIdAndUserTypeAndSocialType(userId, userType, socialType);
        if (bind == null) {
            return null;
        }
        SocialUserDO user = socialUserDataRepository.findById(bind.getSocialUserId());
        Assert.notNull(user, "社交用户不能为空");
        return new SocialUserRespDTO(user.getOpenid(), user.getNickname(), user.getAvatar(), bind.getUserId());
    }

    @Override
    public SocialUserRespDTO getSocialUserByCode(Integer userType, Integer socialType, String code, String state) {
        // 获得社交用户
        SocialUserDO socialUser = authSocialUser(socialType, userType, code, state);
        Assert.notNull(socialUser, "社交用户不能为空");

        SocialUserBindDO bind = socialUserBindDataRepository.findOne(new DefaultConfigStore().and("user_type", userType)
                                        .and("social_user_id", socialUser.getId()));
        return new SocialUserRespDTO(
                socialUser.getOpenid(), socialUser.getNickname(), socialUser.getAvatar(),
                bind != null ? bind.getUserId() : null);
    }

    /**
     * 授权获得对应的社交用户
     * 如果授权失败，则会抛出 {@link ServiceException} 异常
     *
     * @param socialType 社交平台的类型 {@link SocialTypeEnum}
     * @param userType 用户类型
     * @param code     授权码
     * @param state    state
     * @return 授权用户
     */
    @NotNull
    public SocialUserDO authSocialUser(Integer socialType, Integer userType, String code, String state) {
        // 先查 DB
        SocialUserDO socialUser = socialUserDataRepository.findByTypeAndCodeAndState(socialType, code, state);
        if (socialUser != null) {
            return socialUser;
        }

        // 调用三方
        AuthUser authUser = socialClientService.getAuthUser(socialType, userType, code, state);
        Assert.notNull(authUser, "三方用户不能为空");

        // DB 再查
        socialUser = socialUserDataRepository.findByTypeAndOpenid(socialType, authUser.getUuid());
        if (socialUser == null) {
            socialUser = new SocialUserDO();
        }
        socialUser.setType(socialType).setCode(code).setState(state)
                  .setOpenid(authUser.getUuid())
                  .setToken(authUser.getToken().getAccessToken())
                  .setRawTokenInfo(toJsonString(authUser.getToken()))
                  .setNickname(authUser.getNickname())
                  .setAvatar(authUser.getAvatar())
                  .setRawUserInfo(toJsonString(authUser.getRawUserInfo()));

        if (socialUser.getId() == null) {
            socialUserDataRepository.insert(socialUser);
        } else {
            socialUserDataRepository.update(socialUser);
        }
        return socialUser;
    }

    // ==================== 社交用户 CRUD ====================

    @Override
    public SocialUserDO getSocialUser(Long id) {
        return socialUserDataRepository.findById(id);
    }

    @Override
    public PageResult<SocialUserDO> getSocialUserPage(SocialUserPageReqVO pageReqVO) {
        return socialUserDataRepository.findPage(pageReqVO);
    }

}
