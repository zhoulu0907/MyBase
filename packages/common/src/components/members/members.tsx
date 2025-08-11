import { useState } from 'react';
import { Button, Modal, Input, Tree, Space, List } from '@arco-design/web-react';
import { IconUser, IconClose, IconBranch } from '@arco-design/web-react/icon';

interface IProps {
  visible: boolean;
  cancel: () => void;
}
// 添加成员
const AddMembers = (props: IProps) => {
  const { visible, cancel } = props;

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>选择成员</div>}
      onOk={cancel}
      onCancel={cancel}
      visible={visible}
      autoFocus={false}
      focusLock={true}
      simple
      closable={true}
      style={{ width: 716 }}
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
      <SelectContactModal />
    </Modal>
  );
};

// 树形数据
const treeData = [
  {
    key: '1',
    title: '帮助中心测试',
    icon: <IconBranch />,
    children: [{ key: '2', title: '总裁办', icon: <IconBranch /> }]
  },
  {
    key: '3',
    title: '产品中心',
    icon: <IconBranch />,
    children: [
      {
        key: '4',
        title: '产品1部',
        icon: <IconBranch />,
        children: [
          { key: '5', title: '陈奕迅', icon: <IconUser /> },
          { key: '6', title: '刘德华', icon: <IconUser /> },
          { key: '7', title: '刘德华', icon: <IconUser /> },
          { key: '8', title: '刘德华', icon: <IconUser /> },
          { key: '9', title: '刘德华', icon: <IconUser /> },
          { key: '10', title: '刘德华', icon: <IconUser /> },
          { key: '11', title: '刘德华', icon: <IconUser /> },
          { key: '12', title: '刘德华', icon: <IconUser /> },
          { key: '13', title: '刘德华', icon: <IconUser /> },
          { key: '14', title: '刘德华', icon: <IconUser /> },
          { key: '15', title: '刘德华', icon: <IconUser /> },
          { key: '16', title: '刘德华', icon: <IconUser /> },
          { key: '17', title: '刘德华', icon: <IconUser /> },
          { key: '18', title: '刘德华', icon: <IconUser /> }
        ]
      }
    ]
  },
  { key: '19', title: '研发中心', icon: <IconBranch /> },
  { key: '20', title: '销售部', icon: <IconBranch /> }
];

// key 到 title 的映射（右侧显示用）
const keyTitleMap = {};
// @ts-ignore
function buildKeyTitleMap(nodes) {
  // @ts-ignore
  nodes.forEach((node) => {
    // @ts-ignore
    keyTitleMap[node.key] = node.title;
    if (node.children) buildKeyTitleMap(node.children);
  });
}

buildKeyTitleMap(treeData);

const SelectContactModal = () => {
  const [_activeTab, setActiveTab] = useState('0');
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  // 树节点点击事件
  // @ts-ignore
  const handleTreeSelect = (selectedKeysArr, { selected, node }) => {
    const key = node.key;
    // 只有叶子节点才可添加 node.props.isLeaf
    if (selected && !selectedKeys.includes(key)) {
      setSelectedKeys([...selectedKeys, key]);
    }
  };

  // 右侧移除
  // @ts-ignore
  const handleRemove = (key) => {
    setSelectedKeys(selectedKeys.filter((k) => k !== key));
  };

  return (
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
        <Input placeholder="搜索用户、群组、部门或用户组" />

        <Tree
          treeData={treeData}
          blockNode
          selectable
          selectedKeys={[]}
          onSelect={handleTreeSelect}
          icon={(node: any) => node.icon}
          style={{
            height: '100%',
            overflowY: 'auto',
            marginTop: 15,
          }}
        />
      </div>

      <div style={{
        display: 'flex',
        flexDirection: 'column',
        flex: 0.9,
      }}>
        已选择：{selectedKeys.length}个<br />
        <br />
        <List
          bordered={false}
          dataSource={selectedKeys}
          style={{}}
          wrapperStyle={{
            // height: '100%',
            overflow: 'auto',
            flex: 1
          }}
          render={(key) => (
            <Space
              key={key}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                marginBottom: 12,
                paddingRight: 24
              }}
            >
              {/* @ts-ignore */}
              {keyTitleMap[key]}
              <Button type="text" size="mini" shape="circle" icon={<IconClose />} onClick={() => handleRemove(key)} />
            </Space>
          )}
        />
      </div>
    </div>
  );
};

export default AddMembers;
