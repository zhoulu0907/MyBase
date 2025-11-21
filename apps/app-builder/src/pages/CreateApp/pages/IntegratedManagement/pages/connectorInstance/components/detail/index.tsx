import { Menu } from '@arco-design/web-react';
import { IconBackward, IconEdit } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import styles from './index.module.less';

interface ConnectorInstanceDetailProps {
  // 连接器实例ID
  id: string;
}

const ConnectorInstanceDetail: React.FC<ConnectorInstanceDetailProps> = ({ id }) => {
  const [activeKey, setActiveKey] = useState<'base' | 'action' | 'logic'>('base');

  return (
    <div className={styles.connectorInstanceDetail}>
      <div className={styles.sider}>
        <div className={styles.siderHeader}>
          <div>
            <IconBackward />
          </div>
          <div>连接器实例2</div>
          <div>
            <IconEdit />
          </div>
        </div>
        <div className={styles.sideMenu}>
          <Menu style={{ width: '100%' }} defaultSelectedKeys={['base']}>
            <Menu.Item key="base">基本信息</Menu.Item>
            <Menu.Item key="action">动作配置</Menu.Item>
            <Menu.Item key="logic">关联逻辑流</Menu.Item>
          </Menu>
        </div>
      </div>
      <div className={styles.content}>
        {activeKey === 'base' && <div>基本信息</div>}
        {activeKey === 'action' && <div>动作配置</div>}
        {activeKey === 'logic' && <div>关联逻辑流</div>}
      </div>
    </div>
  );
};

export default ConnectorInstanceDetail;
