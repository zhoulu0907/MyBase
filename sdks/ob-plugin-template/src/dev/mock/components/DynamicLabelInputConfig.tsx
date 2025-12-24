import { Checkbox, Form, Input } from '@arco-design/web-react';
import styles from '../styles/index.module.less';

interface Props {
  onChange: (value: any) => void;
  item: any;
  value: any;
}

const DynamicLabelInputConfig = ({ onChange, item, value }: Props) => {
  const config = value || {};
  
  return (
    <Form.Item
      className={styles.formItem}
      label={
        <>
          {item.name}
          {typeof config.display === 'boolean' && (
            <Checkbox
              checked={config.display}
              style={{ float: 'right' }}
              onChange={(v) => {
                onChange({ ...config, display: v });
              }}
            >
              显示标题
            </Checkbox>
          )}
        </>
      }
    >
      <Input
        placeholder={`请输入${item.name}`}
        value={config.text}
        onChange={(v) => {
          onChange({ ...config, text: v });
        }}
      />
    </Form.Item>
  );
};

export default DynamicLabelInputConfig;
