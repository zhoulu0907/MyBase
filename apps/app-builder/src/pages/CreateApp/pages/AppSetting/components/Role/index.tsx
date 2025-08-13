import { useEffect, useState } from 'react';
import { Input, Menu, Tabs, Radio } from '@arco-design/web-react';
import { IconNav } from '@arco-design/web-react/icon';
import FuncPermission from '../FuncPermission';
import FieldPermission from '../FieldPermission';
import DataPermission from '../DataPermission';
import UserTable from '../UserTable';
import { RoleType, type Role } from '@onebase/app';
import styles from './index.module.less';

const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;
const TabPane = Tabs.TabPane;
const RadioGroup = Radio.Group;
const InputSearch = Input.Search;

interface IProps {
  roleInfo: Role | undefined;
  onAddMembers: () => void;
}
type ManagerType = 'permission' | 'members';

// 角色面板
const RoleInfo = (props: IProps) => {
  const { roleInfo, onAddMembers } = props;
  console.log('user roleInfo', roleInfo);

  const [_activeTab, setActiveTab] = useState('0');
  const [managerType, setManagerType] = useState<ManagerType>('permission');

  useEffect(() => {
    if (roleInfo?.roleType === RoleType.ADMIN) {
      setManagerType('members');
      return;
    }
    setManagerType('permission');
  }, [roleInfo]);

  const handleSelected = (value: ManagerType) => {
    setManagerType(value);
  };

  return (
    <div className={styles.userWrapper}>
      <div className={styles.header}>
        <div className={styles.headerTitle}>{roleInfo?.roleName || '管理员'}</div>
        {roleInfo?.roleType !== RoleType.ADMIN && (
          <RadioGroup type="button" name="lang" defaultValue="permission" onChange={handleSelected}>
            <Radio value="permission">权限管理</Radio>
            <Radio value="members">成员管理</Radio>
          </RadioGroup>
        )}
      </div>

      {managerType === 'members' ? (
        <UserTable roleInfo={roleInfo} onAddMembers={onAddMembers} />
      ) : (
        <div className={styles.user}>
          <div className={styles.left}>
            <div className={styles.search}>
              <InputSearch placeholder="搜索分组或页面" />
            </div>
            <div className={styles.menu}>
              <Menu defaultOpenKeys={['3']} defaultSelectedKeys={['3_2']} onClickMenuItem={setActiveTab}>
                <MenuItem key="0">
                  <IconNav />
                  菜单1
                </MenuItem>
                <MenuItem key="1">
                  <IconNav />
                  菜单2
                </MenuItem>
                <MenuItem key="2">
                  <IconNav />
                  菜单3
                </MenuItem>

                <SubMenu key="3" title="分组1">
                  <MenuItem key="3_0">
                    <IconNav />
                    菜单4
                  </MenuItem>
                  <MenuItem key="3_1">
                    <IconNav />
                    菜单5
                  </MenuItem>
                  <MenuItem key="3_2">
                    <IconNav />
                    菜单6
                  </MenuItem>
                </SubMenu>
                <MenuItem key="4">
                  <IconNav />
                  菜单7
                </MenuItem>
              </Menu>
            </div>
          </div>

          <div className={styles.right}>
            <Tabs defaultActiveTab="1">
              <TabPane key="1" title="功能权限">
                <FuncPermission />
              </TabPane>
              <TabPane key="2" title="数据权限">
                <DataPermission />
              </TabPane>
              <TabPane key="3" title="字段权限">
                <FieldPermission />
              </TabPane>
            </Tabs>
          </div>
        </div>
      )}
    </div>
  );
};

export default RoleInfo;
