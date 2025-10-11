import { Button, Dropdown, Form, Input, Menu, Modal, Radio } from '@arco-design/web-react';
import { IconDown, IconPlus } from '@arco-design/web-react/icon';
import { createPageView, listPageView, ViewType, type PageView } from '@onebase/app';
import { usePageViewEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const { useForm } = Form;

interface ViewProps {
  pageSetId: string;
}

// 视图组件
const View: React.FC<ViewProps> = ({ pageSetId }) => {
  useSignals();

  const { pageViews, setPageViews, curViewId, setCurViewId } = usePageViewEditorSignal;

  const [createForm] = useForm();
  const [createViewModalVisible, setCreateViewModalVisible] = useState(false);
  const [dropListVisible, setDropListVisible] = useState(false);

  useEffect(() => {
    handleListPageView();
  }, [pageSetId]);

  const handleListPageView = async () => {
    const res = await listPageView({
      pageSetId: pageSetId
    });

    if (res && res.pages) {
      const newCurViewId = res.pages.find(
        (item: PageView) => item.isDefaultEditViewMode || item.isDefaultDetailViewMode
      )?.id;

      if (newCurViewId) {
        setCurViewId(newCurViewId);
      }

      setPageViews(res.pages);
    }
  };

  const showViewType = (item: PageView | null) => {
    if (!item) {
      return <div></div>;
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

    return <div></div>;
  };

  const handleSelectView = (id: string) => {
    setCurViewId(id);
  };

  const dropList = (
    <div className={styles.dropList}>
      <Menu
        onClickMenuItem={() => {
          setDropListVisible(false);
        }}
      >
        {Object.entries(pageViews.value).map(([id, view]: [string, any]) => {
          return (
            <Menu.Item key={id} onClick={() => handleSelectView(id)}>
              <div key={id} className={styles.dropItem}>
                <div className={styles.dropItemLabel}>{view.pageName}</div>
                <div>{showViewType(view)}</div>
              </div>
            </Menu.Item>
          );
        })}

        <Menu.Item key="addView">
          <Button
            type="text"
            size="mini"
            className={styles.addViewButton}
            onClick={() => setCreateViewModalVisible(true)}
          >
            <IconPlus />
            新增视图
          </Button>
        </Menu.Item>
      </Menu>
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

        setCreateViewModalVisible(false);
      })
      .catch((e) => {
        console.log(e.errors);
      });
  };

  return (
    <div className={styles.viewWrapper}>
      <div className={styles.viewTitle}>{pageViews.value[curViewId.value]?.pageName}</div>
      <Dropdown
        droplist={dropList}
        position="bl"
        trigger="click"
        popupVisible={dropListVisible}
        onVisibleChange={(visible) => {
          setDropListVisible(visible);
        }}
      >
        <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          {showViewType(pageViews.value[curViewId.value])}
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
              <Radio value={ViewType.MIX}>混合视图</Radio>
              <Radio value={ViewType.EDIT}>编辑视图</Radio>
              <Radio value={ViewType.DETAIL}>详情视图</Radio>
            </Radio.Group>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default View;
