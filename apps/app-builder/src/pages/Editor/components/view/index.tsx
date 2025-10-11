import { Button, Dropdown, Form, Input, Modal, Radio } from '@arco-design/web-react';
import { IconDown, IconPlus } from '@arco-design/web-react/icon';
import { createPageView, listPageView, type PageView } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const { useForm } = Form;

interface ViewProps {
  pageSetId: string;
}

// 视图组件
const View: React.FC<ViewProps> = ({ pageSetId }) => {
  // TODO(mickey): 放到single中
  const [viewList, setViewList] = useState<PageView[]>([]);
  const [createForm] = useForm();
  const [createViewModalVisible, setCreateViewModalVisible] = useState(false);

  useEffect(() => {
    handleListPageView();
  }, [pageSetId]);

  const handleListPageView = async () => {
    const res = await listPageView({
      pageSetId: pageSetId
    });

    console.log(res);

    if (res && res.pages) {
      const newViewList = res.pages.map((item: PageView) => ({
        ...item
      }));

      setViewList(newViewList);
    }
  };

  const showViewType = (item: PageView | null) => {
    if (!item) {
      return <div className={`${styles.viewLabel} ${styles.mixViewTitle}`}>默认视图</div>;
    }
    if (item.detailViewMode && item.editViewMode) {
      return <div className={`${styles.viewLabel} ${styles.mixViewTitle}`}>混合视图</div>;
    }
    if (item.detailViewMode && item.editViewMode) {
      return <div className={`${styles.viewLabel} ${styles.editViewTitle}`}>编辑视图</div>;
    }
    if (item.detailViewMode) {
      return <div className={`${styles.viewLabel} ${styles.detailViewTitle}`}>详情视图</div>;
    }

    return <div className={`${styles.viewLabel} ${styles.mixViewTitle}`}>默认视图</div>;
  };

  const selectDefaultView = () => {
    let defaultView = null;
    if (viewList.length > 0) {
      defaultView = viewList[0];
    }
    viewList.forEach((item) => {
      if (item.isDefaultEditViewMode || item.isDefaultDetailViewMode) {
        defaultView = item;
      }
    });

    return defaultView;
  };

  const dropList = (
    <div className={styles.dropList}>
      {viewList.map((item) => (
        <div key={item.id} className={styles.dropItem}>
          <div className={styles.dropItemLabel}>{item.pageName}</div>
          <div>{showViewType(item)}</div>
        </div>
      ))}

      <Button type="text" size="mini" className={styles.addViewButton} onClick={() => setCreateViewModalVisible(true)}>
        <IconPlus />
        新增视图
      </Button>
    </div>
  );

  const handleCloseModal = () => {
    setCreateViewModalVisible(false);
    createForm.resetFields();
  };

  const handleCreateView = async () => {
    createForm
      .validate()
      .then(async () => {
        const res = await createPageView({
          pageSetId: pageSetId,
          viewType: createForm.getFieldValue('viewType'),
          viewName: createForm.getFieldValue('viewName')
        });
        console.log(res);
        setCreateViewModalVisible(false);
      })
      .catch((e) => {
        console.log(e.errors);
      });
  };

  return (
    <div className={styles.viewWrapper}>
      <div className={styles.viewTitle}>{selectDefaultView()?.pageName}</div>
      <Dropdown droplist={dropList} position="br">
        <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          {showViewType(selectDefaultView())}
          <IconDown />
        </span>
      </Dropdown>

      <Modal
        visible={createViewModalVisible}
        onCancel={handleCloseModal}
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
