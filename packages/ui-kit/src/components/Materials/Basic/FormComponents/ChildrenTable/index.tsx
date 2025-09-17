import { useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { ReactSortable } from 'react-sortablejs';
import { IconPlus } from '@arco-design/web-react/icon';
import { useSignals } from '@preact/signals-react/runtime';

import { Layout, Message, Table, Button, Popconfirm, Form, Grid } from '@arco-design/web-react';
import { getComponentConfig } from 'src/components/Materials/schema';
import EditRender from 'src/components/render/EditRender';
import DragableTable from './dragableTable';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { COMPONENT_GROUP_NAME } from 'src/utils/const';
import { STATUS_OPTIONS, STATUS_VALUES, LAYOUT_VALUES, LAYOUT_OPTIONS } from '../../../constants';
import { ALL_COMPONENT_TYPES, FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { getComponentSchema } from '../../../schema';
import { type XChildrenTableConfig } from './schema';
import './index.css';

const leftPanelWidth = 343;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40 + 32 + 10;
const canvasMarginWidth = 10;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth + canvasMarginWidth;

const XChildrenTable = (props: XChildrenTableConfig & { runtime?: boolean }) => {
  const { colCount, id, runtime = true, label, layout, tooltip, labelColSpan = 100, status, verify } = props;

  useSignals();

  const {
    curComponentID,
    setComponents,
    setCurComponentID,
    clearCurComponentID,
    curComponentSchema,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    showDeleteButton,
    setShowDeleteButton,

    layoutSubComponents,
    setLayoutSubComponents
  } = usePageEditorSignal();

  const [columns, setColumns] = useState<any[]>([]);
  const [tableData, setTableData] = useState<any[]>([]);

  // 从 store 中获取当前组件的列数据，如果不存在则初始化为空数组
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  useEffect(() => {
    if (colComponents[0].length === 0) return;
    const index = {
      id: 'index',
      title: '序号',
      dataIndex: 'index',
      align: 'center',
      width: 65,
      render: (_: any, __: any, rowIndex: number) => rowIndex + 1,
    };
    const operation = {
      id: 'operation',
      title: '操作',
      dataIndex: 'operation',
      align: 'center',
      fixed: 'right',
      width: 150,
      render: (_: any, record: any) => (
        <div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
          <Button size="small" type="text" onClick={() => handleCopy(record)}>
            复制
          </Button>
          <Popconfirm
            title="确认删除吗?"
            disabled={colComponents[0].length === 1}
            onOk={() => handleDelete(record.key)}
          >
            <Button size="small" type="text" disabled={colComponents[0].length === 1} status="danger">
              删除
            </Button>
          </Popconfirm>
        </div>
      ),
    }

    const copyData = [...colComponents[0]];

    const customData = copyData.map(comp => {

      const { id: cpID, type: itemType, displayName } = comp;

      const schemaConfig = getComponentConfig(pageComponentSchemas[cpID], itemType);
      const schema = getComponentSchema(itemType as any);

      schema.config = schemaConfig;
      schema.config.cpName = displayName;
      schema.config.id = cpID;

      const props = {
        id: cpID,
        type: itemType,
        ...schema,
        config: {
          ...schema.config,
          label: {
            ...schema.config.label,
            display: false
          }
        },
      };

      setPageComponentSchemas(cpID, props);

      if (tableData.length === 0) {
        const defaultData = [{ [comp.id]: '', key: nanoid() }];
        setTableData(defaultData);
      } else if (tableData.length === 1) {
        setTableData(prev => {
          // 创建第一个对象的副本并添加新字段
          const updatedFirstItem = { ...prev[0], [comp.id]: '' };
          // 创建新数组，替换第一个元素
          return [updatedFirstItem];
        });
      }

      const label = {
        text: displayName,
        display: false
      };

      return {
        ...comp,
        id: comp.id,
        title: displayName,
        dataIndex: comp.id,
        align: 'center',
        width: 200,
        render: (_: any, _record: any) => (
          <div key={comp.id}>
            <EditRender runtime={runtime} cpId={comp.id} cpType={comp.type} pageComponentSchema={props} reset={{ label }} />
          </div>
        )
      }
    });

    const newColumns = [index, ...customData, operation];
    setColumns(newColumns);
  }, [colComponents[0], runtime]);

  // 如果列数变了，就重新初始化列
  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);

  // 复制
  const handleCopy = (record: any) => {
    Message.success('复制成功');
    setTableData(prev => [...prev, { ...record, key: nanoid() }]);
  };

  // 删除
  const handleDelete = (key: string) => {
    setTableData(prev => prev.filter(item => item.key !== key));
    Message.success('删除成功');
  };

  // 新增
  const handleAdd = () => {
    setTableData(prev => [...prev, { ...tableData[0], key: nanoid() }]);
  };

  return (
    <Layout className="XChildrenTable">
      {colComponents.map((_colComponents, index) => (
        <Grid.Row key={index} className="item">
          <ReactSortable
            id={`workspace-content-${id}-${index}`}
            list={colComponents[index]}
            setList={(newList) => {
              // 使用函数式更新确保状态更新的原子性
              //   setColComponentsMap(id, (prevColumns: any[][]) => {
              //     const updatedColumns = [...(prevColumns || [])];
              //     updatedColumns[index] = newList;
              //     return updatedColumns;
              //   });

              //   const updatecolComponents = colComponents;
              //   updatecolComponents[index] = newList;
              //   setLayoutSubComponents(id, updatecolComponents);
              colComponents[index] = newList;
            }}
            onAdd={(e) => {
              // 允许拖入的组件
              const validata = ['XInputText', 'XInputTextArea', 'XInputNumber', 'XDatePicker', 'XRadio', 'XCheckbox', 'XSelectOne', 'XSelectMutiple', 'XImgUpload', 'XFileUpload', 'XUserSelect', 'XDeptSelect'];
              console.debug("onAdd", e.item.getAttribute('data-cp-type'));

              const cpID = e.item.id || e.item.getAttribute('data-cp-id');
              console.log(`拖入组件${id}内， 索引为${index}， 拖入组件为 ${cpID}`);
              const itemType = e.item.getAttribute('data-cp-type') || '';
              if (!validata.includes(itemType)) {
                return Message.warning('不支持的组件类型');
              }

              const itemDisplayName = e.item.getAttribute('data-cp-displayname');
              const schemaConfig = getComponentConfig(pageComponentSchemas[cpID!], itemType!);
              const schema = getComponentSchema(itemType as any);

              schema.config = schemaConfig;
              schema.config.cpName = itemDisplayName;
              schema.config.id = cpID;

              const props = {
                id: cpID,
                type: itemType,
                ...schema
              };

              setPageComponentSchemas(cpID!, props);

              const containerType = FORM_COMPONENT_TYPES.CHILDREN_TABLE;

              const containerSchemaConfig = getComponentConfig(pageComponentSchemas[id], containerType);
              const containerSchema = getComponentSchema(containerType);

              containerSchema.config = containerSchemaConfig;
              containerSchema.config.cpName = '子表单';
              containerSchema.config.id = id;

              const containerProps = {
                id,
                type: containerType,
                ...containerSchema
              };

              setCurComponentID(id);
              setCurComponentSchema(containerProps);

              setShowDeleteButton(false);
            }}
            onRemove={(e) => {
              const cpID = e.item.getAttribute('data-cp-id');
              console.log(`删除组件${id}内， 索引为${index}， 删除组件为 ${cpID}`);
            }}
            group={{
              name: COMPONENT_GROUP_NAME
            }}
            sort={true}
            forceFallback={true}
            animation={150}
            fallbackOnBody={true}
            swapThreshold={0.65}
            className="content"
          // onStart={(e) => {
          //   console.log('onStart', e);
          //   const cpID = e.item.getAttribute('data-id') || '';
          //   setCurComponentID(cpID);
          //   const curComponentSchema = pageComponentSchemas[cpID] || {};
          //   setCurComponentSchema(curComponentSchema);
          //   setShowDeleteButton(true);
          // }}
          >
            <Form.Item
              label={label.display && label.text}
              // field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHILDREN_TABLE}_${nanoid()}`}
              layout={layout}
              tooltip={tooltip}
              labelCol={{
                style: { width: labelColSpan, flex: 'unset' }
              }}
              wrapperCol={{ style: { flex: 1 } }}
              rules={[{ required: verify?.required }]}
              hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
              style={{
                margin: 0,
                width: '100%',
                display: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL] === layout ? 'flex' : 'unset',
                opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
              }}
            >
              <div style={{
                width: '100%',
                minHeight: 130,
                display: 'flex',
                alignItems: 'center',
                maxWidth: runtime ? '100%' : `calc(100vw - ${componentMaxWidth + labelColSpan + 2}px)`,
              }}>
                {
                  runtime ? <Table columns={columns} data={tableData} scroll={{ x: 'max-content' }} /> :
                    <DragableTable id={id} columns={columns} data={tableData} runtime={runtime} setColumns={setColumns} />
                }
              </div>
              <Button type='outline' icon={<IconPlus />} style={{ pointerEvents: runtime ? 'unset' : 'none', marginTop: 10 }} onClick={handleAdd}>新增一项</Button>
            </Form.Item>

          </ReactSortable>
        </Grid.Row>
      ))}
    </Layout>
  );
};

export default XChildrenTable;