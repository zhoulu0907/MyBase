import { Avatar } from '@arco-design/web-react';
import { getFileUrlById } from '@onebase/platform-center';
import styles from './index.module.less';

interface IAdminInfo {
  avatar: string;
  deptId: string;
  email: string;
  id: string;
  nickname: string;
  username: string;
  mobile: string;
}

interface IUserProfileAvatar {
  adminInfo: IAdminInfo | null;
  avatarUrl?: string;
}

const UserProfileAvatar: React.FC<IUserProfileAvatar> = ({ adminInfo, avatarUrl }) => {
  const defaultNickName = adminInfo?.nickname?.charAt(0) || 'U';

  const getAvatar = () => {
    if (avatarUrl) {
      return <img src={getFileUrlById(avatarUrl)} alt="avatar" />;
    }
    if (adminInfo?.avatar) {
      return <img src={adminInfo?.avatar} alt="avatar" />;
    }
    return defaultNickName;
  };

  return (
    <Avatar size={32} className={adminInfo?.avatar ? '' : styles.avatarBackground}>
      {getAvatar()}
    </Avatar>
  );
};

export default UserProfileAvatar;
