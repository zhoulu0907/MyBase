import TickSVG from '@/assets/images/tick.svg';
import { Dropdown, Form, Input, Menu, Modal, Radio } from '@arco-design/web-react';
import { IconDown, IconPlus } from '@arco-design/web-react/icon';
import { generateId, ViewType, type PageView } from '@onebase/app';
import {
  createPageEditorSignal,
  useEditorSignalMap,
  useFormEditorSignal,
  usePageEditorSignal,
  usePageViewEditorSignal
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useState } from 'react';
import styles from './index.module.less';

const { useForm } = Form;

interface ViewProps {
  pageSetId: string;
}

// 视图组件
const View: React.FC<ViewProps> = ({ pageSetId }) => {
  useSignals();

  const { pageViews, curViewId, setCurViewId } = usePageViewEditorSignal;

  const [createForm] = useForm();
  const [createViewModalVisible, setCreateViewModalVisible] = useState(false);
  const [dropListVisible, setDropListVisible] = useState(false);

  const { pageComponentSchemas, components, layoutSubComponents } = usePageEditorSignal();

  const showViewType = (item: PageView | null) => {
    if (!item) {
      return <div></div>;
    }
    if (item.detailViewMode && item.editViewMode) {
      return <div className={`${styles.viewLabel} ${styles.mixViewTitle}`}>混合视图</div>;
    }
    if (item.editViewMode) {
      return <div className={`${styles.viewLabel} ${styles.editViewTitle}`}>编辑视图</div>;
    }
    if (item.detailViewMode) {
      return <div className={`${styles.viewLabel} ${styles.detailViewTitle}`}>详情视图</div>;
    }

    return <div></div>;
  };

  const handleSelectView = (id: string) => {
    // 保存当前配置到editorSignalMap，切换视图后，载入新的配置到useFormEditorSignal
    useEditorSignalMap.get(curViewId.value)!.setComponents(components);

    useEditorSignalMap.get(curViewId.value)!.loadPageComponentSchemas(pageComponentSchemas);
    useEditorSignalMap.get(curViewId.value)!.loadLayoutSubComponents(layoutSubComponents);

    setCurViewId(id);

    useFormEditorSignal.setComponents(useEditorSignalMap.get(id)!.components.value);
    useFormEditorSignal.loadPageComponentSchemas(useEditorSignalMap.get(id)!.pageComponentSchemas.value);
    useFormEditorSignal.loadLayoutSubComponents(useEditorSignalMap.get(id)!.layoutSubComponents.value);
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
                {view.id === curViewId.value ? (
                  <img className={styles.dropItemIcon} src={TickSVG} alt="tick" />
                ) : (
                  <div style={{ width: '16px', height: '16px' }}></div>
                )}
                <div className={styles.dropItemLabel}>{view.pageName}</div>
                <div>{showViewType(view)}</div>
              </div>
            </Menu.Item>
          );
        })}

        <Menu.Item key="addView">
          <div className={styles.addViewButton} onClick={() => setCreateViewModalVisible(true)}>
            <IconPlus fontSize={14} style={{ marginRight: '2px', color: '#029e9e' }} />
            <div>新增视图</div>
          </div>
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
        // 创建临时视图
        const pageId = await generateId();
        usePageViewEditorSignal.addPageView({
          id: pageId,
          pageName: createForm.getFieldValue('viewName'),
          //   TODO(mickey): 定义成常量， 重构categoryType
          pageType: 'form',
          editViewMode:
            createForm.getFieldValue('viewType') === ViewType.EDIT ||
            createForm.getFieldValue('viewType') === ViewType.MIX
              ? 1
              : 0,
          detailViewMode:
            createForm.getFieldValue('viewType') === ViewType.DETAIL ||
            createForm.getFieldValue('viewType') === ViewType.MIX
              ? 1
              : 0,
          isDefaultEditViewMode: 0,
          isDefaultDetailViewMode: 0,
          created: true
        });

        useEditorSignalMap.set(pageId, createPageEditorSignal());

        setCreateViewModalVisible(false);
        // setCurViewId(pageId);
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
