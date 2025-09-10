import { Layout, Message, Table, Button, Popconfirm, Form, Grid, type TableColumnProps } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { getComponentConfig, getComponentWidth } from 'src/components/Materials/schema';
import EditRender from 'src/components/render/EditRender';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { COMPONENT_GROUP_NAME, type GridItem } from 'src/utils/const';
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
  const { colCount, id, runtime = true, label, layout, tooltip, labelColSpan, status, verify } = props;

  useSignals();

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
    setLayoutSubComponents
  } = usePageEditorSignal();

  const [columns, setColumns] = useState<any[]>([]);
  const [tableData, setTableData] = useState<any[]>([]);

  // 从 store 中获取当前组件的列数据，如果不存在则初始化为空数组
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);
  // console.error('colComponents', colComponents[0], columns, tableData);

  const handleEdit = (record: any) => {
    Message.info(`编辑: ${record.name}`);
    // 在这里启动编辑逻辑
  };

  const handleDelete = (key: string) => {
    // setData(prev => prev.filter(item => item.key !== key));
    Message.success('删除成功');
  };

  useEffect(() => {
    if (colComponents[0].length > 0) {
      const index = {
        title: '序号',
        dataIndex: 'index',
        align: 'center',
        width: 70,
        render: (_: any, __: any, rowIndex: number) => rowIndex + 1,
      };
      const operation = {
        title: '操作',
        dataIndex: 'operations',
        align: 'center',
        fixed: 'right',
        width: 150,
        render: (_: any, record: any) => (
          <div style={{ display: 'flex', gap: '8px' }}>
            <Button size="small" type="text" onClick={() => handleEdit(record)}>
              复制
            </Button>
            <Popconfirm
              title="确认删除吗?"
              onOk={() => handleDelete(record.key)}
            >
              <Button size="small" type="text" status="danger">
                删除
              </Button>
            </Popconfirm>
          </div>
        ),
      }
      const label = {
        display: false,
        text: '',
      }

      const customData = colComponents[0].reverse().map(comp => ({
        title: comp.displayName,
        dataIndex: comp.id,
        align: 'center',
        width: 180,
        render: (_, row: any) => (
          <EditRender runtime={runtime} cpId={comp.id} cpType={comp.type} pageComponentSchema={pageComponentSchemas[comp.id]} reset={{ label }} />
        )
      }));

      const newData = [index, ...customData, operation];
      const data = customData.map((comp, index) => ({
        key: index,
        [comp.dataIndex]: index,
      }));

      setColumns(newData);
      colComponents[0].length === 1 && setTableData(data);
    }
  }, [colComponents[0]]);

  // 如果列数变了，就重新初始化列
  useEffect(() => {
    // console.log('layoutSubComponents:  ', colComponents);

    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      // console.log('id', id, 'colCount', colCount);
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);

  const handleDeleteComponent = (componentId: string) => {
    // 从组件列表中移除
    // 遍历二维数组的每一列，过滤掉 id 匹配的组件
    const updatedColumns = colComponents.map((col) => col.filter((cp) => cp.id !== componentId));
    setLayoutSubComponents(id, updatedColumns);

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  return (
    <Layout className="XChildrenTable">
      {colComponents.map((_colComponents, index) => (
        <Grid.Col key={index} className="item">
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
              console.log("onAdd", e.item.getAttribute('data-cp-type'));

              // console.log("e.item.id", e.item.getAttribute('data-cp-id'))
              // console.log("e.item.getAttribute('data-cp-type')", e.item.getAttribute('data-cp-type'))
              // console.log("e.item.getAttribute('data-cp-displayname')", e.item.getAttribute('data-cp-displayname'))

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

              if (itemType === ALL_COMPONENT_TYPES.COLUMN_LAYOUT) {
                console.log('创建布局组件: ', cpID);
              }

              setPageComponentSchemas(cpID!, props);

              setCurComponentID(cpID!);
              setCurComponentSchema(props);

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
            onStart={(e) => {
              console.log('onStart', e);
              const cpID = e.item.getAttribute('data-id') || '';
              setCurComponentID(cpID);
              const curComponentSchema = pageComponentSchemas[cpID] || {};
              setCurComponentSchema(curComponentSchema);
              setShowDeleteButton(true);
            }}
          >
            <Form.Item
              label={label.display && label.text}
              // field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RADIO}_${nanoid()}`}
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
                display: 'flex',
                maxWidth: runtime ? '100%' : `calc(100vw - ${componentMaxWidth}px)`,
                opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
              }}
            >
              <div style={{
                width: '100%',
                // pointerEvents: runtime ? 'unset' : 'none'
              }}>
                <Table columns={columns} data={tableData} scroll={{ x: 'max-content' }} />
              </div>
            </Form.Item>
            {/* {colComponents[index] &&
              colComponents[index].map((cp: GridItem) => (
                <div
                  key={cp.id}
                  data-cp-type={cp.type}
                  data-cp-displayname={cp.displayName}
                  data-cp-id={cp.id}
                  className="componentItem"
                  style={{
                    width: getComponentWidth(pageComponentSchemas[cp.id], cp.type),
                    borderColor: curComponentID === cp.id ? '#4FAE7B' : 'transparent'
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
                  <EditRender cpId={cp.id} cpType={cp.type} pageComponentSchema={pageComponentSchemas[cp.id]} />

                  {curComponentID === cp.id && showDeleteButton && (
                    <div
                      className="deleteButton"
                      onClick={(e) => {
                        e.stopPropagation();
                        console.log('删除组件: ', cp.id);
                        handleDeleteComponent(cp.id);
                      }}
                    >
                      <IconDelete />
                    </div>
                  )}
                </div>
              ))} */}
          </ReactSortable>
        </Grid.Col>
      ))}
    </Layout>
  );
};

export default XChildrenTable;
