import AvatarSVG from '@/assets/images/avatar.svg';
import React, { useEffect, useState } from 'react';
import { TokenManager } from '@onebase/common';
import { UserPermissionManager } from '@/utils/permission';
import { getPermissionInfo } from '@onebase/platform-center';
import { useNavigate } from 'react-router-dom';
import { Popup, Cell } from '@arco-design/mobile-react';
import logout from '../../../../assets/images/logout.svg';
import file from '../../../../assets/images/file.svg';
import lock from '../../../../assets/images/lock.svg';
import account from '../../../../assets/images/account.svg';
import styles from './index.module.less';

const Me: React.FC = () => {

  const [nickname, setNickname] = useState('-');
  const [username, setUsername] = useState('-');

  const navigate = useNavigate();
  const handleLogout = () => {
    // 清除 token
    TokenManager.clearToken();
    UserPermissionManager.clearUserPermissionInfo();
    // 关闭弹窗
    window.modalInstance?.close();
    // 跳转到登录页
    navigate('/login', { replace: true });
  };

  const toLogout = () => {
    window.modalInstance = Popup.open({
      contentStyle: { borderRadius: '10px 10px 0 0' },
      children: (
        <div className={styles.popupContainer}>
          <div className={styles.popupTitle}>确认退出当前账号吗？</div>
          <div className={styles.popupLogout} onClick={handleLogout}>退出登录</div>
          <div className={styles.popupCancel} onClick={() => window.modalInstance?.close()}>取消</div>
        </div>
      ),
    });
  }

  useEffect(() => {
    getUserInfo();
  }, []);

  const getUserInfo = async () => {
    const res = await getPermissionInfo();
    UserPermissionManager.setUserPermissionInfo(res);
    // userPermissionSignal.setPermissionInfo(res);
    console.warn('res', res);
    setNickname(res.user.nickname);
    setUsername(res.user.username);
  };

  return (
    <div className={styles.me}>
      <div className={styles.meTitle}>
        <img src={AvatarSVG} alt="avatar" />
        <div className={styles.meInfo}>
          <div className={styles.meNickname}>{nickname}</div>
          <div className={styles.meUsername}>{username}</div>
        </div>
      </div>
      <Cell.Group className={styles.meCellGroup}>
        <Cell icon={<img className={styles.meCellIcon} src={file} alt="logout" />} label="用户协议" showArrow />
        <Cell icon={<img className={styles.meCellIcon} src={lock} alt="logout" />} label="隐私政策" showArrow />
        <Cell icon={<img className={styles.meCellIcon} src={account} alt="logout" />} label="关于我们" showArrow />
        <Cell icon={<img className={styles.meCellIcon} src={logout} alt="logout" />} label="退出登录" showArrow onClick={toLogout} />
      </Cell.Group>
    </div>
  );
};

export default Me;
