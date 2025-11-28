// render-element.tsx
import { Input, Select, Button, DatePicker } from '@arco-design/web-react';
import { IconLaunch } from '@arco-design/web-react/icon';
import { FormulaEditor } from '@/components/FormulaEditor';
import type { ConditionRule } from '../../constants';
import styles from '../../siderbar-line.module.less';
import { approvalResultOptions } from '../../constants';
const Option = Select.Option;

interface RenderElementProps {
  item: ConditionRule;
  index: number;
  isDisabled: boolean;
  onRuleChange: (index: number, field: string, value: any) => void;
  onOpenFormula: (index: number) => void;
  formulaVisible: boolean;
  formulaData: string;
  onFormulaConfirm: (formulaData: string, formattedFormula: string, params: any) => void;
  setFormulaVisible: (visible: boolean) => void;
  formSummaryOptions: any[];
}

export const RenderElement: React.FC<RenderElementProps> = ({
  item,
  index,
  isDisabled,
  onRuleChange,
  onOpenFormula,
  formulaVisible,
  formulaData,
  onFormulaConfirm,
  setFormulaVisible,
  formSummaryOptions
}) => {
  // 变量
  const variableMap = {
    TEXT: ['TEXT', 'LONG_TEXT', 'EMAIL', 'PHONE', 'URL']
  };
  const getVariableOptions = (item: any) => {
    return formSummaryOptions
      .filter((optionItem) => {
        // 如果找不到对应的变量类型，返回所有选项
        if (!variableMap[item.fieldType as keyof typeof variableMap]) return true;
        return variableMap[item.fieldType as keyof typeof variableMap].includes(optionItem.fieldType);
      })
      .map((optionItem) => (
        <Option key={optionItem.fieldId} value={optionItem.fieldId}>
          {optionItem.displayName}
        </Option>
      ));
  };

  const switchKey = `${item.fieldType}_${item.op}`;
  console.log({ switchKey });

  const inputKey: string[] = [
    'TEXT_EQUALS',
    'TEXT_NOT_EQUALS',
    'TEXT_CONTAINS',
    'TEXT_NOT_CONTAINS',
    'TEXT_EXISTS_IN',
    'TEXT_NOT_EXISTS_IN',
    'LONG_TEXT_EQUALS',
    'LONG_TEXT_NOT_EQUALS',
    'LONG_TEXT_CONTAINS',
    'LONG_TEXT_NOT_CONTAINS',
    'EMAIL_EQUALS',
    'EMAIL_NOT_EQUALS',
    'EMAIL_CONTAINS',
    'EMAIL_NOT_CONTAINS',
    'EMAIL_EXISTS_IN',
    'EMAIL_NOT_EXISTS_IN',
    'PHONE_EQUALS',
    'PHONE_NOT_EQUALS',
    'PHONE_CONTAINS',
    'PHONE_NOT_CONTAINS',
    'PHONE_EXISTS_IN',
    'PHONE_NOT_EXISTS_IN',
    'URL_EQUALS',
    'URL_NOT_EQUALS',
    'URL_CONTAINS',
    'URL_NOT_CONTAINS',
    'ADDRESS_EQUALS',
    'ADDRESS_NOT_EQUALS',
    'ADDRESS_CONTAINS',
    'ADDRESS_NOT_CONTAINS',
    'AUTO_CODE_EQUALS',
    'AUTO_CODE_NOT_EQUALS',
    'AUTO_CODE_CONTAINS',
    'AUTO_CODE_NOT_CONTAINS',
    'AUTO_CODE_EXISTS_IN',
    'AUTO_CODE_NOT_EXISTS_IN'
  ];

  const numberKey: string[] = []; // 数字输入框

  const scopeKey: string[] = []; // 数字范围

  const complexInfo = {
    DATETIME_EQUALS: {
      type: 'date',
      options: 'EQUALS'
    },
    DATETIME_LATER_THAN: {
      type: 'date',
      options: 'LATER_THAN'
    },
    DATETIME_RANGE: {
      type: 'dateRange',
      options: 'LATER_RANGE'
    },
    DATA_SELECTION_EQUALS: {
      type: 'select',
      options: approvalResultOptions
    },
    DATA_SELECTION_NOT_EQUALS: {
      type: 'select',
      options: approvalResultOptions
    },
    DATA_SELECTION_CONTAINS: {
      type: 'selectMultiple',
      options: approvalResultOptions
    }
  };

  let elementTypeInfo: any = { type: '', options: [] };

  // 先排除单纯组件  【输入框 数字输入框 数字范围选择组件】
  if (inputKey.includes(switchKey)) {
    elementTypeInfo.type = 'input';
  } else if (numberKey.includes(switchKey)) {
    elementTypeInfo.type = 'number';
  } else if (scopeKey.includes(switchKey)) {
    elementTypeInfo.type = 'scope';
  }
  // 如果没有 就继续处理
  if (!elementTypeInfo.type) {
    if (switchKey in complexInfo) {
      elementTypeInfo = complexInfo[switchKey as keyof typeof complexInfo];
    }
  }

  // 如果类型为静态值
  if (item.operatorType === 'value' || !item.operatorType) {
    switch (elementTypeInfo.type) {
      case 'input':
        return (
          <Input
            className={styles.ruleItemInput}
            style={{ width: 150 }}
            allowClear
            value={item.value}
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, 'value', value)}
          />
        );
      case 'date':
        return (
          <DatePicker
            className={styles.ruleItemInput}
            style={{ width: 150 }}
            value={item.value}
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, 'value', value)}
          />
        );
      case 'dateRange':
        return (
          <DatePicker.RangePicker
            className={styles.ruleItemInput}
            style={{ width: 150 }}
            value={item.value}
            disabled={isDisabled}
            onChange={(dateString, date) => {
              onRuleChange(index, 'value', dateString);
            }}
            showTime={false}
          />
        );
      case 'select':
        return (
          <Select
            className={styles.ruleItemSelect}
            value={item.value}
            style={{ width: 150 }}
            showSearch
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, 'value', value)}
            filterOption={(inputValue, option) =>
              option.props.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0 ||
              option.props.children.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
            }
          >
            {elementTypeInfo.options?.map((option: any) => (
              <Option key={option.value} value={option.value}>
                {option.label}
              </Option>
            ))}
          </Select>
        );
      case 'selectMultiple':
        return (
          <Select
            className={styles.ruleItemSelect}
            value={item.value}
            style={{ width: 150 }}
            showSearch
            mode="multiple"
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, 'value', value)}
            filterOption={(inputValue, option) =>
              option.props.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0 ||
              option.props.children.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
            }
          >
            {elementTypeInfo.options?.map((option: any) => (
              <Option key={option.value} value={option.value}>
                {option.label}
              </Option>
            ))}
          </Select>
        );
      default:
        return (
          <Input
            className={styles.ruleItemInput}
            style={{ width: 150 }}
            allowClear
            value={item.value}
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, 'value', value)}
          />
        );
    }
  }

  // 如果类型为变量
  if (item.operatorType === 'variables') {
    return (
      <Select
        className={styles.ruleItemSelect}
        value={item.value}
        style={{ width: 150 }}
        disabled={isDisabled}
        onChange={(value) => onRuleChange(index, 'value', value)}
      >
        {getVariableOptions(item)}
      </Select>
    );
  }

  // 如果类型为公式
  if (item.operatorType === 'formula') {
    return (
      <>
        <Button
          className={styles.ruleItemButton}
          style={{ width: 150 }}
          onClick={() => onOpenFormula(index)}
          disabled={isDisabled}
        >
          {item.value ? '已设置公式' : 'ƒx 编辑公式'}
          {item.value ? <IconLaunch /> : ''}
        </Button>
        <FormulaEditor
          initialFormula={formulaData}
          visible={formulaVisible}
          onConfirm={onFormulaConfirm}
          onCancel={() => setFormulaVisible(false)}
        />
      </>
    );
  }

  return null;
};
