import { useState, type FC } from 'react';
import { Button, Menu } from '@arco-design/web-react';
import { IconPlus, IconUser, IconPlusCircle } from '@arco-design/web-react/icon';
import Admin from '../Admin';
import User from '../User';
import AddMembers from '@/components/AddMembers';
import styles from './index.module.less';

const MenuItem = Menu.Item;

// 应用权限
const AppPermission: FC = () => {
  const [visible, setVisible] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<string>('admin');

  const handleSelectmenu = (val: string) => {
    if (val === 'add') {
      return;
    }
    setActiveTab(val);
  };

  return (
    <div className={styles.AppPermission}>
      <div className={styles.left}>
        <Menu className={styles.menu} defaultSelectedKeys={['admin']} onClickMenuItem={handleSelectmenu}>
          <div className={styles.user}>
            <label>管理员角色</label>
            <MenuItem key="admin">
              <IconUser />
              管理员
            </MenuItem>
          </div>
          <div>
            <label>用户角色</label>
            <MenuItem key="user">
              <IconUser />
              普通用户
            </MenuItem>
            <MenuItem key="add" className={styles.add}>
              <IconPlus style={{ color: 'rgb(var(--primary-6))' }} />
              添加角色
            </MenuItem>
          </div>
        </Menu>
      </div>
      <div className={styles.right}>
        <div className={styles.admin}>
          <div className={styles.header}>
            <div className={styles.headerTitle}>管理员</div>
            <Button type="primary" size="large" icon={<IconPlusCircle />} onClick={() => setVisible(true)}>
              添加成员
            </Button>
          </div>
          {activeTab === 'admin' && <Admin />}
          {activeTab === 'user' && <User />}
        </div>
      </div>

      <AddMembers visible={visible} cancel={() => setVisible(false)} />
    </div>
  );
};

export default AppPermission;
