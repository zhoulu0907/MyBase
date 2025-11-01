import {
  COMPONENT_GROUP_NAME,
  COMPONENT_MAP,
  EDITOR_TYPES,
  ENTITY_COMPONENT_TYPES,
  ENTITY_FIELD_TYPE,
  FORM_COMPONENT_TYPES,
  getComponentConfig,
  getComponentSchema,
  STATUS_OPTIONS,
  STATUS_VALUES,
  type GridItem
} from '@onebase/ui-kit';
import { EditRender } from '@onebase/ui-kit-mobile';
import { cloneDeep } from 'lodash-es';
import { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { v4 as uuidv4 } from 'uuid';

import MobileIcon from '@/assets/images/mobile_icon.svg';
import MobileActiveIcon from '@/assets/images/mobile_icon_active.svg';
import PCIcon from '@/assets/images/pc_icon.svg';
import PCActiveIcon from '@/assets/images/pc_icon_active.svg';

import NextIcon from '@/assets/images/next_icon.svg';
import PrevActiveIcon from '@/assets/images/prev_icon_active.svg';

import CompDeleteIcon from '@/assets/images/app_delete.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';

import type { EditorProps } from '@/common/props';
import { Divider } from '@arco-design/web-react';
import { getEntityFieldOptions, type AppEntityField, type EntityFieldOption } from '@onebase/app';
import { EditMode, getHashQueryParam } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import 'react-grid-layout/css/styles.css';
import View from '../view';
import styles from './index.module.less';

interface EditorWorkspaceProps {
  props: EditorProps;
}

const EditorWorkspace: React.FC<EditorWorkspaceProps> = ({ props }) => {
  useSignals();

  const {
    editMode,
    setEditMode,
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
    layoutSubComponents,
    setLayoutSubComponents,
    delLayoutSubComponents,

    pageViews,
    curViewId,
    setCurViewId,
    updatePageViewName
  } = props;

  const [showEmpty, setShowEmpty] = useState(true);
  const [isFormEditor, setIsFormEditor] = useState(false);
  const [pageSetId, setPageSetId] = useState('');

  useEffect(() => {
    const pageSetId = getHashQueryParam('pageSetId');
    if (pageSetId) {
      setPageSetId(pageSetId);
    }
  }, []);

  useEffect(() => {
    if (components.length === 0) {
      setShowEmpty(true);
    } else {
      setShowEmpty(false);
    }
  }, [components]);

  const hash = window.location.hash;
  useEffect(() => {
    setIsFormEditor(hash.includes(EDITOR_TYPES.FORM_EDITOR));
  }, [hash]);

  // 取消隐藏组件
  const handleShowComponent = (componentId: string) => {
    const schema = pageComponentSchemas[componentId];
    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];

    setPageComponentSchemas(componentId, schema);
    setCurComponentID(componentId);
    setCurComponentSchema(schema);
    setShowDeleteButton(false);
  };

  // 复制组件
  const handleCopyComponent = (comp: any, originId: string) => {
    addComponents(comp);

    const idMap = new Map();
    idMap.set(originId, comp.id);

    let rootComponentProps = null; // 保存根组件的 props

    // 递归复制组件及其子组件
    function copyComponentRecursive(oldId: string, newId: string) {
      // 1. 复制组件配置
      const originalComp = pageComponentSchemas[oldId];
      if (!originalComp) return;

      const schemaConfig = cloneDeep(getComponentConfig(pageComponentSchemas[oldId], comp.type));

      const schema = getComponentSchema(comp.type);

      schema.config = schemaConfig;
      schema.config.cpName = comp.displayName || '';
      schema.config.id = newId; // 使用新 ID

      console.debug('newProps', schema, originalComp);
      const newProps = {
        id: newId,
        type: comp.type,
        ...schema
      };

      // 如果是根组件，保存 props
      if (newId === comp.id) {
        rootComponentProps = newProps;
      }

      // 保存新组件配置
      setPageComponentSchemas(newId, newProps);

      // 2. 复制子组件结构
      if (layoutSubComponents[oldId]) {
        const newSubComponents = layoutSubComponents[oldId].map((row) =>
          row.map((item: any) => {
            // 为每个子组件创建新 ID
            const childNewId = idMap.get(item.id) || `${item.type}-${uuidv4()}`;

            // 记录子组件 ID 映射
            if (!idMap.has(item.id)) {
              idMap.set(item.id, childNewId);
              // 递归复制子组件
              copyComponentRecursive(item.id, childNewId);
            }

            // 返回更新了 ID 的子组件引用
            return {
              ...item,
              id: childNewId
            };
          })
        );

        // 保存子组件结构
        setLayoutSubComponents(newId, newSubComponents);
      }
    }

    // 开始递归复制
    copyComponentRecursive(originId, comp.id);

    // 设置当前组件 - 使用保存的 props
    setCurComponentID(comp.id!);
    setCurComponentSchema(rootComponentProps);
    setShowDeleteButton(false);
  };

  // 删除组件
  const handleDeleteComponent = (componentId: string) => {
    // 从组件列表中移除
    delComponents(componentId);
    delPageComponentSchemas(componentId);
    delLayoutSubComponents(componentId);

    if (layoutSubComponents[componentId]) {
      // 收集所有需要删除的组件 ID
      const idsToDelete = new Set<string>();

      // 递归收集需要删除的组件 ID
      function collectDeleteIds(id: string) {
        if (layoutSubComponents[id]) {
          layoutSubComponents[id].forEach((row: any) => {
            row.forEach(({ id: childId }: { id: string }) => {
              if (!idsToDelete.has(childId)) {
                idsToDelete.add(childId);
                // 递归收集子组件的子组件
                collectDeleteIds(childId);
              }
            });
          });
        }
      }

      // 开始收集
      collectDeleteIds(componentId);

      // 删除所有收集到的组件
      idsToDelete.forEach((id: string) => {
        // 明确参数类型
        delPageComponentSchemas(id);
        delLayoutSubComponents(id);
      });
    }

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  const getFieldOptions = async (fieldId: string) => {
    const options = await getEntityFieldOptions(fieldId);
    if (!options) return [];

    return options.map((option: EntityFieldOption) => ({
      label: option.optionLabel,
      value: option.optionValue,
      chosen: false,
      selected: false
    }));
  };

  // 切换pc端、移动端编辑模式
  const switchEditMode = (mode: EditMode) => {
    console.log('switchEditMode: ', mode);
    setEditMode(mode);
  };

  return (
    <div className={styles.formEditorWorkspace}>
      <div className={styles.workspaceHeader}>
        <div className={styles.workspaceHeaderLeft}>
          {isFormEditor && pageSetId && (
            <View
              pageSetId={pageSetId}
              components={components}
              pageComponentSchemas={pageComponentSchemas}
              layoutSubComponents={layoutSubComponents}
              pageViews={pageViews}
              curViewId={curViewId}
              setCurViewId={setCurViewId}
              updatePageViewName={updatePageViewName}
            />
          )}
        </div>
        <div className={styles.workspaceHeaderRight}>
          {/* TODO 撤回重做 */}
          <div className={styles.editorStepCtrl}>
            <img className={styles.pageModeIcon} src={PrevActiveIcon} />
            <img className={styles.pageModeIcon} src={NextIcon} />
          </div>
          <Divider type="vertical" />
          <div className={styles.pageModeCtrl}>
            <>
              <img
                className={styles.pageModeIcon}
                src={editMode === EditMode.MOBILE ? PCIcon : PCActiveIcon}
                onClick={() => editMode === EditMode.MOBILE && switchEditMode(EditMode.PC)}
              />
              <img
                className={styles.pageModeIcon}
                src={editMode === EditMode.PC ? MobileIcon : MobileActiveIcon}
                onClick={() => editMode === EditMode.PC && switchEditMode(EditMode.MOBILE)}
              />
            </>
          </div>
        </div>
      </div>

      <div
        className={styles.workspaceBody}
        id="workspace-body"
        onMouseDown={(e: React.MouseEvent<HTMLDivElement>) => {
          const target = e.target as HTMLElement;
          if (target.id === 'workspace-content') {
            // 点击空白区域取消选中
            console.log('点击空白区域取消选中');
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
            newList.forEach(async (item) => {
              // console.log(item);
              if (item.type == 'entity') {
                if (item.entityType === '子表') {
                  item.fields
                    .filter(
                      (field: AppEntityField) =>
                        field.fieldName !== 'lock_version' &&
                        field.fieldName !== 'deleted' &&
                        field.fieldName !== 'parent_id' &&
                        field.isSystemField !== 1
                    )
                    .map(async (field: AppEntityField) => {
                      let cpType = COMPONENT_MAP[field.fieldType];
                      let cpID = `${cpType}-${uuidv4()}`;
                      const schema = getComponentSchema(cpType as any);

                      if (
                        field.fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE ||
                        field.fieldType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE
                      ) {
                        getFieldOptions(field.fieldId).then((options: any) => {
                          schema.config.defaultValue = options;
                        });
                      }

                      schema.config.cpName = field.displayName;
                      schema.config.id = cpID;
                      schema.config.dataField = [item.entityId, field.fieldId];
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
                    });

                  const cpName = '子表单';
                  const cpType = 'XSubTable';
                  const cpID = `${cpType}-${uuidv4()}`;

                  const schema = getComponentSchema(cpType as any);

                  const newColumns = item.fields
                    .filter((field: AppEntityField) => field.isSystemField !== 1)
                    .map((field: AppEntityField) => ({
                      id: field.fieldId,
                      title: field.displayName,
                      dataIndex: field.fieldId,
                      dataType: field.fieldType,
                      disabled: undefined,
                      selected: false,
                      chosen: false
                    }));

                  schema.config.cpName = cpName;
                  schema.config.id = cpID;
                  schema.config.dataField = [item.entityId, item.id];
                  schema.config.label.text = cpName;
                  schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];
                  schema.config.columns = [...newColumns];
                  schema.config.subTable = item.id;

                  const props = {
                    id: cpID,
                    type: cpType,
                    ...schema
                  };

                  setPageComponentSchemas(cpID!, props);
                  //   setCurComponentID(cpID!);
                  //   setCurComponentSchema(props);
                  setShowDeleteButton(false);

                  entityList.push({ displayName: cpName, id: cpID, type: cpType });

                  newList.splice(newList.indexOf(item), 1);
                } else if (item.entityType === '主表') {
                  item.fields
                    .filter(
                      (field: AppEntityField) =>
                        field.fieldName !== 'lock_version' &&
                        field.fieldName !== 'deleted' &&
                        field.fieldName !== 'parent_id' &&
                        field.isSystemField !== 1
                    )
                    .forEach(async (field: AppEntityField) => {
                      let cpType = COMPONENT_MAP[field.fieldType];
                      let cpID = `${cpType}-${uuidv4()}`;
                      console.log('cpType', cpType, field);

                      const schema = getComponentSchema(cpType as any);

                      if (
                        field.fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE ||
                        field.fieldType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE
                      ) {
                        getFieldOptions(field.fieldId).then((options: any) => {
                          schema.config.defaultValue = options;
                        });
                      }

                      schema.config.cpName = field.displayName;
                      schema.config.id = cpID;
                      schema.config.dataField = [item.entityId, field.fieldId];
                      schema.config.label.text = field.displayName;
                      const props = {
                        id: cpID,
                        type: cpType,
                        ...schema
                      };

                      setPageComponentSchemas(cpID!, props);
                      //   setCurComponentID(cpID!);
                      //   setCurComponentSchema(props);
                      setShowDeleteButton(false);

                      entityList.push({ displayName: field.displayName, id: cpID, type: cpType });
                    });
                  // 移除当前item
                  newList.splice(newList.indexOf(item), 1);
                }
              }
            });
            newList.push(...entityList);

            setComponents(newList);
          }}
          onAdd={async (e) => {
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

              // 主子表 字段组件
              if (entityID && fieldID) {
                console.log('dataField:  ', entityID, fieldID);
                schema.config.dataField = [entityID, fieldID];
                schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];

                if (itemType === FORM_COMPONENT_TYPES.SELECT_ONE || itemType === FORM_COMPONENT_TYPES.SELECT_MUTIPLE) {
                  const options = await getFieldOptions(fieldID);
                  schema.config.defaultValue = options;
                }
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
                  width: '100%',
                  //   width: getComponentWidth(pageComponentSchemas[cp.id], cp.type),
                  borderColor: curComponentID === cp.id ? '#009E9E' : '',
                  borderStyle: curComponentID === cp.id ? 'solid' : 'dashed'
                }}
                onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                  e.stopPropagation();
                  console.log('点击组件: ', cp.id);

                  setCurComponentID(cp.id);

                  const curComponentSchema = {
                    id: cp.id,
                    type: cp.type,
                    displayName: cp.displayName,
                    ...pageComponentSchemas[cp.id]
                  };

                  setCurComponentSchema(curComponentSchema);

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
                    {pageComponentSchemas[cp.id].config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                      <>
                        <div
                          className={styles.copyButton}
                          onClick={(e) => {
                            e.stopPropagation();
                            console.debug('取消隐藏组件: ', cp);
                            handleShowComponent(cp.id);
                          }}
                        >
                          <img src={CompShowIcon} alt="component show" />
                        </div>
                        <Divider className={styles.divider} type="vertical" />
                      </>
                    )}

                    <div
                      className={styles.copyButton}
                      onClick={(e) => {
                        e.stopPropagation();
                        console.log('复制组件: ', cp);
                        handleCopyComponent({ ...cp, id: `${cp.type}-${uuidv4()}` }, cp.id);
                      }}
                    >
                      <img src={CompCopyIcon} alt="component copy" />
                    </div>
                    <Divider className={styles.divider} type="vertical" />
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
                      <img src={CompDeleteIcon} alt="component delete" />
                    </div>
                  </div>
                )}
              </div>
            ))}
        </ReactSortable>

        {/* {showEmpty && (
          <div className={styles.formEmpty}>
            <div className={styles.formEmptyContent}>
              <img src={EmptyIcon} alt="页面无组件" />
              拖拽左侧面板里的组件到这里
              <br />
              开始使用吧！
            </div>
          </div>
        )} */}
      </div>
    </div>
  );
};

export default EditorWorkspace;
