import CompDeleteIcon from '@/assets/images/app_delete.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';
import { Divider } from '@arco-design/web-react';
import { ENTITY_TYPE, ENTITY_TYPE_VALUE, type AppEntityField } from '@onebase/app';
import { getDictDataListByType, getDictDetail } from '@onebase/platform-center';
import { useSignals } from '@preact/signals-react/runtime';
import { cloneDeep } from 'lodash-es';
import React from 'react';
import { ReactSortable, type SortableEvent } from 'react-sortablejs';
import {
  ALL_COMPONENT_TYPES,
  ENTITY_COMPONENT_TYPES,
  FORM_COMPONENT_TYPES
} from 'src/components/Materials/componentTypes';
import { COMPONENT_MAP } from 'src/components/Materials/componentsMap';
import {
  COLOR_MODE_TYPES,
  DEFAULT_OPTIONS_TYPE,
  DEFAULT_VALUE_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from 'src/components/Materials/constants';
import { getComponentConfig, getComponentSchema, getComponentWidth } from 'src/components/Materials/schema';
import EditRender from 'src/components/render/EditRender';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { useAppEntityStore } from 'src/signals/store_entity';
import { type GridItem } from 'src/utils/const';
import { v4 as uuidv4 } from 'uuid';
import './index.css';

interface LayoutReactSortableProps {
  id: string;
  sortableId: string;
  colComponents: any[][];
  groupName: string;
  index: number;
  runtime?: boolean;
}
const LayoutReactSortable: React.FC<LayoutReactSortableProps> = ({
  id,
  sortableId,
  colComponents,
  groupName,
  index,
  runtime = true
}) => {
  useSignals();
  const { mainEntity } = useAppEntityStore();
  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    showDeleteButton,
    setShowDeleteButton,
    layoutSubComponents,
    setLayoutSubComponents,
    setSubTableComponents
  } = usePageEditorSignal();

  // 新增组件/组件拖拽进来
  const handleSortableAdd = (e: SortableEvent) => {
    const cpID = e.item.id || e.item.getAttribute('data-cp-id');
    const itemType = e.item.getAttribute('data-cp-type');
    const itemDisplayName = e.item.getAttribute('data-cp-displayname');

    // const entityID = e.item.getAttribute('data-entity-id');
    // const fieldId = e.item.getAttribute('data-field-id');

    const entityName = e.item.getAttribute('data-entity-name');
    const fieldName = e.item.getAttribute('data-field-name');

    // 子表字段不允许
    if (
      (entityName && entityName !== mainEntity.entityName) ||
      itemType === ENTITY_COMPONENT_TYPES.MAIN_ENTITY ||
      itemType === ENTITY_COMPONENT_TYPES.SUB_ENTITY
    ) {
      return;
    }

    const schemaConfig = getComponentConfig(pageComponentSchemas[cpID!], itemType!);

    const schema = getComponentSchema(itemType as any);

    schema.config = schemaConfig;
    schema.config.cpName = itemDisplayName;
    schema.config.id = cpID;
    schema.config.dataField = entityName && fieldName ? [entityName, fieldName] : [];

    const props = {
      id: cpID,
      type: itemType,
      ...schema
    };

    if (itemType === ALL_COMPONENT_TYPES.COLUMN_LAYOUT) {
      console.log('创建布局组件: ', cpID);
    }

    setPageComponentSchemas(cpID!, props);

    setCurComponentID(cpID!);
    setCurComponentSchema(props);

    setShowDeleteButton(false);
  };
  // 排序
  const handleSortableStart = (e: SortableEvent) => {
    const cpID = e.item.getAttribute('data-id') || '';
    setCurComponentID(cpID);
    const curComponentSchema = pageComponentSchemas[cpID] || {};
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };
  // 组件set
  const handleSortableSetList = (newList: any[]) => {
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
          schema.config.defaultValue = field.defaultValue;
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
          if (cpType === FORM_COMPONENT_TYPES.SELECT_ONE || cpType === FORM_COMPONENT_TYPES.SELECT_MUTIPLE) {
            // 判断是否引用字典数据
            if (field.dictTypeId) {
              const res = await getDictDetail(field.dictTypeId);
              const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
              const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
              if (dictOptions.length) {
                schema.config.defaultOptions = dictOptions;
              }
            } else if (field.options?.length) {
              schema.config.defaultOptions = field.options.map((e: any) => ({
                chosen: field.defaultValue && e.optionValue === field.defaultValue,
                label: e.optionLabel,
                value: e.optionValue
              }));
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
          schema.config.dataField = [item.entityName, field.fieldName];
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
          if (subType === FORM_COMPONENT_TYPES.SELECT_ONE || subType === FORM_COMPONENT_TYPES.SELECT_MUTIPLE) {
            if (ele.dictTypeId) {
              const res = await getDictDetail(ele.dictTypeId);
              const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
              const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
              if (dictOptions.length) {
                const newDefaultOptionsConfig = {
                  type: DEFAULT_OPTIONS_TYPE.CUSTOM,
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
          subSchema.config.dataField = [item.entityName, ele.fieldName];
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
      } else if (item.entityID && item.entityID !== mainEntity.entityId) {
        // 子表 数据字段  不做任何操作
      } else {
        // 主表字段、普通字段
        entityList.push(item);
      }
    });
    colComponents[index] = entityList;
  };

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
  const handleCopyComponent = (comp: any, originId: string, index: number) => {
    // ID 映射表，记录旧 ID 到新 ID 的映射
    const idMap = new Map<string, string>();
    idMap.set(originId, comp.id);

    let rootComponentProps = null; // 保存根组件的 props

    // 递归复制组件及其子组件
    function copyComponentRecursive(oldId: string, newId: string) {
      // 1. 复制组件配置
      const originalComp = pageComponentSchemas[oldId];
      if (!originalComp) return;

      // 深拷贝组件配置
      const schemaConfig = cloneDeep(getComponentConfig(pageComponentSchemas[oldId], comp.type));
      const schema = getComponentSchema(comp.type);

      schema.config = schemaConfig;
      schema.config.cpName = comp.displayName || '';
      schema.config.id = newId;

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
          row.map((item) => {
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

    // 3. 将复制的组件添加到当前布局组件的 layoutSubComponents 中
    if (layoutSubComponents[id]) {
      // 获取当前布局组件的子组件结构
      const currentLayoutSubComponents = layoutSubComponents[id];

      // 创建新的子组件引用
      const newSubComponentRef = {
        id: comp.id,
        type: comp.type
      };

      // 添加到布局组件的对应列中
      const updatedLayoutSubComponents = [...currentLayoutSubComponents];
      if (updatedLayoutSubComponents[index]) {
        updatedLayoutSubComponents[index] = [...updatedLayoutSubComponents[index], newSubComponentRef];
      } else {
        updatedLayoutSubComponents[index] = [newSubComponentRef];
      }

      // 更新布局组件的子组件结构
      setLayoutSubComponents(id, updatedLayoutSubComponents);
    }

    // 设置当前组件
    setCurComponentID(comp.id!);
    setCurComponentSchema(rootComponentProps);
    setShowDeleteButton(false);

    console.log('布局内复制完成，ID 映射:', Object.fromEntries(idMap));
  };

  // 删除组件
  const handleDeleteComponent = (componentId: string) => {
    // 从组件列表中移除
    // 遍历二维数组的每一列，过滤掉 id 匹配的组件
    const updatedColumns = colComponents.map((col) => col.filter((cp) => cp.id !== componentId));
    setLayoutSubComponents(id, updatedColumns);
    delPageComponentSchemas(componentId);

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      clearCurComponentID();
    }
  };

  return (
    <ReactSortable
      id={sortableId}
      list={colComponents[index]}
      group={{ name: groupName }}
      sort={true}
      forceFallback={true}
      animation={150}
      fallbackOnBody={true}
      swapThreshold={0.65}
      className="content"
      onAdd={handleSortableAdd}
      onStart={handleSortableStart}
      setList={handleSortableSetList}
    >
      {colComponents[index] &&
        colComponents[index].map((cp: GridItem) => (
          <div
            key={cp.id}
            data-cp-type={cp.type}
            data-cp-displayname={cp.displayName}
            data-cp-id={cp.id}
            className="componentItem"
            style={{
              width: `calc(${getComponentWidth(pageComponentSchemas[cp.id], cp.type)} - 8px)`,
              borderColor: curComponentID === cp.id ? '#4FAE7B' : 'transparent',
              borderStyle: curComponentID === cp.id ? 'solid' : 'dashed',
              margin: '4px'
            }}
            onClick={(e: React.MouseEvent<HTMLDivElement>) => {
              e.stopPropagation();
              console.log('点击组件: ', cp.id);
              setCurComponentID(cp.id);

              const curComponentSchema = pageComponentSchemas[cp.id];
              setCurComponentSchema(curComponentSchema);
              setShowDeleteButton(true);
            }}
          >
            <EditRender
              runtime={runtime}
              cpId={cp.id}
              cpType={cp.type}
              pageComponentSchema={pageComponentSchemas[cp.id]}
            />

            {/* 操作按钮 */}
            {curComponentID === cp.id && showDeleteButton && (
              <div className="operationArea">
                {pageComponentSchemas[cp.id].config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <>
                    <div
                      className="copyButton"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleShowComponent(cp.id);
                      }}
                    >
                      <img src={CompShowIcon} alt="component show" />
                    </div>
                    <Divider className="divider" type="vertical" />
                  </>
                )}

                <div
                  className="copyButton"
                  onClick={(e) => {
                    e.stopPropagation();
                    console.log('复制组件: ', cp);
                    handleCopyComponent({ ...cp, id: `${cp.type}-${uuidv4()}` }, cp.id, index);
                  }}
                >
                  <img src={CompCopyIcon} alt="component copy" />
                </div>
                <Divider className="divider" type="vertical" />

                <div
                  className="deleteButton"
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
  );
};

export default LayoutReactSortable;
