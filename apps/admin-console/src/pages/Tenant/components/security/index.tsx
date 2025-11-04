import { Button, Menu } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const MenuItem = Menu.Item;

interface WorkspaceSecurityProps {}

const WorkspaceSecurity: React.FC<WorkspaceSecurityProps> = ({}) => {
  useEffect(() => {}, []);

  const [activeMenuItem, setActiveMenuItem] = useState<string>('');
  const handleClickMenuItem = (key: string) => {
    setActiveMenuItem(key);
  };

  //   TODO(mickey): 联调接口，获取配置项目和配置项
  const handleSave = () => {
    console.log('保存');
  };

  return (
    <div className={styles.workspaceSecurityPage}>
      <div className={styles.sider}>
        <Menu style={{ width: 200 }} mode="pop" onClickMenuItem={handleClickMenuItem}>
          <MenuItem key="1">配置项1</MenuItem>
          <MenuItem key="2">配置项2</MenuItem>
          <MenuItem key="3">配置项3</MenuItem>
        </Menu>
      </div>
      <div className={styles.content}>
        <div className={styles.contentHeader}>
          <div className={styles.contentTitle}>{activeMenuItem}</div>
          <Button type="primary" onClick={handleSave}>
            更新配置
          </Button>
        </div>
        <div className={styles.contentBody}>
          <div className={styles.contentBodyItem}>
            <div className={styles.contentBodyItemTitle}>配置项1</div>
            <div className={styles.contentBodyItemContent}>配置项1内容</div>
          </div>
        </div>
        <div className={styles.contentFooter}></div>
      </div>
    </div>
  );
};

export default WorkspaceSecurity;
