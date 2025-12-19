import {
  Button,
  Divider,
  Dropdown,
  Form,
  Grid,
  Input,
  Menu,
  Modal,
  Select,
  Switch,
  Tag,
  TreeSelect
} from '@arco-design/web-react';
import { IconDelete, IconLaunch, IconMoreVertical, IconPlus } from '@arco-design/web-react/icon';
import { FieldType, InteractionActionType, VALIDATION_TYPE } from '@onebase/app';
import { listToTree } from '@onebase/common';
import { getDeptList, getSimpleUserPage, type DictData } from '@onebase/platform-center';
import {
  getFieldOptionsConfig,
  FORM_COMPONENT_TYPES,
  useFormEditorSignal,
  usePageViewEditorSignal,
  useAppEntityStore
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import styles from './index.module.less';
import { getOperatorOptions } from './ruleMap';
import { FormulaEditor } from '@/components/FormulaEditor';

const Row = Grid.Row;
const Col = Grid.Col;

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

const formActionOptions = [
  {
    label: '显示',
    value: InteractionActionType.Show
  },
  {
    label: '隐藏',
    value: InteractionActionType.Hide
  },
  {
    label: '可编辑',
    value: InteractionActionType.Editable
  },
  {
    label: '只读',
    value: InteractionActionType.Readonly
  },
  {
    label: '必填',
    value: InteractionActionType.Required
  },
  {
    label: '非必填',
    value: InteractionActionType.NoRequired
  },
  {
    label: '设置字段值',
    value: InteractionActionType.SetFieldValue
  }
];

interface InteractionRuleModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: () => void;
}

interface Rule {
  id: string;
  name: string;
  enabled: number;
  description?: string;
  interactionCondition: any[];
  formAction: any[];
}

