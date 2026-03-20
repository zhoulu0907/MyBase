import { Divider, Form } from '@arco-design/web-react';
import { useEffect, useRef, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { useSignals } from '@preact/signals-react/runtime';
import {
  COMPONENT_GROUP_NAME,
  type GridItem,
  useWorkbenchSignal,
  getOrCreatePageConfig,
  shouldShowInWorkspace,
  isPageConfig,
  getWorkbenchComponentWidth,
  type WorkbenchComponentType
} from '@onebase/ui-kit';
import { currentEditorSignal } from '@onebase/ui-kit/src/signals/current_editor';
import { loadMicroApp, initGlobalState, type MicroApp } from 'qiankun';
import { EditMode, getMobileEditorURL } from '@onebase/common';
import { useWorkbenchContainer } from '../../hooks/use-workbench-container';
import { useWorkbenchHandlers } from '../../hooks/use-workbench-handlers';
import { WorkbenchItem } from './components/workbench-item';
import { EmptyState } from './components/empty-state';
import { SORTABLE_CONFIG } from '../../utils/constants';
import { applyGridLayout } from '../../utils/grid-layout';
import 'react-grid-layout/css/styles.css';
import styles from './index.module.less';

import NextIcon from '@/assets/images/next_icon.svg';
import PrevActiveIcon from '@/assets/images/prev_icon_active.svg';
import MobileIcon from '@/assets/images/mobile_icon.svg';
import PCActiveIcon from '@/assets/images/pc_icon_active.svg';

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
    curComponentSchema,
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

  const updateComponents = (items: GridItem[], rowSpanOverrides?: Record<string, number>) => {
    const laid = applyGridLayout(items, (id) => wbComponentSchemas[id], rowSpanOverrides);
    setWorkbenchComponents(laid);
  };

  // 子组件上报内容高度 → 更新 rowSpan 并重新布局
  const handleHeightChange = (componentId: string, rowSpan: number) => {
    const schema = wbComponentSchemas[componentId];
    if (!schema) return;
    if ((schema.config?.gridLayout?.rowSpan ?? 1) === rowSpan) return;

    setWbComponentSchemas(componentId, {
      ...schema,
      config: { ...schema.config, gridLayout: { rowSpan } }
    });
    // 直接传入新 rowSpan，避免等待 signal 更新导致布局滞后
    updateComponents(workbenchComponents, { [componentId]: rowSpan });
  };

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

  useEffect(() => {
    setShowEmpty(workbenchComponents?.length === 0);
  }, [workbenchComponents]);

  useEffect(() => {
    if (mobileEditorDragRef.current) return;
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
    return () => {
      mobileEditorDrag?.unmount();
      mobileEditorDragRef.current = null;
    };
  }, []);

  const handlers = useWorkbenchHandlers({
    wbComponentSchemas,
    setWbComponentSchemas,
    delWbComponentSchemas,
    setWorkbenchComponents: updateComponents,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    setShowDeleteButton,
    workbenchComponents
  });

  // 兼容旧数据：schemas 就绪后为缺少 layout 的组件补全一次
  useEffect(() => {
    if (!workbenchComponents?.length) return;
    const allReady = workbenchComponents.every((cp: GridItem) => wbComponentSchemas[cp.id] != null);
    if (!allReady) return;
    if (!workbenchComponents.every((cp: GridItem) => 'layout' in cp)) {
      updateComponents(workbenchComponents);
    }
  }, [workbenchComponents, wbComponentSchemas]);

  const loadPageConfig = () => {
    const [pageConfigId, pageConfigSchema] = getOrCreatePageConfig(wbComponentSchemas);

    // 确保页面配置已保存
    if (!wbComponentSchemas[pageConfigId]) {
      setWbComponentSchemas(pageConfigId, pageConfigSchema);
    }

    setCurComponentSchema(pageConfigSchema);
  };

  // 获取页面配置
  const pageConfig = getOrCreatePageConfig(wbComponentSchemas)[1].config;

  // 处理空白区域点击
  const handleBodyMouseDown = (e: React.MouseEvent<HTMLDivElement>) => {
    if ((e.target as HTMLElement).id === 'workspace-content') {
      clearCurComponentID?.();
      setShowDeleteButton(false);
      loadPageConfig();
    }
  };

  // 首次加载时初始化页面配置
  useEffect(() => {
    if (!isInitialized.current && !curComponentID && Object.keys(wbComponentSchemas).length > 0) {
      isInitialized.current = true;
      loadPageConfig();
    }
  }, [wbComponentSchemas, curComponentID]);

  // 无选中组件时同步页面配置
  useEffect(() => {
    if (!curComponentID && isPageConfig(curComponentSchema)) {
      const [pageConfigId] = getOrCreatePageConfig(wbComponentSchemas);
      const latest = wbComponentSchemas[pageConfigId];
      if (latest && JSON.stringify(latest.config) !== JSON.stringify(curComponentSchema?.config)) {
        setCurComponentSchema(latest);
      }
    }
  }, [wbComponentSchemas, curComponentID]);

  const isMobileMode = editMode.value === EditMode.MOBILE;

  const runtimeBodyWidth = 1920 - (pageConfig?.showSidebar ? 200 : 0);
  const wbFontSize = `${Math.min(20, Math.max(10, (containerWidth / runtimeBodyWidth) * 16))}px`;

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
              style={
                {
                  backgroundColor: pageConfig.pageBgColor || undefined,
                  backgroundImage: pageConfig.pageBgImg ? `url(${pageConfig.pageBgImg})` : undefined,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center',
                  backgroundRepeat: 'no-repeat'
                } as React.CSSProperties
              }
              onAdd={handlers.handleComponentAdd}
              onStart={handlers.handleDragStart}
              group={{ name: COMPONENT_GROUP_NAME }}
            >
              {workbenchComponents
                ?.filter((cp: GridItem) => shouldShowInWorkspace(cp.type))
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
                      wbFontSize={wbFontSize}
                      onOperation={{
                        show: handlers.handleShowComponent,
                        copy: handlers.handleCopyComponent,
                        delete: handlers.handleDeleteComponent,
                        widthChange: handlers.handleWidthChange,
                        heightChange: handleHeightChange,
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
