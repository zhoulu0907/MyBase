// render-element.tsx
import { Input, Select, Button, DatePicker, InputNumber } from '@arco-design/web-react';
import { useState, useEffect, useRef } from 'react';
import { IconLaunch } from '@arco-design/web-react/icon';
import { FormulaEditor } from '@/components/FormulaEditor';
import type { ConditionRule } from '../../constants';
import styles from '../../siderbar-line.module.less';
import { FieldType } from '../../constants';
import { getUserPage, type PageParam } from '@onebase/platform-center';
import {
  InputKeyType,
  NumberKeyType,
  ScopeKeyType,
  VARIABLE_MAP,
  ComplexInfo,
  OperatorType,
  ElementType
} from './constants';
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
  const getUserData = (elementTypeInfo: any) => {
    const params: PageParam = {
      pageNo: 1,
      pageSize: 100
    };
    getUserPage(params)
      .then((res: any) => {
        if (Array.isArray(res?.list)) {
          const selectArr: any[] = [];
          res.list?.forEach((item: any) => {
            selectArr.push({
              value: item.id,
              label: item.nickname
            });
          });
          elementTypeInfo.options = selectArr;
        }
      })
      .catch((err: any) => {
        console.info('Api getUserPage Error:', err);
      });
  };
  const getVariableOptions = (item: any) => {
    return formSummaryOptions
      .filter((optionItem) => {
        const fieldType = optionItem.fieldType as FieldType;
        // 如果找不到对应的变量类型，返回所有选项
        if (!VARIABLE_MAP[item.fieldType as FieldType]) return true;
        return VARIABLE_MAP[item.fieldType as FieldType]?.includes(fieldType) ?? false;
      })
      .map((optionItem) => (
        <Option key={optionItem.fieldId} value={optionItem.fieldId}>
          {optionItem.displayName}
        </Option>
      ));
  };

  const switchKey = `${item.fieldType}_${item.op}`;
  console.log({ switchKey });

  let elementTypeInfo: any = { type: '', options: [] };

  // 先排除单纯组件  【输入框 数字输入框 数字范围选择组件】
  if (Object.values(InputKeyType).includes(switchKey as InputKeyType)) {
    elementTypeInfo.type = ElementType.INPUT;
  } else if (Object.values(NumberKeyType).includes(switchKey as NumberKeyType)) {
    elementTypeInfo.type = ElementType.NUMBER;
  } else if (Object.values(ScopeKeyType).includes(switchKey as ScopeKeyType)) {
    elementTypeInfo.type = ElementType.SCOPE;
  }

  // 如果没有 就继续处理
  if (!elementTypeInfo.type) {
    if (switchKey in ComplexInfo) {
      elementTypeInfo = ComplexInfo[switchKey as keyof typeof ComplexInfo];
    }
  }
  console.log(elementTypeInfo);

  // 用户选择数据
  useEffect(() => {
    if (elementTypeInfo.type === ElementType.USER_SELECT) {
      getUserData(elementTypeInfo);
    }
  }, [elementTypeInfo.type]);

  // 如果类型为静态值
  if (item.operatorType === OperatorType.VALUE || !item.operatorType) {
    switch (elementTypeInfo.type) {
      case ElementType.INPUT:
        return (
          <Input
            className={styles.ruleItemInput}
            style={{ width: 150 }}
            allowClear
            value={item.value}
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, OperatorType.VALUE, value)}
          />
        );
      case ElementType.DATE:
        return (
          <DatePicker
            className={styles.ruleItemInput}
            style={{ width: 150 }}
            value={item.value}
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, OperatorType.VALUE, value)}
          />
        );
      case ElementType.DATE_RANGE:
        return (
          <DatePicker.RangePicker
            className={styles.ruleItemInput}
            style={{ width: 150 }}
            value={item.value}
            disabled={isDisabled}
            onChange={(dateString, date) => {
              onRuleChange(index, OperatorType.VALUE, dateString);
            }}
            showTime={false}
          />
        );
      case ElementType.SELECT:
      case ElementType.USER_SELECT:
        return (
          <Select
            className={styles.ruleItemSelect}
            value={item.value}
            style={{ width: 150 }}
            showSearch
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, OperatorType.VALUE, value)}
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
      case ElementType.SELECT_MULTIPLE:
        return (
          <Select
            className={styles.ruleItemSelect}
            value={item.value}
            style={{ width: 150 }}
            showSearch
            mode="multiple"
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, OperatorType.VALUE, value)}
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
      case ElementType.NUMBER:
        return <InputNumber disabled={isDisabled} className={styles.ruleItemInputNumber} style={{ width: 150 }} />;
      default:
        return (
          <Input
            className={styles.ruleItemInput}
            style={{ width: 150 }}
            allowClear
            value={item.value}
            disabled={isDisabled}
            onChange={(value) => onRuleChange(index, OperatorType.VALUE, value)}
          />
        );
    }
  }

  // 如果类型为变量
  if (item.operatorType === OperatorType.VARIABLES) {
    return (
      <Select
        className={styles.ruleItemSelect}
        value={item.value}
        style={{ width: 150 }}
        disabled={isDisabled}
        onChange={(value) => onRuleChange(index, OperatorType.VALUE, value)}
      >
        {getVariableOptions(item)}
      </Select>
    );
  }

  // 如果类型为公式
  if (item.operatorType === OperatorType.FORMULA) {
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
