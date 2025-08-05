import React, { useState, useEffect } from 'react';
// import { Button, Space } from '@arco-design/web-react';
import ERchart from './ERchart';
import EditDrawer from './Drawer/EditEntityDrawer';
import type { EntityNode, EntityERProps, EntityData, EdgeData } from '../utils/interface';
import CreateFieldModal from '../Pages/Entity/CreateFieldModal';
import CreateRelationModal from '../Pages/Entity/CreateRelationModal';
import { Modal } from '@arco-design/web-react';


// const mockData = {
//   nodes: [
//     {
//       id: 'sub-table',
//       title: '子表',
//       x: 100,
//       y: 100,
//       fields: [
//         { id: 'sub-table-field-3', name: '日期时间', type: '日期时间', isSystem: true },
//         { id: 'sub-table-field-4', name: '选择', type: '选择', isSystem: true },
//       ],
//     },
//     {
//       id: 'user-system',
//       title: '用户系统对象',
//       x: 400,
//       y: -100,
//       fields: [
//         { id: 'user-system-field-2', name: '用户口', type: '文本', isSystem: true },
//         { id: 'user-system-field-3', name: '姓名', type: '文本', isSystem: true },
//         { id: 'user-system-field-4', name: '头像', type: '文本', isSystem: false },
//         { id: 'user-system-field-5', name: '手机号', type: '文本', isSystem: false },
//         { id: 'user-system-field-6', name: '邮箱', type: '文本', isSystem: false },
//         { id: 'user-system-field-7', name: '工号', type: '文本', isSystem: false },
//         { id: 'user-system-field-8', name: '上级', type: '人员单选', isSystem: false },
//         { id: 'user-system-field-9', name: '类型', type: '单选', isSystem: false },
//         { id: 'user-system-field-10', name: '账号状态', type: '单选', isSystem: false },
//       ],
//     },
//     {
//       id: 'test-frontend',
//       title: '测试前台',
//       x: 800,
//       y: 0,
//       fields: [
//         // 系统字段 (13个)
//         { name: 'id', type: '文本', isSystem: true, id: 'id' },
//         { name: 'created_at', type: '文本', isSystem: true, id: 'created_at' },
//         { name: 'updated_at', type: '文本', isSystem: true, id: 'updated_at' },
//         { name: 'created_by', type: '文本', isSystem: true, id: 'created_by' },
//         { name: 'updated_by', type: '文本', isSystem: true, id: 'updated_by' },
//         { name: 'deleted_at', type: '文本', isSystem: true, id: 'deleted_at' },
//         { name: 'deleted_by', type: '文本', isSystem: true, id: 'deleted_by' },
//         { name: 'version', type: '文本', isSystem: true, id: 'version' },
//         { name: 'status', type: '文本', isSystem: true, id: 'status' },
//         { name: 'tenant_id', type: '文本', isSystem: true, id: 'tenant_id' },
//         { name: 'org_id', type: '文本', isSystem: true, id: 'org_id' },
//         { name: 'owner_id', type: '文本', isSystem: true, id: 'owner_id' },
//         { name: 'owner_type', type: '文本', isSystem: true, id: 'owner_type' },
//         // 自定义字段 (5个)
//         { name: '单行输入', type: '文本', isSystem: false, id: 'single-input' },
//         { name: '子表', type: '主子关系', isSystem: false, id: 'sub-table' },
//         { name: '数据选择', type: '数据多选', isSystem: false, id: 'data-select' },
//       ],
//     },
//     {
//       id: 'department',
//       title: '部门',
//       x: 1200,
//       y: 100,
//       fields: [
//         { name: '部门ID', type: '文本', isSystem: true, id: 'department-id' },
//         { name: '部门编号', type: '文本', isSystem: true, id: 'department-code' },
//         { name: '部门名称', type: '文本', isSystem: false, id: 'department-name' },
//         { name: '部门主管', type: '人员单选', isSystem: false, id: 'department-manager' },
//         { name: '部门状态', type: '单选', isSystem: false, id: 'department-status' },
//         { name: '结构ID', type: '文本', isSystem: false, id: 'structure-id' },
//         { name: '结构名称', type: '文本', isSystem: false, id: 'structure-name' },
//         { name: '部门层级', type: '数字', isSystem: false, id: 'department-level' },
//         { name: '同级部门排序', type: '数字', isSystem: false, id: 'department-sort' },
//       ],
//     },
//   ],
//   edges: [
//     {
//       source: { cell: 'sub-table', port: 'sub-table-field-3' }, // 从子表的自定义字段
//       target: { cell: 'user-system', port: 'user-system-field-5' }, // 到测试前台的子表字段
//       // label: '主子关系',
//     },
//     {
//       source: { cell: 'test-frontend', port: 'test-frontend-field-5' }, // 从测试前台的数据选择字段
//       target: { cell: 'department', port: 'department-field-3' }, // 到部门的部门名称字段
//       // label: '数据关联',
//     },
//   ],
// };

