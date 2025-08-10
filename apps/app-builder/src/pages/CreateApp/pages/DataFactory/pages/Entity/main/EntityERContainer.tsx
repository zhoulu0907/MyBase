import React, { useEffect, useState } from 'react';
// import { Button, Space } from '@arco-design/web-react';
import type {
  EdgeData,
  EntityData,
  EntityERProps,
  EntityNode
} from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Button, Modal } from '@arco-design/web-react';
import EditDrawer from '../components/Drawers/EditEntityDrawer';
import FieldDetailDrawer from '../components/Drawers/FieldDetailDrawer';
import ERchart from '../components/ERchart';
import CreateEntityModal from '../components/Modals/CreateEntityModal';
// import CreateFieldModal from '../components/Modals/CreateFieldModal';
import { resouceId } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import { IconPlus } from '@arco-design/web-react/icon';
import { getEntityGraph } from '@onebase/app';
import ConfigFieldModal from '../components/Modals/ConfigFieldModal';
import CreateRelationModal from '../components/Modals/CreateRelationModal';
import styles from '../index.module.less';

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
export const EntityERContainer: React.FC<{
  refreshEntityList: boolean;
  setRefreshEntityList: (refresh: boolean) => void;
  onlyUpdateNode: boolean;
  setOnlyUpdateNode: (onlyUpdateNode: boolean) => void;
}> = ({ refreshEntityList, setRefreshEntityList, onlyUpdateNode, setOnlyUpdateNode }) => {
  // const [mode, setMode] = useState<'view' | 'edit'>('view');
  const [data, setData] = useState<EntityERProps['data']>(
    JSON.parse(
      localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
    ) as unknown as EntityData
  );
  const [editDrawerVisible, setEditDrawerVisible] = useState(false);
  const [editingNode, setEditingNode] = useState<EntityNode | null>(null);
  const [createEntityModalVisible, setCreateEntityModalVisible] = useState(false);
  // const [createFieldModalVisible, setCreateFieldModalVisible] = useState(false);
  const [configFieldModalVisible, setConfigFieldModalVisible] = useState(false);
  const [nodeId, setNodeId] = useState('');
  const [nodedata, setNodedata] = useState<EntityNode | null>(null);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [createRelationModalVisible, setCreateRelationModalVisible] = useState(false);
  const [updateRelationOptions, setUpdateRelationOptions] = useState(false);
  const [fieldDetailDrawerVisible, setFieldDetailDrawerVisible] = useState(false);
  const [selectedFieldId, setSelectedFieldId] = useState<string>('');

  const loadEntityList = async () => {
    const res = await getEntityGraph(resouceId);
    console.log('loadEntityList', res);
    if (res?.entities || res?.relationships) {
      setData({
        nodes: res?.entities || [],
        edges: res?.relationships || []
      });
    }
    // setEntityList(res);
  };

  const handleNodeEdit = (editData: Partial<EntityNode>) => {
    console.log('节点编辑:', editData);
    // 这里可以更新节点数据
    const { nodes, edges } = JSON.parse(
      localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
    );
    const newNodes = nodes.map((node: EntityNode) => {
      if (node.entityId === editData.entityId) {
        return editData;
      }
      return node;
    });
    localStorage.setItem('entityFormValues', JSON.stringify({ nodes: newNodes, edges: edges }));
    setEditDrawerVisible(true);
    setEditingNode(editData as unknown as EntityNode);
    setRefreshEntityList(!refreshEntityList);
    setOnlyUpdateNode(true);
  };

  const handleNodeAddField = (node: EntityNode) => {
    setConfigFieldModalVisible(true);
    setNodedata(node as unknown as EntityNode);
  };

  const handleNodeAddRelation = (id: string) => {
    console.log('添加关联:', id);
    setCreateRelationModalVisible(true);
    setUpdateRelationOptions(true);
    setOnlyUpdateNode(false);
  };

  const handleFieldClick = (fieldId: string) => {
    console.log('字段点击:', fieldId);
    setSelectedFieldId(fieldId);
    setFieldDetailDrawerVisible(true);
  };

  const handleNodeDelete = (id: string) => {
    console.log('删除节点:', id);
    setDeleteModalVisible(true);
    setNodeId(id);
  };

  const confirmDelete = () => {
    setDeleteLoading(true);
    console.log('删除节点:', nodeId);
    const { nodes, edges } = JSON.parse(
      localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
    );

    const newNodes = nodes.filter((node: EntityNode) => node.entityId !== nodeId);
    const newEdges = edges?.filter((edge: EdgeData) => edge.source.cell !== nodeId && edge.target.cell !== nodeId);
    setData({
      nodes: newNodes,
      edges: newEdges
    });
    localStorage.setItem('entityFormValues', JSON.stringify({ nodes: newNodes, edges: newEdges }));
    setDeleteModalVisible(false);
    setDeleteLoading(false);
    setRefreshEntityList(true);
    setOnlyUpdateNode(true);
  };

  const cancelDelete = () => {
    setDeleteModalVisible(false);
  };

  const handleSuccessCallback = async () => {
    await loadEntityList();
    setRefreshEntityList(true);
    setOnlyUpdateNode(true);
  };

  const createEntityCallback = () => {
    setRefreshEntityList(true);
    setOnlyUpdateNode(false);
  };

  useEffect(() => {
    if (refreshEntityList) {
      const { nodes, edges } = JSON.parse(
        localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [], edges: [] })
      );
      setData({
        nodes: nodes,
        edges: edges
      });
      setRefreshEntityList(false);
    }
  }, [refreshEntityList]);

  useEffect(() => {
    loadEntityList();
  }, []);

  // useEffect(() => {
  //   const storedData = localStorage.getItem('entityFormValues');
  //   if (storedData) {
  //     const parsedData = JSON.parse(storedData);
  //     setData(parsedData as EntityERProps['data']);
  //   }
  // }, []);

  return (
    <div style={{ height: '100%' }} className={styles['entity-page-container']}>
      <ERchart
        mode="edit"
        data={data as unknown as EntityERProps['data']}
        onNodeEdit={handleNodeEdit}
        onNodeAddField={handleNodeAddField}
        onNodeAddRelation={handleNodeAddRelation}
        onNodeDelete={handleNodeDelete}
        onFieldClick={handleFieldClick}
        onlyUpdateNode={onlyUpdateNode}
      />
      <Button
        type="primary"
        className={styles['entity-page-create-button']}
        onClick={() => {
          setCreateEntityModalVisible(true);
        }}
      >
        <IconPlus />
        创建业务实体
      </Button>

      {/* 交互弹窗、抽屉、模态框 */}
      <EditDrawer
        visible={editDrawerVisible}
        setVisible={setEditDrawerVisible}
        editingNode={editingNode as EntityNode}
        setEditingNode={(node: EntityNode | null) => setEditingNode(node)}
        onNodeEdit={handleNodeEdit}
        successCallback={handleSuccessCallback}
      />
      <CreateEntityModal
        visible={createEntityModalVisible}
        setVisible={setCreateEntityModalVisible}
        successCallback={createEntityCallback}
      />
      <ConfigFieldModal
        visible={configFieldModalVisible}
        setVisible={setConfigFieldModalVisible}
        entity={nodedata as EntityNode}
        successCallback={handleSuccessCallback}
      />
      <CreateRelationModal
        visible={createRelationModalVisible}
        setVisible={setCreateRelationModalVisible}
        successCallback={handleSuccessCallback}
        updateRelationOptions={updateRelationOptions}
        setUpdateRelationOptions={setUpdateRelationOptions}
      />
      <FieldDetailDrawer
        visible={fieldDetailDrawerVisible}
        setVisible={setFieldDetailDrawerVisible}
        fieldId={selectedFieldId}
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
