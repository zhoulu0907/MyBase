import { useSignals } from '@preact/signals-react/runtime';
import { ENTITY_FIELD_TYPE } from '@/components/DataFactory';
import { Button, Form, Layout, Table, Message } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import { type XSubTableConfig } from './schema';
import { FormComp, FormSchema } from '@/components/Materials/Basic/FormComponents';
import DragableTable from './dragableTable';
import { ReactSortable } from 'react-sortablejs';
import './index.css';
import { usePageEditorSignal } from '@/hooks';
import { getComponentConfig, getComponentSchema } from '@/components/Materials/schema';
import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { COMPONENT_GROUP_NAME } from '@/utils';
import { v4 as uuidv4 } from 'uuid';
import { LAYOUT_OPTIONS, LAYOUT_VALUES } from '@/components/Materials/constants';
import { COMPONENT_MAP } from './components_map';

const leftPanelWidth = 318;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40 + 32 + 10;
const canvasMarginWidth = 10;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth + canvasMarginWidth;

const XSubTable = (props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { cpName, columns = [], id, runtime = true, label, layout, tooltip, labelColSpan = 100, status, verify } = props;

  useSignals();

  const {
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    setShowDeleteButton,
  } = usePageEditorSignal();

  // window['pageComponentSchemas'] = pageComponentSchemas;

  const [subTableData, setSubTableData] = useState<any[]>([]);
  const [subTableColumns, setSubTableColumns] = useState<any[]>([]);
  const [components, setComponents] = useState<any[]>([]);

  useEffect(() => {
    console.log('subTableData: ', subTableData);
  }, [subTableData]);

  const handleAdd = () => {
    console.log('add');
    const newData = columns.reduce((acc, column) => {
      acc[column.dataIndex] = '';
      return acc;
    }, {});
    newData.key = `${subTableData.length + 1}`;

    setSubTableData((prevData) => {
      const newDataArray = [...prevData, newData];
      return newDataArray;
    });
  };

  const handleDelete = (key: string) => {
    setSubTableData((prevData) => {
      const filteredData = prevData.filter((item) => item.key !== key);
      return filteredData;
    });
  };

  useEffect(() => {
    // console.log('columns', columns);
    const tableColumns = [];

    for (const column of columns) {
      // 组件类型与 apps/app-builder/src/pages/Editor/components/panel/components/metadata/component_map.ts 文件下一致
      const inputType = column.dataType === ENTITY_FIELD_TYPE.TEXT.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.ID.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.URL.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.ADDRESS.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.STRUCTURE.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.ARRAY.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.GEOGRAPHY.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.PASSWORD.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.ENCRYPTED.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.AGGREGATE.VALUE;


      const displayName = column.title;
      const itemType = COMPONENT_MAP[column.dataType];
      const cpID = `${itemType}-${uuidv4()}`;

      if (column.dataIndex !== 'index' && column.dataIndex !== 'operation') {
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
            },
            status
          }
        };
        setPageComponentSchemas(cpID, props);
      }

      if (inputType) {
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XInputText {...FormSchema.XInputTextSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.LONG_TEXT.VALUE) {
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XInputTextArea {...FormSchema.XInputTextAreaSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.EMAIL.VALUE) {
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XInputEmail {...FormSchema.XInputEmailSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.PHONE.VALUE) {
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XInputPhone {...FormSchema.XInputPhoneSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.NUMBER.VALUE) {
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XInputNumber {...FormSchema.XInputNumberSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.DATE.VALUE) { // DATE_PICKER
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XDatePicker {...FormSchema.XDatePickerSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.DATETIME.VALUE) { // DATE_TIME_PICKER
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XDateTimePicker {...FormSchema.XDateTimePickerSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.TIME.VALUE) { // TIME_PICKER
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <Form.Item initialValue={col} field={`${id}.${index}.${column.dataIndex}`}>
                <FormComp.XTimePicker {...FormSchema.XTimePickerSchema.config} label={{ text: col.title, display: false }} runtime={runtime} />
              </Form.Item>
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.BOOLEAN.VALUE) { // SWITCH
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XSwitch {...FormSchema.XSwitchSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.SELECT.VALUE) { // SELECT_ONE
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XSelectOne {...FormSchema.XSelectOneSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE) { // SELECT_MUTIPLE
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XSelectMutiple {...FormSchema.XSelectMutipleSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.AUTO_CODE.VALUE) { // AUTO_CODE
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <Form.Item initialValue={col} field={`${id}.${index}.${column.dataIndex}`}>
                <FormComp.XAutoCode {...FormSchema.XAutoCodeSchema.config} label={{ text: col.title, display: false }} runtime={runtime} />
              </Form.Item>
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.USER.VALUE || column.dataType === ENTITY_FIELD_TYPE.MULTI_USER.VALUE) { // USER_SELECT
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XUserSelect {...FormSchema.XUserSelectSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.DEPARTMENT.VALUE || column.dataType === ENTITY_FIELD_TYPE.MULTI_DEPARTMENT.VALUE) { // DEPT_SELECT
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <Form.Item initialValue={col} field={`${id}.${index}.${column.dataIndex}`}>
                <FormComp.XDeptSelect {...FormSchema.XDeptSelectSchema.config} label={{ text: col.title, display: false }} runtime={runtime} />
              </Form.Item>
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.RELATION.VALUE) { // RELATED_FORM
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XRelatedForm {...FormSchema.XRelatedFormSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.FILE.VALUE) { // FILE_UPLOAD
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XFileUpload {...FormSchema.XFileUploadSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.IMAGE.VALUE) { // IMG_UPLOAD
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XImgUpload {...FormSchema.XImgUploadSchema.config} label={{ text: col.title, display: false }} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.DATA_SELECTION.VALUE || column.dataType === ENTITY_FIELD_TYPE.MULTI_DATA_SELECTION.VALUE) { // DATA_SELECT
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XUserSelect {...FormSchema.XUserSelectSchema.config} runtime={runtime} dataField={[`${id}.${index}.${column.dataIndex}`]} />
            );
          }
        });
      }
    }
    tableColumns.push({
      title: '操作',
      dataIndex: 'action',
      width: 100,
      fixed: 'right',
      render: (_col: any, record: any, index: number) => {
        return (
          <Button type="text" onClick={() => handleDelete(record.key)}>
            删除
          </Button>
        );
      }
    });
    setSubTableColumns(tableColumns);
  }, [columns]);

  useEffect(() => {
    const newComp = subTableColumns.filter(v => v.dataIndex !== 'action').map(v => {
      return {
        id: v?.id,
        type: v?.type,
        displayName: v?.displayName,
        selected: false,
        chosen: false
      }
    });
    setComponents(newComp);
  }, [subTableColumns]);

  return (
    <Layout className="XSubTable">
      <div className="item">
        <ReactSortable
          id={`workspace-content-XSubTable`}
          list={components}
          setList={(newList) => {
            setComponents(newList);
          }}
          onAdd={(e) => {
            // 允许拖入的组件
            const validata = [
              'XInputText',
              'XInputTextArea',
              'XInputNumber',
              'XDatePicker',
              'XRadio',
              'XCheckbox',
              'XSelectOne',
              'XSelectMutiple',
              'XImgUpload',
              'XFileUpload',
              'XUserSelect',
              'XDeptSelect'
            ];
            console.debug('onAdd', e.item.getAttribute('data-cp-type'));

            const cpID = e.item.id || e.item.getAttribute('data-cp-id');
            console.log(`拖入组件${id}内， 拖入组件为 ${cpID}`);
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

            const containerType = FORM_COMPONENT_TYPES.SUB_TABLE;

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
            console.log(`删除组件${id}内， 删除组件为 ${cpID}`);
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
        >
          <div className="subTableHeader">{cpName}</div>
          <div className="subTableContent" style={{
            maxWidth: runtime
              ? '100%'
              : `calc(100vw - ${componentMaxWidth + (LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL] === layout ? labelColSpan : 0) + 2}px)`
          }}>
            {
              runtime ? (
                <Table columns={subTableColumns} data={subTableData} size="small" scroll={{ x: 'max-content' }} style={{
                  width: '100%'
                }} />
              ) : (
                <DragableTable
                  id={id}
                  status={status}
                  columns={subTableColumns}
                  data={subTableData}
                  runtime={runtime}
                  setColumns={setSubTableColumns}
                />
              )
            }

          </div>
          <div className="subTableFooter">
            <Button
              className="addButton"
              type="outline"
              icon={<IconPlus />}
              style={{ pointerEvents: runtime ? 'unset' : 'none', marginTop: 10 }}
              onClick={handleAdd}
            >
              新增一项
            </Button>
          </div>
        </ReactSortable>
      </div>
    </Layout>
  );
};

export default XSubTable;
