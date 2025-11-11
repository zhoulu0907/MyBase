import { Button, Divider, Form, Grid, Input, Modal, Select, Switch, Tag } from '@arco-design/web-react';
import { IconDelete, IconLaunch, IconPlus } from '@arco-design/web-react/icon';
import { FieldType, VALIDATION_TYPE } from '@onebase/app';
import { useFormEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import { getOperatorOptions } from './ruleMap';

const Row = Grid.Row;
const Col = Grid.Col;

const Option = Select.Option;

const opCodeOptions = [
  {
    label: '公式',
    value: FieldType.FORMULA
  },
  {
    label: '静态值',
    value: FieldType.VALUE
  }
];

interface InteractionRuleModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: () => void;
}

const InteractionRuleModal: React.FC<InteractionRuleModalProps> = ({ visible, onCancel, onOk }) => {
  useSignals();
  const { components, pageComponentSchemas } = useFormEditorSignal;

  const [cpOptions, setCpOptions] = useState<{ label: string; value: string }[]>([]);

  // 获取组件下拉列表
  const getComponentOptions = () => {
    const cpOptions = Object.values(pageComponentSchemas.value).map((item: any) => ({
      label: item.config?.label?.text || '',
      value: item.config?.id
    }));

    console.log('cpOptions: ', cpOptions);
    setCpOptions(cpOptions);
  };

  useEffect(() => {
    console.log('components: ', components.value);
    console.log('pageComponentSchemas: ', pageComponentSchemas.value);
    visible && getComponentOptions();
  }, [visible, pageComponentSchemas]);

  const [form] = Form.useForm();

  const interactionCondition = Form.useWatch('interactionCondition', form);

  const rules = [
    {
      id: '1',
      name: '规则1',
      enabled: 1
    },
    {
      id: '2',
      name: '规则2',
      enabled: 0
    },
    {
      id: '3',
      name: '规则3',
      enabled: 1
    },
    {
      id: '4',
      name: '规则4',
      enabled: 0
    }
  ];

  const handleOk = () => {
    onOk();
  };

  const handleCancel = () => {
    onCancel();
  };

  return (
    <Modal
      style={{ width: 1200, height: 600 }}
      title={<div style={{ textAlign: 'left' }}>交互规则管理</div>}
      visible={visible}
      onCancel={handleCancel}
      onOk={handleOk}
    >
      <div className={styles.interactionRuleModal}>
        <div className={styles.left}>
          <div className={styles.leftHeader}>
            <Input.Search placeholder="搜索" />
            <Button type="text" icon={<IconPlus />}>
              新建
            </Button>
          </div>
          <div className={styles.leftContent}>
            {rules.map((rule) => (
              <div key={rule.id} className={styles.ruleItem}>
                <div className={styles.ruleItemName}>{rule.name}</div>
                <div className={styles.ruleItemEnabled}>
                  {rule.enabled ? <Tag color="green">启用</Tag> : <Tag color="gray">禁用</Tag>}
                  <IconDelete
                    onClick={() => {
                      console.log('delete rule: ', rule.id);
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
        <div className={styles.right}>
          <Form layout="vertical" form={form}>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="规则名称" field="name">
                  <Input />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="启用状态" field="enabled" triggerPropName="checked">
                  <Switch />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col span={24}>
                <Form.Item label="规则描述" field="description">
                  <Input.TextArea />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <div className={styles.ruleCondition}>
                <div>当满足以下条件时</div>
                <div style={{ width: '100%' }}>
                  <Form.List field={'interactionCondition'}>
                    {(conditions, { add: addCondition, remove: removeCondition }) => {
                      return (
                        <div>
                          {conditions.map((item, index) => {
                            return (
                              <div key={item.key}>
                                <div className={styles.items}>
                                  <div className={styles.tag}>且</div>
                                  <Form.List field={item.field + '.conditions'}>
                                    {(condition, { add: childAdd, remove: childRemove }) => {
                                      return (
                                        <div style={{ width: '100%' }}>
                                          {condition.map((item: any, childIndex) => {
                                            return (
                                              // 字段id
                                              <Grid.Row key={item.key} gutter={8} align="center">
                                                <Grid.Col span={8}>
                                                  <Form.Item field={item.field + '.cpId'}>
                                                    <Select
                                                      className={styles.itemSelect}
                                                      options={cpOptions}
                                                      onChange={(_value) => {
                                                        form.setFieldValue(item.field + '.op', undefined);
                                                        form.setFieldValue(item.field + '.operatorType', undefined);
                                                        form.setFieldValue(item.field + '.value', undefined);
                                                      }}
                                                    />
                                                  </Form.Item>
                                                </Grid.Col>

                                                {/* 操作符 */}
                                                <Grid.Col span={4}>
                                                  <Form.Item field={item.field + '.op'}>
                                                    <Select
                                                      className={styles.itemSelect}
                                                      disabled={form.getFieldValue(item.field + '.cpId') == undefined}
                                                      onChange={(_value) => {
                                                        form.setFieldValue(item.field + '.operatorType', undefined);
                                                        form.setFieldValue(item.field + '.value', undefined);
                                                      }}
                                                      options={getOperatorOptions(
                                                        components.value.find(
                                                          (item: any) =>
                                                            item.config?.id === form.getFieldValue(item.field + '.cpId')
                                                        )?.config?.type
                                                      )}
                                                    >
                                                      {/* {form.getFieldValue(item.field)?.fieldId &&
                                              entityFieldValidationTypes &&
                                              entityFieldValidationTypes
                                                .find((cc) => cc.fieldId == form.getFieldValue(item.field).fieldId)
                                                ?.validationTypes.map((operator: ValidationTypeItem) => (
                                                  <Option key={operator.code} value={operator.code}>
                                                    {operator.name}
                                                  </Option>
                                                ))} */}
                                                    </Select>
                                                  </Form.Item>
                                                </Grid.Col>

                                                {/* 不为空和为空不需要选择操作类型 */}
                                                {form.getFieldValue(item.field + '.op') != VALIDATION_TYPE.IS_EMPTY &&
                                                  form.getFieldValue(item.field + '.op') !=
                                                    VALIDATION_TYPE.IS_NOT_EMPTY && (
                                                    <>
                                                      <Grid.Col span={3}>
                                                        <Form.Item field={item.field + '.operatorType'}>
                                                          <Select
                                                            className={styles.itemSelect}
                                                            disabled={
                                                              form.getFieldValue(item.field + '.op') == undefined
                                                            }
                                                            options={opCodeOptions}
                                                            onChange={() => {
                                                              form.setFieldValue(item.field + '.value', undefined);
                                                              // 如果是范围类型 需要用数组兜底
                                                              if (
                                                                form.getFieldValue(item.field + '.op') ==
                                                                VALIDATION_TYPE.RANGE
                                                              ) {
                                                                form.setFieldValue(item.field + '.value', [
                                                                  undefined,
                                                                  undefined
                                                                ]);
                                                              }
                                                            }}
                                                          ></Select>
                                                        </Form.Item>
                                                      </Grid.Col>

                                                      <Grid.Col span={8}>
                                                        {form.getFieldValue(item.field + '.operatorType') ==
                                                          undefined && (
                                                          <Form.Item field={item.field + '.value'}>
                                                            <Input placeholder="请输入" disabled />
                                                          </Form.Item>
                                                        )}
                                                        {form.getFieldValue(item.field + '.operatorType') ==
                                                          FieldType.VALUE && (
                                                          <Form.Item field={item.field + '.value'}>
                                                            <Input placeholder="请输入静态值" />
                                                          </Form.Item>
                                                        )}

                                                        {form.getFieldValue(item.field + '.operatorType') ==
                                                          FieldType.VARIABLES && (
                                                          <Form.Item field={item.field + '.value'}>
                                                            <Select />
                                                          </Form.Item>
                                                        )}

                                                        {form.getFieldValue(item.field + '.operatorType') ==
                                                          FieldType.FORMULA && (
                                                          <Form.Item field={item.field + '.value'}>
                                                            <Button
                                                              //   onClick={() => openFormulaEditor(item.field + '.value')}
                                                              long
                                                            >
                                                              {form.getFieldValue(item.field + '.value')
                                                                ? '已设置公式'
                                                                : 'ƒx 编辑公式'}
                                                              {form.getFieldValue(item.field + '.value') ? (
                                                                <IconLaunch />
                                                              ) : (
                                                                ''
                                                              )}
                                                            </Button>
                                                          </Form.Item>
                                                        )}
                                                      </Grid.Col>
                                                    </>
                                                  )}

                                                <Grid.Col span={1}>
                                                  <IconDelete
                                                    style={{ fontSize: '15px', color: '#4E5969', marginBottom: '15px' }}
                                                    onClick={() => {
                                                      childRemove(childIndex);
                                                      if (condition.length === 1) {
                                                        removeCondition(index);
                                                      }
                                                    }}
                                                  />
                                                </Grid.Col>
                                              </Grid.Row>
                                            );
                                          })}

                                          <Button type="text" size="small" onClick={() => childAdd()}>
                                            + 添加且条件
                                          </Button>
                                        </div>
                                      );
                                    }}
                                  </Form.List>
                                </div>

                                {index !== (conditions || [])?.length - 1 && (
                                  <Divider
                                    orientation="center"
                                    style={{
                                      marginTop: '10px',
                                      marginBottom: '10px',
                                      marginLeft: '10px',
                                      marginRight: '10px'
                                    }}
                                  >
                                    <div className={styles.dividerText}>或</div>
                                  </Divider>
                                )}
                              </div>
                            );
                          })}
                          <Button
                            type="text"
                            onClick={() => {
                              addCondition({
                                conditions: [undefined]
                              });
                            }}
                          >
                            + 添加或条件
                          </Button>
                        </div>
                      );
                    }}
                  </Form.List>
                </div>
              </div>
            </Row>
            <Row gutter={16}>
              <div className={styles.ruleAction}>
                <div>则执行动作</div>
                <div style={{ width: '100%' }}>
                  <Form.List field={'.actions'}>
                    {(actions, { add: addAction, remove: removeAction }) => {
                      return (
                        <div>
                          {actions.map((item, index) => {
                            return (
                              <div key={item.key}>
                                <div className={styles.items}>
                                  <Form.List field={item.field + '.actions'}>
                                    {(condition, { add: childAdd, remove: childRemove }) => {
                                      return (
                                        <div style={{ width: '100%' }}>
                                          {condition.map((item: any, childIndex) => {
                                            return (
                                              // 字段id
                                              <Grid.Row key={item.key} gutter={8} align="center">
                                                <Grid.Col span={8}>
                                                  <Form.Item field={item.field + '.fieldId'}>
                                                    <Select
                                                      className={styles.itemSelect}
                                                      options={[]}
                                                      onChange={(_value) => {
                                                        form.setFieldValue(item.field + '.op', undefined);
                                                        form.setFieldValue(item.field + '.operatorType', undefined);
                                                        form.setFieldValue(item.field + '.value', undefined);
                                                      }}
                                                    />
                                                  </Form.Item>
                                                </Grid.Col>

                                                {/* 操作符 */}
                                                <Grid.Col span={4}>
                                                  <Form.Item field={item.field + '.op'}>
                                                    <Select
                                                      className={styles.itemSelect}
                                                      disabled={
                                                        form.getFieldValue(item.field + '.fieldId') == undefined
                                                      }
                                                      onChange={(_value) => {
                                                        form.setFieldValue(item.field + '.operatorType', undefined);
                                                        form.setFieldValue(item.field + '.value', undefined);
                                                      }}
                                                    ></Select>
                                                  </Form.Item>
                                                </Grid.Col>

                                                {/* 不为空和为空不需要选择操作类型 */}
                                                {form.getFieldValue(item.field + '.op') != VALIDATION_TYPE.IS_EMPTY &&
                                                  form.getFieldValue(item.field + '.op') !=
                                                    VALIDATION_TYPE.IS_NOT_EMPTY && (
                                                    <>
                                                      <Grid.Col span={3}>
                                                        <Form.Item field={item.field + '.operatorType'}>
                                                          <Select
                                                            className={styles.itemSelect}
                                                            disabled={
                                                              form.getFieldValue(item.field + '.op') == undefined
                                                            }
                                                            options={opCodeOptions}
                                                            onChange={() => {
                                                              form.setFieldValue(item.field + '.value', undefined);
                                                              // 如果是范围类型 需要用数组兜底
                                                              if (
                                                                form.getFieldValue(item.field + '.op') ==
                                                                VALIDATION_TYPE.RANGE
                                                              ) {
                                                                form.setFieldValue(item.field + '.value', [
                                                                  undefined,
                                                                  undefined
                                                                ]);
                                                              }
                                                            }}
                                                          ></Select>
                                                        </Form.Item>
                                                      </Grid.Col>

                                                      <Grid.Col span={8}>
                                                        {form.getFieldValue(item.field + '.operatorType') ==
                                                          undefined && (
                                                          <Form.Item field={item.field + '.value'}>
                                                            <Input placeholder="请输入" disabled />
                                                          </Form.Item>
                                                        )}
                                                        {form.getFieldValue(item.field + '.operatorType') ==
                                                          FieldType.VALUE && (
                                                          <Form.Item field={item.field + '.value'}>
                                                            <Input placeholder="请输入静态值" />
                                                          </Form.Item>
                                                        )}

                                                        {form.getFieldValue(item.field + '.operatorType') ==
                                                          FieldType.VARIABLES && (
                                                          <Form.Item field={item.field + '.value'}>
                                                            <Select />
                                                          </Form.Item>
                                                        )}

                                                        {form.getFieldValue(item.field + '.operatorType') ==
                                                          FieldType.FORMULA && (
                                                          <Form.Item field={item.field + '.value'}>
                                                            <Button
                                                              //   onClick={() => openFormulaEditor(item.field + '.value')}
                                                              long
                                                            >
                                                              {form.getFieldValue(item.field + '.value')
                                                                ? '已设置公式'
                                                                : 'ƒx 编辑公式'}
                                                              {form.getFieldValue(item.field + '.value') ? (
                                                                <IconLaunch />
                                                              ) : (
                                                                ''
                                                              )}
                                                            </Button>
                                                          </Form.Item>
                                                        )}
                                                      </Grid.Col>
                                                    </>
                                                  )}

                                                <Grid.Col span={1}>
                                                  <IconDelete
                                                    style={{ fontSize: '15px', color: '#4E5969', marginBottom: '15px' }}
                                                    onClick={() => {
                                                      childRemove(childIndex);
                                                      if (condition.length === 1) {
                                                        removeAction(index);
                                                      }
                                                    }}
                                                  />
                                                </Grid.Col>
                                              </Grid.Row>
                                            );
                                          })}
                                        </div>
                                      );
                                    }}
                                  </Form.List>
                                </div>
                              </div>
                            );
                          })}
                          <Button
                            type="text"
                            onClick={() => {
                              addAction({
                                actions: [undefined]
                              });
                            }}
                          >
                            + 添加动作
                          </Button>
                        </div>
                      );
                    }}
                  </Form.List>
                </div>
              </div>
            </Row>
          </Form>
        </div>
      </div>
    </Modal>
  );
};

export default InteractionRuleModal;
