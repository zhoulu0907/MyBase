import { Button, Dropdown, Form, Input, Modal, Radio } from '@arco-design/web-react';
import { IconDown, IconPlus } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import styles from './index.module.less';

const { useForm } = Form;

interface ViewProps {}

// 视图组件
const View: React.FC<ViewProps> = ({}) => {
  const viewList = [
    {
      id: '01',
      label: '默认表单视图',
      viewType: 'mix'
    },
    {
      id: '02',
      label: 'xx编辑视图',
      viewType: 'edit'
    },
    {
      id: '03',
      label: 'xx表单视图',
      viewType: 'detail'
    }
  ];
  const [createForm] = useForm();
  const [createViewModalVisible, setCreateViewModalVisible] = useState(false);

  const showViewType = (viewType: string) => {
    switch (viewType) {
      case 'mix':
        return <div className={`${styles.viewLabel} ${styles.mixViewTitle}`}>混合视图</div>;
      case 'edit':
        return <div className={`${styles.viewLabel} ${styles.editViewTitle}`}>编辑视图</div>;
      case 'detail':
        return <div className={`${styles.viewLabel} ${styles.detailViewTitle}`}>详情视图</div>;
    }
  };

  const dropList = (
    <div className={styles.dropList}>
      {viewList.map((item) => (
        <div key={item.id} className={styles.dropItem}>
          <div className={styles.dropItemLabel}>{item.label}</div>
          <div>{showViewType(item.viewType)}</div>
        </div>
      ))}

      <Button type="text" size="mini" className={styles.addViewButton} onClick={() => setCreateViewModalVisible(true)}>
        <IconPlus />
        新增视图
      </Button>
    </div>
  );

  const handleCreateView = () => {
    setCreateViewModalVisible(false);
  };

  return (
    <div className={styles.viewWrapper}>
      <div className={styles.viewTitle}>默认表单视图</div>
      <Dropdown droplist={dropList} position="br">
        <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          {showViewType('mix')}
          <IconDown />
        </span>
      </Dropdown>

      <Modal
        visible={createViewModalVisible}
        onCancel={() => setCreateViewModalVisible(false)}
        onOk={handleCreateView}
        title="新增视图"
        style={{ width: 295 }}
      >
        <Form layout="vertical" form={createForm}>
          <Form.Item label="视图名称" field="viewName" rules={[{ required: true, message: '请输入视图名称' }]}>
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item label="视图类型" field="viewType" rules={[{ required: true, message: '请选择视图类型' }]}>
            <Radio.Group type="button">
              <Radio value="mix">混合视图</Radio>
              <Radio value="edit">编辑视图</Radio>
              <Radio value="detail">详情视图</Radio>
            </Radio.Group>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default View;
