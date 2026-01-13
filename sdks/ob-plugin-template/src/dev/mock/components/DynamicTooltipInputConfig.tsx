import { Checkbox, Form, Input } from '@arco-design/web-react';
import styles from '../styles/index.module.less';

interface Props {
  onChange: (value: any) => void;
  item: any;
  value: any;
  isDivider?: boolean;
}

const DynamicTooltipInputConfig = ({ onChange, item, value, isDivider }: Props) => {
  const config = value || {};
  const textValue = isDivider ? config.text : config;

  return (
    <Form.Item
      className={styles.formItem}
      label={
        <>
          {item.name}
          {isDivider && typeof config.display === 'boolean' && (
            <Checkbox
              checked={config.display}
              style={{ float: 'right' }}
              onChange={(v) => {
                onChange({ ...config, display: v });
              }}
            >
              显示描述
            </Checkbox>
          )}
        </>
      }
    >
      <Input.TextArea
        placeholder={`请输入${item.name}`}
        value={textValue}
        maxLength={500}
        onChange={(v) => {
          onChange(isDivider ? { ...config, text: v } : v);
        }}
      />
    </Form.Item>
  );
};

export default DynamicTooltipInputConfig;
