import AvatarSVG from '@/assets/images/avatar.svg';
import React, { useEffect, useState } from 'react';
import { TokenManager } from '@onebase/common';
import { UserPermissionManager } from '@/utils/permission';
import { getPermissionInfo } from '@onebase/platform-center';
import { useNavigate, useLocation } from 'react-router-dom';
import { Popup, Cell } from '@arco-design/mobile-react';
import logout from '../../../../assets/images/logout.svg';
import file from '../../../../assets/images/file.svg';
import lock from '../../../../assets/images/lock.svg';
import account from '../../../../assets/images/account.svg';
import styles from './index.module.less';

interface MeProps {
  nickname: string;
  username: string;
}

const Me: React.FC<MeProps> = ({ nickname, username }) => {

  const navigate = useNavigate();
  const location = useLocation();
  const handleLogout = () => {
    // 清除 token
    TokenManager.clearToken();
    UserPermissionManager.clearUserPermissionInfo();
    // 关闭弹窗
    window.modalInstance?.close();
    // 跳转到登录页
    navigate(`/login?redirectURL=${window.location.origin}#${(location.pathname)}`, { replace: true });
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
        <Cell icon={<img className={styles.meCellIcon} src={file} alt="protocol" />} label="用户协议" showArrow onClick={() => navigate('/onebase/runtime-home/protocol')} />
        <Cell icon={<img className={styles.meCellIcon} src={lock} alt="privacy" />} label="隐私政策" showArrow onClick={() => navigate('/onebase/runtime-home/privacy')} />
        <Cell icon={<img className={styles.meCellIcon} src={account} alt="about" />} label="关于我们" showArrow onClick={() => navigate('/onebase/runtime-home/about')} />
        <Cell icon={<img className={styles.meCellIcon} src={logout} alt="logout" />} label="退出登录" showArrow onClick={toLogout} />
      </Cell.Group>
    </div>
  );
};

export default Me;
