import { Form, Switch } from '@arco-design/web-react';
import styles from '../styles/index.module.less';

interface Props {
  onChange: (value: any) => void;
  item: any;
  value: any;
}

const DynamicSwitchInputConfig = ({ onChange, item, value }: Props) => {
  return (
    <Form.Item
      label={<div style={{ textAlign: 'left' }}><span>{item.name}</span></div>}
      labelCol={{ span: 21 }}
      wrapperCol={{ span: 1 }}
      layout="horizontal"
      className={styles.formItem}
    >
      <Switch
        size="small"
        checked={value}
        onChange={(v) => {
          onChange(v);
        }}
      />
    </Form.Item>
  );
};

export default DynamicSwitchInputConfig;
