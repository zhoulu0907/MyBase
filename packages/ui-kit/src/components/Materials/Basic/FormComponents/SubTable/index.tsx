import { Divider, Layout, Form, Button, Table } from '@arco-design/web-react';
import { IconPlus, IconDelete } from '@arco-design/web-react/icon';
import { type XSubTableConfig } from './schema';
import { useSignals } from '@preact/signals-react/runtime';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { ReactSortable } from 'react-sortablejs';
import { getComponentConfig } from 'src/components/Materials/schema';
import { getComponentSchema } from '../../../schema';
import { FORM_COMPONENT_TYPES, ENTITY_COMPONENT_TYPES } from '../../../componentTypes';
import EditRender from 'src/components/render/EditRender';
import { COMPONENT_GROUP_NAME, EDITOR_TYPES, type GridItem } from 'src/utils/const';
import { STATUS_OPTIONS, STATUS_VALUES, COLOR_MODE_TYPES,DEFAULT_OPTIONS_TYPE } from '../../../constants';
import { v4 as uuidv4 } from 'uuid';
import CompDeleteIcon from '@/assets/images/app_delete.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';
import { useEffect, useState } from 'react';
import PreviewRender from 'src/components/render/PreviewRender';
import { pagesRuntimeSignal } from '@onebase/common';
import { ENTITY_TYPE_VALUE } from '@onebase/app';
import { useAppEntityStore } from 'src/signals/store_entity'
import { getDictDetail, getDictDataListByType } from '@onebase/platform-center';
import './index.css';

