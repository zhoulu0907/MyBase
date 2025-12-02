import { TabBar } from '@arco-design/mobile-react';
import { IconUser, IconHome } from '@arco-design/mobile-react/esm/icon';
import { useNavigate, useLocation } from 'react-router-dom';
import { useEffect, useState } from 'react';
// import { TokenManager } from '@onebase/common';
import { UserPermissionManager } from '@/utils/permission';
import { getPermissionInfo } from '@onebase/platform-center';
import styles from './index.module.less';
import Home from './components/home';
import Me from './components/me';
export default function RuntimeHome() {
  const [nickname, setNickname] = useState('-');
  const [username, setUsername] = useState('-');
  const navigate = useNavigate();
  const location = useLocation();
  const sp = new URLSearchParams(location.search);
  const [activeIndex, setActiveIndex] = useState(Number(sp.get('curTab') || 0));
  const tabs = [
    {
      title: '首页',
      icon: <IconHome />
    },
    {
      title: '我的',
      icon: <IconUser />
    }
  ];

  const clickTab = (index: number) => {
    setActiveIndex(index);
    const sp = new URLSearchParams(location.search);
    sp.set('curTab', String(index));
    const to = `${location.pathname}?${sp.toString()}`;
    navigate(to, { replace: true });
  };

  useEffect(() => {
    getUserInfo();
  }, []);

  const getUserInfo = async () => {
    const res = await getPermissionInfo();
    UserPermissionManager.setUserPermissionInfo(res);
    // userPermissionSignal.setPermissionInfo(res);
    setNickname(res.user.nickname);
    setUsername(res.user.username);
  };

  return (
    <div className={styles.runtimeHome}>
      {activeIndex === 0 ? <Home nickname={nickname} /> : <Me {...{ nickname, username }} />}
      <TabBar
        fixed={true}
        className={styles.tabBar}
        activeIndex={activeIndex}
        onChange={(index: number) => {
          clickTab(index);
        }}
      >
        {tabs.map((tab, index) => (
          <TabBar.Item title={tab.title} icon={tab.icon} key={index} />
        ))}
      </TabBar>
    </div>
  );
}