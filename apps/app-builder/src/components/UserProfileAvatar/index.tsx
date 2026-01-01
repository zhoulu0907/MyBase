import type { IAdminInfo } from '@/pages/Home/components/header/AppHeader';
import { Avatar } from '@arco-design/web-react';
import { getFileUrlById } from '@onebase/platform-center';
import styles from './index.module.less';

interface IUserProfileAvatar {
  adminInfo: IAdminInfo | null;
  avatarUrl?: string;
  size?: number;
}

const UserProfileAvatar: React.FC<IUserProfileAvatar> = ({ adminInfo, avatarUrl, size = 32 }) => {
  const defaultNickName = adminInfo?.nickname?.charAt(0) || 'U';

  const getAvatar = () => {
    if (avatarUrl) {
      return <img src={getFileUrlById(avatarUrl)} alt="avatar" />;
    }
    if (adminInfo?.avatar) {
      return <img src={getFileUrlById(adminInfo.avatar)} alt="avatar" />;
    }
    return defaultNickName;
  };

  return (
    <Avatar size={size} className={adminInfo?.avatar ? '' : styles.avatarBackground}>
      {getAvatar()}
    </Avatar>
  );
};

export default UserProfileAvatar;
