import type { IAdminInfo } from "@/pages/Home/components/header/AppHeader";
import { Avatar } from "@arco-design/web-react"
import styles from './index.module.less';

interface IUserProfileAvatar {
    adminInfo: IAdminInfo | null;
    avatarUrl?: string;
}

const UserProfileAvatar:React.FC<IUserProfileAvatar> = ({ adminInfo , avatarUrl }) => {
    const defaultNickName = adminInfo?.nickname?.charAt(0) || 'U';

    const getAvatar = () => {
        if(avatarUrl) {
            return <img src={avatarUrl} alt="avatar" /> ;
        }
        if(adminInfo?.avatar) {
            return <img src={adminInfo?.avatar} alt="avatar" /> 
        }
        return defaultNickName
    }

    return (
        <Avatar size={32} className={adminInfo?.avatar ?  '' : styles.avatarBackground}>{getAvatar()}</Avatar>
    )
}

export default UserProfileAvatar;