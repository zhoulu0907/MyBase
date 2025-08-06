import { useState, type FC } from 'react';
import { Input, Menu, Tabs } from '@arco-design/web-react';
import { IconNav } from '@arco-design/web-react/icon';
import FuncPermission from '../FuncPermission';
import FieldPermission from '../FieldPermission';
import DataPermission from '../DataPermission';
import styles from './index.module.less';

const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;
const TabPane = Tabs.TabPane;
const InputSearch = Input.Search;

// 普通用户面板
const User: FC = () => {
    const [_activeTab, setActiveTab] = useState('0');

    return (
        <div className={styles.user}>
            <div className={styles.left}>
                <div className={styles.search}>
                    <InputSearch placeholder='搜索分组或页面' />
                </div>
                <div className={styles.menu}>
                    <Menu
                        defaultOpenKeys={['3']}
                        defaultSelectedKeys={['3_2']}
                        onClickMenuItem={setActiveTab}
                    >
                        <MenuItem key='0'>
                            <IconNav />
                            菜单1
                        </MenuItem>
                        <MenuItem key='1'>
                            <IconNav />
                            菜单2
                        </MenuItem>
                        <MenuItem key='2'>
                            <IconNav />
                            菜单3
                        </MenuItem>

                        <SubMenu key='3' title='分组1'>
                            <MenuItem key='3_0'>
                                <IconNav />
                                菜单4
                            </MenuItem>
                            <MenuItem key='3_1'>
                                <IconNav />
                                菜单5
                            </MenuItem>
                            <MenuItem key='3_2'>
                                <IconNav />
                                菜单6
                            </MenuItem>
                        </SubMenu>
                        <MenuItem key='4'>
                            <IconNav />
                            菜单7
                        </MenuItem>
                    </Menu>
                </div>
            </div>

            <div className={styles.right}>
                <Tabs defaultActiveTab='1'>
                    <TabPane key='1' title='功能权限'>
                        <FuncPermission />
                    </TabPane>
                    <TabPane key='2' title='数据权限'>
                        <DataPermission />
                    </TabPane>
                    <TabPane key='3' title='字段权限'>
                        <FieldPermission />
                    </TabPane>
                </Tabs>
            </div>
        </div>
    );
};

export default User;
