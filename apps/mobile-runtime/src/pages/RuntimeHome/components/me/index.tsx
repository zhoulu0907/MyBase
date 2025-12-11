import AvatarSVG from '@/assets/images/avatar.svg';
import { Cell, Popup } from '@arco-design/mobile-react';
import React from 'react';
import { logout } from '@/utils/session';

import { useNavigate } from 'react-router-dom';
import accountSVG from '@/assets/images/account.svg';
import fileSVG from '@/assets/images/file.svg';
import lockSVG from '@/assets/images/lock.svg';
import logoutSVG from '@/assets/images/logout.svg';
import styles from './index.module.less';

interface MeProps {
  nickname: string;
  username: string;
}

const Me: React.FC<MeProps> = ({ nickname, username }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // 关闭弹窗
    window.modalInstance?.close();
    // 跳转到登录页
    logout(navigate);
  };

  const toLogout = () => {
    window.modalInstance = Popup.open({
      contentStyle: { borderRadius: '10px 10px 0 0' },
      children: (
        <div className={styles.popupContainer}>
          <div className={styles.popupTitle}>确认退出当前账号吗？</div>
          <div className={styles.popupLogout} onClick={handleLogout}>
            退出登录
          </div>
          <div className={styles.popupCancel} onClick={() => window.modalInstance?.close()}>
            取消
          </div>
        </div>
      )
    });
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
        <Cell
          icon={<img className={styles.meCellIcon} src={fileSVG} alt="protocol" />}
          label="用户协议"
          showArrow
          onClick={() => navigate('/onebase/runtime-home/protocol')}
        />
        <Cell
          icon={<img className={styles.meCellIcon} src={lockSVG} alt="privacy" />}
          label="隐私政策"
          showArrow
          onClick={() => navigate('/onebase/runtime-home/privacy')}
        />
        <Cell
          icon={<img className={styles.meCellIcon} src={accountSVG} alt="about" />}
          label="关于我们"
          showArrow
          onClick={() => navigate('/onebase/runtime-home/about')}
        />
        <Cell
          icon={<img className={styles.meCellIcon} src={logoutSVG} alt="logout" />}
          label="退出登录"
          showArrow
          onClick={toLogout}
        />
      </Cell.Group>
    </div>
  );
};

export default Me;
