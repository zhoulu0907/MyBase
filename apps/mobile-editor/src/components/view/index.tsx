import CopySVG from '@/assets/images/copy.svg';
import TickSVG from '@/assets/images/tick.svg';
import { Dropdown, Form, Input, Menu, Modal, Radio } from '@arco-design/web-react';
import { IconDown, IconEdit, IconPlus } from '@arco-design/web-react/icon';
import { generateId, ViewType, type PageView } from '@onebase/app';
import {
  createPageEditorSignal,
  type EditConfig
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import styles from './index.module.less';

const { useForm } = Form;

interface ViewProps {
  pageSetId: string;
  components: EditConfig[];
  pageComponentSchemas: { [key: string]: EditConfig };
  layoutSubComponents: { [key: string]: any[][] };

  pageViews: any;
  curViewId: string;
  subTableComponents: { [key: string]: EditConfig[] };
  setCurViewId: (id: string) => void;
  updatePageViewName: (id: string, name: string) => void;
  useEditorSignalMap: Map<string, any>;
  useFormEditorSignal: any;
  usePageViewEditorSignal: any;

}

// 视图组件
const View: React.FC<ViewProps> = ({
  useEditorSignalMap,
  components,
  pageComponentSchemas,
  layoutSubComponents,
  pageViews,
  curViewId,
  setCurViewId,
  subTableComponents,
  updatePageViewName,
  usePageViewEditorSignal,
  useFormEditorSignal
}) => {
  useSignals();

  const [createForm] = useForm();
  const [createViewModalVisible, setCreateViewModalVisible] = useState(false);
  const [dropListVisible, setDropListVisible] = useState(false);
  const [editViewName, setEditViewName] = useState(false);

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
    useEditorSignalMap.get(curViewId)!.setComponents(components);

    useEditorSignalMap.get(curViewId)!.loadPageComponentSchemas(pageComponentSchemas);
    useEditorSignalMap.get(curViewId)!.loadLayoutSubComponents(layoutSubComponents);
    useEditorSignalMap.get(curViewId)!.loadSubTableComponents(subTableComponents);

    // 切换到新视图
    switchToView(id);
  };

  const handleCopyView = async (e: React.MouseEvent<HTMLImageElement>, id: string) => {
    e.stopPropagation();
    console.log('copy view: ', id);
    // 保存当前配置到editorSignalMap
    useEditorSignalMap.get(curViewId)!.setComponents(components);
    useEditorSignalMap.get(curViewId)!.loadPageComponentSchemas(pageComponentSchemas);
    useEditorSignalMap.get(curViewId)!.loadLayoutSubComponents(layoutSubComponents);
    useEditorSignalMap.get(curViewId)!.loadSubTableComponents(subTableComponents);

    const view = pageViews[id];
    if (!view) {
      return;
    }

    const newId = await generateId();
    const newView = {
      ...view,
      created: true,
      id: newId,
      isDefaultEditViewMode: 0,
      isDefaultDetailViewMode: 0,
      pageName: view.pageName + '-副本'
    };

    const oldComponents = useEditorSignalMap.get(id)?.components.value;
    const newComponents = [] as EditConfig[];
    const oldPageComponentSchemas = useEditorSignalMap.get(id)?.pageComponentSchemas.value;
    const newPageComponentSchemas = {} as { [key: string]: EditConfig };
    const oldLayoutSubComponents = useEditorSignalMap.get(id)?.layoutSubComponents.value;
    const newLayoutSubComponents = {} as { [key: string]: any[][] };
    const oldSubTableComponents = useEditorSignalMap.get(id)?.subTableComponents.value;
    const newSubTableComponents = {} as { [key: string]: any[] };

    // 创建一个 string-string 的 map
    const idMap: { [key: string]: string } = {};

    // 替换 pageComponentSchemas 的 id
    Object.entries(oldPageComponentSchemas).forEach(([id, schema]) => {
      const cpType = `${id.split('-')[0]}`;
      const newCpID = cpType + '-' + uuidv4();
      const newSchema = {
        ...(schema as EditConfig),
        id: newCpID
      } as EditConfig;
      newSchema.config.id = newCpID;

      newPageComponentSchemas[newCpID] = newSchema;
      idMap[id] = newCpID;
    });

    // 替换 components 的 id
    oldComponents.forEach((component: EditConfig) => {
      const newCpID = idMap[component.id];
      const newComponent = {
        ...component,
        id: newCpID
      } as EditConfig;
      newComponents.push(newComponent);
    });

    // 替换 layoutSubComponents 的 id
    Object.entries(oldLayoutSubComponents).forEach(([id, subComponents]) => {
      if (!idMap[id]) {
        return;
      }

      const newCpID = idMap[id];
      const newSubComponents = [] as EditConfig[][];

      for (const block of subComponents as EditConfig[][]) {
        const newBlock = [] as EditConfig[];
        for (const subComponent of block) {
          const newSubComponent = {
            ...subComponent,
            id: idMap[subComponent.id]
          } as EditConfig;
          newBlock.push(newSubComponent);
        }
        newSubComponents.push(newBlock);
      }
      newLayoutSubComponents[newCpID] = newSubComponents;
    });

    // 替换 subTableComponents 的id
    Object.entries(oldSubTableComponents).forEach((item: any) => {
      if (!idMap[item.id]) {
        return;
      }
      const newCpID = idMap[item.id];
      const newSubComponents = { ...item, id: newCpID };
      newLayoutSubComponents[newCpID] = newSubComponents;
    });

    // 创建视图副本
    usePageViewEditorSignal.addPageView(newView);
    // 复制组件
    useEditorSignalMap.set(newId, createPageEditorSignal());
    useEditorSignalMap.get(newId)!.setComponents(newComponents);
    useEditorSignalMap.get(newId)!.loadPageComponentSchemas(newPageComponentSchemas);
    useEditorSignalMap.get(newId)!.loadLayoutSubComponents(newLayoutSubComponents);
    useEditorSignalMap.get(newId)!.loadSubTableComponents(newSubTableComponents);
    // usePageViewEditorSignal.pageViews[newId] = newView;

    // 切换到新视图
    switchToView(newId);
  };

  const switchToView = (id: string) => {
    setCurViewId(id);
    useFormEditorSignal.setComponents(useEditorSignalMap.get(id)!.components.value);
    useFormEditorSignal.loadPageComponentSchemas(useEditorSignalMap.get(id)!.pageComponentSchemas.value);
    useFormEditorSignal.loadLayoutSubComponents(useEditorSignalMap.get(id)!.layoutSubComponents.value);
    useFormEditorSignal.loadSubTableComponents(useEditorSignalMap.get(id)!.subTableComponents.value);
  };

  const dropList = (
    <div className={styles.dropList}>
      <Menu
        onClickMenuItem={() => {
          setDropListVisible(false);
        }}
      >
        {Object.entries(pageViews).map(([id, view]: [string, any]) => {
          return (
            <Menu.Item key={id} onClick={() => handleSelectView(id)}>
              <div key={id} className={styles.dropItem}>
                {view.id === curViewId ? (
                  <img className={styles.dropItemIcon} src={TickSVG} alt="tick" />
                ) : (
                  <div style={{ width: '16px', height: '16px' }}></div>
                )}
                <div className={styles.dropItemLabel}>{view.pageName}</div>
                <div className={styles.dropItemType}>
                  <div>{showViewType(view)}</div>
                  <img className={styles.copyIcon} src={CopySVG} alt="copy" onClick={(e) => handleCopyView(e, id)} />
                </div>
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

        // 切换到新视图
        switchToView(pageId);
      })
      .catch((e) => {
        console.log(e.errors);
      });
  };

  // 表单名称为空时 置为原来的值
  let oldPageViewName = '';
  const pageViewNameBlur = (e: any) => {
    if (e.currentTarget.value === '') {
      updatePageViewName(curViewId, oldPageViewName);
    }
    setEditViewName(false);
    oldPageViewName = '';
  };

  return (
    <div className={styles.viewWrapper}>
      {editViewName ? (
        <Input
          size="small"
          autoFocus
          defaultValue={pageViews[curViewId]?.pageName}
          onChange={(e: any) => {
            updatePageViewName(curViewId, e);
          }}
          onPressEnter={pageViewNameBlur}
          onBlur={pageViewNameBlur}
          onFocus={(e) => {
            oldPageViewName = e.currentTarget.value;
          }}
          style={{ maxWidth: '200px', height: '28px' }}
        />
      ) : (
        <div className={styles.viewTitle} onClick={() => setEditViewName(true)}>
          <div className={styles.viewTitleText}>{pageViews[curViewId]?.pageName}</div> <IconEdit />
        </div>
      )}

      <Dropdown
        droplist={dropList}
        getPopupContainer={(triggerNode: HTMLElement) => triggerNode.parentElement}
        position="bl"
        trigger="click"
        popupVisible={dropListVisible}
        onVisibleChange={(visible) => {
          setDropListVisible(visible);
        }}
      >
        <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          {showViewType(pageViews[curViewId])}
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
