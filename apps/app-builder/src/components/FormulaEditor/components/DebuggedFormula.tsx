import { DatePicker, Form, Input, InputNumber, Switch, Table, TimePicker, Typography } from '@arco-design/web-react';
import { IconArrowRight, IconLoading, IconPlusCircle } from '@arco-design/web-react/icon';
import styles from './DebuggedFormula.module.less';
import { debugFormula, type fieldListWithNodeData } from '@onebase/app';
import { useRef, useState } from 'react';
import dayjs from 'dayjs';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';

interface VariableItem {
  fieldName: string;
  fieldId: string;
  fieldType: any;
}

interface DebuggedFormulaProps {
  entityFields: VariableItem[];
  formula: string;
  tableData: fieldListWithNodeData;
}

export function DebuggedFormula(props: DebuggedFormulaProps) {
  const { entityFields, formula, tableData = {} } = props;
  const [displayValue, setDisplayValue] = useState<any>();
  const [loading, setLoading] = useState<boolean>(false); //当调用接口的时候显示加载中
  const [form] = Form.useForm();
  const formRef = useRef<any>(null); // Form实例引用

  const handleTableItem = (
    fieldNames: string[],
    newTableKey: string,
    result: { [key: string]: string[] },
    rows: any
  ) => {
    fieldNames.forEach((fieldName) => {
      // 生成目标 key：表格标识 + 字段名（如 "tableRows$数据查询节点(多条)111.任务名称"）
      const targetKey = `${newTableKey}.${fieldName}`;
      result[targetKey] = rows?.map((row: any) => row[fieldName] || '');
    });
  };

  const transformTableData = (items: fieldListWithNodeData[]) => {
    const result: { [key: string]: string[] } = {};
    Object.entries(items).forEach(([tableKey, rows]) => {
      if (!rows.length) return;
      if (tableKey.includes('$')) {
        const newTableKey = tableKey.replace('tableRows', '');
        const fieldNames = Object.keys(rows[0]);
        // 遍历每个字段名，提取所有行的该字段值
        handleTableItem(fieldNames, newTableKey, result, rows);
      }
    });
    return result;
  };

  const withSingleEscapedChar = (char: string) => {
    const isSingleEscapedChar = /^[\x00-\x1F\\"]$/;
    if (isSingleEscapedChar.test(char)) {
      return JSON.stringify(char).slice(1, -1);
    }
    return char;
  };

  const handleFormula = async () => {
    setLoading(true);
    try {
      const values = await form.validate();
      const allData = formRef.current?.getFieldsValue() || [];
      const formattedTableData = transformTableData(allData);
      let newValidFieldResult: { [key: string]: any } = {};
      Object.keys(values)?.map((key) => {
        const fieldObj = values[key];
        if (!key.includes('$')) {
          newValidFieldResult = {
            ...newValidFieldResult,
            [key]: fieldObj
          };
        }
      });
      const data = await debugFormula({
        formula: formula,
        parameters: { ...(newValidFieldResult || {}), ...(formattedTableData || {}) }
      });
      if (data.resultType) {
        // 'String', 'Double', 'ArrayList', 'Boolean', 'LocalDateTime', 'Long', 'HashMap'
        switch (data.resultType) {
          case 'ArrayList':
            const listResult = JSON.stringify(data.result || []);
            setDisplayValue(listResult);
            break;
          case 'Boolean':
            setDisplayValue(`${data.result}`);
            break;
          case 'LocalDateTime':
            const dateTimeResult = dayjs(data.result).format('YYYY-MM-DD');
            setDisplayValue(dateTimeResult);
            break;
          case 'HashMap':
            const jsonResult = JSON.stringify(data.result || {});
            setDisplayValue(jsonResult);
            break;
          case 'Double':
          case 'Long':
          case 'String':
          default:
            setDisplayValue(withSingleEscapedChar(data.result));
        }
      }
      console.log(data, 'data');
    } catch (error) {
      console.log('提交数据失败 error:', error);
    } finally {
      setLoading(false);
    }
  };

  const renderFormItem = (fieldype: any) => {
    switch (fieldype) {
      case ENTITY_FIELD_TYPE.NUMBER.VALUE:
        return <InputNumber placeholder="please enter" />;
      case ENTITY_FIELD_TYPE.DATE.VALUE:
        return <DatePicker showTime style={{ width: '100%' }} />;
      case ENTITY_FIELD_TYPE.DATETIME.VALUE:
        return <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" style={{ width: '100%' }} />;
      case ENTITY_FIELD_TYPE.TIME.VALUE:
        return <TimePicker format="HH:mm:ss" style={{ width: '100%' }} />;
      case ENTITY_FIELD_TYPE.BOOLEAN.VALUE:
        return <Switch />;
      default:
        return <Input />;
    }
  };

  //生成table的column数据
  const getColumns = (rootField: string, fields: VariableItem[]): any => {
    const columns = fields.map((data) => ({
      title: data.fieldName,
      dataIndex: data.fieldName,
      key: data.fieldName,
      width: 150,
      render: (_: any, record: any, index: number) => (
        <Form.Item
          className={styles.bodycellFormItem}
          field={`tableRows${rootField}[${index}].${data.fieldName}`}
          rules={[{ required: true, message: `请输入${data.fieldName}` }]}
        >
          <Input />
        </Form.Item>
      )
    }));
    const indexColumn = {
      title: '',
      fixed: 'left',
      width: 8,
      render: (_: any, record: any, index: number) => index + 1
    };
    return [indexColumn, ...columns];
  };

  //生成新增行的空数据结构
  const getNewRow = (fieldConfig: VariableItem[]) => {
    return fieldConfig.reduce((row: any, { fieldName }) => {
      row[fieldName] = '';
      return row;
    }, {});
  };

  return (
    <div className={styles.debugModeContainer}>
      <div className={styles.header}>
        <Typography.Title heading={6} className={styles.leftTitle}>
          字段赋值
        </Typography.Title>
        <Typography.Title heading={6} className={styles.rightTitle}>
          公式计算结果
        </Typography.Title>
      </div>
      <div className={styles.content}>
        <Form className={styles.variablesDisplay} form={form} ref={formRef}>
          {entityFields.map((item) => {
            return (
              <Form.Item label={item.fieldName} field={item.fieldName} rules={[{ required: true }]}>
                {renderFormItem(item.fieldType)}
              </Form.Item>
            );
          })}
          {/* 表格 */}
          {Object.keys(tableData)?.map((key) => {
            return (
              <>
                <Typography.Title heading={6} className={styles.title}>
                  {key}
                </Typography.Title>
                <Form.List
                  field="tableRows"
                  initialValue={tableData[key].fieldList.length > 0 ? [getNewRow(tableData[key].fieldList)] : []}
                >
                  {(fields, { add }) => {
                    return (
                      <>
                        <Table
                          className={styles.variableTable}
                          border
                          borderCell
                          columns={getColumns(key, tableData[key].fieldList)}
                          data={fields.map((item) => item.key)}
                          rowKey="id"
                          pagination={false}
                        />
                        <IconPlusCircle
                          fontSize={24}
                          className={styles.iconPlus}
                          onClick={() => {
                            add(getNewRow(tableData[key].fieldList));
                          }}
                        />
                      </>
                    );
                  }}
                </Form.List>
              </>
            );
          })}
        </Form>
        <div className={styles.calculateIcon} onClick={handleFormula}>
          <span>公式计算</span>
          <IconArrowRight style={{ fontSize: 50, color: '#4FAE7B', fontWeight: 'bold' }} />
        </div>
        <div className={styles.rightContent}>{loading ? <IconLoading fontSize={24} /> : displayValue}</div>
      </div>
    </div>
  );
}
