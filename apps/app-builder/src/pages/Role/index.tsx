import styles from './index.module.less';
import { createRole, updateRole, deleteRole } from '@onebase/platform-center';
import type { RoleVO } from '@onebase/platform-center';

const Sider = Layout.Sider;
const Header = Layout.Header;
const Content = Layout.Content;
const TabPane = Tabs.TabPane;

export default function RolePage() {
  return (
    <div className={styles.rolePage}>
        角色管理
    </div>
  );
}