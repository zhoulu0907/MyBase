import { Button, Form, Grid, Select, type FormInstance } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import styles from '../../index.module.less';
import { useSignals } from '@preact/signals-react/runtime';
import { etlEditorSignal, ETLJoinType } from '@onebase/common';
import { useEffect, useState } from 'react';

const { Option } = Select;
const FormItem = Form.Item;

const NODEFIELDTYPE = {
  LEFT: 'leftFieldFqn',
  RIGHT: 'rightFieldFqn'
};

interface JoinRowFieldsProps {
  index: number;
  rowField: any;
  leftFieldList: any[];
  rightFieldList: any[];
  form: FormInstance;
  remove: (index: number) => void;
}

const JoinRowFields = (props: JoinRowFieldsProps) => {
  useSignals();
  const { curNode, nodeData, setNodeData } = etlEditorSignal;
  const { index, rowField, leftFieldList, rightFieldList, form, remove } = props;

  const [finialRightFieldList, setFinialRightFieldList] = useState<any[]>(rightFieldList);
  const [finialLeftFieldList, setFinialLeftFieldList] = useState<any[]>(leftFieldList);

  useEffect(() => {
    const leftFieldValue: string = form.getFieldValue(rowField.field + NODEFIELDTYPE.LEFT);
    if (leftFieldValue && leftFieldList.length > 0) {
      handleLeftFieldChange(leftFieldValue);
    } else {
      setFinialRightFieldList(rightFieldList);
    }
  }, [rightFieldList]);

  useEffect(() => {
    const rightFieldValue: string = form.getFieldValue(rowField.field + NODEFIELDTYPE.RIGHT);
    if (rightFieldValue && rightFieldList.length > 0) {
      handleRightFieldChange(rightFieldValue);
    } else {
      setFinialLeftFieldList(leftFieldList);
    }
    setFinialLeftFieldList(leftFieldList);
  }, [leftFieldList]);

  const handleLeftFieldChange = (value: string) => {
    if (value) {
      const leftField = leftFieldList.find((field) => field.fieldFqn === value);
      const finialRightFieldList = rightFieldList.filter((ritField) => ritField.fieldType === leftField.fieldType);
      setFinialRightFieldList(finialRightFieldList);
      const nodeFieldRightValue = form.getFieldValue(rowField.field + NODEFIELDTYPE.RIGHT);
      const isInclude = finialRightFieldList.find((field) => field.fieldFqn === nodeFieldRightValue);
      if (!isInclude) {
        form.setFieldValue(rowField.field + NODEFIELDTYPE.RIGHT, undefined);
      }
    } else {
      setFinialRightFieldList(rightFieldList);
    }
    setCurNodeData();
  };

  const handleRightFieldChange = (value: string) => {
    if (value) {
      const rightField = rightFieldList.find((field) => field.fieldFqn === value);
      const finialLeftFieldList = leftFieldList.filter((leftField) => leftField.fieldType === rightField.fieldType);
      setFinialLeftFieldList(finialLeftFieldList);
      const nodeFieldLeftValue = form.getFieldValue(rowField.field + NODEFIELDTYPE.LEFT);
      const isInclude = finialLeftFieldList.find((field) => field.fieldFqn === nodeFieldLeftValue);
      if (!isInclude) {
        form.setFieldValue(rowField.field + NODEFIELDTYPE.LEFT, undefined);
      }
    } else {
      setFinialLeftFieldList(leftFieldList);
    }
    setCurNodeData();
  };

  const setCurNodeData = () => {
    const formValue = form.getFieldsValue();
    const payload = nodeData.value[curNode.value.id];
    payload.config = {
      ...payload.config,
      ...formValue
    };

    setNodeData(curNode.value.id, payload);
  };

  return (
    <>
      <Grid.Row className={styles.fieldRow}>
        <Grid.Col span={8}>
          <FormItem noStyle field={rowField.field + NODEFIELDTYPE.LEFT}>
            <Select placeholder="请选择字段" allowClear onChange={(value) => handleLeftFieldChange(value)}>
              {finialLeftFieldList.map((field) => (
                <Option key={field.fieldFqn} value={field.fieldFqn}>
                  {field.fieldName}
                </Option>
              ))}
            </Select>
          </FormItem>
        </Grid.Col>
        <Grid.Col span={2} className={styles.center}>
          <div className={styles.equals}>=</div>
        </Grid.Col>
        <Grid.Col span={8}>
          <FormItem noStyle field={rowField.field + NODEFIELDTYPE.RIGHT}>
            <Select placeholder="请选择字段" allowClear onChange={(value) => handleRightFieldChange(value)}>
              {finialRightFieldList.map((field) => (
                <Option key={field.fieldFqn} value={field.fieldFqn}>
                  {field.fieldName}
                </Option>
              ))}
            </Select>
          </FormItem>
        </Grid.Col>
        <Grid.Col span={1}>
          <Button
            type="text"
            icon={<IconDelete />}
            onClick={() => {
              remove(index);
              setCurNodeData();
            }}
          ></Button>
        </Grid.Col>
      </Grid.Row>
    </>
  );
};

export default JoinRowFields;
