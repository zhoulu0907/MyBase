/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useEffect, useState, useCallback, startTransition, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { type WorkflowLineEntity } from '@flowgram.ai/free-layout-editor';
import { Form, Switch, Input, Select, Button, Message, Divider } from '@arco-design/web-react';
import { useClientContext } from '@flowgram.ai/free-layout-editor';
import { IconClose } from '@arco-design/web-react/icon';
import close from '../../assets/close.svg';
import BottomBtn from '../bottomBtn';
import { SidebarContext } from '../../context';
import { FormulaEditor } from '@/components/FormulaEditor';
import { getEntityFieldsWithChildren, getPageSetMetaData } from '@onebase/app';
import type { ConditionRule } from './constants';
import { FieldType, Operator, preNodeOptions, instanceOptions, entityOptions } from './constants';
import {
  textOpOption,
  longTextOpOption,
  emailOpOption,
  phoneOpOption,
  urlOpOption,
  addressOpOption,
  numberOpOption,
  dateOpOption,
  datetimeOpOption,
  booleanOpOption,
  selectOpOption,
  multiSelectOpOption,
  autoCodeOpOption,
  userOpOption,
  multiUserOpOption,
  departmentOpOption,
  multiDepartmentOpOption,
  dataSelectionOpOption,
  multiDataSelectionOpOption,
  fileOpOption,
  imageOpOption,
  geographyOpOption,
  passwordOpOption,
  encryptedOpOption,
  aggregateOpOption,
  idOpOption
} from './constants';
import { RenderElement } from './components/renderElement';
import styles from './siderbar-line.module.less';
const FormItem = Form.Item;
const Option = Select.Option;

