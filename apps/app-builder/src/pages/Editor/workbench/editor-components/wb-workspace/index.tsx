import { Divider, Form } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { useSignals } from '@preact/signals-react/runtime';
import { getComponentWidth, COMPONENT_GROUP_NAME, type GridItem, usePageEditorSignal } from '@onebase/ui-kit';
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
  const [pageMode, setPageMode] = useState<string>('pc');
  const { containerRef, containerWidth } = useWorkbenchContainer();

  useSignals();

  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    components,
    setComponents,
    setShowDeleteButton
  } = usePageEditorSignal();

  // 处理组件列表变化
  useEffect(() => {
    setShowEmpty(components.length === 0);
  }, [components]);

  // 事件处理
  const handlers = useWorkbenchHandlers({
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    setComponents,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    setShowDeleteButton,
    components
  });

  // 处理空白区域点击
  const handleBodyMouseDown = (e: React.MouseEvent<HTMLDivElement>) => {
    const target = e.target as HTMLElement;
    if (target.id === 'workspace-content') {
      clearCurComponentID();
      setShowDeleteButton(false);
    }
  };

  return (
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
            {pageMode === 'pc' && (
              <>
                <img className={styles.pageModeIcon} src={PCActiveIcon} />
                <img className={styles.pageModeIcon} src={MobileIcon} onClick={() => setPageMode('mobile')} />
              </>
            )}
            {pageMode === 'mobile' && (
              <>
                <img className={styles.pageModeIcon} src={PCIcon} onClick={() => setPageMode('pc')} />
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
            list={components}
            setList={setComponents}
            filter={SORTABLE_CONFIG.filter}
            preventOnFilter={SORTABLE_CONFIG.preventOnFilter}
            sort={SORTABLE_CONFIG.sort}
            forceFallback={SORTABLE_CONFIG.forceFallback}
            className={styles.workspaceContent}
            onAdd={handlers.handleComponentAdd}
            onStart={handlers.handleDragStart}
            group={{ name: COMPONENT_GROUP_NAME }}
          >
            {components
              .filter((cp: GridItem) => cp.type !== 'entity')
              .map((cp: GridItem) => {
                const currentWidth = getComponentWidth(pageComponentSchemas[cp.id], cp.type);
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
