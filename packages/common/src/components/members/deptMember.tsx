import { useCallback, useEffect, useState } from 'react';
import { Button, Input, Space, List, Breadcrumb, Avatar, Typography, Spin, Radio } from '@arco-design/web-react';
import { IconRight, IconClose } from '@arco-design/web-react/icon';
import { formatDeptAndUsers } from './const';

interface IData {
  children: IData[];
  [property: string]: any;
}
interface IProps {
  title?: string;
  data: IData;
  loading: boolean;
  visible: boolean;
  selectedMembers: any[];
  isMultiple?: boolean;
  onExpand: (value: string) => void;
  onSearch: (value: string) => void;
  onUpdateSelectedMembers?: (members: any[]) => void;
}

// 添加成员
const DeptMember = (props: IProps) => {
  const {
    title = '选择成员',
    visible,
    data,
    loading,
    selectedMembers,
    isMultiple = true,
    onExpand,
    onSearch,
    onUpdateSelectedMembers
  } = props;

  const renderData = formatDeptAndUsers(data);
  const [selectedKeys, setSelectedKeys] = useState<string[]>(() => {
    return selectedMembers.map((member) => member.key);
  });
  const [breadcrumbs, setBreadcrumbs] = useState<{ key?: string; title: string; id?: string }[]>([
    { key: renderData.key || '-', title: renderData.title || '根目录' }
  ]);
  const isSelectDepartment = title === 'specifiedDepartment';

  useEffect(() => {
    setSelectedKeys(selectedMembers.map((member) => member.key));
  }, [selectedMembers, visible]);

  const removeMember = (key: string) => {
    const newKeys = selectedKeys.filter((k) => k !== key);
    setSelectedKeys(newKeys);

    const newSelectedMembers = selectedMembers.filter((m) => m.key !== key);
    if (onUpdateSelectedMembers) {
      onUpdateSelectedMembers(newSelectedMembers);
    }
  };

  // 重置状态函数 待定
  const resetState = useCallback(() => {
    // 重置面包屑
    setBreadcrumbs([{ key: renderData.key || '-', title: renderData.title || '根目录' }]);
    // 清空已选中的用户
    setSelectedKeys([]);
    if (onUpdateSelectedMembers) {
      onUpdateSelectedMembers([]);
    }
  }, [renderData, onUpdateSelectedMembers]);

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

  // 构建部门完整路径
  const buildDepartmentPath = () => {
    const deptNames = breadcrumbs.slice(1).map((breadcrumb) => breadcrumb.title);
    return deptNames.length > 0 ? deptNames.join('/') : '未分配部门';
  };

  return (
      <div
        style={{
          height: 500,
          border: '1px solid #e5e6eb',
          borderRadius: 4,
          boxSizing: 'border-box',
          padding: 12,
          display: 'flex',
          flexDirection: 'row',
          justifyContent: 'space-between',
          position: 'relative'
        }}
      >
        <div
          style={{
            flex: 1.1,
            display: 'flex',
            flexDirection: 'column',
            marginRight: 24
          }}
        >
          <Space direction="vertical">
            <Input.Search
              placeholder={title === 'specifiedDepartment' ? '搜索部门' : '搜索用户或部门'}
              onChange={onSearch}
              onPressEnter={(e) => {
                console.log('ipt关键字', e);
                const value = e.target.value;
                onSearch(value);
              }}
            />

            <Breadcrumb separator={<IconRight />}>
              {breadcrumbs.map((node, index) => (
                <Breadcrumb.Item key={node.key} onClick={() => handleBreadcrumbClick(node, index)}>
                  <Typography.Text style={{ cursor: 'pointer' }}>{node.title}</Typography.Text>
                </Breadcrumb.Item>
              ))}
            </Breadcrumb>
          </Space>

          <div style={{ overflow: 'hidden auto' }}>
            <Spin loading={loading} block style={{ height: '100%' }}>
              {renderData?.children?.map((item: any) =>
                item.type === 'user' && !isSelectDepartment ? (
                  <div key={`user-${item.key}`} style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}>
                    {isMultiple ? (
                        <input
                      type="checkbox"
                      checked={selectedKeys.includes(item.key)}
                      onChange={(e) => {
                        const checked = e.target.checked;
                        if (checked) {
                          setSelectedKeys([...selectedKeys, item.key]);
                          const newSelectedMembers = [
                            ...selectedMembers,
                            {
                              key: item.key,
                              name: item.title,
                              department: buildDepartmentPath() // 使用构建的完整路径
                            }
                          ];
                          if (onUpdateSelectedMembers) {
                            onUpdateSelectedMembers(newSelectedMembers);
                          }
                        } else {
                          removeMember(item.key);
                        }
                      }}
                      style={{ cursor: 'pointer' }}
                    />
                    ) : (
                        <Radio key={item.key}
                           checked={selectedKeys.includes(item.key)}
                            onChange={() => {
                                setSelectedKeys([item.key])
                                const newSelectedMembers = [
                                    {
                                        key: item.key,
                                        name: item.title,
                                        department: buildDepartmentPath()
                                    }];
                                if (onUpdateSelectedMembers) {
                                    onUpdateSelectedMembers(newSelectedMembers);
                                }}}/>
                    )}
                    <Avatar size={24} style={{ backgroundColor: 'rgb(var(--primary-6))' }}>
                      {item.title?.slice(0, 1) || 'U'}
                    </Avatar>
                    <span>{item.title}</span>
                  </div>
                ) : item.type !== 'user' && isSelectDepartment ? (
                  <div
                    key={`dept-${item.key}`}
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      gap: 8,
                      marginTop: 8
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}>
                      {isMultiple ? (
                        <input
                        type="checkbox"
                        checked={selectedKeys.includes(item.key)}
                        onChange={(e) => {
                          const checked = e.target.checked;
                          if (checked) {
                            setSelectedKeys([...selectedKeys, item.key]);
                            const newSelectedMembers = [
                              ...selectedMembers,
                              {
                                key: item.key,
                                name: item.title,
                                department: buildDepartmentPath()
                              }
                            ];
                            if (onUpdateSelectedMembers) {
                              onUpdateSelectedMembers(newSelectedMembers);
                            }
                          } else {
                            removeMember(item.key);
                          }
                        }}
                        style={{ cursor: 'pointer' }}
                      />
                      ) : (
                        <Radio key={item.key}
                             checked={selectedKeys.includes(item.key)}
                             onChange={(e) => {
                                console.log(e)
                                setSelectedKeys([item.key]);
                                const newSelectedMembers = [
                                    {
                                        key: item.key,
                                        name: item.title,
                                        department: buildDepartmentPath()
                                    }];
                                if (onUpdateSelectedMembers) {
                                    onUpdateSelectedMembers(newSelectedMembers);
                                }}}/>
                      )}
                      <Avatar size={24} style={{ backgroundColor: 'rgb(var(--primary-6))' }}>
                        部
                      </Avatar>
                      <span>{item.title}</span>
                    </div>
                    <Button type="text" onClick={() => handleDeptClick(item)}>
                      下级
                      <IconRight />
                    </Button>
                  </div>
                ) : item.type !== 'user' && !isSelectDepartment ? (
                  <div
                    key={`dept-${item.key}`}
                    style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 8 }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <Avatar size={24} style={{ backgroundColor: 'rgb(var(--primary-6))' }}>
                        部
                      </Avatar>
                      <span>{item.title}</span>
                    </div>
                    <Button type="text" onClick={() => handleDeptClick(item)}>
                      下级
                      <IconRight />
                    </Button>
                  </div>
                ) : null
              )}
            </Spin>
          </div>
        </div>

        {/* 右侧 */}
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            flex: 0.9
          }}
        >
          <div style={{ width: '300px', height: '100%', paddingLeft: '16px', overflow: 'hidden' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
              <span>已选择: {selectedMembers.length} 个</span>
              <Button
                type="text"
                onClick={() => {
                  setSelectedKeys([]);
                  if (onUpdateSelectedMembers) {
                    onUpdateSelectedMembers([]);
                  }
                }}
              >
                清空
              </Button>
            </div>
            <div style={{ height: 'calc(100% - 40px)', overflow: 'auto' }}>
              <List
                split={false}
                bordered={false}
                dataSource={selectedMembers}
                render={(item) => (
                  <List.Item
                    key={item.key}
                    style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '4px 0' }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <Avatar size={24} style={{ backgroundColor: 'rgb(var(--primary-6))' }}>
                          {item.name[0]}
                        </Avatar>
                        <div>
                          <div>{item.name}</div>
                          <div style={{ color: '#ccc' }}>{item.department || '未分配部门'}</div>
                        </div>
                      </div>

                      <Button type="text" icon={<IconClose />} onClick={() => removeMember(item.key)} />
                    </div>
                  </List.Item>
                )}
              />
            </div>
          </div>
        </div>
      </div>
  );
};

export default DeptMember;
