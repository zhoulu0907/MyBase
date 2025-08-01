import React, { useState } from 'react';
import { Button, Space } from '@arco-design/web-react';
import ERchart from './ERchart';
import EditDrawer from './Drawer/EditDrawer';
import type { EntityNode, EntityERProps } from '../utils/interface';

// 修改示例数据，添加字段级别的关联
const mockData = {
  nodes: [
    {
      id: 'sub-table',
      title: '子表',
      x: 100,
      y: 100,
      fields: [
        { id: 'sub-table-field-1', name: '系统字段', type: '系统字段 (13)', isSystem: true },
        { id: 'sub-table-field-2', name: '自定义字段', type: '自定义字段 (2)', isSystem: false },
        { id: 'sub-table-field-3', name: '日期时间', type: '日期时间', isSystem: false },
        { id: 'sub-table-field-4', name: '选择', type: '选择', isSystem: false },
      ],
    },
    {
      id: 'user-system',
      title: '用户系统对象',
      x: 400,
      y: 100,
      fields: [
        { id: 'user-system-field-1', name: '系统字段', type: '系统字段 (27)', isSystem: true },
        { id: 'user-system-field-2', name: '用户口', type: '文本', isSystem: false },
        { id: 'user-system-field-3', name: '姓名', type: '文本', isSystem: false },
        { id: 'user-system-field-4', name: '头像', type: '文本', isSystem: false },
        { id: 'user-system-field-5', name: '手机号', type: '文本', isSystem: false },
        { id: 'user-system-field-6', name: '邮箱', type: '文本', isSystem: false },
        { id: 'user-system-field-7', name: '工号', type: '文本', isSystem: false },
        { id: 'user-system-field-8', name: '上级', type: '人员单选', isSystem: false },
        { id: 'user-system-field-9', name: '类型', type: '单选', isSystem: false },
        { id: 'user-system-field-10', name: '账号状态', type: '单选', isSystem: false },
      ],
    },
    {
      id: 'test-frontend',
      title: '测试前台',
      x: 700,
      y: 100,
      fields: [
        // 系统字段 (13个)
        { name: 'id', type: '文本', isSystem: true },
        { name: 'created_at', type: '文本', isSystem: true },
        { name: 'updated_at', type: '文本', isSystem: true },
        { name: 'created_by', type: '文本', isSystem: true },
        { name: 'updated_by', type: '文本', isSystem: true },
        { name: 'deleted_at', type: '文本', isSystem: true },
        { name: 'deleted_by', type: '文本', isSystem: true },
        { name: 'version', type: '文本', isSystem: true },
        { name: 'status', type: '文本', isSystem: true },
        { name: 'tenant_id', type: '文本', isSystem: true },
        { name: 'org_id', type: '文本', isSystem: true },
        { name: 'owner_id', type: '文本', isSystem: true },
        { name: 'owner_type', type: '文本', isSystem: true },
        // 自定义字段 (5个)
        { name: '单行输入', type: '文本', isSystem: false },
        { name: '单行输入', type: '文本', isSystem: false },
        { name: '单行输入', type: '文本', isSystem: false },
        { name: '子表', type: '主子关系', isSystem: false },
        { name: '数据选择', type: '数据多选', isSystem: false },
      ],
    },
    {
      id: 'department',
      title: '部门',
      x: 1000,
      y: 100,
      fields: [
        { name: '系统字段', type: '系统字段 (17)', isSystem: true },
        { name: '部门ID', type: '文本', isSystem: false },
        { name: '部门编号', type: '文本', isSystem: false },
        { name: '部门名称', type: '文本', isSystem: false },
        { name: '部门主管', type: '人员单选', isSystem: false },
        { name: '部门状态', type: '单选', isSystem: false },
        { name: '结构ID', type: '文本', isSystem: false },
        { name: '结构名称', type: '文本', isSystem: false },
        { name: '部门层级', type: '数字', isSystem: false },
        { name: '同级部门排序', type: '数字', isSystem: false },
      ],
    },
  ],
  edges: [
    {
      source: { cell: 'sub-table', port: 'sub-table-field-3' }, // 从子表的自定义字段
      target: { cell: 'user-system', port: 'user-system-field-5' }, // 到测试前台的子表字段
      // label: '主子关系',
    },
    {
      source: { cell: 'test-frontend', port: 'test-frontend-field-5' }, // 从测试前台的数据选择字段
      target: { cell: 'department', port: 'department-field-3' }, // 到部门的部门名称字段
      // label: '数据关联',
    },
  ],
};

// 模式切换示例
export const EntityERWithModeSwitch: React.FC = () => {
  const [mode, setMode] = useState<'view' | 'edit'>('view');
  const [data, setData] = useState(mockData);
  const [editDrawerVisible, setEditDrawerVisible] = useState(false);
  const [editingNode, setEditingNode] = useState<EntityNode | null>(null);

  const handleNodeEdit = (editData: EntityNode) => {
    console.log('节点编辑:', editData);
    // 这里可以更新节点数据
    setEditDrawerVisible(true);
    setEditingNode(editData);
  };

  const handleNodeAdd = () => {
    const newNode = {
      id: `node-${Date.now()}`,
      title: '新节点',
      x: Math.random() * 800 + 100,
      y: Math.random() * 400 + 100,
      fields: [
        { name: '系统字段', type: '系统字段 (13)', isSystem: true },
        { name: '新字段', type: '文本', isSystem: false },
      ],
    };

    setData(prev => ({
      ...prev,
      nodes: [...prev.nodes, newNode],
    }));
  };

  return (
    <div style={{ height: '100%' }}>
      <div style={{ marginBottom: '16px' }}>
        <Space>
          <Button
            type={mode === 'view' ? 'primary' : 'default'}
            onClick={() => setMode('view')}
          >
            查看模式
          </Button>
          <Button
            type={mode === 'edit' ? 'primary' : 'default'}
            onClick={() => setMode('edit')}
          >
            编辑模式
          </Button>
        </Space>
      </div>
      
      <ERchart
        mode={mode}
        data={data as unknown as EntityERProps['data']}
        onNodeEdit={handleNodeEdit}
        onNodeAdd={handleNodeAdd}
      />
      <EditDrawer
        visible={editDrawerVisible}
        setVisible={setEditDrawerVisible}
        editingNode={editingNode as EntityNode}
        setEditingNode={(node: EntityNode | null) => setEditingNode(node)}
        onNodeEdit={handleNodeEdit}
      />
    </div>
  );
};

export default EntityERWithModeSwitch; 