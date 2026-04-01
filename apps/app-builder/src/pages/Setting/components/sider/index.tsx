import corpSVG from '@/assets/images/building-line.svg';
import corpActiveSVG from '@/assets/images/building-line_active.svg';
import externalUserSVG from '@/assets/images/external_user.svg';
import externalUserActiveSVG from '@/assets/images/external_user_active.svg';
import dictSVG from '@/assets/images/file.svg';
import dictActiveSVG from '@/assets/images/file_active.svg';
import organizationSVG from '@/assets/images/organization-chart.svg';
import organizationActiveSVG from '@/assets/images/organization-chart_active.svg';
import plugSVG from '@/assets/images/plug.svg';
import plugActiveSVG from '@/assets/images/plug_active.svg';
import securitySVG from '@/assets/images/security.svg';
import securityActiveSVG from '@/assets/images/security_active.svg';
import tenantInfoSVG from '@/assets/images/space-ship-line.svg';
import tenantInfoActiveSVG from '@/assets/images/space-ship-line_active.svg';
import appLicationManageSVG from '@/assets/images/terminal-window-line.svg';
import appLicationManageActiveSVG from '@/assets/images/terminal-window-line_active.svg';
import userSVG from '@/assets/images/user-group.svg';
import userActiveSVG from '@/assets/images/user-group_active.svg';
import roleSVG from '@/assets/images/user.svg';
import roleActiveSVG from '@/assets/images/user_active.svg';
import userInfoSVG from '@/assets/images/userInfo.svg';
import userInfoActiveSVG from '@/assets/images/userInfo_active.svg';
import copilotdocSVG from '@/assets/images/aidoc_line2.svg';
import copilotdocActiveSVG from '@/assets/images/aidoc_line_active2.svg';
import wxminiSVG from '@/assets/images/wxmini_line2.svg';
import wxminiActiveSVG from '@/assets/images/wxmini_line_active2.svg';
import agentSVG from '@/assets/images/agent.svg';
import agentActiveSVG from '@/assets/images/agent_active.svg';
import { userPermissionSignal } from '@/store/singals/user_permission';
import { Button, Layout, Menu, Message } from '@arco-design/web-react';
import { IconMenuFold, IconMenuUnfold } from '@arco-design/web-react/icon';
import { hasMenu, TENANT_MENUS } from '@onebase/common';
import { oauthAuthorize } from '@onebase/platform-center';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import type { MenuItemType } from './menuData';
import styles from './sider.module.less';
import VerticalMenuItem from './VerticalMenuItem';
import verticalStyles from './VerticalSider.module.less';

const { Sider } = Layout;
const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;

interface SiderProps {
  className?: string;
  collapsed?: boolean;
  onCollapse?: (collapsed: boolean) => void;
}

