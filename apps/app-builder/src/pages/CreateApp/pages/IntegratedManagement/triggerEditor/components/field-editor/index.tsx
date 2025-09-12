import { Button, Form, Grid, Input, Select } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './index.module.less';

export interface FieldEditorProps {
  fields: { label: string; value: string }[];
  dataList: { fieldId: string; fieldType: string; fieldValue: string }[];
  onChange: (dataList: { fieldId: string; fieldType: string; fieldValue: string }[]) => void;
}

const valueTypeOptions = [{ label: '值', value: 'value' }];

const FieldEditor: React.FC<FieldEditorProps> = ({ fields, dataList, onChange }) => {
  const [form] = Form.useForm();

  const addFieldItem = () => {
    console.log(dataList);

    onChange([...dataList, { fieldId: '', fieldType: '', fieldValue: '' }]);
  };

  const deleteFieldItem = (index: number) => {
    const newDataList = [...dataList];
    newDataList.splice(index, 1);
    onChange(newDataList);
  };

  return (
    <div className={styles.conditionWrapper}>
      <Form form={form} layout="vertical">
        {dataList.map((item, index) => {
          return (
            <Grid.Row gutter={8}>
              <Grid.Col span={6}>
                <Form.Item field="fieldId" initialValue={item.fieldId}>
                  <Select options={fields} />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={3}>
                <div style={{ lineHeight: '32px' }}>的值设为</div>
              </Grid.Col>
              <Grid.Col span={5}>
                <Form.Item field="fieldType" initialValue={item.fieldType}>
                  <Select options={valueTypeOptions} />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={8}>
                <Form.Item field="fieldValue" initialValue={item.fieldValue}>
                  <Input />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={2}>
                <Button type="text" icon={<IconDelete />} onClick={() => deleteFieldItem(index)} />
              </Grid.Col>
            </Grid.Row>
          );
        })}
        <Grid.Row>
          <Button type="dashed" icon={<IconPlus />} onClick={addFieldItem}>
            添加字段
          </Button>
        </Grid.Row>
      </Form>
    </div>
  );
};

export default FieldEditor;
