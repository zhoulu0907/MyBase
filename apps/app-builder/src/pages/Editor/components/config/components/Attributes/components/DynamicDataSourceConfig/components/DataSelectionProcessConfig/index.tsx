import { Drawer } from '@arco-design/web-react';
import React from 'react';

import styles from './index.module.less';

interface DataSelectionProcessConfigProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
}

const DataSelectionProcessConfig: React.FC<DataSelectionProcessConfigProps> = ({ visible, setVisible }) => {
  return (
    <>
      <Drawer
        placement="bottom"
        height={'80vh'}
        visible={visible}
        headerStyle={{ justifyContent: 'center' }}
        bodyStyle={{ padding: 0 }}
        title="数据选择过程"
        footer={null}
        onCancel={() => {
          setVisible(false);
        }}
      >
        <div className={styles.container}>
          <div className={styles.leftColumn}>{/* 左侧内容区 */}</div>
          <div className={styles.rightColumn}>{/* 右侧配置区 */}</div>
        </div>
      </Drawer>
    </>
  );
};

export default DataSelectionProcessConfig;
