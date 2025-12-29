import { Form, Radio } from '@arco-design/web-react';
import styles from '../styles/index.module.less';

interface Props {
  onChange: (value: any) => void;
  item: any;
  value: any;
}

const DynamicWidthRadioConfig = ({ onChange, item, value }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Radio.Group
        type="button"
        direction="horizontal"
        size="mini"
        value={value}
        onChange={(v) => {
          onChange(v);
        }}
      >
        {item.range?.map((option: any) => (
          <Radio key={option.key} value={option.value} className={styles.widthRadio}>
            {option.text?.startsWith('editor.')
              ? option.text.split('.').pop()
              : option.text }
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
};

export default DynamicWidthRadioConfig;
