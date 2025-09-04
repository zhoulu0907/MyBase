import { IconCopy, IconDelete } from '@arco-design/web-react/icon';
import { getComponentSchema } from '@onebase/ui-kit';
import { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { v4 as uuidv4 } from 'uuid';

import {
  COMPONENT_GROUP_NAME,
  EditRender,
  getComponentWidth,
  type GridItem,
  usePageEditorSignal
} from '@onebase/ui-kit';

import EmptyIcon from '@/assets/images/empty.svg';
import MobileIcon from '@/assets/images/mobile_icon.svg';
import MobileActiveIcon from '@/assets/images/mobile_icon_active.svg';
import PCIcon from '@/assets/images/pc_icon.svg';
import PCActiveIcon from '@/assets/images/pc_icon_active.svg';

// import PrevIcon from '@/assets/images/prev_icon.svg';
import NextIcon from '@/assets/images/next_icon.svg';
import PrevActiveIcon from '@/assets/images/prev_icon_active.svg';
// import NextActiveIcon from '@/assets/images/next_icon_active.svg';

import { Divider } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import 'react-grid-layout/css/styles.css';
import styles from './index.module.less';

export default function EditorWorkspace() {
  const [showEmpty, setShowEmpty] = useState(true);

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
    addComponents,
    setComponents,
    delComponents,
    showDeleteButton,
    setShowDeleteButton,

    delLayoutSubComponents
  } = usePageEditorSignal();

  const [pageMode, setPageMode] = useState<string>('pc');

  useEffect(() => {
    if (components.length === 0) {
      setShowEmpty(true);
    } else {
      setShowEmpty(false);
    }
  }, [components]);

  // 复制组件
  const handleCopyComponent = (comp: any) => {
    addComponents(comp);

    const schema = getComponentSchema(comp.type);
    // console.log('schema', schema);

    schema.config.cpName = comp.displayName;
    schema.config.id = comp.id;

    const props = {
      id: comp.id,
      type: comp.type,
      ...schema
    };

    setPageComponentSchemas(comp.id, props);
    setCurComponentID(comp.id!);
    setCurComponentSchema(props);
    setShowDeleteButton(false);
  };

  // 删除组件
  const handleDeleteComponent = (componentId: string) => {
    // 从组件列表中移除
    delComponents(componentId);
    delPageComponentSchemas(componentId);
    delLayoutSubComponents(componentId);

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  return (
    <div className={styles.formEditorWorkspace}>
      <div className={styles.workspaceHeader}>
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

      <div
        className={styles.workspaceBody}
        id="workspace-body"
        onMouseDown={(e: React.MouseEvent<HTMLDivElement>) => {
          // 点击空白区域取消选中

          const target = e.target as HTMLElement;
          if (target.id === 'workspace-content') {
            clearCurComponentID();
            setShowDeleteButton(false);
          }
        }}
      >
        <ReactSortable
          id="workspace-content"
          list={components}
          setList={(newList) => {
            setComponents(newList);
          }}
          onAdd={(e) => {
            // console.log('onAdd', e);

            let cpID = e.item.id || e.item.getAttribute('data-cp-id');
            const itemType = e.item.getAttribute('data-cp-type');
            const itemDisplayName = e.item.getAttribute('data-cp-displayname');

            const fieldID = e.item.getAttribute('data-field-id');
            const entityID = e.item.getAttribute('data-entity-id');
            const dataLabel = e.item.getAttribute('data-label');

            console.log(`拖入组件 ${cpID},类型 ${itemType}, 名称 ${itemDisplayName} 组件名称 ${dataLabel}`);

            if (cpID) {
              const cpSchema = pageComponentSchemas[cpID];
              // 如果组件已经存在，则不进行创建
              if (cpSchema && cpSchema.config && cpSchema.editData) {
                console.log(`组件 ${cpID} 已存在，不进行创建`);
                setCurComponentID(cpID!);
                setCurComponentSchema(cpSchema);
                setShowDeleteButton(false);
                return;
              }
            }

            const schema = getComponentSchema(itemType as any);
            console.log('schema', schema);

            schema.config.cpName = itemDisplayName;
            schema.config.id = cpID;

            if (entityID && fieldID) {
              console.log('dataField:    ', entityID, fieldID);
              schema.config.dataField = [entityID, fieldID];
            }

            if (dataLabel) {
              console.log(schema);
              schema.config.label = dataLabel;
            }

            const props = {
              id: cpID,
              type: itemType,
              ...schema
            };

            setPageComponentSchemas(cpID!, props);
            setCurComponentID(cpID!);

            setCurComponentSchema(props);
            setShowDeleteButton(false);
          }}
          group={{ name: COMPONENT_GROUP_NAME }}
          sort={true}
          forceFallback={true}
          className={styles.workspaceContent}
          onStart={(e) => {
            // console.log('onStart', e);
            const cpID = e.item.getAttribute('data-cp-id') || '';
            setCurComponentID(cpID);
            const curComponentSchema = pageComponentSchemas[cpID] || {};
            setCurComponentSchema(curComponentSchema);
            setShowDeleteButton(true);
          }}
        >
          {components.map((cp: GridItem) => (
            <div
              key={cp.id}
              data-cp-type={cp.type}
              data-cp-displayname={cp.displayName}
              data-cp-id={cp.id}
              className={styles.componentItem}
              style={{
                width: getComponentWidth(pageComponentSchemas[cp.id], cp.type),
                borderColor: curComponentID === cp.id ? '#009E9E' : '',
                borderStyle: curComponentID === cp.id ? 'solid' : 'dashed'
              }}
              onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                e.stopPropagation();
                console.log('点击组件: ', cp.id);

                setCurComponentID(cp.id);

                const curComponentSchema = pageComponentSchemas[cp.id];
                setCurComponentSchema(curComponentSchema);

                // console.log('当前组件的ID: ', cp.id);
                // console.log('当前组件的配置: ', curComponentSchema);
                setShowDeleteButton(true);
              }}
            >
              <EditRender cpId={cp.id} cpType={cp.type} pageComponentSchema={pageComponentSchemas[cp.id]} />

              {curComponentID === cp.id && showDeleteButton && (
                <div className={styles.operationArea}>
                  <div
                    className={styles.copyButton}
                    onClick={(e) => {
                      e.stopPropagation();
                      // console.log('复制组件: ', cp);
                      handleCopyComponent({ ...cp, id: `${cp.type}-${uuidv4()}` });
                    }}
                  >
                    <IconCopy />
                  </div>
                  {/* 删除按钮 */}
                  {/* TODO(mickey): 组件继续封装，和layout中的共用一套 */}
                  <div
                    className={styles.deleteButton}
                    onClick={(e) => {
                      e.stopPropagation();
                      console.log('删除组件: ', cp.id);
                      handleDeleteComponent(cp.id);
                    }}
                  >
                    <IconDelete />
                  </div>
                </div>
              )}
            </div>
          ))}
        </ReactSortable>

        {showEmpty && (
          <div className={styles.formEmpty}>
            <div className={styles.formEmptyContent}>
              <img src={EmptyIcon} alt="页面无组件" />
              拖拽左侧面板里的组件到这里
              <br />
              开始使用吧！
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
