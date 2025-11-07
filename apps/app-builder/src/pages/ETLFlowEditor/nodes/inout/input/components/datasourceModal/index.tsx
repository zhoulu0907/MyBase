import { Button, Input, Modal, Tabs } from '@arco-design/web-react';
import { IconPlus, IconSwap } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import CreateExternalModal from '../createExternalModal';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

interface DatasourceModalProps {
  // 控制弹窗是否显示
  isModalVisible: boolean;
  // 关闭弹窗的回调
  onClose: () => void;
  // （可选）确定按钮的回调
  onOk?: () => void;
}

const DatasourceModal: React.FC<DatasourceModalProps> = ({ isModalVisible, onClose, onOk }) => {
  const [activeTab, setActiveTab] = useState('external');

  const [createExternalModalVisible, setCreateExternalModalVisible] = useState(false);
  const handleCreateExternalModalClose = () => {
    setCreateExternalModalVisible(false);
  };
  const handleCreateExternalModalCreate = () => {
    setCreateExternalModalVisible(false);
  };

  const handleOk = () => {
    onOk?.();
  };
  const handleClose = () => {
    onClose?.();
  };

  return (
    <Modal
      onCancel={handleClose}
      onOk={handleOk}
      visible={isModalVisible}
      title="更改输入源"
      style={{
        width: '720px'
      }}
    >
      <div className={styles.datasourceModal}>
        <div className={styles.left}>
          <div className={styles.leftHeader}>第一步: 选择数据源</div>
          <div className={styles.leftContent}>
            <Tabs defaultActiveTab={activeTab} onChange={setActiveTab}>
              <TabPane key="internal" title="内部数据源" disabled></TabPane>
              <TabPane key="external" title="外部数据源"></TabPane>
            </Tabs>

            {activeTab == 'internal' && (
              <div className={styles.internalDatasource}>
                <div className={styles.internalDatasourceHeader}>
                  <Input.Search placeholder="搜索内部数据源" />
                </div>
                <div className={styles.internalDatasourceContent}></div>
              </div>
            )}

            {activeTab == 'external' && (
              <div className={styles.externalDatasource}>
                <div className={styles.externalDatasourceHeader}>
                  <Input.Search placeholder="搜索外部数据源" />
                  <div className={styles.externalDatasourceCreate}>
                    <div>已创建外部数据源</div>
                    <Button type="text" icon={<IconPlus />} onClick={() => setCreateExternalModalVisible(true)}>
                      新建
                    </Button>
                  </div>
                </div>
                <div className={styles.externalDatasourceContent}>
                  <div className={styles.externalDatasourceContentItem}>
                    <div>数据库</div>
                    <Button type="text" icon={<IconSwap />}></Button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
        <div className={styles.right}>
          <div className={styles.rightHeader}>
            <div className={styles.rightHeaderTitle}>第二步: 选择字段</div>
            <div className={styles.rightHeaderSearch}>
              <Input.Search placeholder="搜索字段" />
            </div>
          </div>
        </div>
      </div>
      <CreateExternalModal
        visible={createExternalModalVisible}
        onClose={handleCreateExternalModalClose}
        onCreate={handleCreateExternalModalCreate}
      />
    </Modal>
  );
};

export default DatasourceModal;
