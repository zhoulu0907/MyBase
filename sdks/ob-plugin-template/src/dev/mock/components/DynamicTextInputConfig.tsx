import { Form, Input, InputNumber } from '@arco-design/web-react';
import styles from '../styles/index.module.less';

interface Props {
  onChange: (value: any) => void;
  item: any;
  value: any;
  inputType?: 'text' | 'textarea' | 'number';
}

const DynamicTextInputConfig = ({ onChange, item, value, inputType = 'text' }: Props) => {
  const renderInput = () => {
    if (inputType === 'textarea') {
      return (
        <Input.TextArea
          placeholder={`请输入${item.name}`}
          value={value}
          onChange={(v) => onChange(v)}
          autoSize={{ minRows: 3 }}
        />
      );
    }
    if (inputType === 'number') {
      return (
        <InputNumber
          placeholder={`请输入${item.name}`}
          value={value}
          onChange={(v) => onChange(v)}
          style={{ width: '100%' }}
        />
      );
    }
    return (
      <Input
        placeholder={`请输入${item.name}`}
        value={value}
        onChange={(v) => onChange(v)}
      />
    );
  };

  return (
    <Form.Item className={styles.formItem} label={item.name}>
      {renderInput()}
    </Form.Item>
  );
};

export default DynamicTextInputConfig;