// 模式切换示例
export const EntityERWithModeSwitch: React.FC<{ refreshEntityList: boolean, setRefreshEntityList: (refresh: boolean) => void, onlyUpdateNode: boolean, setOnlyUpdateNode: (onlyUpdateNode: boolean) => void }> = ({ refreshEntityList, setRefreshEntityList, onlyUpdateNode, setOnlyUpdateNode }) => {
  // const [mode, setMode] = useState<'view' | 'edit'>('view');
  const [data, setData] = useState<EntityERProps['data']>(JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({nodes: [], edges: []})) as unknown as EntityData);
  const [editDrawerVisible, setEditDrawerVisible] = useState(false);
  const [editingNode, setEditingNode] = useState<EntityNode | null>(null);
  const [createFieldModalVisible, setCreateFieldModalVisible] = useState(false);
  const [nodeId, setNodeId] = useState('');
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [createRelationModalVisible, setCreateRelationModalVisible] = useState(false);
  const [updateRelationOptions, setUpdateRelationOptions] = useState(false);
  // const [onlyUpdateNode, setOnlyUpdateNode] = useState(false);

  const handleNodeEdit = (editData: EntityNode) => {
    console.log('节点编辑:', editData);
    // 这里可以更新节点数据
    const { nodes, edges } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({nodes: [], edges: []})) ;
    const newNodes = nodes.map((node: EntityNode) => {
      if (node.id === editData.id) {
        return editData;
      }
      return node;
    });
    localStorage.setItem('entityFormValues', JSON.stringify({nodes: newNodes, edges: edges}));
    setEditDrawerVisible(true);
    setEditingNode(editData);
    setRefreshEntityList(!refreshEntityList);
    setOnlyUpdateNode(true);
  };

  const handleNodeAddField = (id: string) => {
    setCreateFieldModalVisible(true);
    setNodeId(id);
  };

  const handleNodeAddRelation = (id: string) => {
    console.log('添加关联:', id);
    setCreateRelationModalVisible(true);
    setUpdateRelationOptions(true);
    setOnlyUpdateNode(false);
  };

  const handleNodeDelete = (id: string) => {
    console.log('删除节点:', id);
    setDeleteModalVisible(true);
    setNodeId(id);
  };

  const confirmDelete = () => {
    setDeleteLoading(true);
    console.log('删除节点:', nodeId);
    const { nodes, edges } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({nodes: [], edges: []})) ;

    const newNodes = nodes.filter((node: EntityNode) => node.id !== nodeId);
    const newEdges = edges?.filter((edge: EdgeData) => edge.source.cell !== nodeId && edge.target.cell !== nodeId);
    setData({
      nodes: newNodes,
      edges: newEdges,
    });
    localStorage.setItem('entityFormValues', JSON.stringify({nodes: newNodes, edges: newEdges}));
    setDeleteModalVisible(false);
    setDeleteLoading(false);
    setRefreshEntityList(true);
    setOnlyUpdateNode(true);
  };

  const cancelDelete = () => {
    setDeleteModalVisible(false);
  };

  const handleSuccessCallback = () => {
    setRefreshEntityList(true);
    setOnlyUpdateNode(true);
  };

  useEffect(() => {
    if (refreshEntityList) {
      const { nodes, edges } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({nodes: [], edges: []}));
      setData({
        nodes: nodes,
        edges: edges,
      });
      setRefreshEntityList(false);
    }
  }, [refreshEntityList]);

  // useEffect(() => {
  //   const storedData = localStorage.getItem('entityFormValues');
  //   if (storedData) {
  //     const parsedData = JSON.parse(storedData);
  //     setData(parsedData as EntityERProps['data']);
  //   }
  // }, []);

  return (
    <div style={{ height: '100%' }}>
      {/* <div style={{ marginBottom: '16px' }}>
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
      </div> */}
      
      <ERchart
        mode='edit'
        data={data as unknown as EntityERProps['data']}
        onNodeEdit={handleNodeEdit}
        onNodeAddField={handleNodeAddField}
        onNodeAddRelation={handleNodeAddRelation}
        onNodeDelete={handleNodeDelete}
        onlyUpdateNode={onlyUpdateNode}
      />
      <EditDrawer
        visible={editDrawerVisible}
        setVisible={setEditDrawerVisible}
        editingNode={editingNode as EntityNode}
        setEditingNode={(node: EntityNode | null) => setEditingNode(node)}
        onNodeEdit={handleNodeEdit}
        successCallback={handleSuccessCallback}
      />
      <CreateFieldModal
        visible={createFieldModalVisible}
        setVisible={setCreateFieldModalVisible}
        entityId={nodeId}
        successCallback={handleSuccessCallback}
      />
      <CreateRelationModal
        visible={createRelationModalVisible}
        setVisible={setCreateRelationModalVisible}
        successCallback={handleSuccessCallback}
        updateRelationOptions={updateRelationOptions}
        setUpdateRelationOptions={setUpdateRelationOptions}
      />
      {/* 删除确认对话框 */}
      <Modal
        title="确认删除"
        visible={deleteModalVisible}
        onOk={confirmDelete}
        onCancel={cancelDelete}
        confirmLoading={deleteLoading}
        okText="确认删除"
        cancelText="取消"
      >
        <p>确定要删除这个业务实体吗？删除后无法恢复。</p>
      </Modal>
    </div>
  );
};

export default EntityERWithModeSwitch; 