/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { type WorkflowLineEntity } from '@flowgram.ai/free-layout-editor';
import { Form, Switch, Input, Select, Button } from '@arco-design/web-react';
import { IconClose } from '@arco-design/web-react/icon';
import close from '../../assets/close.svg';
import BottomBtn from '../bottomBtn';
import { getEntityFieldsWithChildren, getPageSetMetaData } from '@onebase/app';
import type { FieldOption, OpOptions, ConditionRule } from './constants';
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
  const [currentIndex, setCurrentIndex] = useState<any>(0);
  const [conditionGroups, setConditionGroups] = useState<ConditionRule[][]>([
    [
      {
        fieldScope: '',
        fieldId: '',
        op: '',
        operatorType: '',
        value: '',
        fieldType: ''
      }
    ]
  ]);
  const [form] = Form.useForm();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const { line } = props;

  const handleClose = () => {};
  function handleSubmit() {
    console.log(conditionGroups);
  }
  // 规则改变
  const handleRuleChange = (groupIndex: number, ruleIndex: number, field: string, value: any, item?: any) => {
    const newGroups = [...conditionGroups];
    const newRules = [...newGroups[groupIndex]];
    const fields = ['fieldScope', 'fieldId', 'op', 'operatorType', 'value'];
    const currentFieldIndex = fields.indexOf(field);
    if (field === 'fieldId') {
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

  const openFormulaEditor = (index: any) => {
    // setCurrentIndex(index);
    // setFormulaData(conditionRule[currentIndex].value.formulaData || '');
    // 设置数据的index
    setFormulaVisible(true);
  };

  const handleFormulaConfirm = (formulaData: string, formattedFormula: string, params: any) => {
    console.log(formulaData, formattedFormula, params);

    // conditionRule[currentIndex].value = { formulaData: formulaData, formula: formattedFormula, parameters: params };
    setFormulaVisible(false);
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
        fieldId: '',
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
          fieldId: '',
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

    // 如果当前组只有一条规则，则删除整个组
    if (newGroups[groupIndex].length <= 1) {
      // 删除整个组
      setConditionGroups(newGroups.filter((_, i) => i !== groupIndex));
      return;
    }

    // 否则只删除当前规则
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
  }, []);
  return (
    // <NodeRenderContext.Provider value={contextValue}>
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
          <FormItem className={styles.directionRow} label="默认分支" field="isDefault">
            <Switch size="small" />
            <span className={styles.formTips}>开启后，若其他所有分支条件均不满足，则该分支将被执行</span>
          </FormItem>
          <FormItem
            rules={[{ required: true, message: '请输入分支名称' }]}
            className={styles.directionColumn}
            label="分支名称"
            field="branchName"
          >
            <Input placeholder="请输入分支名称" />
          </FormItem>
          <div className={styles.directionColumn}>
            <div className={styles.conditionRule}>
              {conditionGroups.map((group, groupIndex) => (
                <div key={groupIndex} className={styles.conditionRuleItem}>
                  {group.map((item, index) => {
                    return (
                      <div className={styles.conditionRuleItemLine} key={index}>
                        <Select
                          className={styles.ruleItemSelect}
                          value={item.fieldScope}
                          style={{ width: 152 }}
                          onChange={(value) => handleRuleChange(groupIndex, index, 'fieldScope', value)}
                        >
                          <Option value={'pre_node'}>上个审批节点属性</Option>
                          <Option value={'instance'}>流程实例属性</Option>
                          <Option value={'entity'}>表单字段</Option>
                        </Select>
                        {/* 业务字段 */}
                        <Select
                          className={styles.ruleItemSelect}
                          value={item.fieldId}
                          style={{ width: 120 }}
                          disabled={!item.fieldScope}
                          onChange={(value) => handleRuleChange(groupIndex, index, 'fieldId', value, item)}
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
                          disabled={!item.fieldScope || !item.fieldId}
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
                              {item.fieldType !== FieldType.GEOGRAPHY && <Option value={'value'}>静态值</Option>}
                              {renderVariables(item) && <Option value={'variables'}>变量</Option>}

                              {renderFormula(item) && <Option value={'formula'}>公式</Option>}
                            </Select>
                            {
                              <RenderElement
                                item={item}
                                index={index}
                                groupIndex={groupIndex}
                                isDisabled={!item.fieldScope || !item.fieldId || !item.op || !item.operatorType}
                                onRuleChange={handleRuleChange}
                                onOpenFormula={openFormulaEditor}
                                formulaVisible={formulaVisible}
                                formulaData={formulaData}
                                onFormulaConfirm={handleFormulaConfirm}
                                setFormulaVisible={setFormulaVisible}
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
                </div>
              ))}

              <Button type="outline" onClick={handleAddGroup}>
                + 或者
              </Button>
            </div>
            {/* </FormItem> */}
          </div>
        </Form>
      </div>
      <BottomBtn handleSubmit={handleSubmit} />
    </div>

    // </NodeRenderContext.Provider>
  );
}
