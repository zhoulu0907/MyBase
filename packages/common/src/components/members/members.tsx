import { useState } from 'react';
import { Button, Modal, Input, Space, List, Breadcrumb, Avatar, Typography } from '@arco-design/web-react';
import { IconRight, IconDelete } from '@arco-design/web-react/icon';

const treeDataMock = [
  {
    key: 'tenant',
    title: '这是一个租户',
    children: [
      {
        key: 'dept1',
        title: '这是一个一级部门',
        children: [
          { key: 'wuxian', title: '巫炫', type: 'user' },
          { key: 'zhangsan', title: '张三', type: 'user' },
        ]
      },
      {
        key: 'dept2',
        title: '这是一个一级部门',
        children: [
          { key: 'lisi', title: '李四', type: 'user' },
          { key: 'wangwu', title: '王五', type: 'user' }
        ]
      },
      {
        key: 'dept3',
        title: '这是一个一级部门',
        children: [
          { key: 'zhaoliu', title: '赵六', type: 'user' }
        ]
      },
      { key: 'zhangsan', title: '张三', type: 'user' },
    ]
  },
];

interface IProps {
  title: string;
  width: number;
  visible: boolean;
  treeData: any[];
  cancel: () => void;
  onConfirm: () => void;
}

// 添加成员
const AddMembers = (props: IProps) => {
  const { title = '选择成员', width = 800, visible, treeData = treeDataMock, cancel } = props;

  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const [selectedMembers, setSelectedMembers] = useState<any[]>([]);
  const [path, setPath] = useState<any[]>([treeData[0]]); // 当前路径（面包屑）

  // 获取当前层级数据
  const currentNode = path[path.length - 1];
  const currentChildren = currentNode.children || [];

  const onCheck = (checkedKeys: any, info: any) => {
    const checkedUsers = info.checkedNodes
      .filter((node: any) => node.type === 'user')
      .map((node: any) => ({
        key: node.key,
        name: node.title
      }));

    setSelectedKeys(checkedKeys);
    setSelectedMembers(checkedUsers);
  };

  const removeMember = (key: string) => {
    const newKeys = selectedKeys.filter(k => k !== key);
    setSelectedKeys(newKeys);
    setSelectedMembers(selectedMembers.filter(m => m.key !== key));
  };

  const goToChild = (node: any) => {
    setPath([...path, node]);
  };

  const goToBreadcrumb = (index: number) => {
    setPath(path.slice(0, index + 1));
  };

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>{title}</div>}
      onOk={cancel}
      onCancel={cancel}
      visible={visible}
      autoFocus={false}
      focusLock={true}
      simple
      closable={true}
      style={{ width }}
      maskClosable={true}
      footer={
        <div style={{ textAlign: 'right' }}>
          <Button type="default" onClick={cancel} style={{ marginRight: 12 }}>
            取消
          </Button>
          <Button type="primary" onClick={cancel}>
            确认
          </Button>
        </div>
      }
    >
      <div style={{
        height: 500,
        border: '1px solid #e5e6eb',
        borderRadius: 4,
        boxSizing: 'border-box',
        padding: 12,
        display: 'flex',
        flexDirection: 'row',
        justifyContent: 'space-between',
        position: 'relative',
      }}>
        <div style={{
          flex: 1.1,
          display: 'flex',
          flexDirection: 'column',
          marginRight: 24,
        }}>
          <Space direction='vertical'>
            <Input.Search placeholder="搜索用户或部门" />

            <Breadcrumb separator={<IconRight />}>
              {path.map((node, index) => (
                <Breadcrumb.Item key={node.key} onClick={() => goToBreadcrumb(index)}>
                  <Typography.Text style={{ cursor: 'pointer' }}>
                    {node.title}
                  </Typography.Text>
                </Breadcrumb.Item>
              ))}
            </Breadcrumb>
          </Space>

          <div>
            {currentChildren.map((item: any) =>
              item.type === 'user' ? (
                <div key={item.key} style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}>
                  <input
                    type="checkbox"
                    checked={selectedKeys.includes(item.key)}
                    onChange={(e) => {
                      const checked = e.target.checked;
                      if (checked) {
                        setSelectedKeys([...selectedKeys, item.key]);
                        setSelectedMembers([...selectedMembers, { key: item.key, name: item.title }]);
                      } else {
                        removeMember(item.key);
                      }
                    }}
                    style={{cursor: 'pointer'}}
                  />
                  <Avatar size={24} style={{ backgroundColor: 'rgb(var(--primary-6))' }}>
                    {item.title[0]}
                  </Avatar>
                  <span>{item.title}</span>
                </div>
              ) : (
                <div
                  key={item.key}
                  style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 8 }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Avatar size={24} style={{ backgroundColor: 'rgb(var(--primary-6))' }}>部</Avatar>
                    <span>{item.title}</span>
                  </div>
                  <Button type="text" onClick={() => goToChild(item)}>下级</Button>
                </div>
              )
            )}
          </div>
        </div>

        {/* 右侧 */}
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          flex: 0.9,
        }}>
          <div style={{ width: '300px', paddingLeft: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
              <span>已选择: {selectedMembers.length} 个</span>
              <Button
                type="text"
                size="mini"
                onClick={() => {
                  setSelectedKeys([]);
                  setSelectedMembers([]);
                }}
              >
                清空
              </Button>
            </div>
            <List
              bordered={false}
              dataSource={selectedMembers}
              render={(item) => (
                <List.Item
                  key={item.key}
                  style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <Avatar size={24} style={{ backgroundColor: 'rgb(var(--primary-6))' }}>
                        {item.name[0]}
                      </Avatar>
                      <span>{item.name}</span>
                    </div>

                    <Button
                      type="text"
                      icon={<IconDelete />}
                      onClick={() => removeMember(item.key)}
                    />
                  </div>
                </List.Item>
              )}
            />
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default AddMembers;
