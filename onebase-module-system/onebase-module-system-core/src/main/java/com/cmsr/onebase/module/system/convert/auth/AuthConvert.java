package com.cmsr.onebase.module.system.convert.auth;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.consts.NumberConstant;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.cmsr.onebase.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.cmsr.onebase.module.system.api.social.dto.SocialUserBindReqDTO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.permission.MenuTypeEnum;
import com.cmsr.onebase.module.system.vo.auth.*;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.filterList;
import static com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO.ID_ROOT;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    AuthLoginRespVO convert(OAuth2AccessTokenDO bean);

    default AuthPermissionInfoRespVO convert(AdminUserDO user, List<RoleDO> roleList, List<MenuDO> menuList, String expectCode) {
        return AuthPermissionInfoRespVO.builder()
                .user(BeanUtils.toBean(user, AuthPermissionInfoRespVO.UserVO.class))
                .roles(convertSet(roleList, RoleDO::getId))
                // 权限标识信息,过滤 permission 以 expectCode 为开头的权限
                .permissions(convertSet(menuList, MenuDO::getPermission).stream().filter(
                        permission -> StringUtils.isBlank(expectCode) || permission.startsWith(expectCode)
                ).collect(Collectors.toSet()))
                // 菜单树，过滤以expectCode为开头的权限
                .menus(buildMenuTree(menuList, expectCode))
                .build();
    }

    @Mapping(source = "visible", target = "visible", qualifiedByName = "integerToBoolean")
    @Mapping(source = "keepAlive", target = "keepAlive", qualifiedByName = "integerToBoolean")
    @Mapping(source = "alwaysShow", target = "alwaysShow", qualifiedByName = "integerToBoolean")
    AuthPermissionInfoRespVO.MenuVO convertTreeNode(MenuDO menu);

    /**
     * 将菜单列表，构建成菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    default List<AuthPermissionInfoRespVO.MenuVO> buildMenuTree(List<MenuDO> menuList, String expectCode) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        // 移除按钮
        menuList.removeIf(menu -> menu.getType().equals(MenuTypeEnum.Action.getType()));
        // 排序，保证菜单的有序性
        menuList.sort(Comparator.comparing(MenuDO::getSort));

        // 构建菜单树
        // 使用 LinkedHashMap 的原因，是为了排序 。实际也可以用 Stream API ，就是太丑了。
        Map<Long, AuthPermissionInfoRespVO.MenuVO> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(), AuthConvert.INSTANCE.convertTreeNode(menu)));
        // 处理父子关系
        treeNodeMap.values().stream().filter(node -> !node.getParentId().equals(ID_ROOT)).forEach(childNode -> {
            // 获得父节点
            AuthPermissionInfoRespVO.MenuVO parentNode = treeNodeMap.get(childNode.getParentId());
            if (parentNode == null) {
                LoggerFactory.getLogger(getClass()).error("[buildRouterTree][resource({}) 找不到父资源({})]",
                        childNode.getId(), childNode.getParentId());
                return;
            }
            // 将自己添加到父节点中
            if (parentNode.getChildren() == null) {
                parentNode.setChildren(new ArrayList<>());
            }
            parentNode.getChildren().add(childNode);
        });
        // 获得到所有的根节点
        if (StringUtils.isBlank(expectCode)) {
            return filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()));
        } else {
            // 如果指定期望的顶级菜单和权限点
            return filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()) && expectCode.equals(node.getPermission()));
        }
    }

    SocialUserBindReqDTO convert(Long userId, Integer userType, AuthSocialLoginReqVO reqVO);

    SmsCodeSendReqDTO convert(AuthSmsSendReqVO reqVO);

    SmsCodeUseReqDTO convert(AuthSmsLoginReqVO reqVO, Integer scene, String usedIp);

    default AuthLoginRespVO convert(OAuth2AccessTokenDO accessTokenDO, TenantDO tennantDO) {
        AuthLoginRespVO respVO = AuthConvert.INSTANCE.convert(accessTokenDO);
        respVO.setTenantId(tennantDO.getId());
        respVO.setTenantWebsite(tennantDO.getWebsite());
        return respVO;
    }

    @Named("integerToBoolean")
    default Boolean integerToBoolean(Integer value) {
        return value != null && value != NumberConstant.ZERO;
    }
}