export function SidebarLineRenderer(props: { line: WorkflowLineEntity }) {
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  const [formulaData, setFormulaData] = useState<string>('');
  const [formSummaryOptions, setFormSummaryOptions] = useState<any[]>([]);
  const [currentEditingRule, setCurrentEditingRule] = useState<{ groupIndex: number; ruleIndex: number } | null>(null);
  const [conditionGroups, setConditionGroups] = useState<ConditionRule[][]>([
    [
      {
        fieldScope: '',
        fieldUuid: '',
        op: '',
        operatorType: '',
        value: '',
        fieldType: ''
      }
    ]
  ]);
  const [form] = Form.useForm();
  const location = useLocation();
  const ctx = useClientContext();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const { setLineData } = useContext(SidebarContext);
  const { line } = props;

  const handleClose = useCallback(() => {
    startTransition(() => {
      setLineData(undefined);
    });
  }, []);
  const handleSubmit = () => {
    form.validate(['name']).then((values) => {
      let fromIsDefault = form.getFieldValue('isDefault');

      let isValid = false;
      if (!fromIsDefault) {
        isValid = conditionGroups.every((group) =>
          group.every((item) => {
            if (item.op === Operator.IS_EMPTY || item.op === Operator.IS_NOT_EMPTY) {
              return item.fieldScope && item.fieldUuid && item.op;
            }
            return item.fieldScope && item.fieldUuid && item.op && item.operatorType && item.value;
          })
        );
      }

      if (!fromIsDefault) {
        if (!isValid) {
          form.setFields({
            condition: {
              error: {
                message: '请检查条件规则是否填写完整',
                type: 'error'
              }
            }
          });
          return;
        }
        form.setFields({
          condition: {
            value: conditionGroups
          }
        });
      }
      const fromValue = form.getFieldsValue();
      let priority = line.lineData?.priority;
      if (!priority) {
        const allLines = ctx.document.linesManager.getAllLines();
        const conditionalBranchLines = allLines.filter((lineItem: any) => lineItem.info.from.includes(line.info.from));
        const existingDefaultBranch = conditionalBranchLines.find(
          (lineItem) => lineItem.lineData?.isDefault && lineItem.id !== line.id
        );
        const maxPriority = conditionalBranchLines.length;

        if (fromValue.isDefault) {
          if (existingDefaultBranch) {
            existingDefaultBranch.lineData = {
              ...existingDefaultBranch.lineData,
              priority: maxPriority - 1,
              isDefault: false
            };
          }
          priority = maxPriority;
        } else {
          if (existingDefaultBranch) {
            existingDefaultBranch.lineData = {
              ...existingDefaultBranch.lineData,
              priority: maxPriority
            };
            priority = maxPriority - 1;
          } else {
            priority = conditionalBranchLines.length;
          }
        }
      }
      line.lineData = {
        ...fromValue,
        priority
      };

      Message.success('保存成功');
      handleClose();
    });
  };
  // 规则改变
  const handleRuleChange = (groupIndex: number, ruleIndex: number, field: string, value: any, item?: any) => {
    const newGroups = [...conditionGroups];
    const newRules = [...newGroups[groupIndex]];
    const fields = ['fieldScope', 'fieldUuid', 'op', 'operatorType', 'value'];
    const currentFieldIndex = fields.indexOf(field);
    if (field === 'fieldUuid') {
      const ops = renderFieldIdOptions(item);
      const selectedField = ops.find((opt: any) => opt.value === value);
      newRules[ruleIndex] = {
        ...newRules[ruleIndex],
        fieldType: selectedField?.type || ''
      };
    }
    newRules[ruleIndex] = {
      ...newRules[ruleIndex],
      [field]: value,
      ...fields.slice(currentFieldIndex + 1).reduce(
        (acc, f) => ({
          ...acc,
          [f]: f === 'fieldType' ? newRules[ruleIndex][f] : ''
        }),
        {}
      )
    };

    newGroups[groupIndex] = newRules;
    setConditionGroups(newGroups);
  };

  const openFormulaEditor = (item: any, groupIndex: number, ruleIndex: number) => {
    setCurrentEditingRule({ groupIndex, ruleIndex });
    setFormulaData(item.value?.formulaData || '');
    setFormulaVisible(true);
  };

  const handleFormulaConfirm = (formulaData: string, formattedFormula: string, params: any) => {
    if (!currentEditingRule) return;

    const { groupIndex, ruleIndex } = currentEditingRule;
    const newGroups = [...conditionGroups];
    const newRules = [...newGroups[groupIndex]];

    newRules[ruleIndex] = {
      ...newRules[ruleIndex],
      value: {
        formulaData,
        formula: formattedFormula,
        parameters: params
      }
    };

    newGroups[groupIndex] = newRules;
    setConditionGroups(newGroups);
    setFormulaVisible(false);
    setCurrentEditingRule(null);
  };

  const renderFieldIdOptions = (item: any) => {
    let options: any = [];
    switch (item.fieldScope) {
      case 'pre_node':
        options = preNodeOptions;
        break;
      case 'instance':
        options = instanceOptions;
        break;
      case 'entity':
        options = entityOptions;
        break;
      default:
        options = [];
    }
    return options;
  };

  const renderOpOptions = (item: any) => {
    let options: any = [];
    switch (item?.fieldType) {
      case FieldType.TEXT:
        options = textOpOption;
        break;
      case FieldType.LONG_TEXT:
        options = longTextOpOption;
        break;
      case FieldType.EMAIL:
        options = emailOpOption;
        break;
      case FieldType.PHONE:
        options = phoneOpOption;
        break;
      case FieldType.URL:
        options = urlOpOption;
        break;
      case FieldType.ADDRESS:
        options = addressOpOption;
        break;
      case FieldType.NUMBER:
        options = numberOpOption;
        break;
      case FieldType.DATE:
        options = dateOpOption;
        break;
      case FieldType.DATETIME:
        options = datetimeOpOption;
        break;
      case FieldType.BOOLEAN:
        options = booleanOpOption;
        break;
      case FieldType.SELECT:
        options = selectOpOption;
        break;
      case FieldType.MULTI_SELECT:
        options = multiSelectOpOption;
        break;
      case FieldType.AUTO_CODE:
        options = autoCodeOpOption;
        break;
      case FieldType.USER:
        options = userOpOption;
        break;
      case FieldType.MULTI_USER:
        options = multiUserOpOption;
        break;
      case FieldType.DEPARTMENT:
        options = departmentOpOption;
        break;
      case FieldType.MULTI_DEPARTMENT:
        options = multiDepartmentOpOption;
        break;
      case FieldType.DATA_SELECTION:
        options = dataSelectionOpOption;
        break;
      case FieldType.MULTI_DATA_SELECTION:
        options = multiDataSelectionOpOption;
        break;
      case FieldType.FILE:
        options = fileOpOption;
        break;
      case FieldType.IMAGE:
        options = imageOpOption;
        break;
      case FieldType.GEOGRAPHY:
        options = geographyOpOption;
        break;
      case FieldType.PASSWORD:
        options = passwordOpOption;
        break;
      case FieldType.ENCRYPTED:
        options = encryptedOpOption;
        break;
      case FieldType.AGGREGATE:
        options = aggregateOpOption;
        break;
      case FieldType.ID:
        options = idOpOption;
        break;
      case FieldType.DATA_SELECTION_RESULT:
        options = dataSelectionOpOption;
        break;
      default:
        options = [];
    }
    return options.map((option: any) => (
      <Option key={option.value} value={option.value}>
        {option.label}
      </Option>
    ));
  };

  // 表单摘要数据获取
  const getFormSummaryData = async () => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    const { parentFields } = await getEntityFieldsWithChildren(mainMetaData);
    setFormSummaryOptions(parentFields);
  };

  const handleAddRule = (groupIndex: number) => {
    const newGroups = [...conditionGroups];
    newGroups[groupIndex] = [
      ...newGroups[groupIndex],
      {
        fieldScope: '',
        fieldUuid: '',
        op: '',
        operatorType: '',
        value: '',
        fieldType: ''
      }
    ];
    setConditionGroups(newGroups);
  };

  // 添加新的条件组
  const handleAddGroup = () => {
    setConditionGroups([
      ...conditionGroups,
      [
        {
          fieldScope: '',
          fieldUuid: '',
          op: '',
          operatorType: '',
          value: '',
          fieldType: ''
        }
      ]
    ]);
  };

  // 修改删除规则的函数
  const handleDeleteRule = (groupIndex: number, ruleIndex: number) => {
    const newGroups = [...conditionGroups];
    if (newGroups[groupIndex].length <= 1) {
      if (newGroups.length <= 1) return;
      setConditionGroups(newGroups.filter((_, i) => i !== groupIndex));
      return;
    }
    newGroups[groupIndex] = newGroups[groupIndex].filter((_, i) => i !== ruleIndex);
    setConditionGroups(newGroups);
  };

  const RANGE_TYPES = [FieldType.NUMBER, FieldType.DATE, FieldType.DATETIME, FieldType.AGGREGATE] as const;

  const renderVariables = (item: { fieldType: string; op: string }) => {
    return !RANGE_TYPES.includes(item.fieldType as (typeof RANGE_TYPES)[number]) || item.op !== Operator.RANGE;
  };

  const renderFormula = (item: { fieldType: string; op: string }) => {
    const disabledTypes = [
      FieldType.ADDRESS,
      FieldType.DATA_SELECTION,
      FieldType.MULTI_DATA_SELECTION,
      FieldType.GEOGRAPHY,
      FieldType.ID
    ];
    const rangeTypes = [FieldType.NUMBER, FieldType.DATE, FieldType.DATETIME, FieldType.AGGREGATE];
    return (
      !disabledTypes.includes(item.fieldType as FieldType) &&
      !(rangeTypes.includes(item.fieldType as FieldType) && item.op === Operator.RANGE)
    );
  };
  useEffect(() => {
    getFormSummaryData();
    console.log(line.lineData);

    if (line.lineData) {
      form.setFieldsValue(line.lineData);
      line.lineData.condition && setConditionGroups(line.lineData.condition);
    }
  }, []);
  console.log(conditionGroups, 'conditionGroups');

  return (
    <div
      style={{
        borderRadius: 8,
        border: '1px solid rgba(82,100,154, 0.13)',
        margin: 0,
        height: 'calc(100% - 2px)',
        overflow: 'hidden',
        background: '#fff'
      }}
    >
      <div className={styles.sidebarLineHeader}>
        <div className={styles.leftTitle}>
          <div className={styles.title}>连线</div>
        </div>
        <img className={styles.close} src={close} alt="" onClick={handleClose} />
      </div>
      <div className={styles.sidebarLineContent}>
        <Form form={form} autoComplete="off">
          <FormItem className={styles.directionRow}>
            <FormItem
              label="默认分支"
              field="isDefault"
              triggerPropName="checked"
              initialValue={line.lineData?.isDefault || false}
            >
              <Switch size="small" />
            </FormItem>
            <span className={styles.formTips}>开启后，若其他所有分支条件均不满足，则该分支将被执行</span>
          </FormItem>
          <FormItem
            rules={[{ required: true, message: '请输入分支名称' }]}
            className={styles.directionColumn}
            label="分支名称"
            field="name"
          >
            <Input placeholder="请输入分支名称" />
          </FormItem>

          <FormItem
            className={styles.conditionalFormItem}
            shouldUpdate={(prevValues, curValues) => prevValues.isDefault !== curValues.isDefault}
          >
            {(values) => {
              if (values.isDefault) return null;
              return (
                <FormItem
                  rules={[{ required: true, message: '请填写条件规则' }]}
                  className={styles.directionColumn}
                  label="条件规则"
                  field="condition"
                >
                  <div key={'condition'} className={styles.conditionRule}>
                    {conditionGroups.map((group, groupIndex) => (
                      <>
                        <div key={groupIndex} className={styles.conditionRuleItem}>
                          {group.map((item, index) => {
                            return (
                              <div className={styles.conditionRuleItemLine} key={`conditionRuleItemLine_${index}`}>
                                <Select
                                  className={styles.ruleItemSelect}
                                  value={item.fieldScope}
                                  style={{ width: 142 }}
                                  onChange={(value) => handleRuleChange(groupIndex, index, 'fieldScope', value)}
                                >
                                  <Option key={'pre_node'} value={'pre_node'}>
                                    上个审批节点属性
                                  </Option>
                                  <Option key={'instance'} value={'instance'}>
                                    流程实例属性
                                  </Option>
                                  <Option key={'entity'} value={'entity'}>
                                    表单字段
                                  </Option>
                                </Select>
                                {/* 业务字段 */}
                                <Select
                                  className={styles.ruleItemSelect}
                                  value={item.fieldUuid}
                                  style={{ width: 120 }}
                                  disabled={!item.fieldScope}
                                  onChange={(value) => handleRuleChange(groupIndex, index, 'fieldUuid', value, item)}
                                >
                                  {renderFieldIdOptions(item).map((option: any) => (
                                    <Option key={option.value} value={option.value}>
                                      {option.label}
                                    </Option>
                                  ))}
                                </Select>
                                {/* 比较操作符 */}
                                <Select
                                  className={styles.ruleItemSelect}
                                  value={item.op}
                                  style={{ width: 100 }}
                                  disabled={!item.fieldScope || !item.fieldUuid}
                                  onChange={(value) => handleRuleChange(groupIndex, index, 'op', value)}
                                >
                                  {renderOpOptions(item)}
                                </Select>

                                {item.op !== 'IS_EMPTY' && item.op !== 'IS_NOT_EMPTY' && (
                                  <>
                                    {/* 值的来源类型 */}
                                    <Select
                                      className={styles.ruleItemSelect}
                                      value={item.operatorType}
                                      style={{ width: 85 }}
                                      disabled={!item.fieldScope || !item.fieldScope || !item.op}
                                      onChange={(value) => handleRuleChange(groupIndex, index, 'operatorType', value)}
                                    >
                                      {item.fieldType !== FieldType.GEOGRAPHY && (
                                        <Option value={'value'}>静态值</Option>
                                      )}
                                      {renderVariables(item) && <Option value={'variables'}>变量</Option>}

                                      {renderFormula(item) && <Option value={'formula'}>公式</Option>}
                                    </Select>
                                    {
                                      <RenderElement
                                        item={item}
                                        index={index}
                                        groupIndex={groupIndex}
                                        isDisabled={
                                          !item.fieldScope || !item.fieldUuid || !item.op || !item.operatorType
                                        }
                                        onRuleChange={handleRuleChange}
                                        onOpenFormula={() => openFormulaEditor(item, groupIndex, index)}
                                        formSummaryOptions={formSummaryOptions}
                                      />
                                    }
                                  </>
                                )}
                                <IconClose
                                  onClick={() => handleDeleteRule(groupIndex, index)}
                                  style={{ marginLeft: '8px', cursor: 'pointer' }}
                                />
                              </div>
                            );
                          })}
                          <div className={styles.addAndRule} onClick={() => handleAddRule(groupIndex)}>
                            +并且
                          </div>
                          <div className={styles.andLine}>且</div>
                        </div>

                        {groupIndex < conditionGroups.length - 1 && (
                          <Divider orientation="center">
                            <div className={styles.orLine}>或</div>
                          </Divider>
                        )}
                      </>
                    ))}

                    <Button type="outline" onClick={handleAddGroup}>
                      + 或者
                    </Button>
                  </div>
                </FormItem>
              );
            }}
          </FormItem>
        </Form>
      </div>
      <BottomBtn handleSubmit={handleSubmit} submitOnly />
      <FormulaEditor
        initialFormula={formulaData}
        visible={formulaVisible}
        onConfirm={(formulaData, formattedFormula, params) =>
          handleFormulaConfirm(formulaData, formattedFormula, params)
        }
        onCancel={() => setFormulaVisible(false)}
      />
    </div>
  );
}
