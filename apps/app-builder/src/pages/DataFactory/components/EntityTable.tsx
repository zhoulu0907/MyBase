import { Button, Table, type TableColumnProps, Tag } from '@arco-design/web-react';

// 定义实体节点类型
interface EntityNode {
  id: string;
  title: string;
  x: number;
  y: number;
  fields: Array<{
    id: string;
    name: string;
    type: string;
    isSystem: boolean;
  }>;
}

// 使用 EntityERExample 中的 mockData
const mockData = {
  nodes: [
    {
      id: 'sub-table',
      title: '子表',
      x: 100,
      y: 100,
      fields: [
        {
          id: 'sub-table-field-3',
          name: '日期时间',
          type: '日期时间',
          isSystem: true
        },
        { id: 'sub-table-field-4', name: '选择', type: '选择', isSystem: true }
      ]
    },
    {
      id: 'user-system',
      title: '用户系统对象',
      x: 400,
      y: -100,
      fields: [
        {
          id: 'user-system-field-2',
          name: '用户口',
          type: '文本',
          isSystem: true
        },
        {
          id: 'user-system-field-3',
          name: '姓名',
          type: '文本',
          isSystem: true
        },
        {
          id: 'user-system-field-4',
          name: '头像',
          type: '文本',
          isSystem: false
        },
        {
          id: 'user-system-field-5',
          name: '手机号',
          type: '文本',
          isSystem: false
        },
        {
          id: 'user-system-field-6',
          name: '邮箱',
          type: '文本',
          isSystem: false
        },
        {
          id: 'user-system-field-7',
          name: '工号',
          type: '文本',
          isSystem: false
        },
        {
          id: 'user-system-field-8',
          name: '上级',
          type: '人员单选',
          isSystem: false
        },
        {
          id: 'user-system-field-9',
          name: '类型',
          type: '单选',
          isSystem: false
        },
        {
          id: 'user-system-field-10',
          name: '账号状态',
          type: '单选',
          isSystem: false
        }
      ]
    },
    {
      id: 'test-frontend',
      title: '测试前台',
      x: 800,
      y: 0,
      fields: [
        // 系统字段 (13个)
        { name: 'id', type: '文本', isSystem: true, id: 'id' },
        { name: 'created_at', type: '文本', isSystem: true, id: 'created_at' },
        { name: 'updated_at', type: '文本', isSystem: true, id: 'updated_at' },
        { name: 'created_by', type: '文本', isSystem: true, id: 'created_by' },
        { name: 'updated_by', type: '文本', isSystem: true, id: 'updated_by' },
        { name: 'deleted_at', type: '文本', isSystem: true, id: 'deleted_at' },
        { name: 'deleted_by', type: '文本', isSystem: true, id: 'deleted_by' },
        { name: 'version', type: '文本', isSystem: true, id: 'version' },
        { name: 'status', type: '文本', isSystem: true, id: 'status' },
        { name: 'tenant_id', type: '文本', isSystem: true, id: 'tenant_id' },
        { name: 'org_id', type: '文本', isSystem: true, id: 'org_id' },
        { name: 'owner_id', type: '文本', isSystem: true, id: 'owner_id' },
        { name: 'owner_type', type: '文本', isSystem: true, id: 'owner_type' },
        // 自定义字段 (5个)
        { name: '单行输入', type: '文本', isSystem: false, id: 'single-input' },
        { name: '子表', type: '主子关系', isSystem: false, id: 'sub-table' },
        {
          name: '数据选择',
          type: '数据多选',
          isSystem: false,
          id: 'data-select'
        }
      ]
    },
    {
      id: 'department',
      title: '部门',
      x: 1200,
      y: 100,
      fields: [
        { name: '部门ID', type: '文本', isSystem: true, id: 'department-id' },
        {
          name: '部门编号',
          type: '文本',
          isSystem: true,
          id: 'department-code'
        },
        {
          name: '部门名称',
          type: '文本',
          isSystem: false,
          id: 'department-name'
        },
        {
          name: '部门主管',
          type: '人员单选',
          isSystem: false,
          id: 'department-manager'
        },
        {
          name: '部门状态',
          type: '单选',
          isSystem: false,
          id: 'department-status'
        },
        { name: '结构ID', type: '文本', isSystem: false, id: 'structure-id' },
        {
          name: '结构名称',
          type: '文本',
          isSystem: false,
          id: 'structure-name'
        },
        {
          name: '部门层级',
          type: '数字',
          isSystem: false,
          id: 'department-level'
        },
        {
          name: '同级部门排序',
          type: '数字',
          isSystem: false,
          id: 'department-sort'
        }
      ]
    }
  ],
  edges: [
    {
      source: { cell: 'sub-table', port: 'sub-table-field-3' },
      target: { cell: 'user-system', port: 'user-system-field-5' }
    },
    {
      source: { cell: 'test-frontend', port: 'test-frontend-field-5' },
      target: { cell: 'department', port: 'department-field-3' }
    }
  ]
};

const columns: TableColumnProps[] = [
  {
    title: '实体名称',
    dataIndex: 'title',
    key: 'title'
  },
  {
    title: '实体ID',
    dataIndex: 'id',
    key: 'id'
  },
  {
    title: '字段数量',
    dataIndex: 'fieldCount',
    key: 'fieldCount',
    render: (_, record: EntityNode) => record.fields.length
  },
  {
    title: '系统字段',
    dataIndex: 'systemFieldCount',
    key: 'systemFieldCount',
    render: (_, record: EntityNode) => {
      const systemFields = record.fields.filter((field) => field.isSystem);
      return <Tag color="blue">{systemFields.length} 个</Tag>;
    }
  },
  {
    title: '自定义字段',
    dataIndex: 'customFieldCount',
    key: 'customFieldCount',
    render: (_, record: EntityNode) => {
      const customFields = record.fields.filter((field) => !field.isSystem);
      return <Tag color="green">{customFields.length} 个</Tag>;
    }
  },
  {
    title: '位置',
    dataIndex: 'position',
    key: 'position',
    render: (_, record: EntityNode) => `(${record.x}, ${record.y})`
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
    render: (_: unknown, _record: EntityNode) => (
      <div style={{ display: 'flex', gap: '8px' }}>
        <Button type="primary" size="mini">
          查看
        </Button>
        <Button size="mini">编辑</Button>
        <Button type="outline" size="mini" status="danger">
          删除
        </Button>
      </div>
    ),
    fixed: 'right',
    width: 200
  }
];

const EntityTable = () => {
  return (
    <Table
      columns={columns}
      data={mockData.nodes}
      rowKey="id"
      pagination={{
        pageSize: 10,
        showTotal: true,
        showJumper: true
      }}
      style={{ margin: '16px' }}
    />
  );
};

export default EntityTable;
