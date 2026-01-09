import { Avatar } from '@arco-design/web-react';
import { getCorpResourceById } from '@onebase/common';
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
      return <img src={getCorpResourceById(avatarUrl)} alt="avatar" />;
    }

    if (adminInfo?.avatar) {
      return <img src={getCorpResourceById(adminInfo?.avatar)} alt="avatar" />;
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
