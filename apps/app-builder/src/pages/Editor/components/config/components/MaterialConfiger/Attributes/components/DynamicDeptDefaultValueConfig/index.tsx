import { useState } from 'react';
import { Button, Form, Select } from '@arco-design/web-react';
import { FormulaEditor } from '@/components/FormulaEditor';

export interface DynamicDeptDefaultValueConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const FormItem = Form.Item;

const DynamicDeptDefaultValueConfig: React.FC<DynamicDeptDefaultValueConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const [defaultValueMode, setDefaultValueMode] = useState(configs[item.key]);
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  // const [formulaFieldKey, setFormulaFieldKey] = useState<string>('');
  const [formulaData, setFormulaData] = useState<string>('');

  const handleModeChange = (value: string) => {
    setDefaultValueMode(value);
    handlePropsChange(item.key, value);
  };

  const handleFormulaConfirm = (formulaData: any, formattedFormula: string, params: any) => {
    setFormulaVisible(false);
    // form.setFieldValue(
    //   formulaFieldKey,
    //   {formulaData: formulaData, formula: formattedFormula, parameters: params}
    // );
    setFormulaData('');
    // setFormulaFieldKey('');
  };

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'默认值'} style={{ marginBottom: '8px' }}>
        <Select defaultValue={defaultValueMode} onChange={(value) => handleModeChange(value)}>
          <Select.Option value="custom">自定义</Select.Option>
          <Select.Option value="formula">公式计算</Select.Option>
        </Select>
      </FormItem>
      <FormItem>
        <Button long onClick={() => setFormulaVisible(true)}>
          {defaultValueMode === 'custom' ? '设置' : 'ƒx 编辑公式'}
        </Button>
      </FormItem>

      <FormulaEditor
        initialFormula={formulaData}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />
    </>
  );
};

export default DynamicDeptDefaultValueConfig;
