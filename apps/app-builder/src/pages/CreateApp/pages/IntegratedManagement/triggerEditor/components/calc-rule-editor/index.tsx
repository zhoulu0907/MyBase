import { FormulaEditor } from '@/components/FormulaEditor';
import { Button, Form, Grid, Input, Select, type FormInstance } from '@arco-design/web-react';
import { IconDelete, IconLaunch, IconPlus } from '@arco-design/web-react/icon';
import { FieldType } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

export interface CalcRuleEditorProps {
  nodeId: string;
  form: FormInstance;
}

const valueTypeOptions = [
  { label: '值', value: FieldType.VALUE },
  { label: '公式', value: FieldType.FORMULA }
];

const CaclRuleEditor: React.FC<CalcRuleEditorProps> = ({ form, nodeId }) => {
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  const [formulaFieldKey, setFormulaFieldKey] = useState<string>('');
  const [formulaData, setFormulaData] = useState<string>('');
  const [currentFieldName, setCurrentFieldName] = useState<string>('');

  const fields = Form.useWatch('calRules', form);

  useEffect(() => {}, [form]);

  useEffect(() => {}, [fields]);

  const StaticValueComponent = (fieldName: string) => {
    return (
      <Form.Item field={fieldName}>
        <Input placeholder="请输入静态值" />
      </Form.Item>
    );
  };

  const handleFormulaConfirm = (formulaData: any, formattedFormula: string, params: any) => {
    setFormulaVisible(false);
    form.setFieldValue(formulaFieldKey, { formulaData: formulaData, formula: formattedFormula, parameters: params });
    setFormulaData('');
    setFormulaFieldKey('');
  };

  const openFormulaEditor = (fieldKey: string) => {
    setCurrentFieldName(form.getFieldValue(fieldKey)?.field);
    setFormulaVisible(true);
    setFormulaData(form.getFieldValue(fieldKey)?.formulaData);
    setFormulaFieldKey(`${fieldKey}.value`);
  };

  return (
    <div className={styles.conditionWrapper}>
      <Form.Item validateTrigger={['onChange']}>
        <Form.List field="calRules">
          {(fields, { add, remove }) => {
            return (
              <>
                {fields.map((item: any, index: number) => {
                  return (
                    <Grid.Row gutter={8} key={item.key} align="center">
                      <Grid.Col span={7}>
                        <Form.Item
                          field={item.field + '.field'}
                          rules={[
                            { required: true, message: '请输入字段' },
                            { match: /^[_a-zA-Z0-9\u4E00-\u9FA5]*$/, message: '字段不符合填写要求' },
                            {
                              validator: (value, cb) => {
                                const fields = form.getFieldValue('calRules');
                                const repeatFields = fields.filter((ele: any) => ele.field === value);
                                if (repeatFields.length > 1) {
                                  return cb('字段名称不能重复');
                                }
                                return cb();
                              }
                            }
                          ]}
                        >
                          <Input placeholder="请输入字段名称" onChange={(_value) => {}} />
                        </Form.Item>
                      </Grid.Col>

                      <Grid.Col span={2}>
                        <div style={{ marginBottom: '15px' }}>的值设为</div>
                      </Grid.Col>

                      <Grid.Col span={5}>
                        <Form.Item field={item.field + '.operatorType'}>
                          <Select
                            disabled={form.getFieldValue(item.field + '.field') == undefined}
                            options={valueTypeOptions}
                            onChange={() => {
                              form.setFieldValue(item.field + '.value', undefined);
                            }}
                          />
                        </Form.Item>
                      </Grid.Col>

                      <Grid.Col span={8}>
                        {form.getFieldValue(item.field + '.operatorType') == undefined && (
                          <Form.Item field={item.field + '.value'}>
                            <Input placeholder="请输入" disabled />
                          </Form.Item>
                        )}

                        {form.getFieldValue(item.field + '.operatorType') == FieldType.VALUE &&
                          StaticValueComponent(item.field + '.value')}

                        {form.getFieldValue(item.field + '.operatorType') == FieldType.FORMULA && (
                          <Form.Item field={item.field + '.value'}>
                            <Button onClick={() => openFormulaEditor(item.field)} long>
                              {form.getFieldValue(item.field + '.value') ? '已设置公式' : 'ƒx 编辑公式'}
                              {form.getFieldValue(item.field + '.value') ? <IconLaunch /> : ''}
                            </Button>
                          </Form.Item>
                        )}
                      </Grid.Col>

                      <Grid.Col span={2}>
                        <IconDelete
                          style={{ fontSize: '15px', color: '#4E5969', marginBottom: '15px' }}
                          onClick={() => {
                            remove(index);
                          }}
                        />
                      </Grid.Col>
                    </Grid.Row>
                  );
                })}

                <Grid.Row>
                  <Button
                    type="dashed"
                    icon={<IconPlus />}
                    onClick={() => {
                      add();
                    }}
                  >
                    添加
                  </Button>
                </Grid.Row>
              </>
            );
          }}
        </Form.List>
      </Form.Item>

      <FormulaEditor
        fieldName={currentFieldName}
        initialFormula={formulaData}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />
    </div>
  );
};

export default CaclRuleEditor;