const XSubTable = (props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean }) => {
  useSignals();
  const { id, label, tooltip, status, verify, runtime = true, detailMode, pageType } = props;
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
  } = usePageEditorSignal(pageType || EDITOR_TYPES.FORM_EDITOR);
  const { subTableDataLength } = pagesRuntimeSignal;

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
    const fieldId = e.item.getAttribute('data-field-id');
    const entityId = e.item.getAttribute('data-entity-id');

    // 不允许拖拽主、子表嵌套、主表字段
    if (
      itemType === ENTITY_TYPE_VALUE.MAIN ||
      itemType === ENTITY_TYPE_VALUE.SUB ||
      itemType == ENTITY_COMPONENT_TYPES.MAIN_ENTITY ||
      itemType == ENTITY_COMPONENT_TYPES.SUB_ENTITY ||
      entityId && entityId === mainEntity.entityId
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
    if (entityId) {
      const sameField = subTableComponents[id]?.every((ele) => {
        const dataField = pageComponentSchemas[ele.id].config.dataField;
        return !dataField || dataField?.[0] === entityId;
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
    const currentEntity = subEntities.entities?.find((ele) => ele.entityId === entityId);
    // 当前字段
    const currentField = currentEntity?.fields?.find((ele) => ele.fieldId === fieldId);
    if (currentField) {
      // 数据长度 dataLength
      // 小数位数 decimalPlaces
      // 默认值 defaultValue
      const defaultValueConfig = { ...schema.config.defaultValue, customValue: currentField.defaultValue };
      schema.config.defaultValueConfig = defaultValueConfig;
      // 字段描述 description
      schema.config.tooltip = currentField.description;
      // 是否必填：1-是，0-不是 isRequired
      // 是否唯一：1-是，0-不是 isUnique
      schema.config.verify = {
        ...schema.config.verify,
        required: currentField.isRequired,
        noRepeat: currentField.isUnique
      }

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

    schema.config.cpName = itemDisplayName;
    schema.config.label.text = itemDisplayName;
    schema.config.label.display = false;
    schema.config.dataField = entityId ? [entityId, fieldId] : [];
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
    const cpID = e.item.getAttribute('data-id') || '';
    setCurComponentID(cpID);
    const curComponentSchema = pageComponentSchemas[cpID] || {};
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };
  // 子表单里的元素 点击事件
  const onSubComponentClick = (e: React.MouseEvent<HTMLDivElement>, cp: GridItem) => {
    e.stopPropagation();
    setCurComponentID(cp.id);
    const curComponentSchema = pageComponentSchemas[cp.id];
    setCurComponentSchema(curComponentSchema);
    setShowDeleteButton(true);
  };

  /**
   * 预览
   */
  const [subTableData, setSubTableData] = useState<any[]>([]);
  const [subTableColumns, setSubTableColumns] = useState<any[]>([]);
  const { form } = Form.useFormContext();

  useEffect(() => {
    getTableColumns();
  }, []);

  useEffect(() => {
    let newSubTableData: any[] = [];
    for (let i = 0; i < subTableDataLength.value[id]; i++) {
      newSubTableData.push({ key: `${i}` });
    }
    setSubTableData(newSubTableData);
  }, [subTableDataLength.value]);

  // 获取表格配置 columns
  const getTableColumns = () => {
    let tableColumns = [];
    for (let column of subTableComponents[id] || []) {
      const displayName = pageComponentSchemas[column.id].config.label.text || column.displayName;
      const required = pageComponentSchemas[column.id].config?.verify?.required;
      const tableColumn = {
        title: (
          <>
            {displayName}
            {required ? <span style={{ color: 'red', paddingLeft: '4px' }}>*</span> : null}
          </>
        ),
        dataIndex: column.id,
        key: column.id,
        bodyCellStyle: {
          padding: '4px 0'
        },
        render: (_text: string, _record: any, index: number) => {
          const config = {
            ...pageComponentSchemas[column.id].config,
            dataField: [`${id}.${index}.${pageComponentSchemas[column.id].config?.dataField?.[1] || column.id}`]
          };
          const pageSchema = { ...pageComponentSchemas[column.id], config };
          return (
            <PreviewRender
              cpId={column.id}
              cpType={column.type}
              detailMode={detailMode}
              pageComponentSchema={pageSchema}
              runtime={true}
            />
          );
        }
      };
      tableColumns.push(tableColumn);
    }
    if (runtime && !detailMode) {
      tableColumns.push({
        title: '操作',
        dataIndex: 'action',
        width: 100,
        fixed: 'right',
        render: (_col: any, _record: any, index: number) => {
          return (
            <Button type="text" status="danger" icon={<IconDelete />} onClick={() => handleDelete(index)}></Button>
          );
        }
      });
    }
    setSubTableColumns(tableColumns);
  };

  // 新增
  const handleAdd = () => {
    const keys = subTableComponents[id].map((ele) => ele.id);
    let newData: any = {};
    keys.forEach((key) => {
      newData[key] = undefined;
    });
    setSubTableData((prevData) => [...prevData, newData]);
  };
  // 删除
  const handleDelete = (index: number) => {
    setSubTableData((prevData) => prevData.filter((_, i) => i !== index));

    const formData = form.getFieldsValue();

    const filterFormData = formData[id].filter((_: any, i: number) => i !== index);

    const updateFormData = {
      ...formData,
      [id]: filterFormData
    };

    form.setFieldsValue(updateFormData);
  };

  return (
    <Layout className="XSubTable" style={runtime ? { border: 'none' } : {}}>
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        layout="vertical"
        rules={[{ required: verify?.required }]}
        tooltip={tooltip}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          width: '100%',
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {runtime ? (
          <>
            <div className="subTableContent">
              <Table
                columns={subTableColumns}
                data={subTableData}
                size="small"
                scroll={{ x: 'max-content' }}
                style={{
                  width: '100%'
                }}
                border={{
                  headerCell: true,
                  wrapper: true,
                }}
                rowKey="id"
                pagination={false}
              />
            </div>
            <div className="subTableFooter">
              {!detailMode && (
                <Button
                  type="outline"
                  icon={<IconPlus />}
                  style={{ pointerEvents: runtime ? 'unset' : 'none', marginTop: 10 }}
                  onClick={handleAdd}
                >
                  新增一项
                </Button>
              )}
            </div>
          </>
        ) : (
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
                const isTable = ele.type === ENTITY_TYPE_VALUE.MAIN || ele.type === ENTITY_TYPE_VALUE.SUB;
                // 主表数据
                const isMain = ele.entityID === mainEntity.entityId;
                // 同一个子表
                const isSub = !ele.entityID || !dataField || ele.entityID === dataField
                return !isTable && !isMain && isSub;
              })

              setSubTableComponents(id, newSubList);
            }}
            onAdd={onSubAdd}
            group={{ name: COMPONENT_GROUP_NAME }}
            sort={true}
            forceFallback={true}
            animation={150}
            fallbackOnBody={true}
            swapThreshold={0.65}
            className="XSubTable-content"
            onStart={onSubStart}
          >
            {subTableComponents[id] &&
              subTableComponents[id].map((cp: GridItem, index: number) => (
                <div
                  key={cp.id}
                  data-cp-type={cp.type}
                  data-cp-displayname={cp.displayName}
                  data-cp-id={cp.id}
                  className="componentItem"
                  style={{
                    borderColor: curComponentID === cp.id ? '#4FAE7B' : 'transparent'
                  }}
                  onClick={(e) => {
                    onSubComponentClick(e, cp);
                  }}
                >
                  <div className="simulate-header-item">
                    {pageComponentSchemas[cp.id].config.label.text || pageComponentSchemas[cp.id].config.displayName}
                  </div>
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
                              console.debug('取消隐藏组件: ', cp);
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
        )}
      </Form.Item>
    </Layout>
  );
};

export default XSubTable;
