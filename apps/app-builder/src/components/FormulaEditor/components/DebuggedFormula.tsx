import { DatePicker, Form, Input, InputNumber, Typography } from "@arco-design/web-react";
import { IconArrowRight, IconLoading } from "@arco-design/web-react/icon";
import styles from "./DebuggedFormula.module.less";
import { debugFormula } from "@onebase/app";
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
  const [loading, setLoading] = useState<boolean>(false);  //当调用接口的时候显示加载中
  const [form] = Form.useForm();

  const handleFormula = async() => {
    setLoading(true);
     try {
      const values = await form.validate();
      console.log('提交数据 values:', values);
      let newValidFieldResult:{[key: string]: any}= {};
      Object.keys(values)?.map(key => {
        const fieldObj = values[key];
        if(typeof fieldObj === "object") {
          const fieldName = key + "." + Object.keys(fieldObj);
          const fieldValue = Object.values(fieldObj);
          newValidFieldResult[fieldName] = fieldValue[0] || ""
        }else {
          newValidFieldResult  = {
            ...newValidFieldResult,
            [key]: fieldObj
          }
        }
        
      })
      const data = await debugFormula({
        formula: formula,
        parameters: newValidFieldResult
      });
      setDisplayValue(data.result);
      console.log(data,"data")
    } catch (error) {
      console.log('提交数据失败 error:', error);
    } finally {
      setLoading(false);
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
            console.log(form.getFieldValue(item.fieldName),"11")
            return <FormItem label={item.fieldName} field={item.fieldName} rules={[{required: true}]}>
              {renderFormItem(item.fieldType)}
            </FormItem>
          })}
        </Form>
        <div className={styles.calculateIcon} onClick={handleFormula} >
          <span>公式计算</span>
          <IconArrowRight style={{ fontSize: 30, color: "#4FAE7B" }}/>
        </div>
        <div className={styles.rightContent}>
            {loading ? <IconLoading fontSize={24} /> : displayValue}
        </div>
      </div>
    </div>
  );
}
