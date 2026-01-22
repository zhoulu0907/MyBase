import { Menu } from '@arco-design/web-react';
import { IconTool } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './index.module.less';
import BasicSettings from './components/BasicSettings';

const MenuItem = Menu.Item;

const PageSetting: React.FC = () => {
  return (
    <div className={styles.pageSettingPage}>
      <div className={styles.sider}>
        <Menu className={styles.menu} selectedKeys={['basic']}>
          <MenuItem key="basic">
            <IconTool /> 基础设置
          </MenuItem>
        </Menu>
      </div>
      <div className={styles.content}>
        <BasicSettings />
      </div>
    </div>
  );
};

export default PageSetting;
