import { IconCopy, IconDelete } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { v4 as uuidv4 } from 'uuid';
import { STATUS_OPTIONS,STATUS_VALUES } from '@onebase/ui-kit';

import {
  COMPONENT_GROUP_NAME,
  EditRender,
  ENTITY_COMPONENT_TYPES,
  getComponentSchema,
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
import type { AppEntityField } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import 'react-grid-layout/css/styles.css';
import { COMPONENT_MAP } from '../panel/components/metadata/component_map';
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
  const handleCopyComponent = (comp: any,id:string) => {
    addComponents(comp);

    const schema = getComponentSchema(comp.type);
    // console.log('schema', schema);

    const props = {
      id: comp.id,
      type: comp.type,
      ...schema
    };
    const data = pageComponentSchemas[id];
    data.config.cpName = comp.displayName;
    data.config.id = comp.id
    setPageComponentSchemas(comp.id, {...props,...data});
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
            const entityList: GridItem[] = [];
            newList.forEach((item) => {
              if (item.type == 'entity') {
                item.fields
                  .filter(
                    (field: AppEntityField) =>
                      field.fieldName !== 'lock_version' &&
                      field.fieldName !== 'deleted' &&
                      field.fieldName !== 'parent_id'
                  )
                  .forEach((field: AppEntityField) => {
                    let cpType = COMPONENT_MAP[field.fieldType];
                    let cpID = `${cpType}-${uuidv4()}`;
                    console.log('cpType', cpType, field);
                    const schema = getComponentSchema(cpType as any);

                    schema.config.cpName = field.displayName;
                    schema.config.id = cpID;
                    schema.config.dataField = [item.entityID, field.fieldID];
                    schema.config.label.text = field.displayName;
                    const props = {
                      id: cpID,
                      type: cpType,
                      ...schema
                    };

                    setPageComponentSchemas(cpID!, props);
                    setCurComponentID(cpID!);

                    setCurComponentSchema(props);
                    setShowDeleteButton(false);

                    entityList.push({ displayName: field.displayName, id: cpID, type: cpType });
                  });
                // 移除当前item
                newList.splice(newList.indexOf(item), 1);
              }
            });
            newList.push(...entityList);

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
            if (itemType === ENTITY_COMPONENT_TYPES.MAIN_ENTITY || itemType === ENTITY_COMPONENT_TYPES.SUB_ENTITY) {
              console.log('entity id', entityID);
            } else {
              const schema = getComponentSchema(itemType as any);
              schema.config.cpName = itemDisplayName;
              schema.config.id = cpID;

              // 系统组件
              if (entityID && fieldID) {
                console.log('dataField:    ', entityID, fieldID);
                schema.config.dataField = [entityID, fieldID];
                schema.config.status = STATUS_VALUES[STATUS_OPTIONS.READONLY]
              }

              if (dataLabel) {
                console.log(schema);
                schema.config.label.text = dataLabel;
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
            }
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
          {components
            .filter((cp: GridItem) => cp.type !== 'entity')
            .map((cp: GridItem) => (
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
                <EditRender
                  cpId={cp.id}
                  cpType={cp.type}
                  runtime={false}
                  pageComponentSchema={pageComponentSchemas[cp.id]}
                />

                {curComponentID === cp.id && showDeleteButton && (
                  <div className={styles.operationArea}>
                    <div
                      className={styles.copyButton}
                      onClick={(e) => {
                        e.stopPropagation();
                        console.log('复制组件: ', cp);
                        handleCopyComponent({ ...cp, id: `${cp.type}-${uuidv4()}` },cp.id);
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
