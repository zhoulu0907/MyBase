import helpSVG from "@/assets/images/help_icon.svg";
import { UserPermissionManager } from "@/utils/permission";
import {
    Avatar,
    Button,
    Dropdown,
    Layout,
    Menu,
    Tabs,
} from "@arco-design/web-react";
import { IconMenu, IconPoweroff, IconUser } from "@arco-design/web-react/icon";
import { TokenManager } from "@onebase/common";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "./header.module.less";

const { Header } = Layout;

interface HeaderProps {
    className?: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const { t } = useTranslation();

    // Tab 切换
    // 根据当前路径设置 activeTab
    const getTabKeyFromPath = (pathname: string) => {
        if (pathname.includes('onebase/create-app/page-manager'))
            return 'page-manager';
        if (pathname.includes('onebase/create-app/integrated-management'))
            return 'integrated-management';
        if (pathname.includes('onebase/create-app/data-factory'))
            return 'data-factory';
        if (pathname.includes('onebase/create-app/app-setting'))
            return 'app-setting';
        if (pathname.includes('onebase/create-app/app-release'))
            return 'app-release';
        return 'page-manager';
    };
    const [activeTab, setActiveTab] = useState(() =>
        getTabKeyFromPath(location.pathname)
    );

    useEffect(() => {
        setActiveTab(getTabKeyFromPath(location.pathname));
    }, [location.pathname]);

    // 获取用户信息
    const tokenInfo = TokenManager.getTokenInfo();

    useEffect(() => {
        console.log(tokenInfo);
    }, [tokenInfo]);

    // 登出处理
    const handleLogout = () => {
        // 清除 token
        TokenManager.clearToken();
        // 跳转到登录页
        navigate('/login');
    };

    // 用户菜单
    const userMenu = (
        <Menu>
            <Menu.Item key='profile'>
                <IconUser />
                {t('header.profile')}
            </Menu.Item>
            <Menu.Item key='logout' onClick={handleLogout}>
                <IconPoweroff />
                {t('header.logout')}
            </Menu.Item>
        </Menu>
    );

    return (
        <Header className={`${styles.header} ${className || ""}`}>
            <div className={styles.headerContent}>
                <div className={styles.appInfo}>
                    <Button
                        shape="square"
                        icon={<IconMenu />}
                        onClick={() => {
                            navigate("/onebase/my-app");
                        }}
                        className={styles.menuIcon}
                    />

                    <Button
                        iconOnly
                        shape='square'
                        icon={<IconUser />}
                        style={{ backgroundColor: '#E0A951' }}
                    />
                    <div className={styles.appName}>未命名应用</div>
                    <Button type='text' style={{ background: '#eaf0fd' }}>
                        {t('header.developing')}
                    </Button>
                </div>

                <Tabs
                    type="line"
                    activeTab={activeTab}
                    onChange={(key) => {
                        setActiveTab(key);
                        switch (key) {
                            case "page-manager":
                                navigate("/onebase/create-app/page-manager");
                                break;
                            case "integrated-management":
                                navigate(
                                    "/onebase/create-app/integrated-management"
                                );
                                break;
                            case "data-factory":
                                navigate("/onebase/create-app/data-factory");
                                break;
                            case "app-setting":
                                navigate("/onebase/create-app/app-setting");
                                break;
                            case "app-release":
                                navigate("/onebase/create-app/app-release");
                                break;
                            default:
                                break;
                        }
                    }}
                    size="large"
                >
                    <Tabs.TabPane
                        key="data-factory"
                        title={t("createApp.dataFactory")}
                    />
                    <Tabs.TabPane
                        key="page-manager"
                        title={t("createApp.pageManager")}
                    />
                    <Tabs.TabPane
                        key="integrated-management"
                        title={t("createApp.integratedManagement")}
                    />

                    <Tabs.TabPane
                        key="app-setting"
                        title={t("createApp.appSetting")}
                    />
                    <Tabs.TabPane
                        key="app-release"
                        title={t("createApp.appRelease")}
                    />
                </Tabs>

                <div className={styles.userInfo}>
                    <Button
                        type='text'
                        shape='circle'
                        icon={<img src={helpSVG} alt='Help' style={{ width: 30 }} />}
                        // onClick={() => navigate('/onebase/setting')}
                    />

                    <Button
                        type='outline' /* onClick={() => navigate('/onebase/setting')} */
                    >
                        {t('createApp.preview')}
                    </Button>

                    <Dropdown droplist={userMenu} position='bottom'>
                        <div className={styles.userDropdown}>
                            <Avatar size={32} style={{ backgroundColor: '#4FAE7B' }}>
                                {UserPermissionManager.getUserPermissionInfo()?.user.nickname?.slice(0, 1) || "U"}
                            </Avatar>
                        </div>
                    </Dropdown>
                </div>
            </div>
        </Header>
    );
};

export { AppHeader };
