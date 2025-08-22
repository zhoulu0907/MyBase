import { useState } from 'react';
import { Button, Modal, Input, Space, List, Breadcrumb, Avatar, Typography, Spin } from '@arco-design/web-react';
import { IconRight, IconClose } from '@arco-design/web-react/icon';
import { formatDeptAndUsers } from './const';

interface IData {
  children: IData[];
  [property: string]: any;
}
interface IProps {
  title?: string;
  width?: number;
  data: IData;
  loading: boolean;
  visible: boolean;
  onExpand: (value: string) => void;
  onSearch: (value: string) => void;
  onCancel: () => void;
  onConfirm: (value: any[]) => void;
}

// 添加成员
const AddMembers = (props: IProps) => {
  const { title = '选择成员', width = 800, visible, data, loading, onExpand, onSearch, onCancel, onConfirm } = props;

  const renderData = formatDeptAndUsers(data);
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const [selectedMembers, setSelectedMembers] = useState<any[]>([]);
  const [breadcrumbs, setBreadcrumbs] = useState<
    { key?: string; title: string }[]
  >([{ key: renderData.key || '-', title: renderData.title || "根目录" }]);

  const removeMember = (key: string) => {
    const newKeys = selectedKeys.filter(k => k !== key);
    setSelectedKeys(newKeys);
    setSelectedMembers(selectedMembers.filter(m => m.key !== key));
  };

  // 点击部门，进入下级
  const handleDeptClick = (node: any) => {
    onExpand(node.id);
    setBreadcrumbs((prev) => [...prev, { key: node.key, title: node.title, id: node.id }]);
  };

  // 点击面包屑
  const handleBreadcrumbClick = (node: any, index: number) => {
    onExpand(node.id);
    setBreadcrumbs(breadcrumbs.slice(0, index + 1));
  };

  console.log(renderData?.children, 'renderData?.children')

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>{title}</div>}
      onOk={onCancel}
      onCancel={onCancel}
      visible={visible}
      autoFocus={false}
      focusLock={true}
      simple
      unmountOnExit
      closable={true}
      style={{ width }}
      maskClosable={true}
      footer={
        <div style={{ textAlign: 'right' }}>
          <Button type="default" onClick={onCancel} style={{ marginRight: 12 }}>
            取消
          </Button>
          <Button type="primary" disabled={selectedMembers.length === 0} onClick={() => onConfirm(selectedMembers.map(v => v.key))}>
            确定
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
            <Input.Search placeholder="搜索用户或部门" onChange={onSearch} />

            <Breadcrumb separator={<IconRight />}>
              {breadcrumbs.map((node, index) => (
                <Breadcrumb.Item key={node.key} onClick={() => handleBreadcrumbClick(node, index)}>
                  <Typography.Text style={{ cursor: 'pointer' }}>
                    {node.title}
                  </Typography.Text>
                </Breadcrumb.Item>
              ))}
            </Breadcrumb>
          </Space>

          <div style={{overflow: 'hidden auto'}}>
            <Spin loading={loading} block style={{height: '100%'}}>
              {renderData?.children?.map((item: any) =>
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
                      {item.title?.slice(0, 1) || 'U'}
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
                    <Button type="text" onClick={() => handleDeptClick(item)}>下级<IconRight /></Button>
                  </div>
                )
              )}
            </Spin>
          </div>

        </div>

        {/* 右侧 */}
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          flex: 0.9,
        }}>
          <div style={{ width: '300px', height: '100%', paddingLeft: '16px', overflow: 'hidden' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
              <span>已选择: {selectedMembers.length} 个</span>
              <Button
                type="text"
                onClick={() => {
                  setSelectedKeys([]);
                  setSelectedMembers([]);
                }}
              >
                清空
              </Button>
            </div>
            <div style={{height: 'calc(100% - 40px)', overflow: 'auto'}}>
              <List
                split={false}
                bordered={false}
                dataSource={selectedMembers}
                render={(item) => (
                  <List.Item
                    key={item.key}
                    style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '4px 0' }}
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
                        icon={<IconClose />}
                        onClick={() => removeMember(item.key)}
                      />
                    </div>
                  </List.Item>
                )}
              />
              
            </div>
          </div>
        </div>
      </div>
    </Modal>
  );
};

export default AddMembers;
