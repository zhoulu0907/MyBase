import { Form as MobileForm } from '@arco-design/mobile-react';
import { getDictDataListByType, getDictDetail } from '@onebase/platform-center';
import { v4 as uuidv4 } from 'uuid';

import {
  COLOR_MODE_TYPES,
  COMPONENT_GROUP_NAME,
  COMPONENT_MAP,
  DEFAULT_OPTIONS_TYPE,
  DEFAULT_VALUE_TYPES,
  EDITOR_TYPES,
  ENTITY_COMPONENT_TYPES,
  FORM_COMPONENT_TYPES,
  getComponentConfig,
  getComponentSchema,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  usePageViewEditorSignal,
  useFormEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import { EditRender } from '@onebase/ui-kit-mobile';

import EmptyIcon from '@/assets/images/empty.svg';
import { cloneDeep } from 'lodash-es';
import { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';

import MobileActiveIcon from '@/assets/images/mobile_icon_active.svg';
import PCIcon from '@/assets/images/pc_icon.svg';

import NextIcon from '@/assets/images/next_icon.svg';
import PrevActiveIcon from '@/assets/images/prev_icon_active.svg';

import CompDeleteIcon from '@/assets/images/app_delete.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';

import type { EditorProps } from '@/common/props';
import { ENTITY_TYPE, ENTITY_TYPE_VALUE, type AppEntities, type AppEntity, type AppEntityField } from '@onebase/app';
import { EditMode, getHashQueryParam } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import 'react-grid-layout/css/styles.css';
import View from '../view';
import styles from './index.module.less';

interface EditorWorkspaceProps {
  props: EditorProps & {
    mainEntity?: AppEntity;
    subEntities?: AppEntities;
    useEditorSignalMap: Map<string, any>;
    batchDelPageComponentSchemas: (componentIds: Set<string>) => void;
    batchDelLayoutSubComponents: (componentIds: Set<string>) => void;
    subTableComponents: Record<string, AppEntityField[]>;
    setSubTableComponents: (subTableComponentId: string, componentIds: AppEntityField[]) => void;
    batchDelSubTableComponents: (componentIds: Set<string>) => void;
  };
  isListEditor?: boolean;
}

const EditorWorkspace: React.FC<EditorWorkspaceProps> = ({ props, isListEditor = false }) => {
  useSignals();

  const {
    mainEntity,
    useEditorSignalMap,
    pageViews,
    curViewId,
    setCurViewId,
    updatePageViewName,
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
    batchDelPageComponentSchemas,
    batchDelLayoutSubComponents,
    subTableComponents,
    setSubTableComponents,
    batchDelSubTableComponents
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

    // 布局组件删除
    if (layoutSubComponents[componentId]) {
      // 收集所有需要删除的组件 ID
      const idsToDelete = new Set<string>();
      idsToDelete.add(componentId);

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

      delComponents(componentId);
      // 删除所有收集到的组件
      batchDelPageComponentSchemas(idsToDelete);
      batchDelLayoutSubComponents(idsToDelete);
    } else if (subTableComponents[componentId]) {
      // 子表单删除
      // 收集所有需要删除的组件 ID
      const idsToDelete = new Set<string>();
      idsToDelete.add(componentId);

      // 递归收集需要删除的组件 ID
      function collectDeleteIds(id: string) {
        if (subTableComponents[id]) {
          subTableComponents[id].forEach((row: any) => {
            if (!idsToDelete.has(row.id)) {
              idsToDelete.add(row.id);
            }
          });
        }
      }

      // 开始收集
      collectDeleteIds(componentId);

      delComponents(componentId);
      // 删除所有收集到的组件
      batchDelPageComponentSchemas(idsToDelete);
      batchDelSubTableComponents(idsToDelete);
    } else {
      // 从组件列表中移除
      delComponents(componentId);
      delPageComponentSchemas(componentId);
    }

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  return (
    <div className={styles.editorWorkspace}>
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
              subTableComponents={subTableComponents}
              updatePageViewName={updatePageViewName}
              usePageViewEditorSignal={usePageViewEditorSignal}
              useFormEditorSignal={useFormEditorSignal}
              useEditorSignalMap={useEditorSignalMap}
            />
          )}
        </div>
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

      <MobileForm>
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
          <div className={isListEditor ? styles.workspaceContentOuterList : styles.workspaceContentOuter}>
            <ReactSortable
              id="workspace-content"
              list={components}
              setList={(newList) => {
                const entityList: GridItem[] = [];
                newList.forEach(async (item) => {
                  if (item.type == ENTITY_TYPE_VALUE.MAIN || item.entityType === ENTITY_TYPE.MAIN) {
                    // 主表业务实体
                    const fieldList = item.fields.filter(
                      (field: AppEntityField) =>
                        field.fieldName !== 'lock_version' &&
                        field.fieldName !== 'deleted' &&
                        field.fieldName !== 'parent_id' &&
                        field.isSystemField !== 1
                    );
                    for (let field of fieldList) {
                      let cpType = COMPONENT_MAP[field.fieldType];
                      if (!cpType) {
                        continue;
                      }
                      let cpID = `${cpType}-${uuidv4()}`;

                      const schema = getComponentSchema(cpType as any);

                      // 数据长度 dataLength
                      // 小数位数 decimalPlaces
                      // 默认值 defaultValue => defaultValueConfig
                      if (schema.config.defaultValueConfig) {
                        const defaultValueConfig = {
                          ...schema.config.defaultValueConfig,
                          type: DEFAULT_VALUE_TYPES.CUSTOM,
                          customValue: field.defaultValue
                        };
                        schema.config.defaultValueConfig = defaultValueConfig;
                      }
                      // 字段描述 description
                      schema.config.tooltip = field.description;
                      // 是否必填：1-是，0-不是 isRequired
                      // 是否唯一：1-是，0-不是 isUnique
                      schema.config.verify = {
                        ...schema.config.verify,
                        required: field.isRequired,
                        noRepeat: field.isUnique
                      };

                      // 字段选项列表（单/多选字段专用） options COMPONENT_MAP
                      if (
                        cpType === FORM_COMPONENT_TYPES.SELECT_ONE ||
                        cpType === FORM_COMPONENT_TYPES.SELECT_MUTIPLE
                      ) {
                        // 判断是否引用字典数据
                        if (field.dictTypeId) {
                          const res = await getDictDetail(field.dictTypeId);
                          const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
                          const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
                          if (dictOptions.length) {
                            const newDefaultOptionsConfig = {
                              type: DEFAULT_OPTIONS_TYPE.DICT,
                              disabled: true,
                              dictTypeId: field.dictTypeId,
                              colorMode: true,
                              colorModeType: COLOR_MODE_TYPES.POINT,
                              defaultOptions: dictOptions
                            };
                            schema.config.defaultOptionsConfig = {
                              ...schema.config.defaultOptionsConfig,
                              ...newDefaultOptionsConfig
                            };
                          }
                        } else if (field.options?.length) {
                          // TODO defaultOptions不需要了 直接从接口读取
                          const newDefaultOptionsConfig = {
                            defaultOptions: field.options.map((e: any) => ({
                              label: e.optionLabel,
                              value: e.optionValue
                            }))
                          };
                          schema.config.defaultOptionsConfig = {
                            ...schema.config.defaultOptionsConfig,
                            disabled: true,
                            ...newDefaultOptionsConfig
                          };
                        }
                      }
                      // 字段约束配置（长度/正则） constraints
                      schema.config.constraints = field.constraints;
                      // 自动编号完整配置（含规则项） autoNumberConfig
                      if (cpType === FORM_COMPONENT_TYPES.AUTO_CODE) {
                        schema.config.autoCodeConfig = field.autoNumberConfig || schema.config.autoCodeConfig;
                        schema.config.autoCodeDisabled = field?.autoNumberConfig?.id ? true : false;
                      }
                      // 关联的字典类型ID    dictTypeId

                      schema.config.cpName = field.displayName;
                      schema.config.id = cpID;
                      schema.config.dataField = [item.tableName, field.fieldName];
                      schema.config.label.text = field.displayName;
                      const props = {
                        id: cpID,
                        type: cpType,
                        ...schema
                      };

                      setPageComponentSchemas(cpID!, props);
                      setShowDeleteButton(false);

                      entityList.push({ displayName: field.displayName, id: cpID, type: cpType });
                    }
                  } else if (item.type == ENTITY_TYPE_VALUE.SUB || item.entityType === ENTITY_TYPE.SUB) {
                    // 子表业务实体
                    const cpName = item.entityName || '子表单';
                    const cpType = FORM_COMPONENT_TYPES.SUB_TABLE;
                    const cpID = `${cpType}-${uuidv4()}`;

                    // 子表单 配置
                    const schema = getComponentSchema(cpType as any);
                    schema.config.cpName = cpName;
                    schema.config.id = cpID;
                    schema.config.label.text = cpName;
                    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];
                    schema.config.subTable = item.id;

                    const props = {
                      id: cpID,
                      type: cpType,
                      ...schema
                    };
                    setPageComponentSchemas(cpID!, props);
                    setShowDeleteButton(false);

                    const subFieldList = item.fields.filter(
                      (field: AppEntityField) =>
                        field.fieldName !== 'lock_version' &&
                        field.fieldName !== 'deleted' &&
                        field.fieldName !== 'parent_id' &&
                        field.isSystemField !== 1
                    );
                    // 子表单的每个表单项配置
                    let subFieldComponents: any = [];
                    for (let ele of subFieldList) {
                      const subType = COMPONENT_MAP[ele.fieldType];
                      if (!subType) {
                        continue;
                      }
                      const subSchema = getComponentSchema(subType as any);
                      const subId = `${subType}-${uuidv4()}`;

                      // 数据长度 dataLength
                      // 小数位数 decimalPlaces
                      // 默认值 defaultValue => defaultValueConfig
                      if (subSchema.config.defaultValueConfig) {
                        const defaultValueConfig = {
                          ...subSchema.config.defaultValueConfig,
                          type: DEFAULT_VALUE_TYPES.CUSTOM,
                          customValue: ele.defaultValue
                        };
                        subSchema.config.defaultValueConfig = defaultValueConfig;
                      }
                      // 字段描述 description
                      subSchema.config.tooltip = ele.description;
                      subSchema.config.verify = {
                        ...subSchema.config.verify,
                        required: ele.isRequired,
                        noRepeat: ele.isUnique
                      };

                      // 字段选项列表（单/多选字段专用） options
                      if (
                        subType === FORM_COMPONENT_TYPES.SELECT_ONE ||
                        subType === FORM_COMPONENT_TYPES.SELECT_MUTIPLE
                      ) {
                        if (ele.dictTypeId) {
                          const res = await getDictDetail(ele.dictTypeId);
                          const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
                          const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
                          if (dictOptions.length) {
                            const newDefaultOptionsConfig = {
                              type: DEFAULT_OPTIONS_TYPE.DICT,
                              disabled: true,
                              dictTypeId: ele.dictTypeId,
                              colorMode: true,
                              colorModeType: COLOR_MODE_TYPES.POINT,
                              defaultOptions: dictOptions
                            };
                            subSchema.config.defaultOptionsConfig = {
                              ...subSchema.config.defaultOptionsConfig,
                              ...newDefaultOptionsConfig
                            };
                          }
                        } else if (ele.options?.length) {
                          const newDefaultOptionsConfig = {
                            defaultOptions: ele.options.map((e: any) => ({
                              label: e.optionLabel,
                              value: e.optionValue
                            }))
                          };
                          subSchema.config.defaultOptionsConfig = {
                            ...subSchema.config.defaultOptionsConfig,
                            disabled: true,
                            ...newDefaultOptionsConfig
                          };
                        }
                      }
                      // 字段约束配置（长度/正则） constraints
                      subSchema.config.constraints = ele.constraints;
                      // 自动编号完整配置（含规则项） autoNumberConfig
                      if (subType === FORM_COMPONENT_TYPES.AUTO_CODE) {
                        subSchema.config.autoCodeConfig = ele.autoNumberConfig || subSchema.config.autoCodeConfig;
                        subSchema.config.autoCodeDisabled = ele?.autoNumberConfig?.id ? true : false;
                      }
                      // 关联的字典类型ID    dictTypeId

                      subSchema.config.cpName = ele.displayName;
                      subSchema.config.id = subId;
                      subSchema.config.label.text = ele.displayName;
                      subSchema.config.label.display = false;
                      subSchema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];
                      subSchema.config.dataField = [item.tableName, ele.fieldName];
                      subSchema.config.width = WIDTH_VALUES[WIDTH_OPTIONS.FULL];
                      const subProps = {
                        id: subId,
                        type: subType,
                        ...subSchema
                      };
                      setPageComponentSchemas(subId, subProps);
                      subFieldComponents.push({ id: subId, type: subType, displayName: ele.displayName });
                    }
                    setSubTableComponents(cpID, subFieldComponents);
                    entityList.push({ displayName: cpName, id: cpID, type: cpType });
                  } else if (item.entityId && item.entityId !== mainEntity.entityId) {
                    // 子表 数据字段  不做任何操作
                  } else {
                    // 主表字段、普通字段

                    // 从子表单拖出来的数据
                    const keys = Object.keys(subTableComponents);
                    for (let key of keys) {
                      if (subTableComponents[key]) {
                        const currentSub = subTableComponents[key]?.find((ele: any) => ele.id === item.id);
                        if (currentSub) {
                          const config = {
                            ...pageComponentSchemas[currentSub.id].config,
                            dataField: [],
                            label: {
                              text: pageComponentSchemas[currentSub.id].config?.label?.text,
                              display: true
                            }
                          };
                          setPageComponentSchemas(currentSub.id, { ...pageComponentSchemas[currentSub.id], config });
                          const newList = subTableComponents[key].filter((ele: any) => ele.id !== currentSub.id);
                          setSubTableComponents(key, newList);
                        }
                      }
                    }
                    entityList.push(item);
                  }
                });
                setComponents(entityList);
              }}
              onAdd={async (e) => {
                let cpID = e.item.id || e.item.getAttribute('data-cp-id');
                const itemType = e.item.getAttribute('data-cp-type');
                const itemDisplayName = e.item.getAttribute('data-cp-displayname');

                const tableName = e.item.getAttribute('data-table-name');
                const fieldName = e.item.getAttribute('data-field-name');
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

                // 子表字段不允许
                if (
                  (tableName && tableName !== mainEntity.tableName) ||
                  itemType === ENTITY_COMPONENT_TYPES.MAIN_ENTITY ||
                  itemType === ENTITY_COMPONENT_TYPES.SUB_ENTITY
                ) {
                  console.log('tableName name', tableName);
                } else {
                  const schema = getComponentSchema(itemType as any);
                  schema.config.cpName = itemDisplayName;
                  schema.config.id = cpID;

                  // 主表 字段组件
                  if (tableName && fieldName) {
                    // 获取当前字段数据源配置
                    const currentField = mainEntity.fields?.find((ele: AppEntityField) => ele.fieldName === fieldName);
                    if (currentField) {
                      // 数据长度 dataLength
                      // 小数位数 decimalPlaces
                      // 默认值 defaultValue => defaultValueConfig
                      if (schema.config.defaultValueConfig) {
                        const defaultValueConfig = {
                          ...schema.config.defaultValueConfig,
                          type: DEFAULT_VALUE_TYPES.CUSTOM,
                          customValue: currentField.defaultValue
                        };
                        schema.config.defaultValueConfig = defaultValueConfig;
                      }
                      // 字段描述 description
                      schema.config.tooltip = currentField.description;
                      // 是否必填：1-是，0-不是 isRequired
                      // 是否唯一：1-是，0-不是 isUnique
                      schema.config.verify = {
                        ...schema.config.verify,
                        required: currentField.isRequired,
                        noRepeat: currentField.isUnique
                      };

                      // 字段选项列表（单/多选字段专用） options
                      if (
                        itemType === FORM_COMPONENT_TYPES.SELECT_ONE ||
                        itemType === FORM_COMPONENT_TYPES.SELECT_MUTIPLE
                      ) {
                        if (currentField.dictTypeId) {
                          const res = await getDictDetail(currentField.dictTypeId);
                          const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
                          const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
                          if (dictOptions.length) {
                            const newDefaultOptionsConfig = {
                              type: DEFAULT_OPTIONS_TYPE.DICT,
                              disabled: true,
                              dictTypeId: currentField.dictTypeId,
                              colorMode: true,
                              colorModeType: COLOR_MODE_TYPES.POINT,
                              defaultOptions: dictOptions
                            };
                            schema.config.defaultOptionsConfig = {
                              ...schema.config.defaultOptionsConfig,
                              ...newDefaultOptionsConfig
                            };
                          }
                        } else if (currentField.options?.length) {
                          const newDefaultOptionsConfig = {
                            defaultOptions: currentField.options.map((e) => ({
                              label: e.optionLabel,
                              value: e.optionValue
                            }))
                          };
                          schema.config.defaultOptionsConfig = {
                            ...schema.config.defaultOptionsConfig,
                            disabled: true,
                            ...newDefaultOptionsConfig
                          };
                        }
                      }
                      // 字段约束配置（长度/正则） constraints
                      schema.config.constraints = currentField.constraints;
                      // 自动编号完整配置（含规则项） autoNumberConfig
                      if (itemType === FORM_COMPONENT_TYPES.AUTO_CODE) {
                        schema.config.autoCodeConfig = currentField.autoNumberConfig || schema.config.autoCodeConfig;
                        schema.config.autoCodeDisabled = currentField?.autoNumberConfig?.id ? true : false;
                      }
                      // 关联的字典类型ID    dictTypeId
                    }
                    schema.config.dataField = [tableName, fieldName];
                    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];
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
              group={{ name: COMPONENT_GROUP_NAME, put: true }}
              sort={true}
              chosenClass={styles.ghostClass}
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
                      borderColor: curComponentID === cp.id ? 'rgb(var(--primary-6))' : '',
                      borderStyle: curComponentID === cp.id ? 'solid' : 'dashed',
                      background: curComponentID === cp.id ? 'rgb(var(--primary-1))' : ''
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
                      useStoreSignals={props}
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
                            <span>|</span>
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
                        <span>|</span>
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
          </div>
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
      </MobileForm>
    </div>
  );
};

export default EditorWorkspace;
