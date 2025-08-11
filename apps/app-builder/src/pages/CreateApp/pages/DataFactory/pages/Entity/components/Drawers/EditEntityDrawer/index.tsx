import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Button, Drawer, Message, Tabs } from '@arco-design/web-react';
import { IconCaretRight } from '@arco-design/web-react/icon';
import React, { useEffect, useState } from 'react';
import NodeEditForm from '../NodeEditForm';
import styles from './EditEntityDrawer.module.less';

const DetailDrawer: React.FC<{
  editingNode: EntityNode;
  visible: boolean;
  setVisible: (visible: boolean) => void;
  onNodeEdit: (data: Partial<EntityNode>) => void;
  setEditingNode: (node: EntityNode | null) => void;
  successCallback?: () => void;
}> = ({ editingNode, visible, setVisible, onNodeEdit, setEditingNode, successCallback }) => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [activeTab, setActiveTab] = useState('entity');

  useEffect(() => {
    // 当抽屉关闭时，重置收起状态
    if (!visible) {
      setIsCollapsed(false);
    }
  }, [visible]);

  // 处理节点编辑
  const handleNodeEdit = (formData: Partial<EntityNode>) => {
    if (editingNode && onNodeEdit) {
      onNodeEdit(formData);
      setVisible(false);
      setEditingNode(null);
      Message.success('节点信息已更新');
      if (successCallback) {
        successCallback();
      }
    }
  };

  // 切换抽屉展开/收起状态
  const toggleCollapse = () => {
    setIsCollapsed(!isCollapsed);
  };

  // 关闭抽屉
  const handleClose = () => {
    setVisible(false);
    setIsCollapsed(false);
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'entity':
        return (
          <NodeEditForm
            node={editingNode}
            onSave={handleNodeEdit}
            onCancel={handleClose}
            successCallback={successCallback || (() => {})}
          />
        );
      case 'relation':
        return (
          <div className={styles['tab-content']}>
            <h3>关联关系</h3>
          </div>
        );
      case 'rule':
        return (
          <div className={styles['tab-content']}>
            <h3>数据规则</h3>
          </div>
        );
      case 'method':
        return (
          <div className={styles['tab-content']}>
            <h3>数据方法</h3>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <>
      {/* 抽屉把手按钮 - 只在抽屉可见时显示 */}
      {visible && (
        // <Tooltip content={isCollapsed ? '展开抽屉' : '收起抽屉'}>
        <Button
          type="primary"
          shape="circle"
          size="large"
          icon={<IconCaretRight className={`${styles.icon} ${isCollapsed ? styles.collapsed : styles.expanded}`} />}
          onClick={toggleCollapse}
          className={`${styles.drawerHandleButton} ${isCollapsed ? styles.collapsed : styles.expanded}`}
        />
        // </Tooltip>
      )}

      {/* 左侧Tab导航 */}
      {!isCollapsed && (
        <div className={styles['tab-sidebar']}>
          <Tabs activeTab={activeTab} onChange={setActiveTab} direction="vertical" className={styles['vertical-tabs']}>
            <Tabs.TabPane key="entity" title="业务实体" />
            <Tabs.TabPane key="relation" title="关联关系" />
            <Tabs.TabPane key="rule" title="数据规则" />
            <Tabs.TabPane key="method" title="数据方法" />
          </Tabs>
        </div>
      )}

      <Drawer
        title={null}
        visible={visible && !isCollapsed}
        onCancel={handleClose}
        width={500}
        mask={false}
        placement="right"
        className={styles['edit-entity-drawer']}
      >
        {editingNode && (
          <div className={styles['drawer-container']}>
            {/* 右侧内容区域 */}
            {renderTabContent()}
          </div>
        )}
      </Drawer>
    </>
  );
};

export default DetailDrawer;
