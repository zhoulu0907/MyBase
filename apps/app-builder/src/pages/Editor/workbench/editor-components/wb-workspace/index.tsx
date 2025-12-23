import { Divider, Form } from '@arco-design/web-react';
import { useEffect, useRef, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { useSignals } from '@preact/signals-react/runtime';
import {
  getWorkbenchComponentWidth,
  COMPONENT_GROUP_NAME,
  type GridItem,
  type WorkbenchComponentType,
  useWorkbenchSignal
} from '@onebase/ui-kit';
import { currentEditorSignal } from '@onebase/ui-kit/src/signals/current_editor';
import { loadMicroApp, initGlobalState, type MicroApp } from 'qiankun';
import { EditMode, getMobileEditorURL } from '@onebase/common';
import { useWorkbenchContainer } from '../../hooks/use-workbench-container';
import { useWorkbenchHandlers } from '../../hooks/use-workbench-handlers';
import { WorkbenchItem } from './components/workbench-item';
import { EmptyState } from './components/empty-state';
import { SORTABLE_CONFIG } from '../../utils/constants';
import styles from './index.module.less';

import NextIcon from '@/assets/images/next_icon.svg';
import PrevActiveIcon from '@/assets/images/prev_icon_active.svg';
import MobileIcon from '@/assets/images/mobile_icon.svg';
import PCActiveIcon from '@/assets/images/pc_icon_active.svg';

/**
 * 工作台工作区组件
 */
export default function WorkbenchWorkspace() {
  const [showEmpty, setShowEmpty] = useState(true);
  const { editMode, setEditMode } = currentEditorSignal;
  const mobileEditorDragRef = useRef<MicroApp | null>(null);
  const { containerRef, containerWidth } = useWorkbenchContainer();
  const isInitialized = useRef(false);

  useSignals();

  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    wbComponentSchemas,
    setWbComponentSchemas,
    delWbComponentSchemas,
    workbenchComponents,
    setWorkbenchComponents,
    delWorkbenchComponents,
    showDeleteButton,
    setShowDeleteButton
  } = useWorkbenchSignal();

  const updateComponents = setWorkbenchComponents as (items: GridItem[]) => void;

  // 初始化 qiankun 全局状态，明确列出需要传递的属性
  const qiankunActions = initGlobalState({
    drag: true,
    editMode: editMode.value,
    setEditMode,
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    wbComponentSchemas,
    setWbComponentSchemas,
    delWbComponentSchemas,
    workbenchComponents,
    setWorkbenchComponents,
    delWorkbenchComponents,
    showDeleteButton,
    setShowDeleteButton
  });

  // 处理组件列表变化
  useEffect(() => {
    setShowEmpty(workbenchComponents?.length === 0);
  }, [workbenchComponents]);

  // 加载移动端拖拽子应用
  useEffect(() => {
    if (mobileEditorDragRef.current) {
      return;
    }
    console.log('loading mobile-wb-editor-drag-list');

    const mobileEditorDrag = loadMicroApp({
      name: 'mobile-wb-editor-drag-list',
      entry: getMobileEditorURL(),
      container: '#mobile-wb-editor-drag-list',
      props: {
        onGlobalStateChange: qiankunActions.onGlobalStateChange,
        setGlobalState: qiankunActions.setGlobalState,
        offGlobalStateChange: qiankunActions.offGlobalStateChange
      }
    });
    mobileEditorDragRef.current = mobileEditorDrag;

    // 只在组件卸载时卸载子应用
    return () => {
      mobileEditorDrag?.unmount();
      mobileEditorDragRef.current = null;
    };
  }, []);

  // 事件处理
  const handlers = useWorkbenchHandlers({
    wbComponentSchemas,
    setWbComponentSchemas,
    delWbComponentSchemas,
    setWorkbenchComponents,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    setShowDeleteButton,
    workbenchComponents
  });

  // 获取或创建页面配置 schema
  const getPageConfigSchema = () => {
    return {
      type: 'page',
      config: {
        showHeader: true,
        showSidebar: true
      }
    };
  };

  // 处理空白区域点击
  const handleBodyMouseDown = (e: React.MouseEvent<HTMLDivElement>) => {
    const target = e.target as HTMLElement;
    if (target.id === 'workspace-content') {
      // 只在当前有选中组件时才清除并设置页面配置
      if (curComponentID) {
        clearCurComponentID?.();
        setShowDeleteButton(false);
        // 设置页面配置 schema
        const pageConfigSchema = getPageConfigSchema();
        setCurComponentSchema(pageConfigSchema);
      }
    }
  };

  // 初始化时设置页面配置
  useEffect(() => {
    if (!isInitialized.current && !curComponentID) {
      isInitialized.current = true;
      setCurComponentSchema({
        type: 'page',
        config: {
          showHeader: true,
          showSidebar: true
        }
      });
    }
  }, []);

  const isMobileMode = editMode.value === EditMode.MOBILE;

  return (
    <div className={styles.editorSwitchContainer}>
      {/* 移动端编辑器 */}
      <div
        id="mobile-wb-editor-drag-list"
        className={`${styles.mobileeditordraglist} ${isMobileMode ? styles.active : ''}`}
      ></div>
      {/* Web端编辑器 */}
      <div className={`${styles.workbenchWorkspace} ${!isMobileMode ? styles.active : ''}`}>
        <div className={styles.workspaceHeader}>
          <div className={styles.workspaceHeaderLeft}></div>
          <div className={styles.workspaceHeaderRight}>
            {/* TODO 撤回重做 */}
            <div className={styles.editorStepCtrl}>
              <img className={styles.pageModeIcon} src={PrevActiveIcon} />
              <img className={styles.pageModeIcon} src={NextIcon} />
            </div>
            <Divider type="vertical" />
            <div className={styles.pageModeCtrl}>
              <img className={styles.pageModeIcon} src={PCActiveIcon} />
              <img className={styles.pageModeIcon} src={MobileIcon} onClick={() => setEditMode(EditMode.MOBILE)} />
            </div>
          </div>
        </div>

        <Form>
          <div
            ref={containerRef}
            className={styles.workspaceBody}
            id="workspace-body"
            onMouseDown={handleBodyMouseDown}
          >
            <ReactSortable
              id="workspace-content"
              list={workbenchComponents}
              setList={updateComponents}
              filter={SORTABLE_CONFIG.filter}
              preventOnFilter={SORTABLE_CONFIG.preventOnFilter}
              sort={SORTABLE_CONFIG.sort}
              forceFallback={SORTABLE_CONFIG.forceFallback}
              className={styles.workspaceContent}
              onAdd={handlers.handleComponentAdd}
              onStart={handlers.handleDragStart}
              group={{ name: COMPONENT_GROUP_NAME }}
            >
              {workbenchComponents
                ?.filter((cp: GridItem) => cp.type !== 'entity')
                .map((cp: GridItem) => {
                  const currentWidth = getWorkbenchComponentWidth(
                    wbComponentSchemas[cp.id],
                    cp.type as WorkbenchComponentType
                  );
                  const isSelected = curComponentID === cp.id;

                  return (
                    <WorkbenchItem
                      key={cp.id}
                      component={cp}
                      isSelected={isSelected}
                      currentWidth={currentWidth}
                      containerWidth={containerWidth}
                      pageComponentSchema={wbComponentSchemas[cp.id]}
                      onOperation={{
                        show: handlers.handleShowComponent,
                        copy: handlers.handleCopyComponent,
                        delete: handlers.handleDeleteComponent,
                        widthChange: handlers.handleWidthChange,
                        select: handlers.handleSelectComponent
                      }}
                    />
                  );
                })}
            </ReactSortable>

            {showEmpty && <EmptyState />}
          </div>
        </Form>
      </div>
    </div>
  );
}
