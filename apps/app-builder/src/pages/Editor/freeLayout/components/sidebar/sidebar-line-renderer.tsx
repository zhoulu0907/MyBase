/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { type WorkflowLineEntity } from '@flowgram.ai/free-layout-editor';
import { FormulaEditor } from '@/components/FormulaEditor';
import { Form, Switch, Input, Select, Button, TreeSelect } from '@arco-design/web-react';
import { IconClose, IconLaunch } from '@arco-design/web-react/icon';
import close from '../../assets/close.svg';
import BottomBtn from '../bottomBtn';
import { getEntityFieldsWithChildren, getPageSetMetaData } from '@onebase/app';
import type { FieldOption, OpOptions, ConditionRule } from './constants';
import { FieldType, PreNode, Instance, Operator } from './constants';
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
  const [conditionRule, setConditionRule] = useState<ConditionRule[]>([
    {
      fieldScope: '',
      fieldId: '',
      op: '',
      operatorType: '',
      value: '',
      fieldType: ''
    }
  ]);
  const [fieldIdOptions, setFieldIdOptions] = useState<FieldOption[]>([]); // 第二个下拉框 业务字段
  const [opOptions, setOpOptions] = useState<OpOptions[]>([]); // 第三个下拉框  操作符
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false); // 变量的筛选源数据
  const [formulaData, setFormulaData] = useState<string>(''); // 公式弹窗
  const [formSummaryOptions, setFormSummaryOptions] = useState<any[]>([]); // 公式弹窗的值 用于回显
  const [currentIndex, setCurrentIndex] = useState<any>(0); // 当前操作的数据  用于公式弹窗操作数据
  const [form] = Form.useForm();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const { line } = props;

  const preNodeOptions = [
    {
      label: '审批结果',
      value: PreNode.APPROVAL_RESULT,
      type: FieldType.DATA_SELECTION
    },
    {
      label: '审批⼈',
      value: PreNode.APPROVER_ID,
      type: FieldType.USER
    },
    {
      label: '审批时间',
      value: PreNode.APPROVAL_TIME,
      type: FieldType.DATETIME
    },
    {
      label: '审批⼈部⻔',
      value: PreNode.APPROVER_DEPT_ID,
      type: FieldType.DEPARTMENT
    }
  ];

  const instanceOptions = [
    {
      label: '流程标题',
      value: Instance.BPM_TITLE,
      type: FieldType.TEXT
    },
    {
      label: '发起⼈',
      value: Instance.INITIATOR_ID,
      type: FieldType.USER
    },
    {
      label: '发起部⻔',
      value: Instance.INITIATOR_DEPT_ID,
      type: FieldType.DEPARTMENT
    },
    {
      label: '发起时间',
      value: Instance.SUBMIT_TIME,
      type: FieldType.DATETIME
    },
    {
      label: '创建时间',
      value: Instance.CREATE_TIME,
      type: FieldType.DATETIME
    },
    {
      label: '更新时间',
      value: Instance.UPDATE_TIME,
      type: FieldType.DATETIME
    }
  ];

  const entityOptions = [
    {
      label: '表单字段1',
      value: 'field1',
      type: FieldType.AGGREGATE
    },
    {
      label: '表单字段2',
      value: 'field2',
      type: FieldType.ENCRYPTED
    }
  ];

  const handleClose = () => {};
  function handleSubmit() {
    // form.validate().then((values) => {
    //   console.log(values, conditionRule);
    // });

    console.log(conditionRule);
  }
  // 规则改变
  const handleRuleChange = (index: number, field: string, value: any) => {
    const newRules = [...conditionRule];
    const fields = ['fieldScope', 'fieldId', 'op', 'operatorType', 'value'];
    const currentFieldIndex = fields.indexOf(field);
    if (field === 'fieldScope') {
      let options: any = [];
      switch (value) {
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
      setFieldIdOptions(options);
    }
    if (field === 'fieldId') {
      const selectedField = fieldIdOptions.find((opt) => opt.value === value);
      let options: any = [];
      switch (selectedField?.type) {
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
        default:
          options = [];
      }
      setOpOptions(options);
      newRules[index] = {
        ...newRules[index],
        fieldType: selectedField?.type || ''
      };
    }
    newRules[index] = {
      ...newRules[index],
      [field]: value,
      ...fields.slice(currentFieldIndex + 1).reduce(
        (acc, f) => ({
          ...acc,
          [f]: f === 'fieldType' ? newRules[index][f] : ''
        }),
        {}
      )
    };
    setConditionRule(newRules);
  };

  const openFormulaEditor = (index: any) => {
    setCurrentIndex(index);
    setFormulaData(conditionRule[currentIndex].value.formulaData || '');
    // 设置数据的index
    setFormulaVisible(true);
  };

  const handleFormulaConfirm = (formulaData: string, formattedFormula: string, params: any) => {
    conditionRule[currentIndex].value = { formulaData: formulaData, formula: formattedFormula, parameters: params };
    setFormulaVisible(false);
  };

  // 表单摘要数据获取
  const getFormSummaryData = async () => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    const { parentFields } = await getEntityFieldsWithChildren(mainMetaData);
    setFormSummaryOptions(parentFields);
  };

  const handleAddRule = () => {
    console.log(123123);

    setConditionRule([
      ...conditionRule,
      {
        fieldScope: '',
        fieldId: '',
        op: '',
        operatorType: '',
        value: '',
        fieldType: ''
      }
    ]);
  };

  const handleDeleteRule = (index: number) => {
    if (conditionRule.length <= 1) return;
    const newRules = conditionRule.filter((_, i) => i !== index);
    setConditionRule(newRules);
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
            {/* <FormItem className={styles.directionColumn} label="条件规则" field="conditionRule"> */}
            <div className={styles.conditionRule}>
              <div className={styles.conditionRuleItem}>
                {conditionRule.map((item, index) => {
                  return (
                    <div className={styles.conditionRuleItemLine} key={index}>
                      <Select
                        className={styles.ruleItemSelect}
                        value={item.fieldScope}
                        style={{ width: 152 }}
                        onChange={(value) => handleRuleChange(index, 'fieldScope', value)}
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
                        onChange={(value) => handleRuleChange(index, 'fieldId', value)}
                      >
                        {fieldIdOptions.map((option) => (
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
                        onChange={(value) => handleRuleChange(index, 'op', value)}
                      >
                        {opOptions.map((option) => (
                          <Option key={option.value} value={option.value}>
                            {option.label}
                          </Option>
                        ))}
                      </Select>

                      {item.op !== 'IS_EMPTY' && item.op !== 'IS_NOT_EMPTY' && (
                        <>
                          {/* 值的来源类型 */}
                          <Select
                            className={styles.ruleItemSelect}
                            value={item.operatorType}
                            style={{ width: 85 }}
                            disabled={!item.fieldScope || !item.fieldScope || !item.op}
                            onChange={(value) => handleRuleChange(index, 'operatorType', value)}
                          >
                            <Option value={'value'}>静态值</Option>
                            <Option value={'variables'}>变量</Option>
                            <Option value={'formula'}>公式</Option>
                          </Select>
                          {
                            <RenderElement
                              item={item}
                              index={index}
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
                        onClick={() => handleDeleteRule(index)}
                        style={{ marginLeft: '8px', cursor: 'pointer' }}
                      />
                    </div>
                  );
                })}
                <div className={styles.addAndRule} onClick={handleAddRule}>
                  +并且
                </div>
              </div>
              <Button type="outline">+ 或者</Button>
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