const InteractionRuleModal: React.FC<InteractionRuleModalProps> = ({ visible, onCancel, onOk }) => {
  useSignals();
  const { mainEntity, subEntities } = useAppEntityStore();
  const { components, pageComponentSchemas } = useFormEditorSignal;
  const { curViewId, pageViews, updatePageView } = usePageViewEditorSignal;

  const [cpOptions, setCpOptions] = useState<{ label: string; value: string }[]>([]);

  const [deptTree, setDeptTree] = useState<any[]>([]);

  const [userOptions, setUserOptions] = useState<any[]>([]);
  const [userPageNo, setUserPageNo] = useState<number>(1);
  const [userTotal, setUserTotal] = useState<number | string>(0);
  const [fetching, setFetching] = useState<boolean>(false);

  // 公式
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  const [formulaFieldKey, setFormulaFieldKey] = useState<string>('');
  const [formulaData, setFormulaData] = useState<string>('');

  // 获取组件下拉列表
  const getComponentOptions = () => {
    const cpOptions = Object.values(pageComponentSchemas.value)
      // 过滤图片、文件、子表
      .filter((item: any) => {
        return ![
          FORM_COMPONENT_TYPES.IMG_UPLOAD,
          FORM_COMPONENT_TYPES.FILE_UPLOAD,
          FORM_COMPONENT_TYPES.SUB_TABLE
        ].includes(item?.type);
      })
      .map((item: any) => ({
        label: item.config?.label?.text || item.config?.cpName || '',
        value: item.config?.id
      }));

    setCpOptions(cpOptions);
  };

  useEffect(() => {
    visible && getComponentOptions();
  }, [visible, pageComponentSchemas]);

  const [form] = Form.useForm();

  // 使用 ref 来标记是否正在初始化表单，避免触发 onValuesChange
  const isInitializingRef = React.useRef(false);
  // 使用 ref 存储上一次设置的 rule id，避免重复设置相同的值
  const lastSetRuleIdRef = React.useRef<string>('');

  const [rules, setRules] = useState<Rule[]>([]);
  const [curRule, setCurRule] = useState<string>('');

  // 当 modal 打开时，重新加载 rules
  useEffect(() => {
    if (visible) {
      const initialRules = pageViews.value[curViewId.value]?.interactionRules || [];
      setRules(initialRules);
      if (initialRules.length > 0) {
        setCurRule(initialRules[0].id);
        lastSetRuleIdRef.current = ''; // 重置，确保会设置表单
      } else {
        setCurRule('');
        lastSetRuleIdRef.current = '';
      }
      handleGetUsers();
      hadleGetDept();
    }
  }, [visible, curViewId.value]);

  useEffect(() => {
    if (curRule && lastSetRuleIdRef.current !== curRule) {
      const rule = rules.find((rule) => rule.id === curRule);
      if (rule) {
        isInitializingRef.current = true;
        form.setFieldsValue(rule);
        lastSetRuleIdRef.current = curRule;
        // 使用 requestAnimationFrame 确保在下一个渲染周期重置标志
        requestAnimationFrame(() => {
          isInitializingRef.current = false;
        });
      } else {
        // 如果规则不存在，清空表单
        isInitializingRef.current = true;
        form.resetFields();
        lastSetRuleIdRef.current = '';
        requestAnimationFrame(() => {
          isInitializingRef.current = false;
        });
      }
    } else if (!curRule && lastSetRuleIdRef.current !== '') {
      // 如果 curRule 被清空，清空表单
      isInitializingRef.current = true;
      form.resetFields();
      lastSetRuleIdRef.current = '';
      requestAnimationFrame(() => {
        isInitializingRef.current = false;
      });
    }
  }, [curRule, form]);

  const handleOk = () => {
    let curPageView = pageViews.value[curViewId.value];
    if (curPageView && curPageView.id) {
      const newPageView = {
        ...curPageView,
        interactionRules: rules
      };

      updatePageView(newPageView);

      console.log('newPageView: ', newPageView);
    }

    onOk();
  };

  const handleCancel = () => {
    onCancel();
  };

  const handleAddRule = () => {
    setRules((prevRules) => [
      ...prevRules,
      {
        id: uuidv4().replaceAll('-', ''),
        name: '新规则',
        enabled: 0,
        description: '',
        interactionCondition: [],
        formAction: []
      }
    ]);
  };

  const handleDeleteRule = (ruleId: string) => {
    setRules((prevRules) => {
      const newRules = prevRules.filter((rule) => rule.id !== ruleId);
      // 如果删除的是当前选中的规则，需要更新 curRule
      if (curRule === ruleId) {
        // 如果还有规则，选中第一个；否则清空
        if (newRules.length > 0) {
          setCurRule(newRules[0].id);
        } else {
          setCurRule('');
        }
      }
      return newRules;
    });
  };

  const handleMoveRule = (ruleId: string, direction: 'up' | 'down') => {
    setRules((prevRules) => {
      const currentIndex = prevRules.findIndex((rule) => rule.id === ruleId);
      if (currentIndex === -1) {
        return prevRules;
      }

      const targetIndex = direction === 'up' ? currentIndex - 1 : currentIndex + 1;

      if (targetIndex < 0 || targetIndex >= prevRules.length) {
        return prevRules;
      }

      const nextRules = [...prevRules];
      const [currentRule] = nextRules.splice(currentIndex, 1);
      nextRules.splice(targetIndex, 0, currentRule);
      return nextRules;
    });
  };

  const renderValueFormItem: any = async (cpId: string) => {
    const component = components.value.find((item: any) => item?.id === cpId);
    if (component?.type) {
      switch (component?.type) {
        case FORM_COMPONENT_TYPES.SELECT_ONE:
        case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
        case FORM_COMPONENT_TYPES.RADIO:
        case FORM_COMPONENT_TYPES.CHECKBOX:
          const dataField = pageComponentSchemas.value[cpId]?.config.dataField;
          const [tableName, fieldName] = dataField;
          let options: DictData[] = [];
          const index = fieldName?.indexOf('.');
          const lastIndex = fieldName?.lastIndexOf('.');
          if (index !== -1) {
            const subTableName = fieldName.slice(0, index);
            const subFieldName = lastIndex === -1 ? fieldName : fieldName.slice(lastIndex + 1);
            options = await getFieldOptionsConfig([subTableName, subFieldName], mainEntity, subEntities);
          } else {
            options = await getFieldOptionsConfig(dataField, mainEntity, subEntities);
          }
          return (
            <Select options={options} placeholder="请选择静态值">
              {options.map((ele, index: number) => (
                <Select.Option key={index} value={ele.id}>
                  {ele.label}
                </Select.Option>
              ))}
            </Select>
          );
        case FORM_COMPONENT_TYPES.USER_SELECT:
          return (
            <Select placeholder="请选择人员" allowClear showSearch={true} onPopupScroll={scrollHandler}>
              {userOptions.map((option) => (
                <Select.Option key={option.id} value={option.id}>
                  {option.nickname}
                </Select.Option>
              ))}
            </Select>
          );
        case FORM_COMPONENT_TYPES.DEPT_SELECT:
          return <TreeSelect placeholder="请选择部门" allowClear showSearch={true} treeData={deptTree} />;

        default:
          return <Input placeholder="请输入静态值" />;
      }
    }

    return <Input placeholder="请输入静态值" />;
  };

  const handleGetUsers = async () => {
    const param = {
      pageNo: 1,
      pageSize: 20
    };
    const { list, total } = await getSimpleUserPage(param);
    setUserOptions(list || []);
    setUserTotal(total);
    // console.log('res: ', res);
  };

  // 滚动加载
  const scrollHandler = async (element: HTMLDivElement) => {
    const { scrollTop, scrollHeight, clientHeight } = element;
    const scrollBottom = scrollHeight - (scrollTop + clientHeight);

    if (scrollBottom < 10 && !fetching && Number(userTotal) > userOptions.length) {
      setFetching(true);
      const param = {
        pageNo: userPageNo + 1,
        pageSize: 20
      };
      const { list, total } = await getSimpleUserPage(param);
      setUserPageNo(userPageNo + 1);
      setUserTotal(total);
      setUserOptions((prev) => [...prev, ...list]);
      setFetching(false);
    }
  };

  const hadleGetDept = async () => {
    const res = await getDeptList({});
    const treeData = listToTree(res, {}, true);
    setDeptTree(treeData);
  };

  // 公式
  const openFormulaEditor = (fieldKey: string) => {
    setFormulaVisible(true);
    setFormulaData(form.getFieldValue(fieldKey)?.formulaData);
    setFormulaFieldKey(fieldKey);
  };

  const handleFormulaConfirm = (formulaData: string, formattedFormula: string, params: any) => {
    setFormulaVisible(false);
    form.setFieldValue(formulaFieldKey, { formulaData: formulaData, formula: formattedFormula, parameters: params });
    setFormulaData('');
    setFormulaFieldKey('');
  };

  return (
    <Modal
      style={{ width: 1200, height: 800 }}
      title={<div style={{ textAlign: 'left' }}>交互规则管理</div>}
      visible={visible}
      onCancel={handleCancel}
      onOk={handleOk}
    >
      <div className={styles.interactionRuleModal}>
        <div className={styles.left}>
          <div className={styles.leftHeader}>
            <Input.Search placeholder="搜索" />
            <Button type="text" icon={<IconPlus />} onClick={handleAddRule}>
              新建
            </Button>
          </div>
          <div className={styles.leftContent}>
            {rules.map((rule) => (
              <div
                key={rule.id}
                className={styles.ruleItem}
                style={{ backgroundColor: curRule === rule.id ? '#f7f8fa' : 'transparent' }}
                onClick={() => {
                  setCurRule(rule.id);
                }}
              >
                <div className={styles.ruleItemName}>{rule.name}</div>
                <div className={styles.ruleItemEnabled}>
                  {rule.enabled ? <Tag color="green">启用</Tag> : <Tag color="gray">禁用</Tag>}

                  <Dropdown
                    droplist={
                      <Menu>
                        <Menu.Item key="copy">复制</Menu.Item>
                        <Menu.Item
                          key="move-up"
                          onClick={(event) => {
                            event.stopPropagation();
                            handleMoveRule(rule.id, 'up');
                          }}
                        >
                          优先级上移
                        </Menu.Item>
                        <Menu.Item
                          key="move-down"
                          onClick={(event) => {
                            event.stopPropagation();
                            handleMoveRule(rule.id, 'down');
                          }}
                        >
                          优先级下移
                        </Menu.Item>
                        <Menu.Item
                          key="delete"
                          style={{ color: 'red' }}
                          onClick={(event) => {
                            event.stopPropagation();
                            handleDeleteRule(rule.id);
                          }}
                        >
                          删除
                        </Menu.Item>
                      </Menu>
                    }
                    position="bl"
                  >
                    <Button type="text">
                      <IconMoreVertical />
                    </Button>
                  </Dropdown>
                </div>
              </div>
            ))}
          </div>
        </div>
        <div className={styles.right}>
          {curRule && rules.find((rule) => rule.id === curRule) && (
            <Form
              layout="vertical"
              form={form}
              onValuesChange={(changeValue: any, values: any) => {
                // 如果正在初始化表单，跳过更新 rules，避免无限循环
                if (isInitializingRef.current) {
                  return;
                }
                // 从values中获取当前rule的id
                const curRuleId = values.id;
                if (curRuleId) {
                  // 查找并更新rules列表
                  setRules((prevRules: any[]) =>
                    prevRules.map((rule) => (rule.id === curRuleId ? { ...rule, ...values } : rule))
                  );
                }
              }}
            >
              <Form.Item field="id" hidden={true}>
                <Input />
              </Form.Item>
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
                                                          components.value,
                                                          form.getFieldValue(item.field + '.cpId')
                                                        )}
                                                      />
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
                                                          {/* 静态值 */}
                                                          {form.getFieldValue(item.field + '.operatorType') ==
                                                            FieldType.VALUE && (
                                                            <Form.Item field={item.field + '.value'}>
                                                              {renderValueFormItem(
                                                                form.getFieldValue(item.field + '.cpId')
                                                              )}
                                                            </Form.Item>
                                                          )}
                                                          {/* 变量 */}
                                                          {form.getFieldValue(item.field + '.operatorType') ==
                                                            FieldType.VARIABLES && (
                                                            <Form.Item field={item.field + '.value'}>
                                                              <Select />
                                                            </Form.Item>
                                                          )}
                                                          {/* 公式 */}
                                                          {form.getFieldValue(item.field + '.operatorType') ==
                                                            FieldType.FORMULA && (
                                                            <Form.Item field={item.field + '.value'}>
                                                              <Button
                                                                onClick={() => openFormulaEditor(item.field + '.value')}
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
                                                      style={{
                                                        fontSize: '15px',
                                                        color: '#4E5969',
                                                        marginBottom: '15px'
                                                      }}
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
                    <Form.List field={'formAction'}>
                      {(actions, { add: addAction, remove: removeAction }) => {
                        return (
                          <div>
                            {actions.map((item, index) => {
                              return (
                                <div key={item.key}>
                                  <div className={styles.items}>
                                    <Grid.Row key={item.key} gutter={8} align="center">
                                      <Grid.Col span={4}>
                                        <Form.Item field={item.field + '.action'}>
                                          <Select
                                            className={styles.itemSelect}
                                            options={formActionOptions}
                                            onChange={(_value) => {
                                              form.resetFields([item.field + '.cpId']);
                                            }}
                                          />
                                        </Form.Item>
                                      </Grid.Col>

                                      {![InteractionActionType.SetFieldValue].includes(
                                        form.getFieldValue(item.field + '.action')
                                      ) && (
                                        <>
                                          <Grid.Col span={19}>
                                            <Form.Item field={item.field + '.cpIds'}>
                                              <Select
                                                mode="multiple"
                                                className={styles.itemSelect}
                                                options={cpOptions}
                                                onChange={(_value) => {}}
                                              />
                                            </Form.Item>
                                          </Grid.Col>
                                        </>
                                      )}

                                      {[InteractionActionType.SetFieldValue].includes(
                                        form.getFieldValue(item.field + '.action')
                                      ) && (
                                        <>
                                          <Grid.Col span={8}>
                                            <Form.Item field={item.field + '.cpId'}>
                                              <Select
                                                className={styles.itemSelect}
                                                options={cpOptions}
                                                onChange={(_value) => {}}
                                              />
                                            </Form.Item>
                                          </Grid.Col>

                                          <Grid.Col span={2} style={{ textAlign: 'center', marginBottom: '16px' }}>
                                            <div>的值设为</div>
                                          </Grid.Col>

                                          <Grid.Col span={3}>
                                            <Form.Item field={item.field + '.operatorType'}>
                                              <Select
                                                className={styles.itemSelect}
                                                disabled={form.getFieldValue(item.field + '.cpId') == undefined}
                                                options={opCodeOptions}
                                                onChange={() => {
                                                  form.setFieldValue(item.field + '.value', undefined);
                                                  // 如果是范围类型 需要用数组兜底
                                                  if (form.getFieldValue(item.field + '.op') == VALIDATION_TYPE.RANGE) {
                                                    form.setFieldValue(item.field + '.value', [undefined, undefined]);
                                                  }
                                                }}
                                              ></Select>
                                            </Form.Item>
                                          </Grid.Col>

                                          <Grid.Col span={6}>
                                            {form.getFieldValue(item.field + '.operatorType') == undefined && (
                                              <Form.Item field={item.field + '.value'}>
                                                <Input placeholder="请输入" disabled />
                                              </Form.Item>
                                            )}
                                            {form.getFieldValue(item.field + '.operatorType') == FieldType.VALUE && (
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

                                            {form.getFieldValue(item.field + '.operatorType') == FieldType.FORMULA && (
                                              <Form.Item field={item.field + '.value'}>
                                                <Button onClick={() => openFormulaEditor(item.field + '.value')} long>
                                                  {form.getFieldValue(item.field + '.value')
                                                    ? '已设置公式'
                                                    : 'ƒx 编辑公式'}
                                                  {form.getFieldValue(item.field + '.value') ? <IconLaunch /> : ''}
                                                </Button>
                                              </Form.Item>
                                            )}
                                          </Grid.Col>
                                        </>
                                      )}

                                      <Grid.Col span={1}>
                                        <IconDelete
                                          style={{
                                            fontSize: '15px',
                                            color: '#4E5969',
                                            marginBottom: '15px'
                                          }}
                                          onClick={() => {
                                            removeAction(index);
                                          }}
                                        />
                                      </Grid.Col>
                                    </Grid.Row>
                                  </div>
                                </div>
                              );
                            })}
                            <Button
                              type="text"
                              onClick={() => {
                                addAction({});
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
          )}
        </div>
      </div>

      <FormulaEditor
        initialFormula={formulaData}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />
    </Modal>
  );
};

export default InteractionRuleModal;
