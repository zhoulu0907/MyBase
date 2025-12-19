import { useEffect, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { ReactSortable } from 'react-sortablejs';
import { useSignals } from '@preact/signals-react/runtime';
import { ITypeRules, ValidatorType } from '@arco-design/mobile-utils';
import { IconAdd, IconDelete } from '@arco-design/mobile-react/esm/icon';
import { Cell, Collapse, Ellipsis, Form } from '@arco-design/mobile-react';
import { pagesRuntimeSignal } from '@onebase/common';
import { getDictDataListByType, getDictDetail } from '@onebase/platform-center';
import {
  COLOR_MODE_TYPES,
  COMPONENT_GROUP_NAME,
  DEFAULT_OPTIONS_TYPE,
  DEFAULT_VALUE_TYPES,
  EDITOR_TYPES,
  ENTITY_COMPONENT_TYPES,
  FORM_COMPONENT_TYPES,
  FormSchema,
  getComponentConfig,
  getComponentSchema,
  GridItem,
  STATUS_OPTIONS,
  STATUS_VALUES,
  usePageEditorSignal
} from '@onebase/ui-kit';
import { useAppEntityStore } from '@onebase/ui-kit/src/signals/store_entity';
import { ENTITY_TYPE_VALUE } from '@onebase/app';
import { EditRender, PreviewRender } from '@/components/render';
import CompDeleteIcon from '@/assets/images/app_delete.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';
import './index.css';

type XSubTableConfig = typeof FormSchema.XSubTableSchema.config;

const XSubTable = (props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; form?: any; editLoading?: boolean; useStoreSignals?: any; editPreview?: boolean; }) => {
  useSignals();

  const { id, label, tooltip, status, subTableConfig, verify, runtime = true, detailMode, pageType, form, editLoading, useStoreSignals, editPreview } = props;
  const { mainEntity, subEntities } = useAppEntityStore();

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
    subTableComponents,
    setSubTableComponents
  } = runtime && !editPreview ? usePageEditorSignal(pageType || EDITOR_TYPES.FORM_EDITOR) : useStoreSignals;
  const { subTableDataLength } = pagesRuntimeSignal;
  const [subTableData, setSubTableData] = useState<any[]>([]);

  useEffect(() => {
    if (!subTableData || subTableData.length === 0) {
      let newSubTableData: any[] = [];
      for (let i = 0; i < subTableDataLength.value[id]; i++) {
        newSubTableData.push({ key: i });
      }
      setSubTableData(newSubTableData);
    }
  }, [subTableDataLength.value]);

  // 判断拖拽的组件是否是表单组件
  const isFormComponent = (type: string): boolean => {
    let isForm = false;
    const keys = Object.keys(FORM_COMPONENT_TYPES);
    for (let key of keys) {
      if (type === FORM_COMPONENT_TYPES[key as keyof typeof FORM_COMPONENT_TYPES]) {
        isForm = true;
      }
    }

    return isForm;
  };

  /* 新增数据 */
  const handleAdd = () => {
    const newData = { key: subTableData[subTableData.length - 1]?.key + 1 || 0 };
    setSubTableData((prevData) => [...prevData, newData]);
  };

  /* 删除数据 */
  const handleDelete = (e: any, index: number) => {
    e.stopPropagation();

    const formData = form?.getFieldsValue();

    if (formData) {
      const filtered = Object.fromEntries(Object.entries(formData).filter(([key]) => !key.includes(`.${index}.`)));
      form?.setFieldsValue(filtered);
    }
    setSubTableData((prev) => prev.filter((v) => v.key !== index));
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      type: ValidatorType.Custom,
      validator: (value, callback) => {
        if (!value && verify?.required) {
          callback(`${label.text}是必填项`);
        }
      }
    }
  ];

  // 取消隐藏组件
  const handleShowComponent = (componentId: string) => {
    const schema = pageComponentSchemas[componentId];
    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];

    setPageComponentSchemas(componentId, schema);
    // setTimeout(() => {
    setCurComponentID(componentId);
    setCurComponentSchema(schema);
    setShowDeleteButton(false);
    // }, 0);
  };
  // 复制组件
  const handleCopyComponent = (comp: any, originId: string, index: number) => {
    // ID 映射表，记录旧 ID 到新 ID 的映射
  };
  // 删除组件
  const handleDeleteComponent = (componentId: string) => {
    // 从组件列表中移除 遍历，过滤掉 id 匹配的组件
    const updatedColumns = subTableComponents[id].filter((cp) => cp.id !== componentId);
    setSubTableComponents(id, updatedColumns);
    delPageComponentSchemas(componentId);

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  // 拖拽添加
  const onSubAdd = async (e: any) => {
    const cpID = e.item.getAttribute('data-cp-id') || e.item.getAttribute('data-id') || e.item.id;
    const itemType = e.item.getAttribute('data-cp-type');
    const fieldName = e.item.getAttribute('data-field-name');
    const tableName = e.item.getAttribute('data-table-name');

    // 不允许拖拽主、子表嵌套、主表字段
    if (
      itemType === ENTITY_TYPE_VALUE.MAIN ||
      itemType === ENTITY_TYPE_VALUE.SUB ||
      itemType == ENTITY_COMPONENT_TYPES.MAIN_ENTITY ||
      itemType == ENTITY_COMPONENT_TYPES.SUB_ENTITY ||
      (tableName && tableName === mainEntity.tableName)
    ) {
      return;
    }
    // 只能拖拽表单 && 不能是子表单
    const isForm = isFormComponent(itemType || '');
    if (!itemType || !isForm || itemType === FORM_COMPONENT_TYPES.SUB_TABLE) {
      if (cpID) {
        const updatedColumns = subTableComponents[cpID]?.filter((cp) => cp.id !== cpID);
        if (updatedColumns) {
          setSubTableComponents(id, updatedColumns);
        }
        delPageComponentSchemas(cpID);
        clearCurComponentID();
      }
      return;
    }

    // 拖拽的子表项必须是同一个子表
    if (tableName) {
      const sameField = subTableComponents[id]?.every((ele) => {
        const dataField = pageComponentSchemas[ele.id].config.dataField;
        return !dataField || dataField?.[0] === tableName;
      });
      if (!sameField) {
        return;
      }
    }

    // 表单项配置
    const schemaConfig = getComponentConfig(pageComponentSchemas[cpID!], itemType!);
    const schema = getComponentSchema(itemType as any);
    const itemDisplayName = e.item.getAttribute('data-label') || e.item.getAttribute('data-cp-displayname');
    schema.config = schemaConfig;

    // 当前实体
    const currentEntity = subEntities.entities?.find((ele: any) => ele.tableName === tableName);
    // 当前字段
    const currentField = currentEntity?.fields?.find((ele: any) => ele.fieldName === fieldName);
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
      if (itemType === FORM_COMPONENT_TYPES.SELECT_ONE || itemType === FORM_COMPONENT_TYPES.SELECT_MUTIPLE) {
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
            defaultOptions: currentField.options.map((e: any) => ({
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

    schema.config.cpName = itemDisplayName;
    schema.config.label.text = itemDisplayName;
    schema.config.label.display = false;
    schema.config.dataField = tableName ? [tableName, fieldName] : [];
    schema.config.id = cpID;
    const props = {
      id: cpID,
      type: itemType,
      ...schema
    };
    const newSub = { id: cpID, type: itemType, displayName: itemDisplayName };
    setSubTableComponents(id, [...subTableComponents[id], newSub]);
    setPageComponentSchemas(cpID!, props);
    setCurComponentID(cpID!);
    setCurComponentSchema(props);
    setShowDeleteButton(false);
  };

  // 子表单内排序 拖拽选中
  const onSubStart = (e: any) => {
    e.stopPropagation();
    const cpID = e.item.getAttribute('data-id') || '';
    const curComponentSchema = pageComponentSchemas[cpID] || {};
    setCurComponentID(cpID);
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };

  // 子表单里的元素 点击事件
  const onSubComponentClick = (e: React.MouseEvent<HTMLDivElement>, cp: GridItem) => {
    e.stopPropagation();
    const curComponentSchema = pageComponentSchemas[cp.id];
    setCurComponentID(cp.id);
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };

  return (
    <Form.Item
      className={`inputTextWrapperOBMobile subTableWrapperOBMobile`}
      field=""
      rules={rules}
      layout="vertical"
      label={label.display ? <Ellipsis text={label.text} /> : undefined}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {
        runtime ? (
          <>
            {subTableData.map((item, index) => (
              <Collapse
                className="collapseSubtableOBMobile"
                key={item.key}
                header={
                  <div className="collapseHeaderSubtableObMobile">
                    #{index + 1}
                    <IconDelete onClick={(e) => handleDelete(e, item.key)} />
                  </div>
                }
                value={item.key + ''}
                defaultActive
                content={
                  <Cell.Group>
                    {subTableComponents &&
                      subTableComponents[id] &&
                      subTableComponents[id].map((subTable: any) => {
                        const schema = pageComponentSchemas[subTable.id];

                        const config = {
                          ...schema.config,
                          dataField: [`${id}.${item.key}.${schema.config?.dataField?.[1] || subTable.id}`]
                        };
                        const pageSchema = { ...schema, config };
                        return (
                          <Cell label={<Ellipsis text={config.cpName} />} key={subTable.id} style={{ padding: 0 }}>
                            <PreviewRender
                              editLoading={editLoading}
                              form={form}
                              cpId={subTable.id}
                              cpType={subTable.type}
                              detailMode={detailMode}
                              pageComponentSchema={pageSchema}
                              runtime={true}
                            // showFromPageData={showFromPageData}
                            />
                          </Cell>
                        );
                      })}
                  </Cell.Group>
                }
              />
            ))}
            {!detailMode && <div
              className="onAddSubtableOBMobile"
              onClick={handleAdd}
              style={{ pointerEvents: runtime ? 'unset' : 'none' }}>
              <IconAdd style={{ marginRight: '0.16rem' }} />
              新增一项
            </div>}
          </>
        ) : (
          <div style={{ width: '100%', display: 'flex', flexDirection: 'column' }}>
            <Collapse
              className="collapseSubtableOBMobile"
              header={
                <div className="collapseHeaderSubtableObMobile">
                  #1<IconDelete />
                </div>
              }
              value={''}
              defaultActive
              content={
                <Cell.Group>
                  <ReactSortable
                    id={`workspace-content-subtable-${id}`}
                    list={subTableComponents[id] || []}
                    setList={(newList) => {
                      const dataFieldPage = subTableComponents[id]?.find(
                        (ele) => pageComponentSchemas[ele.id].config?.dataField?.[0]
                      );
                      // 已有的数据源子表id
                      const dataField = pageComponentSchemas?.[dataFieldPage?.id]?.config?.dataField?.[0];
                      /**
                       * 不允许拖拽主、子表嵌套
                       * 拖拽的子表项必须是同一个子表
                       */
                      const newSubList = (newList || []).filter((ele) => {
                        // 主表、子表
                        const isTable =
                          ele.type === ENTITY_TYPE_VALUE.MAIN ||
                          ele.type === ENTITY_TYPE_VALUE.SUB ||
                          ele.type === 'XSubTable';
                        // 主表数据
                        const isMain = ele.tableName && ele.tableName === mainEntity.tableName;
                        // 同一个子表
                        const isSameSub = !ele.tableName || !dataField || ele.tableName === dataField;
                        return !isTable && !isMain && isSameSub;
                      });

                      setSubTableComponents(id, newSubList);
                    }}
                    onAdd={onSubAdd}
                    group={{ name: COMPONENT_GROUP_NAME }}
                    sort={true}
                    forceFallback={true}
                    animation={150}
                    fallbackOnBody={true}
                    swapThreshold={0.65}
                    className="subTableContentOBMobile"
                    onStart={onSubStart}
                  >
                    {subTableComponents &&
                      subTableComponents[id] &&
                      subTableComponents[id].map((cp: GridItem, index: number) => {
                        const schema = pageComponentSchemas[cp.id];
                        const config = {
                          ...schema.config,
                        };

                        return (
                          <div
                            key={cp.id}
                            data-cp-type={cp.type}
                            data-cp-displayname={cp.displayName}
                            data-cp-id={cp.id}
                            data-id={cp.id}
                            className="componentItemSubtableOBMobile"
                            style={{
                              borderColor: curComponentID === cp.id ? 'rgb(var(--primary-6))' : 'transparent'
                            }}
                            onClick={(e) => {
                              onSubComponentClick(e, cp);
                            }}
                          >
                            <Cell label={<Ellipsis text={config.cpName} />} key={cp.id} style={{ padding: 0 }}>
                              <EditRender
                                runtime={runtime}
                                cpId={cp.id}
                                cpType={cp.type}
                                pageComponentSchema={pageComponentSchemas[cp.id]}
                              />
                            </Cell>

                            {/* 操作按钮 */}
                            {curComponentID === cp.id && showDeleteButton && (
                              <div className="operationArea">
                                {pageComponentSchemas[cp.id]?.config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                                  <>
                                    <div
                                      className="copyButton"
                                      onClick={(e) => {
                                        e.stopPropagation();
                                        console.debug('取消隐藏组件: ', cp);
                                        handleShowComponent(cp.id);
                                      }}
                                    >
                                      <img src={CompShowIcon} alt="component show" />
                                    </div>
                                    {/* <Divider className="divider" type="vertical" /> todo */}
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
                                {/* <Divider className="divider" type="vertical" /> todo */}

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
                        )
                      })}
                  </ReactSortable>

                </Cell.Group>
              }
            />
            {!detailMode && <div
              className="onAddSubtableOBMobile"
              onClick={handleAdd}
              style={{ pointerEvents: runtime ? 'unset' : 'none' }}>
              <IconAdd style={{ marginRight: '0.16rem' }} />
              新增一项
            </div>}
          </div>
        )
      }
    </Form.Item>
  );
}

export default XSubTable;
