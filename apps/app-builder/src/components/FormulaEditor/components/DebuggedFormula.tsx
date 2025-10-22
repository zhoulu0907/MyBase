import { DatePicker, Form, Input, InputNumber, Typography } from "@arco-design/web-react";
import { IconArrowRight } from "@arco-design/web-react/icon";
import styles from "./DebuggedFormula.module.less";
import { executeFormula } from "@onebase/app";
import { useState } from "react";
const FormItem = Form.Item;

interface variableItem {
  fieldName: string,
  fieldId: string,
  fieldType: any
}

interface DebuggedFormulaProps {
  allRelatedVariables: variableItem[],
  formula: string
}

export function DebuggedFormula(props: DebuggedFormulaProps) {
  const { allRelatedVariables, formula } = props;
  const [displayValue, setDisplayValue] = useState<any>();
  const [form] = Form.useForm();

  const handleFormula = async() => {
     try {
      const values = await form.validate();
      console.log('提交数据 values:', values);
      const data = await executeFormula({
        formula: formula,
        parameters: values
      });
      setDisplayValue(data.result);
      console.log(data,"data")
    } catch (error) {
      console.log('提交数据失败 error:', error);
    }
  }

  const renderFormItem = (fieldype: any) => {
    switch (fieldype) {
      case "NUMBER":
        return <InputNumber placeholder='please enter' />
      case "DATEPICKER":
        return <DatePicker showTime />
      default:
        return <Input />
    }
  }

  return (
    <div className={styles.debugModeContainer}>
      <div className={styles.header}>
        <Typography.Title heading={6} className={styles.leftTitle}>字段赋值</Typography.Title>
        <Typography.Title heading={6} className={styles.rightTitle}>公式计算结果</Typography.Title>
      </div>
      <div className={styles.content}>
          <Form className={styles.variablesDisplay} form={form}>
          {allRelatedVariables.map((item) => {
            return <FormItem label={item.fieldName} field={item.fieldName} required>
              {renderFormItem(item.fieldType)}
            </FormItem>
          })}
        </Form>
        <div className={styles.calculateIcon} onClick={handleFormula} >
          <span>公式计算</span>
          <IconArrowRight style={{ fontSize: 30, color: "#4FAE7B" }}/>
        </div>
        <div className={styles.rightContent}>{displayValue}</div>
      </div>
    </div>
  );
}
