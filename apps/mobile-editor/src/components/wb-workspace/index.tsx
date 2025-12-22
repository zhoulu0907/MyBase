import { v4 as uuidv4 } from 'uuid';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  COMPONENT_GROUP_NAME,
  type GridItem,
  getWorkbenchComponentWidth
} from '@onebase/ui-kit';
import {
  EditRender,
} from '@onebase/ui-kit-mobile';

import { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { useSignals } from '@preact/signals-react/runtime';

import MobileActiveIcon from '@/assets/images/mobile_icon_active.svg';
import PCIcon from '@/assets/images/pc_icon.svg';

import NextIcon from '@/assets/images/next_icon.svg';
import PrevActiveIcon from '@/assets/images/prev_icon_active.svg';

import CompDeleteIcon from '@/assets/images/app_delete.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';

import { useWorkbenchContainer } from './hooks/use-workbench-container';
import { useWorkbenchHandlers } from './hooks/use-workbench-handlers';
import { EmptyState } from './components/empty-state';
import { SORTABLE_CONFIG } from './utils/constants';

import type { EditorWorkspaceProps } from './types/workbench-component';
import { EditMode } from '@onebase/common';
import 'react-grid-layout/css/styles.css';
import styles from './index.module.less';

const WorkbenchWorkspace: React.FC<EditorWorkspaceProps> = ({ props }) => {
  const [showEmpty, setShowEmpty] = useState(true);

  useSignals();

  // 使用容器hooks
  const { containerRef } = useWorkbenchContainer();

  // 直接从 props 获取数据和函数（已在 WorkbenchEditor 中合并处理）
  const {
    setEditMode,
    editMode,
    workbenchComponents: currentComponents,
    wbComponentSchemas: currentSchemas,
    setWorkbenchComponents,
    setWbComponentSchemas,
    delWbComponentSchemas,
    delWorkbenchComponents,
    curComponentID,
    showDeleteButton,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    setShowDeleteButton
  } = props;

  // 处理组件列表变化
  useEffect(() => {
    setShowEmpty(currentComponents?.length === 0);
  }, [currentComponents]);

  // 事件处理hooks
  const handlers = useWorkbenchHandlers({
    wbComponentSchemas: currentSchemas ?? {},
    workbenchComponents: currentComponents ?? [],
    setWorkbenchComponents,
    setWbComponentSchemas,
    delWbComponentSchemas,
    delWorkbenchComponents,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    setShowDeleteButton
  });

  // 初始化时设置页面配置
  useEffect(() => {
    if (!curComponentID) {
      setCurComponentSchema(handlers.getPageConfigSchema());
    }
  }, [curComponentID]);

  return (
    <div className={styles.editorWorkspace}>
      <div className={styles.workspaceHeader}>
        <div className={styles.workspaceHeaderLeft}></div>
        <div className={styles.workspaceHeaderRight}>
          {/* TODO 撤回重做 */}
          <div className={styles.editorStepCtrl}>
            <img className={styles.pageModeIcon} src={PrevActiveIcon} />
            <img className={styles.pageModeIcon} src={NextIcon} />
          </div>
          <span className={styles.pageModeDivider} />
          <div className={styles.pageModeCtrl}>
            <img className={styles.pageModeIcon} src={PCIcon} onClick={() => setEditMode(EditMode.PC)} />
            <img className={styles.pageModeIcon} src={MobileActiveIcon} />
          </div>
        </div>
      </div>

      <div
        ref={containerRef}
        className={styles.workspaceBody}
        id="workspace-body"
        onMouseDown={handlers.handleBodyMouseDown}
      >
        <ReactSortable
          id="workspace-content"
          list={currentComponents ?? []}
          setList={setWorkbenchComponents}
          group={{ name: COMPONENT_GROUP_NAME, put: true }}
          filter={SORTABLE_CONFIG.filter}
          preventOnFilter={SORTABLE_CONFIG.preventOnFilter}
          sort={SORTABLE_CONFIG.sort}
          forceFallback={SORTABLE_CONFIG.forceFallback}
          chosenClass={styles.ghostClass}
          className={styles.workspaceContent}
          onAdd={handlers.handleComponentAdd}
          onStart={handlers.handleDragStart}
        >
          {(currentComponents ?? [])
            .filter((cp: GridItem) => cp.type !== 'entity')
            .map((cp: GridItem) => (
              <div
                key={cp.id}
                data-cp-type={cp.type}
                data-cp-displayname={cp.displayName}
                data-cp-id={cp.id}
                className={styles.componentItem}
                style={{
                  width: `calc(${getWorkbenchComponentWidth(currentSchemas[cp.id], cp.type)} - 8px)`,
                  borderColor: curComponentID === cp.id ? 'rgb(var(--primary-6))' : '',
                  borderStyle: curComponentID === cp.id ? 'solid' : 'dashed',
                  background: curComponentID === cp.id ? 'rgb(var(--primary-1))' : '',
                  margin: '4px'
                }}
                onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                  e.stopPropagation();
                  console.log('点击组件: ', cp.id);

                  setCurComponentID(cp.id);

                  const curComponentSchema = {
                    id: cp.id,
                    type: cp.type,
                    displayName: cp.displayName,
                    ...currentSchemas[cp.id]
                  };

                  setCurComponentSchema(curComponentSchema);

                  setShowDeleteButton(true);
                }}
              >
                <EditRender
                  cpId={cp.id}
                  cpType={cp.type}
                  runtime={false}
                  pageComponentSchema={currentSchemas[cp.id]}
                />

                {curComponentID === cp.id && showDeleteButton && (
                  <div className={styles.operationArea}>
                    {currentSchemas[cp.id]?.config?.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                      <>
                        <div
                          className={styles.copyButton}
                          onClick={(e) => {
                            e.stopPropagation();
                            console.debug('取消隐藏组件: ', cp);
                            handlers.handleShowComponent(cp.id);
                          }}
                        >
                          <img src={CompShowIcon} alt="component show" />
                        </div>
                        <span>|</span>
                      </>
                    )}

                    <div
                      className={styles.copyButton}
                      onClick={(e) => {
                        e.stopPropagation();
                        console.log('复制组件: ', cp);
                        handlers.handleCopyComponent({ ...cp, id: `${cp.type}-${uuidv4()}` }, cp.id);
                      }}
                    >
                      <img src={CompCopyIcon} alt="component copy" />
                    </div>
                    <span>|</span>
                    <div
                      className={styles.deleteButton}
                      onClick={(e) => {
                        e.stopPropagation();
                        console.log('删除组件: ', cp.id);
                        handlers.handleDeleteComponent(cp.id);
                      }}
                    >
                      <img src={CompDeleteIcon} alt="component delete" />
                    </div>
                  </div>
                )}
              </div>
            ))}
        </ReactSortable>

        {showEmpty && <EmptyState />}
      </div>
    </div>
  );
};

export default WorkbenchWorkspace;
