import type { IAdminInfo } from "@/pages/Home/components/header/AppHeader";
import { Avatar } from "@arco-design/web-react"

interface IUserProfileAvatar {
    adminInfo: IAdminInfo | null
}

const UserProfileAvatar:React.FC<IUserProfileAvatar> = ({ adminInfo }) => {
    const defaultNickName = adminInfo?.nickname?.charAt(0) || 'U';
    return (
        <Avatar size={32}>{adminInfo?.avatar ? <img src={adminInfo?.avatar} alt="avatar" /> : defaultNickName}</Avatar>
    )
}

export default UserProfileAvatar;