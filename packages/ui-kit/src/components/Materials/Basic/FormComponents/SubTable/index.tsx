import { ENTITY_FIELD_TYPE } from '@/components/DataFactory';
import { FormComp, FormSchema } from '@/components/Materials/Basic/FormComponents';
import { Button, Form, Layout, Table } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import { type XSubTableConfig } from './schema';
// import DragableTable from './dragableTable';
import { LAYOUT_OPTIONS, LAYOUT_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { getComponentConfig, getComponentSchema } from '@/components/Materials/schema';
import { usePageEditorSignal } from '@/hooks';
import { pagesRuntimeSignal } from '@onebase/common';
import { v4 as uuidv4 } from 'uuid';
import { COMPONENT_MAP } from '../../../componentsMap';
import './index.css';

const leftPanelWidth = 318;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40 + 32 + 10;
const canvasMarginWidth = 10;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth + canvasMarginWidth;

const XSubTable = (props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    cpName,
    columns = [],
    id,
    runtime = true,
    label,
    layout,
    tooltip,
    labelColSpan = 100,
    status,
    verify,
    dataField
  } = props;

  useSignals();

  const { subTableDataLength } = pagesRuntimeSignal;

  const { form } = Form.useFormContext();

  const {
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    setShowDeleteButton
  } = usePageEditorSignal();

  useEffect(() => {
    console.log(form.getFieldValue(`${id}`));
  }, [form]);

  useEffect(() => {
    console.log('subTableDataLength: ', subTableDataLength.value);
    // if (subTableDataLength.value[id]) {
    //   setSubTableData((prevData) => {
    //     return [];
    //   });
    //   for (let i = 0; i < subTableDataLength.value[id]; i++) {
    //     handleAdd();
    //   }
    // }

    let newSubTableData: any[] = [];
    const newData = columns.reduce((acc, column) => {
      acc[column.dataIndex] = '';
      return acc;
    }, {});
    for (let i = 0; i < subTableDataLength.value[id]; i++) {
      newSubTableData.push({ ...newData, key: `${i}` });
    }
    setSubTableData(newSubTableData);
  }, [subTableDataLength.value]);

  const [subTableData, setSubTableData] = useState<any[]>([]);
  const [subTableColumns, setSubTableColumns] = useState<any[]>([]);
  const [components, setComponents] = useState<any[]>([]);

  // console.log('columnscolumns', columns, id);

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
      const inputType =
        column.dataType === ENTITY_FIELD_TYPE.TEXT.VALUE ||
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
              <div>
                <FormComp.XInputText
                  {...FormSchema.XInputTextSchema.config}
                  label={{ text: col.title, display: false }}
                  runtime={runtime}
                  dataField={[`${id}.${index}.${column.dataIndex}`]}
                />
              </div>
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
              <FormComp.XInputTextArea
                {...FormSchema.XInputTextAreaSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
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
              <FormComp.XInputEmail
                {...FormSchema.XInputEmailSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
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
              <FormComp.XInputPhone
                {...FormSchema.XInputPhoneSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
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
              <FormComp.XInputNumber
                {...FormSchema.XInputNumberSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.DATE.VALUE) {
        // DATE_PICKER
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XDatePicker
                {...FormSchema.XDatePickerSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.DATETIME.VALUE) {
        // DATE_TIME_PICKER
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XDateTimePicker
                {...FormSchema.XDateTimePickerSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.TIME.VALUE) {
        // TIME_PICKER
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <Form.Item initialValue={col} field={`${id}.${index}.${column.dataIndex}`}>
                <FormComp.XTimePicker
                  {...FormSchema.XTimePickerSchema.config}
                  label={{ text: col.title, display: false }}
                  runtime={runtime}
                />
              </Form.Item>
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.BOOLEAN.VALUE) {
        // SWITCH
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XSwitch
                {...FormSchema.XSwitchSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.SELECT.VALUE) {
        // SELECT_ONE
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XSelectOne
                {...FormSchema.XSelectOneSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE) {
        // SELECT_MUTIPLE
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XSelectMutiple
                {...FormSchema.XSelectMutipleSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.AUTO_CODE.VALUE) {
        // AUTO_CODE
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <Form.Item initialValue={col} field={`${id}.${index}.${column.dataIndex}`}>
                <FormComp.XAutoCode
                  {...FormSchema.XAutoCodeSchema.config}
                  label={{ text: col.title, display: false }}
                  runtime={runtime}
                />
              </Form.Item>
            );
          }
        });
      } else if (
        column.dataType === ENTITY_FIELD_TYPE.USER.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.MULTI_USER.VALUE
      ) {
        // USER_SELECT
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XUserSelect
                {...FormSchema.XUserSelectSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (
        column.dataType === ENTITY_FIELD_TYPE.DEPARTMENT.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.MULTI_DEPARTMENT.VALUE
      ) {
        // DEPT_SELECT
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <Form.Item initialValue={col} field={`${id}.${index}.${column.dataIndex}`}>
                <FormComp.XDeptSelect
                  {...FormSchema.XDeptSelectSchema.config}
                  label={{ text: col.title, display: false }}
                  runtime={runtime}
                />
              </Form.Item>
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.RELATION.VALUE) {
        // RELATED_FORM
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XRelatedForm
                {...FormSchema.XRelatedFormSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.FILE.VALUE) {
        // FILE_UPLOAD
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XFileUpload
                {...FormSchema.XFileUploadSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (column.dataType === ENTITY_FIELD_TYPE.IMAGE.VALUE) {
        // IMG_UPLOAD
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XImgUpload
                {...FormSchema.XImgUploadSchema.config}
                label={{ text: col.title, display: false }}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
            );
          }
        });
      } else if (
        column.dataType === ENTITY_FIELD_TYPE.DATA_SELECTION.VALUE ||
        column.dataType === ENTITY_FIELD_TYPE.MULTI_DATA_SELECTION.VALUE
      ) {
        // DATA_SELECT
        tableColumns.push({
          ...column,
          width: 200,
          id: cpID,
          type: itemType,
          displayName,
          render: (col: any, record: any, index: number) => {
            return (
              <FormComp.XUserSelect
                {...FormSchema.XUserSelectSchema.config}
                runtime={runtime}
                dataField={[`${id}.${index}.${column.dataIndex}`]}
              />
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
    const newComp = subTableColumns
      .filter((v) => v.dataIndex !== 'action')
      .map((v) => {
        return {
          id: v?.id,
          type: v?.type,
          displayName: v?.displayName,
          selected: false,
          chosen: false
        };
      });
    setComponents(newComp);
  }, [subTableColumns]);

  return (
    <Layout className="XSubTable">
      <Form.Item
        label={label.display && label.text}
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
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <div
          className="subTableContent"
          style={{
            maxWidth: runtime
              ? '100%'
              : `calc(100vw - ${componentMaxWidth + ((LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL] === layout && label.display) ? labelColSpan : 0) + 2}px)`
          }}
        >
          {
            <Table
              columns={subTableColumns}
              data={subTableData}
              size="small"
              scroll={{ x: 'max-content' }}
              style={{
                width: '100%'
              }}
            />
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
      </Form.Item>
    </Layout>
  );
};

export default XSubTable;
