import { Divider, Form } from '@arco-design/web-react';
import { useEffect, useMemo, useState, useRef } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { useSignals } from '@preact/signals-react/runtime';
import { currentEditorSignal } from '@onebase/ui-kit';
import { loadMicroApp, initGlobalState, type MicroApp } from 'qiankun';
import { EditMode } from '@onebase/common';
import {
  getWorkbenchComponentWidth,
  COMPONENT_GROUP_NAME,
  EDITOR_TYPES,
  type GridItem,
  usePageEditorSignal
} from '@onebase/ui-kit';
import { useWorkbenchContainer } from '../../hooks/use-workbench-container';
import { useWorkbenchHandlers } from '../../hooks/use-workbench-handlers';
import { WorkbenchItem } from './components/workbench-item';
import { EmptyState } from './components/empty-state';
import { SORTABLE_CONFIG } from '../../utils/constants';
import styles from './index.module.less';

import NextIcon from '@/assets/images/next_icon.svg';
import PrevActiveIcon from '@/assets/images/prev_icon_active.svg';
import MobileIcon from '@/assets/images/mobile_icon.svg';
import MobileActiveIcon from '@/assets/images/mobile_icon_active.svg';
import PCIcon from '@/assets/images/pc_icon.svg';
import PCActiveIcon from '@/assets/images/pc_icon_active.svg';

/**
 * 工作台工作区组件
 */
export default function WorkbenchWorkspace() {
  const [showEmpty, setShowEmpty] = useState(true);
  // use global editor signal for edit mode so workbench and other editors share state
  const { editMode, setEditMode } = currentEditorSignal;
  const mobileEditorDragRef = useRef<MicroApp | null>(null);
  const { containerRef, containerWidth } = useWorkbenchContainer();

  useSignals();

  const rawEditorContext = usePageEditorSignal(EDITOR_TYPES.WORKBENCH_EDITOR);
  const editorContext = rawEditorContext as ReturnType<typeof usePageEditorSignal> & {
    workbenchComponents?: GridItem[];
    setWorkbenchComponents?: (components: GridItem[]) => void;
  };

  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    setComponents,
    workbenchComponents,
    setWorkbenchComponents,
    setShowDeleteButton
  } = editorContext;

  const currentComponents = useMemo(() => {
    return (workbenchComponents as GridItem[] | undefined) ?? [];
  }, [workbenchComponents]);
  const updateComponents = (setWorkbenchComponents ?? setComponents) as (items: GridItem[]) => void;

  // 处理组件列表变化
  useEffect(() => {
    setShowEmpty(currentComponents.length === 0);
  }, [currentComponents]);

  // 加载移动端拖拽子应用
  useEffect(() => {
    if (editMode.value !== EditMode.MOBILE) {
      return;
    }

    console.log('loading mobile-editor-drag-list (workbench)');

    const qiankunActions = initGlobalState({ drag: true });

    const mobileEditorDrag = loadMicroApp({
      name: 'mobile-editor-drag-list',
      entry: (window as any).global_config?.MOBILE_EDITOR_URL,
      container: '#mobile-editor-drag-list',
      props: {
        onGlobalStateChange: qiankunActions.onGlobalStateChange,
        setGlobalState: qiankunActions.setGlobalState,
        offGlobalStateChange: qiankunActions.offGlobalStateChange
      }
    });
    mobileEditorDragRef.current = mobileEditorDrag;

    return () => {
      mobileEditorDrag?.unmount();
    };
  }, [editMode.value]);

  // 事件处理
  const handlers = useWorkbenchHandlers({
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    setComponents: updateComponents,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    setShowDeleteButton,
    components: currentComponents
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
      clearCurComponentID?.();
      setShowDeleteButton(false);
      // 设置页面配置 schema
      const pageConfigSchema = getPageConfigSchema();
      setCurComponentSchema(pageConfigSchema);
    }
  };

  // 初始化时设置页面配置
  useEffect(() => {
    if (!curComponentID) {
      setCurComponentSchema({
        type: 'page',
        config: {
          showHeader: true,
          showSidebar: true
        }
      });
    }
  }, [curComponentID, setCurComponentSchema]);

  return editMode.value === EditMode.MOBILE ? (
    <div id="mobile-editor-drag-list" className={styles.mobileeditordraglist}></div>
  ) : (
    <div className={styles.workbenchWorkspace}>
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
            {editMode.value !== EditMode.MOBILE && (
              <>
                <img className={styles.pageModeIcon} src={PCActiveIcon} />
                <img className={styles.pageModeIcon} src={MobileIcon} onClick={() => setEditMode(EditMode.MOBILE)} />
              </>
            )}
            {editMode.value === EditMode.MOBILE && (
              <>
                <img className={styles.pageModeIcon} src={PCIcon} onClick={() => setEditMode(EditMode.PC)} />
                <img className={styles.pageModeIcon} src={MobileActiveIcon} />
              </>
            )}
          </div>
        </div>
      </div>

      <Form>
        <div ref={containerRef} className={styles.workspaceBody} id="workspace-body" onMouseDown={handleBodyMouseDown}>
          <ReactSortable
            id="workspace-content"
            list={currentComponents}
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
            {currentComponents
              .filter((cp: GridItem) => cp.type !== 'entity')
              .map((cp: GridItem) => {
                const currentWidth = getWorkbenchComponentWidth(pageComponentSchemas[cp.id], cp.type);
                const isSelected = curComponentID === cp.id;

                return (
                  <WorkbenchItem
                    key={cp.id}
                    component={cp}
                    isSelected={isSelected}
                    currentWidth={currentWidth}
                    containerWidth={containerWidth}
                    pageComponentSchema={pageComponentSchemas[cp.id]}
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
  );
}
