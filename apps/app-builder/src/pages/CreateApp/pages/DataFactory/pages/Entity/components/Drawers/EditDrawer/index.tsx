import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Drawer, Message, Tabs } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import NodeEditForm from '../NodeEditForm';
import styles from './index.module.less';

interface FormValues {
  code: string;
  name: string;
  description: string;
  systemFields: {
    creator: boolean;
    updater: boolean;
    createTime: boolean;
    updateTime: boolean;
    dataOwner: boolean;
    dataDepartment: boolean;
  };
}

const EditDrawer: React.FC<{
  editingNode: EntityNode;
  visible: boolean;
  setVisible: (visible: boolean) => void;
  onNodeEdit: (data: EntityNode) => void;
  setEditingNode: (node: EntityNode | null) => void;
}> = ({ editingNode, visible, setVisible, onNodeEdit, setEditingNode }) => {
  const [activeTab, setActiveTab] = useState('entity');

  useEffect(() => {
    if (visible) {
      setActiveTab('entity');
    }
  }, [visible]);

  // 处理节点编辑
  const handleNodeEdit = (formData: Partial<FormValues>) => {
    if (editingNode && onNodeEdit) {
      // onNodeEdit(formData);
      const { nodes } = JSON.parse(
        localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
      );
      console.log('handleNodeEdit====', nodes);
      const nodeData = nodes.find((n: EntityNode) => n.entityId === editingNode.entityId);
      console.log('handleNodeEdit====', nodeData);
      if (nodeData) {
        nodeData.id = editingNode.entityId;
        nodeData.code = formData.code || nodeData.code;
        nodeData.displayName = formData.name || nodeData.displayName;
        nodeData.description = formData.description || nodeData.description;
        nodeData.fields = nodeData?.fields || [];
        console.log(formData.systemFields);
        if (formData.systemFields) {
          Object.keys(formData.systemFields).forEach((key: string) => {
            if (formData.systemFields && formData.systemFields[key as keyof typeof formData.systemFields]) {
              nodeData.fields.push({
                id: key,
                name: key,
                type: 'TEXT',
                isSystem: true
              });
            }
          });
        }
        console.log(nodeData);
      }
      onNodeEdit(nodeData);
      setVisible(false);
      setEditingNode(null);
      Message.success('节点信息已更新');
    }
  };

  // 渲染不同tab的内容
  const renderTabContent = () => {
    switch (activeTab) {
      case 'entity':
        return (
          <NodeEditForm
            node={editingNode}
            onSave={(data: Partial<FormValues>) => handleNodeEdit(data)}
            onCancel={() => setVisible(false)}
          />
        );
      case 'relation':
        return (
          <div className={styles['tab-content']}>
            <h3>关联关系</h3>
            <p>这里显示关联关系配置内容</p>
            {/* TODO: 添加关联关系配置组件 */}
          </div>
        );
      case 'rule':
        return (
          <div className={styles['tab-content']}>
            <h3>数据规则</h3>
            <p>这里显示数据规则配置内容</p>
            {/* TODO: 添加数据规则配置组件 */}
          </div>
        );
      case 'method':
        return (
          <div className={styles['tab-content']}>
            <h3>数据方法</h3>
            <p>这里显示数据方法配置内容</p>
            {/* TODO: 添加数据方法配置组件 */}
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <Drawer
      title="编辑节点"
      visible={visible}
      onCancel={() => setVisible(false)}
      width={800}
      className={styles['edit-drawer']}
    >
      {editingNode && (
        <div className={styles['drawer-container']}>
          {/* 左侧Tab导航 */}
          <div className={styles['tab-sidebar']}>
            <Tabs
              activeTab={activeTab}
              onChange={setActiveTab}
              direction="vertical"
              className={styles['vertical-tabs']}
            >
              <Tabs.TabPane key="entity" title="业务实体" />
              <Tabs.TabPane key="relation" title="关联关系" />
              <Tabs.TabPane key="rule" title="数据规则" />
              <Tabs.TabPane key="method" title="数据方法" />
            </Tabs>
          </div>
          
          {/* 右侧内容区域 */}
          <div className={styles['content-area']}>{renderTabContent()}</div>
        </div>
      )}
    </Drawer>
  );
};

export default EditDrawer;
