import { Avatar } from '@arco-design/web-react';
import { getFileUrlById } from '@onebase/platform-center';
import { useParams } from 'react-router-dom';
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

  const { appId } = useParams();

  const getAvatar = () => {
    if (avatarUrl) {
      return <img src={getFileUrlById(avatarUrl)} alt="avatar" />;
    }
    if (adminInfo?.avatar) {
      return <img src={getFileUrlById(adminInfo?.avatar, appId)} alt="avatar" />;
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