const AppSider: React.FC<SiderProps> = ({ className, collapsed = false, onCollapse }) => {
  useSignals();

  const { permissionInfo } = userPermissionSignal;
  const permissionReady = !!permissionInfo.value;
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  const isArtifexDomain = useMemo(() => {
    return window.location.hostname.includes('artifex-cmcc');
  }, []);

  const { tenantId } = useParams();

  const menuConfig: MenuItemType[] = [
    {
      key: 'application',
      title: '应用管理',
      icon: <img src={appLicationManageSVG} />,
      iconActive: <img src={appLicationManageActiveSVG} />,
      iconClass: 'icon-xiangmukongjian1',
      iconActiveClass: 'icon-yingyongguanli-xuanzhong',
      path: `/onebase/${tenantId}/setting/application`,
      permissionKey: TENANT_MENUS.APP
    },
    {
      key: 'user',
      title: '用户管理',
      icon: <img src={userSVG} />,
      iconActive: <img src={userActiveSVG} />,
      iconClass: 'icon-yonghuguanli1',
      iconActiveClass: 'icon-yonghuguanli-xuanzhong1',
      path: `/onebase/${tenantId}/setting/user`,
      permissionKey: TENANT_MENUS.USER
    },
    {
      key: 'role',
      title: '角色管理',
      icon: <img src={roleSVG} />,
      iconActive: <img src={roleActiveSVG} />,
      iconClass: 'icon-jiaoseguanli1',
      iconActiveClass: 'icon-jiaoseguanli-xuanzhong',
      path: `/onebase/${tenantId}/setting/role`,
      permissionKey: TENANT_MENUS.ROLE
    },
    {
      key: 'organization',
      title: '组织管理',
      icon: <img src={organizationSVG} />,
      iconActive: <img src={organizationActiveSVG} />,
      iconClass: 'icon-zuzhiguanli',
      iconActiveClass: 'icon-zuzhiguanli-xuanzhong',
      path: `/onebase/${tenantId}/setting/organization`,
      permissionKey: TENANT_MENUS.DEPT
    },
    {
      key: 'spaceInfo',
      title: '空间信息',
      icon: <img src={tenantInfoSVG} />,
      iconActive: <img src={tenantInfoActiveSVG} />,
      iconClass: 'icon-kongjianxinxi',
      iconActiveClass: 'icon-kongjianxinxi-xuanzhong',
      path: `/onebase/${tenantId}/setting/spaceInfo`,
      permissionKey: TENANT_MENUS.INFO
    },
    {
      key: 'system-dict',
      title: '数据字典管理',
      icon: <img src={dictSVG} />,
      iconActive: <img src={dictActiveSVG} />,
      iconClass: 'icon-a-shujuzidianguanli',
      iconActiveClass: 'icon-a-shujuzidianguanli-xuanzhong',
      path: `/onebase/${tenantId}/setting/system-dict`,
      permissionKey: TENANT_MENUS.DICT
    },
    {
      key: 'security',
      title: '安全设置',
      icon: <img src={securitySVG} />,
      iconActive: <img src={securityActiveSVG} />,
      iconClass: 'icon-anquanshezhi',
      iconActiveClass: 'icon-anquanshezhi-xuanzhong',
      path: `/onebase/${tenantId}/setting/security`,
      permissionKey: TENANT_MENUS.SECURITY
    },
    {
      key: 'profile',
      title: '个人中心',
      icon: <img src={userInfoSVG} />,
      iconActive: <img src={userInfoActiveSVG} />,
      iconClass: 'icon-gerenzhongxin',
      iconActiveClass: 'icon-gerenzhongxin-xuanzhong',
      path: `/onebase/${tenantId}/setting/profile`,
      permissionKey: TENANT_MENUS.PROFILE
    },
    {
      key: 'plugin',
      title: '插件管理',
      icon: <img src={plugSVG} />,
      iconActive: <img src={plugActiveSVG} />,
      iconClass: 'icon-chajianguanli',
      iconActiveClass: 'icon-chajianguanli-xuanzhong',
      path: `/onebase/${tenantId}/setting/plugin`,
      permissionKey: TENANT_MENUS.PLUGIN
    },
    {
      key: 'externalUser',
      title: '外部用户',
      icon: <img src={externalUserSVG} />,
      iconActive: <img src={externalUserActiveSVG} />,
      iconClass: 'icon-yonghuguanli',
      iconActiveClass: 'icon-yonghuguanli-xuanzhong',
      path: `/onebase/${tenantId}/setting/externalUser`,
      permissionKey: TENANT_MENUS.THIRD
    },
    {
      key: 'enterprise',
      title: '企业管理',
      icon: <img src={corpSVG} />,
      iconActive: <img src={corpActiveSVG} />,
      iconClass: 'icon-qiyeguanli',
      iconActiveClass: 'icon-qiyeguanli-xuanzhong',
      path: `/onebase/${tenantId}/setting/enterprise`,
      permissionKey: TENANT_MENUS.CORP
    },
    {
      key: 'agent',
      title: '智能体管理',
      icon: <img src={agentSVG} />,
      iconActive: <img src={agentActiveSVG} />,
      iconClass: 'icon-zhinengtiguanli',
      iconActiveClass: 'icon-zhinengtiguanli-xuanzhong',
      path: `/onebase/${tenantId}/setting/agent`
    },
    {
      key: 'copilotdoc',
      title: 'AI生成文档',
      icon: <img src={copilotdocSVG} />,
      iconActive: <img src={copilotdocActiveSVG} />,
      iconClass: 'icon-a-AIshengchengwendang',
      iconActiveClass: 'icon-AIshengchengwendang-xuanzhong',
      path: `/onebase/${tenantId}/setting/copilotdoc`
    },
    {
      key: 'wxmini',
      title: 'AI生成小程序',
      icon: <img src={wxminiSVG} />,
      iconActive: <img src={wxminiActiveSVG} />,
      iconClass: 'icon-AIshengchengxiaochengxu',
      iconActiveClass: 'icon-AIshengchengxiaochengxu-xuanzhong',
      path: `/onebase/${tenantId}/setting/wxmini`
    }
  ];

  const defaultSelectedKeys = () => {
    const defaultSelectedKeys: string[] = [];
    menuConfig.map((item) => {
      defaultSelectedKeys.push(item.key);
    });
    return defaultSelectedKeys;
  };

  const platMenuData = () => {
    const result: MenuItemType[] = [];
    menuConfig?.forEach((menu) => {
      result.push(menu);
    });
    return result;
  };

  const finalMenuItems = useMemo(() => {
    return platMenuData();
  }, [menuConfig]);

  const findSelectedKeys = React.useCallback((items: MenuItemType[], path: string): string[] => {
    for (const item of items) {
      if (path === item.path || path.startsWith(item.path + '/')) {
        return [item.key];
      }
      if (item.children) {
        const childKeys = findSelectedKeys(item.children, path);
        if (childKeys.length > 0) {
          return [item.key, ...childKeys];
        }
      }
    }
    return [];
  }, []);

  useEffect(() => {
    const keys = findSelectedKeys(finalMenuItems, location.pathname);
    setSelectedKeys(keys);
  }, [location.pathname, findSelectedKeys]);

  const handleMenuClick = async (key: string) => {
    const findPathByKey = (items: MenuItemType[], targetKey: string): string | null => {
      for (const item of items) {
        if (item.key === targetKey) {
          return item.path || null;
        }
        if (item.children) {
          const path = findPathByKey(item.children, targetKey);
          if (path) return path;
        }
      }
      return null;
    };

    const path = findPathByKey(finalMenuItems, key.replace('.$', ''));
    
    if (key === 'agent') {
      try {
        const authorizeRes = await oauthAuthorize({
          client_id: 'aitool',
          scope: '',
          redirect_uri: 'http://10.0.13.16:29500/bote/manager/',
          response_type: 'code',
          auto_approve: true
        });

        if (authorizeRes.code) {
          const callbackUrl = `http://bote.sit.artifex-cmcc.com.cn/bote/api/bote/oauth2/callback?systemCode=onebase&redirect=http://bote.sit.artifex-cmcc.com.cn/bote/manager/%23/&code=${authorizeRes.code}`;
          window.open(callbackUrl, '_blank');
        } else {
          Message.error('获取授权码失败');
        }
      } catch (error) {
        console.error('智能体管理调用失败:', error);
        Message.error('智能体管理调用失败');
      }
    } else if (path) {
      navigate(path);
    }
  };

  const handleCollapseClick = useCallback(() => {
    if (onCollapse) {
      onCollapse(!collapsed);
    }
  }, [onCollapse, collapsed]);

  const renderMenuItems = React.useCallback(
    (items: MenuItemType[]): React.ReactNode => {
      return items
        .map((item) => {
          const permissionKey = item.permissionKey;
          if (permissionReady && permissionKey && !hasMenu(permissionKey as any)) return null;

          if (item.children && item.children.length === 0) {
            return null;
          }

          if (item.children && item.children.length > 0) {
            const childrenNodes = renderMenuItems(item.children) as React.ReactNode[];
            const hasChildren = Array.isArray(childrenNodes) && childrenNodes.filter(Boolean).length > 0;
            if (!hasChildren && !item.path) return null;
            return (
              <SubMenu
                key={item.key}
                className={styles.subMenuWrapper}
                title={
                  <span>
                    {item.icon}
                    <span>{item.title}</span>
                  </span>
                }
              >
                {childrenNodes}
              </SubMenu>
            );
          }

          return (
            <MenuItem
              key={item.key}
              disabled={item.disabled}
              className={styles.menuItemWrapper}
              style={collapsed ? { padding: '0 12px' } : { display: 'flex', alignItems: 'center' }}
            >
              <div className={styles.menuItemContent}>
                {selectedKeys.includes(item.key) ? item.iconActive : item.icon}
                <span className={styles.menuTitle}>{item.title}</span>
              </div>
            </MenuItem>
          );
        })
        .filter(Boolean);
    },
    [collapsed, permissionReady, selectedKeys]
  );

  const renderVerticalMenuItems = React.useCallback(
    (items: MenuItemType[]): React.ReactNode => {
      return items
        .map((item, index) => {
          const permissionKey = item.permissionKey;
          if (permissionReady && permissionKey && !hasMenu(permissionKey as any)) return null;

          if (item.children && item.children.length === 0) {
            return null;
          }

          const isActive = selectedKeys.includes(item.key);
          const iconClass = isActive && item.iconActiveClass ? item.iconActiveClass : item.iconClass;
          const isLast = index === items.length - 1;

          return (
            <VerticalMenuItem
              key={item.key}
              iconClass={iconClass}
              title={item.title}
              active={isActive}
              onClick={() => handleMenuClick(item.key)}
              showDivider={false}
            />
          );
        })
        .filter(Boolean);
    },
    [permissionReady, selectedKeys, handleMenuClick]
  );

  const defaultKeys = defaultSelectedKeys();

  const renderContent = () => {
    return (
      <Menu
        mode="vertical"
        autoOpen={true}
        selectedKeys={selectedKeys}
        defaultSelectedKeys={defaultKeys}
        onClickMenuItem={handleMenuClick}
      >
        {renderMenuItems(menuConfig)}
      </Menu>
    );
  };

  const renderVerticalContent = () => {
    return (
      <div className={verticalStyles.verticalMenuContainer}>
        {renderVerticalMenuItems(menuConfig)}
      </div>
    );
  };

  if (!isArtifexDomain) {
    return (
      <Sider
        className={`${styles.sider} ${className || ''}`}
        collapsed={collapsed}
        onCollapse={onCollapse}
        trigger={null}
        width={240}
      >
        <div className={styles.siderContent}>
          <div className={styles.sliderTitle}>AI+零代码开发平台</div>
          <div className={styles.menuContainer}>{renderContent()}</div>

          <div className={styles.collapseButtonContainer}>
            <Button
              type="text"
              icon={collapsed ? <IconMenuUnfold /> : <IconMenuFold />}
              onClick={handleCollapseClick}
              className={styles.collapseButton}
            />
          </div>
        </div>
      </Sider>
    );
  }

  return (
    <div className={`${verticalStyles.verticalSider} ${className || ''}`}>
      <div className={verticalStyles.verticalSiderContent}>
        <div className={verticalStyles.verticalMenuWrapper}>
          {renderVerticalContent()}
        </div>
      </div>
    </div>
  );
};

export default AppSider;
