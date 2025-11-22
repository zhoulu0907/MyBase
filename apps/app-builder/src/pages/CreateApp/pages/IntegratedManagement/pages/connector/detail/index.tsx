import { Menu } from '@arco-design/web-react';
import { IconArrowLeft, IconEdit } from '@arco-design/web-react/icon';
import { getConnectInstance } from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

interface ConnectorInstanceDetailProps {}

const ConnectorDetailPage: React.FC<ConnectorInstanceDetailProps> = ({}) => {
  const [activeKey, setActiveKey] = useState<'base' | 'action' | 'logic'>('base');

  useEffect(() => {
    const id = getHashQueryParam('id');

    if (id) {
      handleGetIntanceDetail(id);
    }
  }, []);

  const handleGetIntanceDetail = async (id: string) => {
    const res = await getConnectInstance(id);
    console.log('res :', res);
  };

  return (
    <div className={styles.connectorInstanceDetail}>
      <div className={styles.sider}>
        <div className={styles.siderHeader}>
          <IconArrowLeft />
          <div>连接器实例2</div>
          <IconEdit />
        </div>
        <div className={styles.sideMenu}>
          <Menu
            style={{ width: '100%' }}
            defaultSelectedKeys={['base']}
            onClickMenuItem={(key) => setActiveKey(key as 'base' | 'action' | 'logic')}
          >
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

export default ConnectorDetailPage;
